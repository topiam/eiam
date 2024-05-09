/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.service.account.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.UserGroupEntity;
import cn.topiam.employee.common.entity.account.UserGroupMemberEntity;
import cn.topiam.employee.common.entity.account.po.UserPO;
import cn.topiam.employee.common.entity.account.query.UserGroupMemberListQuery;
import cn.topiam.employee.common.exception.UserGroupExistUserException;
import cn.topiam.employee.common.exception.UserGroupNotExistException;
import cn.topiam.employee.common.repository.account.UserGroupMemberRepository;
import cn.topiam.employee.common.repository.account.UserGroupRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.console.converter.account.UserGroupConverter;
import cn.topiam.employee.console.pojo.query.account.UserGroupListQuery;
import cn.topiam.employee.console.pojo.result.account.UserGroupListResult;
import cn.topiam.employee.console.pojo.result.account.UserGroupMemberListResult;
import cn.topiam.employee.console.pojo.save.account.UserGroupCreateParam;
import cn.topiam.employee.console.pojo.update.account.UserGroupUpdateParam;
import cn.topiam.employee.console.service.account.UserGroupService;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.util.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.support.repository.base.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.base.BaseEntity.LAST_MODIFIED_TIME;

/**
 * 用户组实现service
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2021/10/11 21:30
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserGroupServiceImpl implements UserGroupService {
    /**
     * 查询用户分组列表
     *
     * @param page  {@link PageModel}
     * @param query {@link UserGroupListQuery}
     * @return {@link UserGroupListResult}
     */
    @Override
    public Page<UserGroupListResult> getUserGroupList(PageModel page, UserGroupListQuery query) {
        //查询条件
        Specification<UserGroupEntity> specification = userGroupConverter
            .queryUserGroupListParamConvertToSpecification(query);
        //查询映射
        org.springframework.data.domain.Page<UserGroupEntity> list = userGroupRepository
            .findAll(specification, PageRequest.of(page.getCurrent(), page.getPageSize()));
        return userGroupConverter.userGroupEntityConvertToUserGroupResult(list);
    }

    /**
     * 创建用户组
     *
     * @param param {@link UserGroupCreateParam}
     * @return {@link Boolean}
     */
    @Override
    public Boolean createUserGroup(UserGroupCreateParam param) {
        UserGroupEntity entity = userGroupConverter.userGroupCreateParamConvertToEntity(param);
        userGroupRepository.save(entity);
        AuditContext.setTarget(Target.builder().id(entity.getId()).name(entity.getName())
            .type(TargetType.USER_GROUP).build());
        return true;
    }

    /**
     * 更新用户组
     *
     * @param param {@link UserGroupUpdateParam}
     * @return {@link Boolean}
     */
    @Override
    public Boolean updateUserGroup(UserGroupUpdateParam param) {
        UserGroupEntity entity = userGroupConverter.userGroupUpdateParamConvertToEntity(param);
        UserGroupEntity details = getUserGroup(param.getId());
        BeanUtils.merge(entity, details, LAST_MODIFIED_TIME, LAST_MODIFIED_BY);
        userGroupRepository.save(details);
        AuditContext.setTarget(Target.builder().id(details.getId()).name(entity.getName())
            .type(TargetType.USER_GROUP).build());
        return true;
    }

    /**
     * 删除用户组
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean deleteUserGroup(String id) {
        Optional<UserGroupEntity> optional = userGroupRepository.findById(id);
        //用户组不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("删除用户组失败，用户组不存在");
            log.warn(AuditContext.getContent());
            throw new UserGroupNotExistException(AuditContext.getContent());
        }
        //用户组存在用户
        Long count = getUserGroupMemberCount(id);
        if (count > 0) {
            AuditContext.setContent("删除用户组失败，当前用户组下存在用户");
            log.warn(AuditContext.getContent());
            throw new UserGroupExistUserException(AuditContext.getContent());
        }
        userGroupRepository.deleteById(id);
        AuditContext.setTarget(Target.builder().id(id).name(optional.get().getName())
            .type(TargetType.USER_GROUP).build());
        return true;
    }

    /**
     * 获取用户组内分组列表
     *
     * @param query {@link UserGroupMemberListQuery}
     * @return {@link UserGroupMemberListResult}
     */
    @Override
    public Page<UserGroupMemberListResult> getUserGroupMemberList(PageModel model,
                                                                  UserGroupMemberListQuery query) {
        org.springframework.data.domain.Page<UserPO> page = userGroupMemberRepository
            .getUserGroupMemberList(query, PageRequest.of(model.getCurrent(), model.getPageSize()));
        return userGroupConverter.userPoConvertToGroupMemberListResult(page);
    }

    /**
     * 从用户组移除用户
     *
     * @param id     {@link String}
     * @param userId {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean removeMember(String id, String userId) {
        //查询关联关系
        userGroupMemberRepository.deleteByGroupIdAndUserId(id, userId);
        AuditContext.setTarget(Target.builder().id(userId).name("").type(TargetType.USER).build(),
            Target.builder().id(id).name("").type(TargetType.USER_GROUP).build());
        return true;
    }

    /**
     * 添加用户
     *
     * @param userIds {@link String}
     * @param groupId {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean addMember(String groupId, String[] userIds) {
        Optional<UserGroupEntity> optional = userGroupRepository.findById(groupId);
        //用户组不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("操作失败，用户组不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        List<UserGroupMemberEntity> list = new ArrayList<>();
        Lists.newArrayList(userIds).forEach(id -> {
            UserGroupMemberEntity member = new UserGroupMemberEntity();
            member.setGroupId(groupId);
            member.setUserId(id);
            list.add(member);
        });
        //添加
        userGroupMemberRepository.saveAll(list);
        List<Target> targets;
        CompletableFuture<List<Target>> future = CompletableFuture
            .supplyAsync(() -> new ArrayList<>(Arrays.stream(userIds).map(i -> {
                Optional<UserEntity> userEntityOptional = userRepository.findById(i);
                return userEntityOptional.map(userEntity -> Target.builder().id(i)
                    .name(userEntity.getFullName()).type(TargetType.USER).build()).orElse(null);
            }).toList()));
        try {
            // 等待最终结果
            targets = future.get();
        } catch (InterruptedException | ExecutionException e) {
            // 处理异常
            return false;
        }
        targets.add(Target.builder().id(groupId).name(optional.get().getName())
            .type(TargetType.USER_GROUP).build());
        AuditContext.setTarget(targets);
        return true;
    }

    /**
     * 根据ID查询用户分组
     *
     * @param id {@link Long}
     * @return {@link UserGroupEntity}
     */
    @Override
    public UserGroupEntity getUserGroup(String id) {
        return userGroupRepository.findById(id).orElse(null);
    }

    /**
     * 批量移除用户
     *
     * @param userIds {@link String}
     * @param id      {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean batchRemoveMember(String id, List<String> userIds) {
        Optional<UserGroupEntity> optional = userGroupRepository.findById(id);
        //用户组不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("操作失败，用户组不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        userIds.forEach(userId -> userGroupMemberRepository.deleteByGroupIdAndUserId(id, userId));
        List<Target> targets;
        CompletableFuture<List<Target>> future = CompletableFuture
            .supplyAsync(() -> new ArrayList<>(userIds.stream().map(i -> {
                Optional<UserEntity> userEntityOptional = userRepository.findById(i);
                return userEntityOptional.map(userEntity -> Target.builder().id(i)
                    .name(userEntity.getFullName()).type(TargetType.USER).build()).orElse(null);
            }).toList()));
        try {
            // 等待最终结果
            targets = future.get();
        } catch (InterruptedException | ExecutionException e) {
            // 处理异常
            return false;
        }
        targets.add(Target.builder().id(id).name(optional.get().getName())
            .type(TargetType.USER_GROUP).build());
        AuditContext.setTarget(targets);
        return true;
    }

    @Override
    public Long getUserGroupMemberCount(String groupId) {
        return userGroupMemberRepository
            .count(Example.of(new UserGroupMemberEntity().setGroupId(groupId)));
    }

    /**
     * 用户组数据映射
     */
    private final UserGroupConverter        userGroupConverter;

    /**
     * UserGroupRepository
     */
    private final UserGroupRepository       userGroupRepository;

    /**
     * UserGroupMemberRepository
     */
    private final UserGroupMemberRepository userGroupMemberRepository;

    /**
     * UserRepository
     */
    private final UserRepository            userRepository;
}
