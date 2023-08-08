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

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cn.topiam.employee.common.entity.account.OrganizationEntity;
import cn.topiam.employee.common.entity.account.OrganizationMemberEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceEntity;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceSyncHistoryEntity;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceSyncRecordEntity;
import cn.topiam.employee.common.enums.SyncStatus;
import cn.topiam.employee.common.enums.TriggerType;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceActionType;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceObjectType;
import cn.topiam.employee.common.repository.account.*;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceRepository;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceSyncHistoryRepository;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceSyncRecordRepository;
import cn.topiam.employee.common.storage.Storage;
import cn.topiam.employee.core.message.mail.MailMsgEventPublish;
import cn.topiam.employee.core.message.sms.SmsMsgEventPublish;
import cn.topiam.employee.core.mq.UserMessagePublisher;
import cn.topiam.employee.core.mq.UserMessageTag;
import cn.topiam.employee.identitysource.core.domain.User;
import cn.topiam.employee.identitysource.core.processor.IdentitySourceSyncUserPostProcessor;
import cn.topiam.employee.support.repository.domain.IdEntity;
import cn.topiam.employee.support.security.password.PasswordGenerator;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.EntityManager;
import static java.util.stream.Collectors.toSet;

import static cn.topiam.employee.common.constant.CommonConstants.SYSTEM_DEFAULT_USER_NAME;

/**
 * 身份源数据 pull post 处理器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/1 22:04
 */
