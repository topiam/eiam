/*
 * eiam-synchronizer - Employee Identity and Access Management
 * Copyright © 2022-Present Jinan Yuanchuang Network Technology Co., Ltd. (support@topiam.cn)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.topiam.employee.synchronizer.processor;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cn.topiam.employee.common.entity.account.OrganizationEntity;
import cn.topiam.employee.common.entity.account.OrganizationMemberEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceEntity;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceEventRecordEntity;
import cn.topiam.employee.common.enums.SyncStatus;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.common.enums.account.OrganizationType;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceActionType;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceObjectType;
import cn.topiam.employee.common.repository.account.OrganizationMemberRepository;
import cn.topiam.employee.common.repository.account.OrganizationRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceEventRecordRepository;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceRepository;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceSyncHistoryRepository;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceSyncRecordRepository;
import cn.topiam.employee.common.storage.Storage;
import cn.topiam.employee.core.message.mail.MailMsgEventPublish;
import cn.topiam.employee.core.message.sms.SmsMsgEventPublish;
import cn.topiam.employee.core.mq.UserMessagePublisher;
import cn.topiam.employee.core.mq.UserMessageTag;
import cn.topiam.employee.identitysource.core.domain.Dept;
import cn.topiam.employee.identitysource.core.domain.User;
import cn.topiam.employee.identitysource.core.enums.IdentitySourceEventReceiveType;
import cn.topiam.employee.identitysource.core.processor.IdentitySourceEventPostProcessor;
import cn.topiam.employee.identitysource.core.processor.modal.IdentitySourceEventProcessData;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.security.password.PasswordGenerator;

import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.EntityManager;
import static java.util.stream.Collectors.toSet;

import static cn.topiam.employee.common.constant.CommonConstants.SYSTEM_DEFAULT_USER_NAME;

/**
 * 身份源数据 event 处理器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/1 22:04
 */
