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
import org.springframework.util.CollectionUtils;
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
import cn.topiam.employee.common.enums.account.OrganizationType;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceActionType;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceObjectType;
import cn.topiam.employee.common.repository.account.*;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceEventRecordRepository;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceRepository;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceSyncHistoryRepository;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceSyncRecordRepository;
import cn.topiam.employee.common.storage.Storage;
import cn.topiam.employee.core.message.mail.MailMsgEventPublish;
import cn.topiam.employee.identitysource.core.domain.Dept;
import cn.topiam.employee.identitysource.core.domain.User;
import cn.topiam.employee.identitysource.core.enums.IdentitySourceEventReceiveType;
import cn.topiam.employee.identitysource.core.processor.IdentitySourceEventPostProcessor;
import cn.topiam.employee.identitysource.core.processor.modal.IdentitySourceEventProcessData;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.repository.base.IdEntity;
import cn.topiam.employee.support.security.password.PasswordGenerator;

import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.EntityManager;
import static java.util.stream.Collectors.toSet;

import static cn.topiam.employee.support.constant.EiamConstants.PATH_SEPARATOR;
import static cn.topiam.employee.support.constant.EiamConstants.SYSTEM_DEFAULT_USER_NAME;

/**
 * 身份源数据 event 处理器
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/3/1 22:04
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
     * @param thirdPartyUserList {@link List}
     */
    void createUsers(List<User> thirdPartyUserList, LocalDateTime eventTime,
                     IdentitySourceEntity identitySource) {
        List<IdentitySourceEventRecordEntity> records = new ArrayList<>();
        List<UserEntity> createUsers = new ArrayList<>();
        Set<OrganizationMemberEntity> createOrganizationMembers = Sets.newHashSet();
        String targetId = identitySource.getStrategyConfig().getOrganization().getTargetId();
        //根据用户名查询
        List<UserEntity> existUsernames = getByUsernames(
            thirdPartyUserList.stream().map(User::getUserId).toList());
        //根据邮箱查询
        List<UserEntity> existEmails = getEmails(
            thirdPartyUserList.stream().map(User::getEmail).toList());
        //根据手机号查询
        List<UserEntity> existPhones = getPhones(
            thirdPartyUserList.stream().map(User::getPhone).toList());
        thirdPartyUserList.forEach(thirdPartyUser -> {
            log.info("处理上游用户新增事件:[{}]开始", thirdPartyUser.getUserId());
            UserEntity createUser = thirdPartyUserToUserEntity(thirdPartyUser, identitySource);
            log.info("根据上游用户:[{}],创建本地用户: {}", thirdPartyUser.getUserId(),
                JSONObject.toJSONString(createUser));
            if (existUsernames.stream()
                .anyMatch(user -> createUser.getUsername().equals(user.getUsername()))) {
                log.info("跳过上游用户:[{{}}]创建,用户名:[{}]在系统已存在", createUser.getExternalId(),
                    createUser.getUsername());
                addRecord(eventTime, identitySource, createUser.getId(), createUser.getFullName(),
                    records, SyncStatus.FAIL, IdentitySourceObjectType.USER,
                    IdentitySourceActionType.INSERT, "用户名 [" + createUser.getUsername() + "] 已存在");
                return;
            }
            if (StringUtils.isNotBlank(createUser.getEmail()) && existEmails.stream()
                .anyMatch(user -> createUser.getEmail().equals(user.getEmail()))) {
                log.info("跳过上游用户:[{{}}]创建,邮箱:[{}]在系统已存在", createUser.getExternalId(),
                    createUser.getEmail());
                addRecord(eventTime, identitySource, createUser.getId(), createUser.getFullName(),
                    records, SyncStatus.FAIL, IdentitySourceObjectType.USER,
                    IdentitySourceActionType.INSERT, "邮箱 [" + createUser.getEmail() + "] 已存在");
                return;
            }
            if (StringUtils.isNotBlank(createUser.getPhone()) && existPhones.stream()
                .anyMatch(user -> createUser.getPhone().equals(user.getPhone()))) {
                log.info("跳过上游用户:[{{}}]创建,手机号:[{}]在系统已存在", createUser.getExternalId(),
                    createUser.getPhone());
                addRecord(eventTime, identitySource, createUser.getId(), createUser.getFullName(),
                    records, SyncStatus.FAIL, IdentitySourceObjectType.USER,
                    IdentitySourceActionType.INSERT, "手机号 [" + createUser.getPhone() + "] 已存在");
                return;
            }
            createUsers.add(createUser);
            //组织关系
            List<OrganizationEntity> organizationList = organizationRepository
                .findByIdentitySourceIdAndExternalIdIn(identitySource.getId(),
                    thirdPartyUser.getDeptIdList());
            // 根组织
            if (thirdPartyUser.getDeptIdList().stream().anyMatch(
                deptId -> deptId.equals(getRootExternalId(identitySource.getProvider())))) {
                organizationRepository.findById(targetId).ifPresent(organizationList::add);
            }
            organizationList.forEach(organization -> {
                log.info("根据上游用户:[{}],创建组织[{}]({})与成员[{}]({})关系", thirdPartyUser.getUserId(),
                    organization.getName(), organization.getId(), createUser.getUsername(),
                    createUser.getId());
                createOrganizationMember(createOrganizationMembers, createUser, organization);
            });
            addRecord(eventTime, identitySource, createUser.getId(), createUser.getFullName(),
                records, SyncStatus.SUCCESS, IdentitySourceObjectType.USER,
                IdentitySourceActionType.INSERT, "新增成功");
            log.info("处理上游用户新增事件:[{}]结束", thirdPartyUser.getUserId());
        });
        //保存用户
        userRepository.batchSave(createUsers);
        //保存组织关系
        organizationMemberRepository.batchSave(Lists.newArrayList(createOrganizationMembers));
        //保存事件记录
        identitySourceEventRecordRepository.batchSave(records);
        // 批量发送短信邮件欢迎信息(密码通知)
        if (Boolean.TRUE.equals(identitySource.getStrategyConfig().getUser().getEmailNotify())) {
            publishMessage(createUsers);
        }
    }

    /**
     * 修改用户
     *
     * @param thirdPartyUserList {@link List}
     */
    void modifyUsers(List<User> thirdPartyUserList, LocalDateTime eventTime,
                     IdentitySourceEntity identitySource) {
        //@formatter:off
        List<IdentitySourceEventRecordEntity> records = new ArrayList<>();
        List<UserEntity> updateUsers = new ArrayList<>();
        Set<OrganizationMemberEntity> createOrganizationMembers = new HashSet<>();
        //需要删除组织成员关系集合 KEY：用户ID，value：组织ID
        Map<String, Set<String>> deleteOrganizationMembers = Maps.newHashMap();
        Map<UserEntity, Set<OrganizationEntity>> currentUsers = Maps.newHashMap();
        Map<User, Set<OrganizationEntity>> thirdPartyUsers = Maps.newHashMap();
        String targetId = identitySource.getStrategyConfig().getOrganization().getTargetId();
        //根据用户名查询
        List<UserEntity> existUsernames = getByUsernames(thirdPartyUserList.stream().map(User::getUserId).toList());
        //根据邮箱查询
        List<UserEntity> existEmails = getEmails(thirdPartyUserList.stream().map(User::getEmail).toList());
        //根据手机号查询
        List<UserEntity> existPhones = getPhones(thirdPartyUserList.stream().map(User::getPhone).toList());
        //根据上游用户封装数据，因为这里是修改操作，所以只有本地存在才会处理，如果上游用户在本地不存在，减少复杂度，这里不处理，可以走增量拉取
        thirdPartyUserList.forEach(thirdPartyUser -> {

            Optional<UserEntity> optional = userRepository.findByExternalId(thirdPartyUser.getUserId());
            if (optional.isPresent()) {
                UserEntity user = optional.get();
                //查询当前系统用户组织关联信息
                List<OrganizationMemberEntity> organizationMembers = organizationMemberRepository.findAllByUserId(user.getId());
                List<String> orgIdList = organizationMembers.stream().map(OrganizationMemberEntity::getOrgId).toList();
                //查询当前系统用户组织
                List<OrganizationEntity> currentUserOrgList = organizationRepository.findByIdentitySourceIdAndIdIn(identitySource.getId(), orgIdList);
                //添加根组织
                if (orgIdList.contains(targetId)) {
                    organizationRepository.findById(targetId).ifPresent(currentUserOrgList::add);
                }
                currentUsers.put(optional.get(), Sets.newHashSet(currentUserOrgList));
                //查询上游用户组织信息
                List<OrganizationEntity> organizationList = organizationRepository.findByIdentitySourceIdAndExternalIdIn(identitySource.getId(), thirdPartyUser.getDeptIdList());
                // 根组织
                if (thirdPartyUser.getDeptIdList().stream().anyMatch(deptId -> deptId.equals(getRootExternalId(identitySource.getProvider())))) {
                    organizationRepository.findById(targetId).ifPresent(organizationList::add);
                }
                thirdPartyUsers.put(thirdPartyUser, Sets.newHashSet(organizationList));
            }
        });
        thirdPartyUsers.keySet()
            .forEach(thirdPartyUser -> currentUsers.keySet().forEach(currentUser -> {
                if (thirdPartyUser.getUserId().equals(currentUser.getExternalId())) {
                    log.info("处理上游用户修改事件:[{}]开始", thirdPartyUser.getUserId());
                    //更新基本信息
                    setUpdateUser(thirdPartyUser, currentUser, identitySource);
                    log.info("上游用户:[{}]对应系统用户:[{}]({})存在,修改本地用户信息：{}", thirdPartyUser.getUserId(), currentUser.getUsername(), currentUser.getId(), JSONObject.toJSONString(currentUser));
                    if (existUsernames.stream().anyMatch(user -> !currentUser.getExternalId().equals(user.getExternalId()) && currentUser.getUsername().equals(user.getUsername()))) {
                        log.info("跳过上游用户:[{{}}]创建,用户名:[{}]在系统已存在", currentUser.getExternalId(),
                                currentUser.getUsername());
                        addRecord(eventTime, identitySource, currentUser.getId(), currentUser.getFullName(), records, SyncStatus.FAIL,
                                IdentitySourceObjectType.USER, IdentitySourceActionType.UPDATE, "用户名 [" + currentUser.getUsername() + "] 已存在");
                        return;
                    }
                    if (StringUtils.isNotBlank(currentUser.getEmail()) && existEmails.stream().anyMatch(user -> !currentUser.getExternalId().equals(user.getExternalId()) && currentUser.getEmail().equals(user.getEmail()))) {
                        log.info("跳过上游用户:[{{}}]创建,邮箱:[{}]在系统已存在", currentUser.getExternalId(),
                                currentUser.getEmail());
                        addRecord(eventTime, identitySource, currentUser.getId(), currentUser.getFullName(), records, SyncStatus.FAIL,
                                IdentitySourceObjectType.USER, IdentitySourceActionType.UPDATE, "邮箱 [" + currentUser.getEmail() + "] 已存在");
                        return;
                    }
                    if (StringUtils.isNotBlank(currentUser.getPhone()) && existPhones.stream().anyMatch(user -> !currentUser.getExternalId().equals(user.getExternalId()) && currentUser.getPhone().equals(user.getPhone()))) {
                        log.info("跳过上游用户:[{{}}]创建,手机号:[{}]在系统已存在", currentUser.getExternalId(),
                                currentUser.getPhone());
                        addRecord(eventTime, identitySource, currentUser.getId(), currentUser.getFullName(), records, SyncStatus.FAIL,
                                IdentitySourceObjectType.USER, IdentitySourceActionType.UPDATE, "手机号 [" + currentUser.getPhone() + "] 已存在");
                        return;
                    }
                    updateUsers.add(currentUser);
                    //处理组织机构关系
                    Set<OrganizationEntity> thirdPartyOrganizations = thirdPartyUsers.get(thirdPartyUser);
                    Set<OrganizationEntity> currentOrganizations = currentUsers.get(currentUser);
                    //需要创建的新关系
                    thirdPartyOrganizations.stream()
                        .filter(item -> !currentOrganizations.contains(item)).collect(toSet())
                        .forEach(organization -> {createOrganizationMember(createOrganizationMembers, currentUser, organization);
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
                    addRecord(eventTime, identitySource, currentUser.getId(), currentUser.getFullName(), records, SyncStatus.SUCCESS,
                            IdentitySourceObjectType.USER, IdentitySourceActionType.UPDATE, "修改成功");
                    log.info("处理上游用户修改事件:[{}]结束", thirdPartyUser.getUserId());
                }
                else {
                    addRecord(eventTime, identitySource, thirdPartyUser.getUserId(), Objects.nonNull(thirdPartyUser.getUserDetail()) ? thirdPartyUser.getUserDetail().getName() : null, records, SyncStatus.SKIP,
                            IdentitySourceObjectType.ORGANIZATION, IdentitySourceActionType.UPDATE, "用户[" + (Objects.nonNull(thirdPartyUser.getUserDetail()) ? thirdPartyUser.getUserDetail().getName() : thirdPartyUser.getUserId()) + "]不存在");
                }
            }));
        //更新用户信息
        userRepository.batchUpdate(updateUsers);
        //保存组织关系
        organizationMemberRepository.batchSave(Lists.newArrayList(createOrganizationMembers));
        //删除组织关系
        deleteOrganizationMembers.keySet()
            .forEach(user -> deleteOrganizationMembers.get(user)
                .forEach(organization -> organizationMemberRepository
                    .deleteByOrgIdAndUserId(organization, user)));
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
        List<UserEntity> removeUserList = new ArrayList<>();
        list.forEach(user -> {
            log.info("处理上游用户离职事件:[{}]开始", user);
            //离职删除
            removeUserList.add(user);
            //记录日志
            addRecord(eventTime, identitySource, user.getId(), user.getFullName(), records,
                SyncStatus.SUCCESS, IdentitySourceObjectType.USER, IdentitySourceActionType.DELETE,
                "删除成功");
            log.info("处理上游用户离职事件:[{}]结束", user);
        });
        //保存事件记录
        identitySourceEventRecordRepository.batchSave(records);
        //删除用户信息
        if (!CollectionUtils.isEmpty(removeUserList)) {
            List<String> deleteUserIds = removeUserList.stream().map(IdEntity::getId).toList();
            //删除用户
            userRepository.deleteAllById(deleteUserIds);
            //删除用户详情
            userDetailRepository.deleteAllByUserIdIn(deleteUserIds);
            //删除组织用户关联关系
            organizationMemberRepository.deleteAllByUserIdIn(deleteUserIds);
            //删除用户组关系
            userGroupMemberRepository.deleteAllByUserIdIn(deleteUserIds);
        }
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
            entity.setId(UUID.randomUUID().toString());
            entity.setName(thirdPartyOrganization.getName());
            entity.setCode(RandomStringUtils.randomAlphanumeric(7));
            entity.setType(OrganizationType.DEPARTMENT);
            entity.setParentId(parentOrganization.getId());
            entity.setPath(parentOrganization.getPath() + PATH_SEPARATOR + entity.getId());
            entity.setDisplayPath(
                parentOrganization.getDisplayPath() + PATH_SEPARATOR + entity.getName());
            entity.setExternalId(thirdPartyOrganization.getDeptId());
            entity.setOrder(thirdPartyOrganization.getOrder());
            entity.setIdentitySourceId(identitySource.getId());
            entity.setDataOrigin(getDataOrigin(identitySource.getProvider()).getType());
            entity.setLeaf(true);
            entity.setEnabled(true);
            //额外信息
            entity.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setUpdateTime(LocalDateTime.now());
            createOrganizations.add(entity);
            //记录日志
            addRecord(eventTime, identitySource, entity.getId(), entity.getName(), records,
                SyncStatus.SUCCESS, IdentitySourceObjectType.ORGANIZATION,
                IdentitySourceActionType.INSERT, "新增成功");
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
            .findByIdentitySourceIdAndExternalIdIn(identitySource.getId(),
                organizations.stream().map(Dept::getDeptId).toList());
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
                    current
                        .setPath(parentOrganization.getPath() + PATH_SEPARATOR + current.getId());
                    current.setDisplayPath(parentOrganization.getDisplayPath() + PATH_SEPARATOR
                                           + threeParty.getName());
                    //递归组装子级数据
                    updateOrganizations.addAll(processSubOrganizations(current));
                    //当前组织的父组织是否具有子节点，已经不存在子节点，更改 leaf=true
                    List<OrganizationEntity> subOrganizations = organizationRepository
                        .findByParentId(parentOrganization.getId());
                    if (subOrganizations.stream().map(i -> i.getId().equals(current.getId()))
                        .findAny().isEmpty()) {
                        organizationRepository.updateIsLeaf(parentOrganization.getId(), true);
                    }
                }
                if (parentOrganization.getLeaf()) {
                    organizationRepository.updateIsLeaf(parentOrganization.getId(), false);
                }
                //修改基本信息
                current.setName(threeParty.getName());
                current.setOrder(threeParty.getOrder());
                updateOrganizations.add(current);
                //记录日志
                addRecord(eventTime, identitySource, current.getId(), current.getName(), records,
                    SyncStatus.SUCCESS, IdentitySourceObjectType.ORGANIZATION,
                    IdentitySourceActionType.UPDATE, "修改成功");
            } else {
                addRecord(eventTime, identitySource, current.getId(), current.getName(), records,
                    SyncStatus.SKIP, IdentitySourceObjectType.ORGANIZATION,
                    IdentitySourceActionType.UPDATE, "部门[" + threeParty.getName() + "]不存在");
            }
        }));
        //批量保存部门信息
        organizationRepository.batchUpdate(updateOrganizations);
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
        List<OrganizationEntity> list = organizationRepository
            .findByIdentitySourceIdAndExternalIdIn(identitySource.getId(), organizationIds);
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
            record.setId(UUID.randomUUID().toString());
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
            sub.setPath(parent.getPath() + PATH_SEPARATOR + sub.getId());
            sub.setDisplayPath(parent.getDisplayPath() + PATH_SEPARATOR + sub.getName());
            list.add(sub);
            list.addAll(processSubOrganizations(sub));
        });
        return list;
    }

    private static void addRecord(LocalDateTime eventTime, IdentitySourceEntity identitySource,
                                  String id, String name,
                                  List<IdentitySourceEventRecordEntity> records, SyncStatus status,
                                  IdentitySourceObjectType objectType,
                                  IdentitySourceActionType actionType, String desc) {
        //记录日志
        IdentitySourceEventRecordEntity record = new IdentitySourceEventRecordEntity();
        record.setObjectId(id);
        record.setIdentitySourceId(identitySource.getId());
        record.setObjectName(name);
        record.setObjectType(objectType);
        record.setActionType(actionType);
        record.setEventTime(eventTime);
        record.setStatus(status);
        //额外信息
        record.setId(UUID.randomUUID().toString());
        record.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
        record.setUpdateTime(LocalDateTime.now());
        record.setDesc(desc);
        records.add(record);
    }

    protected List<UserEntity> getPhones(List<String> userPhonelist) {
        return userRepository.findAllByPhoneIn(userPhonelist);
    }

    protected List<UserEntity> getEmails(List<String> userEmialList) {
        return userRepository.findAllByEmailIn(userEmialList);
    }

    protected List<UserEntity> getByUsernames(List<String> userNameList) {
        return userRepository.findAllByUsernameIn(userNameList);
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
     * UserDetailRepository
     */
    private final UserDetailRepository                userDetailRepository;

    /**
     * UserGroupMemberRepository
     */
    private final UserGroupMemberRepository           userGroupMemberRepository;

    public DefaultIdentitySourceEventPostProcessor(MailMsgEventPublish mailMsgEventPublish,
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
                                                   UserDetailRepository userDetailRepository,
                                                   UserGroupMemberRepository userGroupMemberRepository) {
        super(mailMsgEventPublish, passwordEncoder, passwordGenerator, transactionDefinition,
            platformTransactionManager, entityManager, identitySourceRepository,
            identitySourceSyncHistoryRepository, identitySourceSyncRecordRepository, storage);
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.identitySourceEventRecordRepository = identitySourceEventRecordRepository;
        this.userDetailRepository = userDetailRepository;
        this.userGroupMemberRepository = userGroupMemberRepository;
    }
}