@Slf4j
@Component
@SuppressWarnings({ "AlibabaMethodTooLong", "DuplicatedCode" })
public class DefaultIdentitySourceUserPostProcessor extends AbstractIdentitySourcePostProcessor
                                                    implements IdentitySourceSyncUserPostProcessor {

    @Override
    public void process(String batch, String identitySourceId, List<User> userList,
                        LocalDateTime startTime, TriggerType triggerType) {
        ProcessDataResult processData = null;
        //保存同步历史
        IdentitySourceSyncHistoryEntity history = saveSyncHistory(batch, identitySourceId,
            startTime, triggerType);
        //手动开启事务
        TransactionStatus transactionStatus = platformTransactionManager
            .getTransaction(transactionDefinition);
        try {
            IdentitySourceEntity identitySource = getIdentitySource(identitySourceId);
            //初始化数据
            InitDataResult initData = initData(identitySource, userList);
            //处理数据
            processData = processData(identitySource, initData);
            //校验数据
            validateData(processData);
            String batchEsUserId = "";
            //批量保存用户
            if (!CollectionUtils.isEmpty(processData.getCreateUsers())) {
                List<UserEntity> createUserList = Lists.newArrayList(processData.getCreateUsers());
                batchEsUserId += createUserList.stream().map(user -> String.valueOf(user.getId()))
                    .collect(Collectors.joining(","));
                userRepository.batchSave(createUserList);
            }
            //批量更新用户
            if (!CollectionUtils.isEmpty(processData.getUpdateUsers())) {
                List<UserEntity> updateUserList = Lists.newArrayList(processData.getUpdateUsers());
                batchEsUserId += updateUserList.stream().map(user -> String.valueOf(user.getId()))
                    .collect(Collectors.joining(","));
                userRepository.batchUpdate(updateUserList);
            }
            //保存组织成员关系
            if (!CollectionUtils.isEmpty(processData.getCreateOrganizationMembers())) {
                List<OrganizationMemberEntity> organizationMemberEntityList = Lists
                    .newArrayList(processData.getCreateOrganizationMembers());
                organizationMemberRepository.batchSave(organizationMemberEntityList);
            }
            //删除相关数据
            List<Long> deleteUserIds = new ArrayList<>();
            if (!CollectionUtils.isEmpty(processData.getDeleteUsers())) {
                deleteUserIds = processData.getDeleteUsers().stream().map(IdEntity::getId).toList();
                //删除用户
                userRepository.deleteAllById(deleteUserIds);
                //删除用户详情
                userDetailRepository.deleteAllByUserIds(deleteUserIds);
                //删除组织用户关联关系
                organizationMemberRepository.deleteAllByUserId(deleteUserIds);
                //删除用户组关系
                userGroupMemberRepository.deleteAllByUserId(deleteUserIds);
            }
            //删除组织关系
            if (!CollectionUtils.isEmpty(processData.getDeleteOrganizationMembers())) {
                for (Long userId : processData.getDeleteOrganizationMembers().keySet()) {
                    for (String orgId : processData.getDeleteOrganizationMembers().get(userId)) {
                        organizationMemberRepository.deleteByOrgIdAndUserId(orgId, userId);
                    }
                }
            }
            //更新同步历史
            updateSyncHistory(processData, history, SyncStatus.SUCCESS);
            //新增同步记录
            saveSyncHistoryRecord(history.getId(), processData);
            //提交事务
            platformTransactionManager.commit(transactionStatus);
            // 异步更新ES用户数据
            userMessagePublisher.sendUserChangeMessage(UserMessageTag.SAVE, batchEsUserId);
            // 异步删除用户ES数据
            if (!CollectionUtils.isEmpty(processData.getDeleteUsers())) {
                userMessagePublisher.sendUserChangeMessage(UserMessageTag.DELETE,
                    deleteUserIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
            }
            if (!CollectionUtils.isEmpty(processData.getCreateUsers())) {
                // 发送密码通知
                publishMessage(Lists.newArrayList(processData.getCreateUsers()));
            }
        } catch (Exception e) {
            log.error("处理用户数据发生异常", e);
            platformTransactionManager.rollback(transactionStatus);
            updateSyncHistory(processData, history, SyncStatus.FAIL);
        }
    }

    /**
     * 新增同步记录
     *
     * @param historyId  {@link Long}
     * @param processData {@link ProcessDataResult}
     */
    private void saveSyncHistoryRecord(Long historyId, ProcessDataResult processData) {
        //创建用户
        saveCreateUsersRecord(historyId, processData.getCreateUsers());
        //更新用户记录
        saveUpdateUsersRecord(historyId, processData.getUpdateUsers());
        //删除用户记录
        saveDeleteUsersRecord(historyId, processData.getDeleteUsers());
        //跳过用户记录
        saveSkipUsersRecord(historyId, processData.getSkipUsers());
    }

    /**
     * 保存创建用户记录
     *
     * @param historyId {@link Long}
     * @param createUsers {@link Set}
     */
    private void saveCreateUsersRecord(Long historyId, Set<UserEntity> createUsers) {
        List<IdentitySourceSyncRecordEntity> list = Lists.newArrayList();
        createUsers.forEach(user -> {
            IdentitySourceSyncRecordEntity entity = new IdentitySourceSyncRecordEntity();
            entity.setId(SNOWFLAKE.nextId());
            entity.setSyncHistoryId(historyId);
            entity.setStatus(SyncStatus.SUCCESS);
            entity.setObjectType(IdentitySourceObjectType.USER);
            entity.setObjectId(user.getId().toString());
            entity.setObjectName(user.getFullName());
            entity.setActionType(IdentitySourceActionType.INSERT);
            //其他信息
            entity.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setUpdateTime(LocalDateTime.now());
            list.add(entity);
        });
        //保存
        identitySourceSyncRecordRepository.batchSave(list);
    }

    /**
     * 保存更新用户记录
     *
     * @param historyId {@link Long}
     * @param updateUsers {@link Set}
     */
    private void saveUpdateUsersRecord(Long historyId, Set<UserEntity> updateUsers) {
        List<IdentitySourceSyncRecordEntity> list = Lists.newArrayList();
        updateUsers.forEach(user -> {
            IdentitySourceSyncRecordEntity entity = new IdentitySourceSyncRecordEntity();
            entity.setId(SNOWFLAKE.nextId());
            entity.setSyncHistoryId(historyId);
            entity.setStatus(SyncStatus.SUCCESS);
            entity.setObjectType(IdentitySourceObjectType.USER);
            entity.setObjectId(user.getId().toString());
            entity.setObjectName(user.getFullName());
            entity.setActionType(IdentitySourceActionType.UPDATE);
            //其他信息
            entity.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setUpdateTime(LocalDateTime.now());
            list.add(entity);
        });
        //保存
        identitySourceSyncRecordRepository.batchSave(list);
    }

    /**
     * 保存删除用户记录
     *
     * @param historyId {@link Long}
     * @param deleteUsers {@link Set}
     */
    private void saveDeleteUsersRecord(Long historyId, Set<UserEntity> deleteUsers) {
        List<IdentitySourceSyncRecordEntity> list = Lists.newArrayList();
        deleteUsers.forEach(user -> {
            IdentitySourceSyncRecordEntity entity = new IdentitySourceSyncRecordEntity();
            entity.setId(SNOWFLAKE.nextId());
            entity.setSyncHistoryId(historyId);
            entity.setStatus(SyncStatus.SUCCESS);
            entity.setObjectType(IdentitySourceObjectType.USER);
            entity.setObjectId(user.getId().toString());
            entity.setObjectName(user.getFullName());
            entity.setActionType(IdentitySourceActionType.DELETE);
            //其他信息
            entity.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setUpdateTime(LocalDateTime.now());
            list.add(entity);
        });
        //保存
        identitySourceSyncRecordRepository.batchSave(list);
    }

    /**
     * 保存跳过用户记录
     *
     * @param historyId {@link Long}
     * @param skipUsers {@link Set}
     */
    private void saveSkipUsersRecord(Long historyId, Set<SkipUser> skipUsers) {
        List<IdentitySourceSyncRecordEntity> list = Lists.newArrayList();
        skipUsers.forEach(skipUser -> {
            UserEntity user = skipUser.getUser();
            IdentitySourceSyncRecordEntity entity = new IdentitySourceSyncRecordEntity();
            entity.setId(SNOWFLAKE.nextId());
            entity.setSyncHistoryId(historyId);
            entity.setStatus(skipUser.getStatus());
            entity.setObjectType(IdentitySourceObjectType.USER);
            entity.setObjectId(user.getId().toString());
            entity.setObjectName(user.getFullName());
            entity.setActionType(skipUser.getActionType());
            //其他信息
            entity.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setUpdateTime(LocalDateTime.now());
            entity.setDesc(skipUser.getReason());
            list.add(entity);
        });
        //保存
        identitySourceSyncRecordRepository.batchSave(list);
    }

    /**
     * 更新同步历史
     *
     * @param processData {@link ProcessDataResult} 数据
     * @param history {@link IdentitySourceSyncHistoryEntity} 身份源同步历史
     * @param status {@link SyncStatus} 同步状态
     */
    private void updateSyncHistory(ProcessDataResult processData,
                                   IdentitySourceSyncHistoryEntity history, SyncStatus status) {
        if (!Objects.isNull(processData)) {
            history.setSkippedCount(processData.getSkipUsers().size());
            history.setDeletedCount(processData.getDeleteUsers().size());
            history.setUpdatedCount(processData.getUpdateUsers().size());
            history.setCreatedCount(processData.getCreateUsers().size());
        }
        history.setEndTime(LocalDateTime.now());
        history.setStatus(status);
        identitySourceSyncHistoryRepository.save(history);
    }

    /**
     * 保存同步历史
     *
     * @param batch {@link String} 批号
     * @param identitySourceId {@link String} 身份源ID
     * @param startTime {@link String} 开始时间
     * @param triggerType {@link String} 任务方式
     * @return {@link IdentitySourceSyncHistoryEntity}
     */
    @NotNull
    private IdentitySourceSyncHistoryEntity saveSyncHistory(String batch, String identitySourceId,
                                                            LocalDateTime startTime,
                                                            TriggerType triggerType) {
        IdentitySourceSyncHistoryEntity history = new IdentitySourceSyncHistoryEntity();
        history.setBatch(batch);
        history.setIdentitySourceId(Long.valueOf(identitySourceId));
        history.setStartTime(startTime);
        history.setObjectType(IdentitySourceObjectType.USER);
        history.setTriggerType(triggerType);
        history.setStatus(SyncStatus.PENDING);
        identitySourceSyncHistoryRepository.save(history);
        return history;
    }

    /**
     * 初始化数据
     *
     * @param identitySource {@link IdentitySourceEntity} 身份源信息
     * @param userList {@link List} 上游用户信息
     */
    private InitDataResult initData(IdentitySourceEntity identitySource, List<User> userList) {
        //@formatter:off
        //封装三方组织及用户，为上游最新数据结构，Key: 用户，Value: 部门列表
        Map<User, Set<OrganizationEntity>> thirdPartyUserOrganizations = Maps.newHashMap();
        //封装当前系统组织及用户， KEY: 用户，Value: 部门列表
        Map<UserEntity, Set<OrganizationEntity>> currentUserOrganizations = Maps.newHashMap();
        //封装三方用户
        for (User user : userList) {
            List<String> deptIdList = user.getDeptIdList();
            List<OrganizationEntity> list = organizationRepository.findByExternalIdIn(deptIdList);
            thirdPartyUserOrganizations.put(user, Sets.newHashSet(list));
        }
        List<OrganizationEntity> orgList = organizationRepository.findByIdentitySourceId(identitySource.getId());
        //封装当前用户
        for (OrganizationEntity organization : orgList) {
            List<UserEntity> users = userRepository.findAllByOrgId(organization.getId());
            users.forEach(user -> {
                if (CollectionUtils.isEmpty(currentUserOrganizations.get(user))) {
                    currentUserOrganizations.put(user, Sets.newHashSet(organization));
                } else {
                    Set<OrganizationEntity> set = currentUserOrganizations.get(user);
                    set.add(organization);
                    currentUserOrganizations.put(user, set);
                }
            });
        }
        //会存在同一来源下组织未关联用户情况，查询这部分数据
        List<UserEntity> userNotExistOrgList = userRepository.findAllByOrgIdNotExistAndIdentitySourceId(identitySource.getId());
        userNotExistOrgList.forEach(user -> currentUserOrganizations.put(user,Sets.newHashSet()));
        return InitDataResult.builder().currentUsers(currentUserOrganizations).thirdPartyUsers(thirdPartyUserOrganizations)
                .build();
        //@formatter:on
    }

    @Data
    @Builder
    private static class InitDataResult implements Serializable {

        @Serial
        private static final long                        serialVersionUID = 5558133274263199406L;

        /**
         * 封装三方部门及用户，为上游最新数据结构，Key: 用户，Value: 当前部门
         */
        private Map<User, Set<OrganizationEntity>>       thirdPartyUsers;
        /**
         * 封装当前系统部门及用户， KEY: 用户，Value: 用户所有部门
         */
        private Map<UserEntity, Set<OrganizationEntity>> currentUsers;
    }

    /**
     * 处理数据
     *
     * @param initData {@link InitDataResult}
     */
    private ProcessDataResult processData(IdentitySourceEntity identitySource,
                                          InitDataResult initData) {
        //@formatter:off
        // 创建、修改用户等 Set集合，Key： 组织ID，Value： 用户信息
        Set<UserEntity> createUsers = new TreeSet<>(Comparator.comparing(UserEntity::getUsername)), updateUsers = new TreeSet<>(Comparator.comparing(UserEntity::getUsername));
        //跳过用户集合，Key：用户，Value：跳过原因
        Set<SkipUser> skipUsers = Sets.newLinkedHashSet();
        //当前系统用户对应系统部门
        Map<UserEntity, Set<OrganizationEntity>> currentUsers = initData.getCurrentUsers();
        //上游用户对应上游部门
        Map<User, Set<OrganizationEntity>> thirdPartyUsers = initData.getThirdPartyUsers();
        //需要创建的组织成员关系
        Set<OrganizationMemberEntity> createOrganizationMembers=Sets.newLinkedHashSet();
        //需要删除组织成员关系集合 KEY：用户ID，value：组织ID
        Map< Long,Set<String>> deleteOrganizationMembers = Maps.newHashMap();
        //上游用户在当前系统存在
        currentUsers.keySet().stream().filter(i -> thirdPartyUsers.keySet().stream().map(User::getUserId).collect(toSet()).contains(i.getExternalId())).collect(toSet()).forEach(currentUser -> thirdPartyUsers.keySet().forEach(thirdPartyUser -> {
            if (currentUser.getExternalId().equals(thirdPartyUser.getUserId())) {
                log.info("处理上游用户:[{}]开始", thirdPartyUser.getUserId());
                //处理用户信息
                if (!equalsUser(thirdPartyUser, currentUser,identitySource.getId())) {
                    //设置更新用户数据
                    setUpdateUser(thirdPartyUser, currentUser,identitySource);
                    log.info("上游用户:[{}]对应系统用户:[{}]({})存在,用户信息不一致,修改用户信息：{}", thirdPartyUser.getUserId(), currentUser.getUsername(), currentUser.getId(), JSONObject.toJSONString(currentUser));
                    updateUsers.add(currentUser);
                } else {
                    skipUsers.add(SkipUser.builder().user(currentUser).actionType(IdentitySourceActionType.UPDATE).status(SyncStatus.SKIP).reason( "用户信息一致").build());
                    log.info("上游用户:[{}]对应系统用户:[{}]({})存在,用户信息一致", thirdPartyUser.getUserId(), currentUser.getUsername(), currentUser.getId());
                }
                //处理组织机构关系
                Set<OrganizationEntity> thirdPartyOrganizations = thirdPartyUsers.get(thirdPartyUser);
                Set<OrganizationEntity> currentOrganizations = currentUsers.get(currentUser);
                //需要创建的新关系
                thirdPartyOrganizations.stream().filter(item -> !currentOrganizations.contains(item)).collect(toSet()).forEach(organization -> {
                    createOrganizationMember(createOrganizationMembers, currentUser, organization);
                    log.info("上游用户:[{}]对应当前系统用户:[{}]({})存在,需要创建组织[{}]({})与成员[{}]({})关系", thirdPartyUser.getUserId(), currentUser.getUsername(), currentUser.getId(), organization.getName(), organization.getId(), currentUser.getUsername(), currentUser.getId());
                });
                //需要删除的旧关系
                currentOrganizations.stream().filter(item -> !thirdPartyOrganizations.contains(item)).collect(toSet()).forEach(organization -> {
                    Set<String> ids = deleteOrganizationMembers.get(currentUser.getId());
                    log.info("上游用户:[{}]对应当前系统用户:[{}]({})存在,需要删除组织[{}]({})与成员[{}]({})关系", thirdPartyUser.getUserId(), currentUser.getUsername(), currentUser.getId(), organization.getName(), organization.getId(), currentUser.getUsername(), currentUser.getId());
                    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(ids)) {
                        ids.add(organization.getId());
                        deleteOrganizationMembers.put(currentUser.getId(), ids);
                        return;
                    }
                    deleteOrganizationMembers.put(currentUser.getId(), Sets.newHashSet(organization.getId()));
                });
                log.info("处理上游用户:[{}]结束", thirdPartyUser.getUserId());
            }
        }));
        //上游用户在当前系统不存在
        thirdPartyUsers.keySet().stream().filter(i -> !currentUsers.keySet().stream().map(UserEntity::getExternalId).collect(toSet()).contains(i.getUserId())).collect(toSet()).forEach(thirdPartyUser -> {
            log.info("处理上游用户:[{}]开始", thirdPartyUser.getUserId());
            //新增用户信息
            UserEntity userEntity = thirdPartyUserToUserEntity(thirdPartyUser, identitySource);
            log.info("上游用户:[{}]在当前系统不存在,创建用户: {}", thirdPartyUser.getUserId(), JSONObject.toJSONString(userEntity));
            createUsers.add(userEntity);
            //处理组织机构关系
            thirdPartyUsers.get(thirdPartyUser).forEach(organization -> {
                createOrganizationMember(createOrganizationMembers, userEntity, organization);
                log.info("上游用户:[{}]在当前系统不存在,创建组织[{}]({})与成员[{}]({})关系", thirdPartyUser.getUserId(), organization.getName(), organization.getId(), userEntity.getUsername(), userEntity.getId());
            });
            log.info("处理上游用户:[{}]结束", thirdPartyUser.getUserId());
        });

        //过滤需要删除的用户
        Set<UserEntity> deleteUsers= currentUsers.keySet().stream().filter(currentUser -> !thirdPartyUsers.keySet().stream().map(User::getUserId).collect(toSet()).contains(currentUser.getExternalId())).collect(toSet());
        for (UserEntity user : deleteUsers) {
            log.info("上游用户不存在,本地存在用户:[{}]({})", user.getUsername(),user.getId());
        }
        return ProcessDataResult.builder()
                //需要创建的用户
                .createUsers(createUsers)
                //需要更新的用户
                .updateUsers(updateUsers)
                //需要跳过的用户
                .skipUsers(skipUsers)
                //需要的删除用户ID
                .deleteUsers(deleteUsers)
                //需要创建的组织成员关系
                .createOrganizationMembers(createOrganizationMembers)
                //需要删除的组织成员关系
                .deleteOrganizationMembers(deleteOrganizationMembers).build();
        //@formatter:on
    }

    /**
     * 校验数据
     *
     * @param processData {@link ProcessDataResult}
     */
    private void validateData(ProcessDataResult processData) {
        //处理创建用户
        validateCreateUserData(processData);
        //处理修改用户
        validateUpdateUserData(processData);
    }

    /**
     * 验证创建用户数据
     *
     * @param processData {@link ProcessDataResult}
     */
    private void validateCreateUserData(ProcessDataResult processData) {
        Set<UserEntity> createUsers = processData.getCreateUsers();
        Set<SkipUser> skipUsers = processData.getSkipUsers();
        Set<OrganizationMemberEntity> createOrganizationMembers = processData
            .getCreateOrganizationMembers();
        if (!CollectionUtils.isEmpty(createUsers)) {
            //根据用户名查询
            List<UserEntity> existUsernames = getByUsernames(createUsers);
            //根据邮箱查询
            List<UserEntity> existEmails = getEmails(createUsers);
            //根据手机号查询
            List<UserEntity> existPhones = getPhones(createUsers);
            existUsernames.forEach(user -> {
                Iterator<UserEntity> userIterator = createUsers.iterator();
                while (userIterator.hasNext()) {
                    UserEntity createUser = userIterator.next();
                    //校验用户名
                    if (createUser.getUsername().equals(user.getUsername())) {
                        log.info("跳过上游用户:[{{}}]创建,用户名:[{}]在系统已存在", createUser.getExternalId(),
                            user.getUsername());
                        skipUsers.add(SkipUser.builder().user(createUser)
                            .actionType(IdentitySourceActionType.INSERT).status(SyncStatus.FAIL)
                            .reason("用户名 [" + createUser.getUsername() + "] 已存在").build());
                        userIterator.remove();
                        //移除创建关系
                        createOrganizationMembers
                            .removeIf(member -> member.getUserId().equals(createUser.getId()));
                    }
                }
            });
            existEmails.forEach(user -> {
                Iterator<UserEntity> userIterator = createUsers.iterator();
                while (userIterator.hasNext()) {
                    UserEntity createUser = userIterator.next();
                    //校验邮箱
                    if (StringUtils.isNotBlank(createUser.getEmail())
                        && createUser.getEmail().equals(user.getEmail())) {
                        log.info("跳过上游用户:[{{}}]创建,邮箱:[{}]在系统已存在", createUser.getExternalId(),
                            user.getEmail());
                        skipUsers.add(SkipUser.builder().user(createUser)
                            .actionType(IdentitySourceActionType.INSERT).status(SyncStatus.FAIL)
                            .reason("邮箱 [" + createUser.getEmail() + "] 已存在").build());
                        userIterator.remove();
                        //移除创建关系
                        createOrganizationMembers
                            .removeIf(member -> member.getUserId().equals(createUser.getId()));
                    }
                }
            });
            existPhones.forEach(user -> {
                Iterator<UserEntity> userIterator = createUsers.iterator();
                while (userIterator.hasNext()) {
                    UserEntity createUser = userIterator.next();
                    //校验手机号
                    if (StringUtils.isNotBlank(createUser.getPhone())
                        && createUser.getPhone().equals(user.getPhone())) {
                        log.info("跳过上游用户:[{{}}]创建,手机号:[{}]在系统已存在", createUser.getExternalId(),
                            user.getPhone());
                        skipUsers.add(SkipUser.builder().user(createUser)
                            .actionType(IdentitySourceActionType.INSERT).status(SyncStatus.FAIL)
                            .reason("手机号 [" + createUser.getPhone() + "] 已存在").build());
                        userIterator.remove();
                        //移除创建关系
                        createOrganizationMembers
                            .removeIf(member -> member.getUserId().equals(createUser.getId()));
                    }
                }
            });
        }
    }

    /**
     * 验证修改用户数据
     *
     * @param processData {@link ProcessDataResult}
     */
    private void validateUpdateUserData(ProcessDataResult processData) {
        Set<UserEntity> updateUsers = processData.getUpdateUsers();
        Set<SkipUser> skipUsers = processData.getSkipUsers();
        if (!CollectionUtils.isEmpty(updateUsers)) {
            //根据用户名查询
            List<UserEntity> existUsernames = getByUsernames(updateUsers);
            //根据邮箱查询
            List<UserEntity> existEmails = getEmails(updateUsers);
            //根据手机号查询
            List<UserEntity> existPhones = getPhones(updateUsers);
            existUsernames.forEach(user -> {
                Iterator<UserEntity> userIterator = updateUsers.iterator();
                while (userIterator.hasNext()) {
                    UserEntity updateUser = userIterator.next();
                    //校验用户名
                    if (updateUser.getUsername().equals(user.getUsername())
                        && !updateUser.getId().equals(user.getId())) {
                        log.info("跳过上游用户:[{{}}]修改,用户名:[{}]在系统已存在", updateUser.getExternalId(),
                            user.getUsername());
                        skipUsers.add(SkipUser.builder().user(updateUser)
                            .actionType(IdentitySourceActionType.UPDATE).status(SyncStatus.FAIL)
                            .reason("用户名 [" + updateUser.getUsername() + "] 已存在").build());
                        userIterator.remove();
                    }
                }
            });
            existEmails.forEach(user -> {
                Iterator<UserEntity> userIterator = updateUsers.iterator();
                while (userIterator.hasNext()) {
                    UserEntity updateUser = userIterator.next();
                    //校验邮箱
                    if (StringUtils.isNotBlank(updateUser.getEmail())
                        && updateUser.getEmail().equals(user.getEmail())
                        && !updateUser.getId().equals(user.getId())) {
                        log.info("跳过上游用户:[{{}}]修改,邮箱:[{}]在系统已存在", updateUser.getExternalId(),
                            user.getEmail());
                        skipUsers.add(SkipUser.builder().user(updateUser)
                            .actionType(IdentitySourceActionType.UPDATE).status(SyncStatus.FAIL)
                            .reason("邮箱 [" + updateUser.getEmail() + "] 已存在").build());
                        userIterator.remove();
                    }
                }
            });
            existPhones.forEach(user -> {
                Iterator<UserEntity> userIterator = updateUsers.iterator();
                while (userIterator.hasNext()) {
                    UserEntity updateUser = userIterator.next();
                    //校验手机号
                    if (StringUtils.isNotBlank(updateUser.getPhone())
                        && updateUser.getPhone().equals(user.getPhone())
                        && !updateUser.getId().equals(user.getId())) {
                        log.info("跳过上游用户:[{{}}]修改,手机号:[{}]在系统已存在", updateUser.getExternalId(),
                            user.getPhone());
                        skipUsers.add(SkipUser.builder().user(updateUser)
                            .actionType(IdentitySourceActionType.UPDATE).status(SyncStatus.FAIL)
                            .reason("手机号 [" + updateUser.getPhone() + "] 已存在").build());
                        userIterator.remove();
                    }
                }
            });
        }
    }

    private List<UserEntity> getPhones(Set<UserEntity> list) {
        List<String> phones = list.stream().map(UserEntity::getPhone).toList();
        return userRepository.findAllByPhoneIn(phones);
    }

    private List<UserEntity> getEmails(Set<UserEntity> list) {
        List<String> emails = list.stream().map(UserEntity::getEmail).toList();
        return userRepository.findAllByEmailIn(emails);
    }

    private List<UserEntity> getByUsernames(Set<UserEntity> list) {
        List<String> usernames = list.stream().map(UserEntity::getUsername).toList();
        return userRepository.findAllByUsernameIn(usernames);
    }

    @Data
    @Builder
    private static class ProcessDataResult implements Serializable {

        @Serial
        private static final long             serialVersionUID = 5995750190105246430L;

        /**
         * 创建用户集合
         */
        private Set<UserEntity>               createUsers;

        /**
         * 更新用户集合
         */
        private Set<UserEntity>               updateUsers;

        /**
         * 跳过用户集合
         */
        private Set<SkipUser>                 skipUsers;

        /**
         * 删除用户
         */
        private Set<UserEntity>               deleteUsers;

        /**
         * 创建组织成员关系
         */
        private Set<OrganizationMemberEntity> createOrganizationMembers;

        /**
         * 删除组织成员关系 KEY：用户ID，value： 组织ID集合
         */
        private Map<Long, Set<String>>        deleteOrganizationMembers;
    }

    @Data
    @Builder
    private static class SkipUser {

        private UserEntity               user;
        private IdentitySourceActionType actionType;
        private SyncStatus               status;
        private String                   reason;
    }

    /**
     * 用户 Repository
     */
    private final UserRepository               userRepository;

    /**
     * 用户详情 Repository
     */
    private final UserDetailRepository         userDetailRepository;

    /**
     * 组织机构 Repository
     */
    private final OrganizationRepository       organizationRepository;

    /**
     * 组织成员 Repository
     */
    private final OrganizationMemberRepository organizationMemberRepository;

    /**
     * 删除用户组关联关系
     */
    private final UserGroupMemberRepository    userGroupMemberRepository;

    /**
     * MessagePublisher
     */
    private final UserMessagePublisher         userMessagePublisher;

    public DefaultIdentitySourceUserPostProcessor(SmsMsgEventPublish smsMsgEventPublish,
                                                  MailMsgEventPublish mailMsgEventPublish,
                                                  TransactionDefinition transactionDefinition,
                                                  PlatformTransactionManager platformTransactionManager,
                                                  EntityManager entityManager,
                                                  IdentitySourceRepository identitySourceRepository,
                                                  IdentitySourceSyncHistoryRepository identitySourceSyncHistoryRepository,
                                                  IdentitySourceSyncRecordRepository identitySourceSyncRecordRepository,
                                                  PasswordEncoder passwordEncoder,
                                                  PasswordGenerator passwordGenerator,
                                                  UserRepository userRepository,
                                                  UserDetailRepository userDetailRepository,
                                                  OrganizationMemberRepository organizationMemberRepository,
                                                  UserGroupMemberRepository userGroupMemberRepository,
                                                  OrganizationRepository organizationRepository,
                                                  Storage storage,
                                                  UserMessagePublisher userMessagePublisher) {
        super(smsMsgEventPublish, mailMsgEventPublish, passwordEncoder, passwordGenerator,
            transactionDefinition, platformTransactionManager, entityManager,
            identitySourceRepository, identitySourceSyncHistoryRepository,
            identitySourceSyncRecordRepository, storage);
        this.userRepository = userRepository;
        this.userDetailRepository = userDetailRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.userGroupMemberRepository = userGroupMemberRepository;
        this.organizationRepository = organizationRepository;
        this.userMessagePublisher = userMessagePublisher;
    }
}
