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
package cn.topiam.employee.console.controller.app;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.common.enums.CheckValidityType;
import cn.topiam.employee.console.pojo.query.app.AppPermissionRoleListQuery;
import cn.topiam.employee.console.pojo.result.app.AppPermissionRoleListResult;
import cn.topiam.employee.console.pojo.result.app.AppPermissionRoleResult;
import cn.topiam.employee.console.pojo.save.app.AppPermissionRoleCreateParam;
import cn.topiam.employee.console.pojo.update.app.PermissionRoleUpdateParam;
import cn.topiam.employee.console.service.app.AppPermissionRoleService;
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
import static cn.topiam.employee.common.constant.AppConstants.APP_PATH;

/**
 * 应用角色
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/26 20:28
 */
@RequiredArgsConstructor
@Validated
@Tag(name = "应用权限-角色")
@RequestMapping(value = APP_PATH + "/permission/role", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class AppPermissionRoleController {

    /**
     * 获取所有角色（分页）
     *
     * @param page {@link PageModel}
     * @return {@link AppPermissionRoleListResult}
     */
    @Operation(summary = "获取角色列表")
    @GetMapping(value = "/list")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Page<AppPermissionRoleListResult>> getPermissionRoleList(PageModel page,
                                                                                  @Validated AppPermissionRoleListQuery query) {
        Page<AppPermissionRoleListResult> result = appPermissionRoleService
            .getPermissionRoleList(page, query);
        return ApiRestResult.<Page<AppPermissionRoleListResult>> builder().result(result).build();
    }

    /**
     * 创建角色
     *
     * @param param {@link AppPermissionRoleCreateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "创建角色")
    @Audit(type = EventType.SAVE_APP_PERMISSION_ROLE)
    @PostMapping(value = "/create")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> createPermissionRole(@Validated @RequestBody AppPermissionRoleCreateParam param) {
        return ApiRestResult.<Boolean> builder()
            .result(appPermissionRoleService.createPermissionRole(param)).build();
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
            .result(appPermissionRoleService.updatePermissionRole(param)).build();
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
            .result(appPermissionRoleService.deletePermissionRole(ids)).build();
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
    public ApiRestResult<AppPermissionRoleResult> getPermissionRole(@PathVariable(value = "id") Long id) {
        AppPermissionRoleResult details = appPermissionRoleService.getPermissionRole(id);
        //返回
        return ApiRestResult.<AppPermissionRoleResult> builder().result(details).build();
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
        Boolean result = appPermissionRoleService.updatePermissionRoleStatus(id, Boolean.TRUE);
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
        Boolean result = appPermissionRoleService.updatePermissionRoleStatus(id, Boolean.FALSE);
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
        Boolean result = appPermissionRoleService.permissionRoleParamCheck(type, value, appId, id);
        //返回
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 角色服务类
     */
    private final AppPermissionRoleService appPermissionRoleService;
}