@Slf4j
@Component
@SuppressWarnings({ "rawtypes", "unchecked", "DuplicatedCode" })
public class DefaultIdentitySourceEventPostProcessor extends AbstractIdentitySourcePostProcessor
                                                     implements IdentitySourceEventPostProcessor {

    /**
     * 处理数据
     *
     * @param eventData {@link  IdentitySourceEventProcessData}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void process(IdentitySourceEventProcessData eventData) {
        log.info("处理数据上游回调数据开始: {}", eventData);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        IdentitySourceEventReceiveType eventType = eventData.getEventType();
        IdentitySourceEntity identitySource = getIdentitySource(eventData.getId());
        LocalDateTime eventTime = eventData.getEventTime();
        //用户添加
        if (IdentitySourceEventReceiveType.USER_ADD.equals(eventType)) {
            createUsers(eventData.getData(), eventTime, identitySource);
            //用户修改
        } else if (IdentitySourceEventReceiveType.USER_MODIFY.equals(eventType)) {
            modifyUsers(eventData.getData(), eventTime, identitySource);
            //用户离职
        } else if (IdentitySourceEventReceiveType.USER_LEAVE.equals(eventType)) {
            leaveUsers(eventData.getData(), eventTime, identitySource);
            //部门添加
        } else if (IdentitySourceEventReceiveType.DEPT_CREATE.equals(eventType)) {
            createOrganizations(eventData.getData(), eventTime, identitySource);
            //部门修改
        } else if (IdentitySourceEventReceiveType.DEPT_MODIFY.equals(eventType)) {
            modifyOrganizations(eventData.getData(), eventTime, identitySource);
            //部门移除
        } else if (IdentitySourceEventReceiveType.DEPT_REMOVE.equals(eventType)) {
            removeOrganizations(eventData.getData(), eventTime, identitySource);
        } else {
            throw new TopIamException(eventData.getProvider().getName() + "身份提供商事件回调非法事件");
        }
        stopWatch.stop();
        log.info("处理数据上游回调数据结束, 执行时长: {} ms", stopWatch.getTotalTimeMillis());
    }

    /**
     * 创建用户
     *
     * @param users {@link List}
     */
    void createUsers(List<User> users, LocalDateTime eventTime,
                     IdentitySourceEntity identitySource) {
        List<IdentitySourceEventRecordEntity> records = new ArrayList<>();
        List<UserEntity> createUsers = new ArrayList<>();
        Set<OrganizationMemberEntity> createOrganizationMembers = Sets.newHashSet();
        List<String> batchEsUserIdList = new ArrayList<>();
        users.forEach(thirdPartyUser -> {
            log.info("处理上游用户新增事件:[{}]开始", thirdPartyUser.getUserId());
            UserEntity userEntity = thirdPartyUserToUserEntity(thirdPartyUser, identitySource);
            log.info("根据上游用户:[{}],创建本地用户: {}", thirdPartyUser.getUserId(),
                JSONObject.toJSONString(userEntity));
            createUsers.add(userEntity);
            //组织关系
            List<OrganizationEntity> organizationList = organizationRepository
                .findByExternalIdIn(thirdPartyUser.getDeptIdList());
            organizationList.forEach(organization -> {
                log.info("根据上游用户:[{}],创建组织[{}]({})与成员[{}]({})关系", thirdPartyUser.getUserId(),
                    organization.getName(), organization.getId(), userEntity.getUsername(),
                    userEntity.getId());
                createOrganizationMember(createOrganizationMembers, userEntity, organization);
            });
            batchEsUserIdList.add(String.valueOf(userEntity.getId()));
            //记录日志
            IdentitySourceEventRecordEntity record = new IdentitySourceEventRecordEntity();
            record.setObjectId(userEntity.getId().toString());
            record.setIdentitySourceId(identitySource.getId());
            record.setObjectName(userEntity.getFullName());
            record.setObjectType(IdentitySourceObjectType.USER);
            record.setActionType(IdentitySourceActionType.INSERT);
            record.setEventTime(eventTime);
            record.setStatus(SyncStatus.SUCCESS);
            //额外信息
            record.setId(SNOWFLAKE.nextId());
            record.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
            record.setCreateTime(LocalDateTime.now());
            record.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
            record.setUpdateTime(LocalDateTime.now());
            records.add(record);
            log.info("处理上游用户新增事件:[{}]结束", thirdPartyUser.getUserId());
        });
        //保存用户
        userRepository.batchSave(createUsers);
        //保存组织关系
        organizationMemberRepository.batchSave(Lists.newArrayList(createOrganizationMembers));
        //保存事件记录
        identitySourceEventRecordRepository.batchSave(records);
        // 异步创建ES用户数据
        userMessagePublisher.sendUserChangeMessage(UserMessageTag.SAVE,
            String.join(",", batchEsUserIdList));
        // 批量发送短信邮件欢迎信息(密码通知)
        publishMessage(createUsers);
    }

    /**
     * 修改用户
     *
     * @param users {@link List}
     */
    void modifyUsers(List<User> users, LocalDateTime eventTime,
                     IdentitySourceEntity identitySource) {
        //@formatter:off
        List<IdentitySourceEventRecordEntity> records = new ArrayList<>();
        List<UserEntity> updateUsers = new ArrayList<>();
        List<OrganizationMemberEntity> createOrganizationMembers = new ArrayList<>();
        List<String> batchEsUserIdList = new ArrayList<>();
        //需要删除组织成员关系集合 KEY：用户ID，value：组织ID
        Map<Long, Set<String>> deleteOrganizationMembers = Maps.newHashMap();
        Map<UserEntity, Set<OrganizationEntity>> currentUsers = Maps.newHashMap();
        Map<User, Set<OrganizationEntity>> thirdPartyUsers = Maps.newHashMap();
        //根据上游用户封装数据，因为这里是修改操作，所以只有本地存在才会处理，如果上游用户在本地不存在，减少复杂度，这里不处理，可以走增量拉取
        users.forEach(thirdParty -> {
            List<OrganizationEntity> list = organizationRepository.findByExternalIdIn(thirdParty.getDeptIdList());
            Optional<UserEntity> optional = userRepository.findByExternalId(thirdParty.getUserId());
            if (optional.isPresent()) {
                currentUsers.put(optional.get(), Sets.newHashSet(list));
                thirdPartyUsers.put(thirdParty, Sets.newHashSet(list));
            }
        });
        thirdPartyUsers.keySet()
            .forEach(thirdPartyUser -> currentUsers.keySet().forEach(currentUser -> {
                if (thirdPartyUser.getUserId().equals(currentUser.getExternalId())) {
                    log.info("处理上游用户修改事件:[{}]开始", thirdPartyUser.getUserId());
                    //更新基本信息
                    setUpdateUser(thirdPartyUser, currentUser, identitySource);
                    log.info("上游用户:[{}]对应系统用户:[{}]({})存在,修改本地用户信息：{}", thirdPartyUser.getUserId(), currentUser.getUsername(), currentUser.getId(), JSONObject.toJSONString(currentUser));
                    updateUsers.add(currentUser);
                    //处理组织机构关系
                    Set<OrganizationEntity> thirdPartyOrganizations = thirdPartyUsers.get(thirdPartyUser);
                    Set<OrganizationEntity> currentOrganizations = currentUsers.get(currentUser);
                    //需要创建的新关系
                    thirdPartyOrganizations.stream()
                        .filter(item -> !currentOrganizations.contains(item)).collect(toSet())
                        .forEach(organization -> {createOrganizationMember(Sets.newHashSet(createOrganizationMembers), currentUser, organization);
                            log.info("上游用户:[{}]对应当前系统用户:[{}]({}),需要创建本地组织[{}]({})与本地成员[{}]({})关系", thirdPartyUser.getUserId(), currentUser.getUsername(), currentUser.getId(), organization.getName(), organization.getId(), currentUser.getUsername(), currentUser.getId());
                        });
                    //需要删除的旧关系
                    currentOrganizations.stream()
                        .filter(item -> !thirdPartyOrganizations.contains(item)).collect(toSet())
                        .forEach(organization -> {
                            Set<String> ids = deleteOrganizationMembers.get(currentUser.getId());
                            log.info("上游用户:[{}]对应当前系统用户:[{}]({}),需要删除本地组织[{}]({})与本地成员[{}]({})关系", thirdPartyUser.getUserId(), currentUser.getUsername(), currentUser.getId(), organization.getName(), organization.getId(), currentUser.getUsername(), currentUser.getId());
                            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(ids)) {
                                ids.add(organization.getId());
                                deleteOrganizationMembers.put(currentUser.getId(), ids);
                                return;
                            }
                            deleteOrganizationMembers.put(currentUser.getId(), Sets.newHashSet(organization.getId()));
                        });
                    //记录日志
                    IdentitySourceEventRecordEntity record = new IdentitySourceEventRecordEntity();
                    record.setObjectId(currentUser.getId().toString());
                    record.setIdentitySourceId(identitySource.getId());
                    record.setObjectName(currentUser.getFullName());
                    record.setObjectType(IdentitySourceObjectType.USER);
                    record.setActionType(IdentitySourceActionType.UPDATE);
                    record.setEventTime(eventTime);
                    record.setStatus(SyncStatus.SUCCESS);
                    //额外信息
                    record.setId(SNOWFLAKE.nextId());
                    record.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
                    record.setCreateTime(LocalDateTime.now());
                    record.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
                    record.setUpdateTime(LocalDateTime.now());
                    records.add(record);

                    // 构建ES用户Id信息
                    batchEsUserIdList.add(String.valueOf(currentUser.getId()));
                    log.info("处理上游用户修改事件:[{}]结束", thirdPartyUser.getUserId());
                }
            }));
        //更新用户信息
        userRepository.batchUpdate(updateUsers);
        //保存组织关系
        organizationMemberRepository.batchSave(createOrganizationMembers);
        //删除组织关系
        deleteOrganizationMembers.keySet()
            .forEach(user -> deleteOrganizationMembers.get(user)
                .forEach(organization -> organizationMemberRepository
                    .deleteByOrgIdAndUserId(organization, user)));
        // 异步更新ES用户数据
        userMessagePublisher.sendUserChangeMessage(UserMessageTag.SAVE, String.join(",", batchEsUserIdList));
        //保存事件记录
        identitySourceEventRecordRepository.batchSave(records);
        //@formatter:on
    }

    /**
     * 用户离职
     *
     * @param userIds {@link List}
     */
    void leaveUsers(List<String> userIds, LocalDateTime eventTime,
                    IdentitySourceEntity identitySource) {
        List<IdentitySourceEventRecordEntity> records = new ArrayList<>();
        List<UserEntity> list = userRepository.findByExternalIdIn(userIds);
        list.forEach(user -> {
            log.info("处理上游用户离职事件:[{}]开始", user);
            //离职锁定
            user.setStatus(UserStatus.LOCKED);
            //记录日志
            IdentitySourceEventRecordEntity record = new IdentitySourceEventRecordEntity();
            record.setObjectId(user.getId().toString());
            record.setObjectName(user.getFullName());
            record.setIdentitySourceId(identitySource.getId());
            record.setObjectType(IdentitySourceObjectType.USER);
            record.setActionType(IdentitySourceActionType.UPDATE);
            record.setEventTime(eventTime);
            record.setStatus(SyncStatus.SUCCESS);
            //额外信息
            record.setId(SNOWFLAKE.nextId());
            record.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
            record.setCreateTime(LocalDateTime.now());
            record.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
            record.setUpdateTime(LocalDateTime.now());
            records.add(record);
            log.info("处理上游用户离职事件:[{}]结束", user);
        });
        //保存事件记录
        identitySourceEventRecordRepository.batchSave(records);
    }

    /**
     * 创建部门
     *
     * @param organizations {@link List}
     */
    void createOrganizations(List<Dept> organizations, LocalDateTime eventTime,
                             IdentitySourceEntity identitySource) {
        List<IdentitySourceEventRecordEntity> records = new ArrayList<>();
        List<OrganizationEntity> createOrganizations = new ArrayList<>();
        organizations.forEach(thirdPartyOrganization -> {
            log.info("处理上游部门新增事件:[{}]开始", thirdPartyOrganization.getDeptId());
            //查询上级部门
            //如果父部门是根节点，那需要查询一下IAM中根节点目前配置的是多少
            Optional<OrganizationEntity> parentOptional;
            if (!thirdPartyOrganization.getParentId()
                .equals(getRootExternalId(identitySource.getProvider()))) {
                parentOptional = organizationRepository
                    .findByExternalId(thirdPartyOrganization.getParentId());
            } else {
                String targetId = identitySource.getStrategyConfig().getOrganization()
                    .getTargetId();
                parentOptional = organizationRepository.findById(targetId);
            }
            if (parentOptional.isEmpty()) {
                return;
            }
            OrganizationEntity parentOrganization = parentOptional.get();
            //更改 leaf=false
            if (parentOrganization.getLeaf()) {
                organizationRepository.updateIsLeaf(parentOrganization.getId(), false);
            }
            //封装数据
            OrganizationEntity entity = new OrganizationEntity();
            entity.setId(String.valueOf(SNOWFLAKE.nextId()));
            entity.setName(thirdPartyOrganization.getName());
            entity.setCode(RandomStringUtils.randomAlphanumeric(7));
            entity.setType(OrganizationType.DEPARTMENT);
            entity.setParentId(parentOrganization.getId());
            entity.setPath(parentOrganization.getPath() + SEPARATE + entity.getId());
            entity
                .setDisplayPath(parentOrganization.getDisplayPath() + SEPARATE + entity.getName());
            entity.setExternalId(thirdPartyOrganization.getDeptId());
            entity.setOrder(thirdPartyOrganization.getOrder());
            entity.setIdentitySourceId(identitySource.getId());
            entity.setDataOrigin(getDataOrigin(identitySource.getProvider()));
            entity.setLeaf(true);
            entity.setEnabled(true);
            //额外信息
            entity.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setUpdateTime(LocalDateTime.now());
            createOrganizations.add(entity);
            //记录日志
            IdentitySourceEventRecordEntity record = new IdentitySourceEventRecordEntity();
            record.setObjectId(entity.getId());
            record.setObjectName(entity.getName());
            record.setIdentitySourceId(identitySource.getId());
            record.setObjectType(IdentitySourceObjectType.ORGANIZATION);
            record.setActionType(IdentitySourceActionType.INSERT);
            record.setEventTime(eventTime);
            record.setStatus(SyncStatus.SUCCESS);
            //额外信息
            record.setId(SNOWFLAKE.nextId());
            record.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
            record.setCreateTime(LocalDateTime.now());
            record.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
            record.setUpdateTime(LocalDateTime.now());
            records.add(record);
            log.info("处理上游部门新增事件:[{}]结束", thirdPartyOrganization.getDeptId());
        });
        //保存部门信息
        organizationRepository.batchSave(createOrganizations);
        //保存事件记录
        identitySourceEventRecordRepository.batchSave(records);
    }

    /**
     * 修改部门
     *
     * @param organizations {@link List}
     */
    void modifyOrganizations(List<Dept> organizations, LocalDateTime eventTime,
                             IdentitySourceEntity identitySource) {
        List<IdentitySourceEventRecordEntity> records = new ArrayList<>();
        List<OrganizationEntity> updateOrganizations = new ArrayList<>();
        //根据外部ID查询
        List<OrganizationEntity> list = organizationRepository
            .findByExternalIdIn(organizations.stream().map(Dept::getDeptId).toList());
        List<String> userIds = new ArrayList<>();
        list.forEach(current -> organizations.forEach(threeParty -> {
            //当前三方ID和上游科室对应
            if (current.getExternalId().equals(threeParty.getDeptId())) {

                //判断父部门是否变化，父部门变化，需要更改次部门的下级部门，判断名称是否更改，需要更改子路径显示名称
                //如果父部门是根节点，那需要查询一下IAM中根节点目前配置的是多少
                Optional<OrganizationEntity> parentOptional;
                if (!threeParty.getParentId()
                    .equals(getRootExternalId(identitySource.getProvider()))) {
                    parentOptional = organizationRepository
                        .findByExternalId(threeParty.getParentId());
                } else {
                    String targetId = identitySource.getStrategyConfig().getOrganization()
                        .getTargetId();
                    parentOptional = organizationRepository.findById(targetId);
                }
                if (parentOptional.isEmpty()) {
                    return;
                }
                //上级部门不一致
                OrganizationEntity parentOrganization = parentOptional.get();
                if (!StringUtils.equals(parentOrganization.getId(), current.getParentId())
                    || !StringUtils.equals(current.getName(), threeParty.getName())) {
                    current.setParentId(parentOrganization.getId());
                    current.setPath(parentOrganization.getPath() + SEPARATE + current.getId());
                    current.setDisplayPath(
                        parentOrganization.getDisplayPath() + SEPARATE + threeParty.getName());
                    //递归组装子级数据
                    updateOrganizations.addAll(processSubOrganizations(current));
                    //当前组织的父组织是否具有子节点，已经不存在子节点，更改 leaf=true
                    List<OrganizationEntity> subOrganizations = organizationRepository
                        .findByParentId(parentOrganization.getId());
                    if (subOrganizations.stream().map(i -> i.getId().equals(current.getId()))
                        .findAny().isEmpty()) {
                        organizationRepository.updateIsLeaf(parentOrganization.getId(), true);
                        // 查询关联用户
                        List<OrganizationMemberEntity> orgMemberList = organizationMemberRepository
                            .findAllByOrgId(parentOrganization.getId());
                        userIds.addAll(orgMemberList.stream()
                            .map(item -> String.valueOf(item.getUserId())).toList());
                    }
                }
                if (parentOrganization.getLeaf()) {
                    organizationRepository.updateIsLeaf(parentOrganization.getId(), false);
                    // 查询关联用户
                    List<OrganizationMemberEntity> orgMemberList = organizationMemberRepository
                        .findAllByOrgId(parentOrganization.getId());
                    userIds.addAll(orgMemberList.stream()
                        .map(item -> String.valueOf(item.getUserId())).toList());
                }
                //修改基本信息
                current.setName(threeParty.getName());
                current.setOrder(threeParty.getOrder());
                updateOrganizations.add(current);
                //记录日志
                IdentitySourceEventRecordEntity record = new IdentitySourceEventRecordEntity();
                record.setObjectId(current.getId());
                record.setObjectName(current.getName());
                record.setIdentitySourceId(identitySource.getId());
                record.setObjectType(IdentitySourceObjectType.ORGANIZATION);
                record.setActionType(IdentitySourceActionType.UPDATE);
                record.setEventTime(eventTime);
                record.setStatus(SyncStatus.SUCCESS);
                //额外信息
                record.setId(SNOWFLAKE.nextId());
                record.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
                record.setCreateTime(LocalDateTime.now());
                record.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
                record.setUpdateTime(LocalDateTime.now());
                records.add(record);
                // 查询关联用户
                List<OrganizationMemberEntity> orgMemberList = organizationMemberRepository
                    .findAllByOrgId(current.getId());
                userIds.addAll(
                    orgMemberList.stream().map(item -> String.valueOf(item.getUserId())).toList());
            }
        }));
        //批量保存部门信息
        organizationRepository.batchUpdate(updateOrganizations);
        // 更新es用户信息
        userMessagePublisher.sendUserChangeMessage(UserMessageTag.SAVE, String.join(",", userIds));
        //保存事件记录
        identitySourceEventRecordRepository.batchSave(records);
    }

    /**
     * 删除部门
     *
     * @param organizationIds {@link List}
     */
    void removeOrganizations(List<String> organizationIds, LocalDateTime eventTime,
                             IdentitySourceEntity identitySource) {
        List<IdentitySourceEventRecordEntity> records = new ArrayList<>();
        List<OrganizationEntity> list = organizationRepository.findByExternalIdIn(organizationIds);
        list.forEach(organization -> {
            log.info("处理上游部门删除事件:[{}]开始", organization.getId());
            IdentitySourceEventRecordEntity record = new IdentitySourceEventRecordEntity();
            record.setObjectId(organization.getId());
            record.setIdentitySourceId(identitySource.getId());
            record.setObjectName(organization.getName());
            record.setObjectType(IdentitySourceObjectType.ORGANIZATION);
            record.setActionType(IdentitySourceActionType.DELETE);
            record.setEventTime(eventTime);
            record.setStatus(SyncStatus.SUCCESS);
            //额外信息
            record.setId(SNOWFLAKE.nextId());
            record.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
            record.setCreateTime(LocalDateTime.now());
            record.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
            record.setUpdateTime(LocalDateTime.now());
            records.add(record);
            log.info("处理上游部门删除事件:[{}]结束", organization.getId());
        });
        //批量删除
        organizationRepository.deleteAllById(
            list.stream().map(OrganizationEntity::getId).collect(Collectors.toSet()));
        //保存事件记录
        identitySourceEventRecordRepository.batchSave(records);
    }

    private List<OrganizationEntity> processSubOrganizations(OrganizationEntity parent) {
        List<OrganizationEntity> list = new ArrayList<>();
        //递归组装子级数据
        List<OrganizationEntity> subOrganizations = organizationRepository
            .findByParentId(parent.getId());
        subOrganizations.forEach(sub -> {
            sub.setParentId(parent.getId());
            sub.setPath(parent.getPath() + SEPARATE + sub.getId());
            sub.setDisplayPath(parent.getDisplayPath() + SEPARATE + sub.getName());
            list.add(sub);
            list.addAll(processSubOrganizations(sub));
        });
        return list;
    }

    /**
     * UserRepository
     */
    private final UserRepository                      userRepository;

    /**
     * OrganizationRepository
     */
    private final OrganizationRepository              organizationRepository;

    /**
     * OrganizationMemberRepository
     */
    private final OrganizationMemberRepository        organizationMemberRepository;

    /**
     * IdentitySourceEventRecordRepository
     */
    private final IdentitySourceEventRecordRepository identitySourceEventRecordRepository;

    /**
     * MessagePublisher
     */
    private final UserMessagePublisher                userMessagePublisher;

    public DefaultIdentitySourceEventPostProcessor(SmsMsgEventPublish smsMsgEventPublish,
                                                   MailMsgEventPublish mailMsgEventPublish,
                                                   PasswordEncoder passwordEncoder,
                                                   PasswordGenerator passwordGenerator,
                                                   TransactionDefinition transactionDefinition,
                                                   PlatformTransactionManager platformTransactionManager,
                                                   EntityManager entityManager,
                                                   IdentitySourceRepository identitySourceRepository,
                                                   IdentitySourceSyncHistoryRepository identitySourceSyncHistoryRepository,
                                                   IdentitySourceSyncRecordRepository identitySourceSyncRecordRepository,
                                                   UserRepository userRepository,
                                                   OrganizationRepository organizationRepository,
                                                   OrganizationMemberRepository organizationMemberRepository,
                                                   IdentitySourceEventRecordRepository identitySourceEventRecordRepository,
                                                   Storage storage,
                                                   UserMessagePublisher userMessagePublisher) {
        super(smsMsgEventPublish, mailMsgEventPublish, passwordEncoder, passwordGenerator,
            transactionDefinition, platformTransactionManager, entityManager,
            identitySourceRepository, identitySourceSyncHistoryRepository,
            identitySourceSyncRecordRepository, storage);
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.identitySourceEventRecordRepository = identitySourceEventRecordRepository;
        this.userMessagePublisher = userMessagePublisher;
    }
}
