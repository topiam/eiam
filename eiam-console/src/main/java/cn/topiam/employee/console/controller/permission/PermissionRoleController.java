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
package cn.topiam.employee.console.controller.permission;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.common.enums.CheckValidityType;
import cn.topiam.employee.console.pojo.query.permission.PermissionRoleListQuery;
import cn.topiam.employee.console.pojo.result.permission.PermissionRoleListResult;
import cn.topiam.employee.console.pojo.result.permission.PermissionRoleResult;
import cn.topiam.employee.console.pojo.save.permission.PermissionRoleCreateParam;
import cn.topiam.employee.console.pojo.update.permission.PermissionRoleUpdateParam;
import cn.topiam.employee.console.service.permission.PermissionRoleService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import static cn.topiam.employee.common.constant.PermissionConstants.PERMISSION_PATH;

/**
 * 应用角色
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/26 20:28
 */
@RequiredArgsConstructor
@Validated
@Tag(name = "应用权限-角色")
@RequestMapping(value = PERMISSION_PATH + "/role", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class PermissionRoleController {

    /**
     * 获取所有角色（分页）
     *
     * @param page {@link PageModel}
     * @return {@link PermissionRoleListResult}
     */
    @Operation(summary = "获取角色列表")
    @GetMapping(value = "/list")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Page<PermissionRoleListResult>> getPermissionRoleList(PageModel page,
                                                                               @Validated PermissionRoleListQuery query) {
        Page<PermissionRoleListResult> result = permissionRoleService.getPermissionRoleList(page,
            query);
        return ApiRestResult.<Page<PermissionRoleListResult>> builder().result(result).build();
    }

    /**
     * 创建角色
     *
     * @param param {@link PermissionRoleCreateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "创建角色")
    @Audit(type = EventType.SAVE_APP_PERMISSION_ROLE)
    @PostMapping(value = "/create")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> createPermissionRole(@Validated @RequestBody PermissionRoleCreateParam param) {
        return ApiRestResult.<Boolean> builder()
            .result(permissionRoleService.createPermissionRole(param)).build();
    }

    /**
     * 修改角色
     *
     * @param param {@link PermissionRoleUpdateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "修改角色")
    @Audit(type = EventType.UPDATE_APP_PERMISSION_ROLE)
    @PutMapping(value = "/update")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> updatePermissionRole(@RequestBody @Validated PermissionRoleUpdateParam param) {
        return ApiRestResult.<Boolean> builder()
            .result(permissionRoleService.updatePermissionRole(param)).build();
    }

    /**
     * 删除角色
     *
     * @param ids {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "删除角色")
    @Audit(type = EventType.DELETE_APP_PERMISSION_ROLE)
    @DeleteMapping(value = "/delete/{ids}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> deletePermissionRole(@PathVariable(value = "ids") String ids) {
        return ApiRestResult.<Boolean> builder()
            .result(permissionRoleService.deletePermissionRole(ids)).build();
    }

    /**
     * 获取角色
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Operation(summary = "获取角色信息")
    @GetMapping(value = "/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<PermissionRoleResult> getPermissionRole(@PathVariable(value = "id") Long id) {
        PermissionRoleResult details = permissionRoleService.getPermissionRole(id);
        //返回
        return ApiRestResult.<PermissionRoleResult> builder().result(details).build();
    }

    /**
     * 启用角色
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "启用角色")
    @PutMapping(value = "/enable/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> enablePermissionRole(@PathVariable(value = "id") String id) {
        Boolean result = permissionRoleService.updatePermissionRoleStatus(id, Boolean.TRUE);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 禁用角色
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "禁用角色")
    @PutMapping(value = "/disable/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> disablePermissionRole(@PathVariable(value = "id") String id) {
        Boolean result = permissionRoleService.updatePermissionRoleStatus(id, Boolean.FALSE);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 参数有效性验证
     *
     * @return {@link Boolean}
     */
    @Operation(summary = "参数有效性验证")
    @GetMapping(value = "/param_check")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> permissionRoleParamCheck(@Parameter(description = "验证类型") @NotNull(message = "验证类型不能为空") CheckValidityType type,
                                                           @Parameter(description = "值") @NotEmpty(message = "验证值不能为空") String value,
                                                           @Parameter(description = "应用ID") @NotNull(message = "应用ID不能为空") Long appId,
                                                           @Parameter(description = "ID") Long id) {
        Boolean result = permissionRoleService.permissionRoleParamCheck(type, value, appId, id);
        //返回
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 角色服务类
     */
    private final PermissionRoleService permissionRoleService;
}
