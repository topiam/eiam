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
package cn.topiam.employee.console.controller.account;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Lists;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.common.entity.account.UserGroupEntity;
import cn.topiam.employee.common.entity.account.query.UserGroupMemberListQuery;
import cn.topiam.employee.common.entity.account.query.UserListQuery;
import cn.topiam.employee.console.converter.account.UserGroupConverter;
import cn.topiam.employee.console.pojo.query.account.UserGroupListQuery;
import cn.topiam.employee.console.pojo.result.account.UserGroupListResult;
import cn.topiam.employee.console.pojo.result.account.UserGroupMemberListResult;
import cn.topiam.employee.console.pojo.result.account.UserGroupResult;
import cn.topiam.employee.console.pojo.save.account.UserCreateParam;
import cn.topiam.employee.console.pojo.save.account.UserGroupCreateParam;
import cn.topiam.employee.console.pojo.update.account.UserGroupUpdateParam;
import cn.topiam.employee.console.service.account.UserGroupService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import static cn.topiam.employee.common.constant.AccountConstants.USER_GROUP_PATH;

/**
 * 用户组
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/7/11 21:18
 */
@Validated
@AllArgsConstructor
@Tag(name = "用户分组")
@RestController
@RequestMapping(value = USER_GROUP_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserGroupController {

    /**
     * 获取分组列表
     *
     * @param page {@link PageModel}
     * @return {@link UserListQuery}
     */
    @Operation(summary = "获取用户组列表")
    @GetMapping(value = "/list")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Page<UserGroupListResult>> getUserGroupList(PageModel page,
                                                                     UserGroupListQuery query) {
        Page<UserGroupListResult> list = userGroupService.getUserGroupList(page, query);
        return ApiRestResult.<Page<UserGroupListResult>> builder().result(list).build();
    }

    /**
     * 创建分组
     *
     * @param param {@link UserCreateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "创建用户组")
    @Audit(type = EventType.CREATE_USER_GROUP)
    @PostMapping(value = "/create")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> createUserGroup(@RequestBody @Validated UserGroupCreateParam param) {
        return ApiRestResult.<Boolean> builder().result(userGroupService.createUserGroup(param))
            .build();
    }

    /**
     * 更改用户分组
     *
     * @param param {@link UserCreateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "修改用户组")
    @Audit(type = EventType.UPDATE_USER_GROUP)
    @PutMapping(value = "/update")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> updateUserGroup(@RequestBody @Validated UserGroupUpdateParam param) {
        return ApiRestResult.<Boolean> builder().result(userGroupService.updateUserGroup(param))
            .build();
    }

    /**
     * 删除分组
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "删除用户组")
    @Audit(type = EventType.DELETE_USER_GROUP)
    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> deleteUserGroup(@PathVariable(value = "id") String id) {
        return ApiRestResult.<Boolean> builder().result(userGroupService.deleteUserGroup(id))
            .build();
    }

    /**
     * 根据ID查询用户分组
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Operation(summary = "获取用户组信息")
    @GetMapping(value = "/get/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<UserGroupResult> getUserGroup(@PathVariable(value = "id") String id) {
        UserGroupEntity entity = userGroupService.getUserGroup(Long.valueOf(id));
        UserGroupResult result = userGroupConverter.entityConvertToUserGroupResult(entity);
        return ApiRestResult.<UserGroupResult> builder().result(result).build();
    }

    /**
     * 添加分组用户
     *
     * @param userIds {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Validated
    @Operation(summary = "添加用户组成员")
    @Audit(type = EventType.ADD_USER_GROUP_MEMBER)
    @PostMapping(value = "/add_member/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> addMember(@PathVariable(value = "id") String id,
                                            @NotNull(message = "成员ID不能为空") @Parameter(description = "成员ID") String[] userIds) {
        return ApiRestResult.<Boolean> builder().result(userGroupService.addMember(id, userIds))
            .build();
    }

    /**
     * 移除分组用户
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "移除用户组成员")
    @Audit(type = EventType.REMOVE_USER_GROUP_MEMBER)
    @DeleteMapping(value = "/remove_member/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> removeMember(@PathVariable(value = "id") String id,
                                               @NotEmpty(message = "用户ID不能为空") @Parameter(description = "用户ID集合") String[] userIds) {
        return ApiRestResult.<Boolean> builder()
            .result(userGroupService.batchRemoveMember(id, Lists.newArrayList(userIds))).build();
    }

    /**
     * 获取分组用户
     *
     * @param query {@link UserGroupMemberListQuery} 参数
     * @return {@link Boolean}
     */
    @Operation(summary = "获取用户组成员")
    @GetMapping(value = "/{id}/member_list")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Page<UserGroupMemberListResult>> getUserGroupMemberList(PageModel model,
                                                                                 UserGroupMemberListQuery query) {
        return ApiRestResult.<Page<UserGroupMemberListResult>> builder()
            .result(userGroupService.getUserGroupMemberList(model, query)).build();
    }

    /**
     * 用户组数据映射
     */
    private final UserGroupConverter userGroupConverter;
    /**
     * 用户分组service
     */
    private final UserGroupService   userGroupService;
}
