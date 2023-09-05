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
import cn.topiam.employee.console.pojo.query.app.AppResourceListQuery;
import cn.topiam.employee.console.pojo.result.app.AppPermissionResourceGetResult;
import cn.topiam.employee.console.pojo.result.app.AppPermissionResourceListResult;
import cn.topiam.employee.console.pojo.result.app.AppPermissionRoleListResult;
import cn.topiam.employee.console.pojo.save.app.AppPermissionResourceCreateParam;
import cn.topiam.employee.console.pojo.save.app.AppPermissionRoleCreateParam;
import cn.topiam.employee.console.pojo.update.app.AppPermissionResourceUpdateParam;
import cn.topiam.employee.console.pojo.update.app.PermissionRoleUpdateParam;
import cn.topiam.employee.console.service.app.AppPermissionResourceService;
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
 * 应用权限
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/12 22:30
 */
@Validated
@Tag(name = "应用权限-资源")
@RequestMapping(value = APP_PATH
                        + "/permission/resource", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class AppPermissionResourceController {

    /**
     * 获取所有资源（分页）
     *
     * @param page {@link PageModel}
     * @return {@link AppPermissionRoleListResult}
     */
    @Operation(summary = "获取资源列表")
    @GetMapping(value = "/list")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Page<AppPermissionResourceListResult>> getPermissionResourceList(PageModel page,
                                                                                          @Validated AppResourceListQuery query) {
        Page<AppPermissionResourceListResult> result = appPermissionResourceService
            .getPermissionResourceList(page, query);
        return ApiRestResult.<Page<AppPermissionResourceListResult>> builder().result(result)
            .build();
    }

    /**
     * 创建资源
     *
     * @param param {@link AppPermissionRoleCreateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "创建资源")
    @Audit(type = EventType.SAVE_APP_PERMISSION_RESOURCE)
    @PostMapping(value = "/create")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> createResource(@Validated @RequestBody AppPermissionResourceCreateParam param) {
        return ApiRestResult.<Boolean> builder()
            .result(appPermissionResourceService.createPermissionResource(param)).build();
    }

    /**
     * 修改资源
     *
     * @param param {@link PermissionRoleUpdateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "修改资源")
    @Audit(type = EventType.UPDATE_APP_PERMISSION_RESOURCE)
    @PutMapping(value = "/update")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> updateResource(@RequestBody @Validated AppPermissionResourceUpdateParam param) {
        return ApiRestResult.<Boolean> builder()
            .result(appPermissionResourceService.updatePermissionResource(param)).build();
    }

    /**
     * 删除资源
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "删除资源")
    @Audit(type = EventType.DELETE_APP_PERMISSION_RESOURCE)
    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> deletePermissionResource(@PathVariable(value = "id") String id) {
        return ApiRestResult.<Boolean> builder()
            .result(appPermissionResourceService.deletePermissionResource(id)).build();
    }

    /**
     * 获取资源
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Operation(summary = "获取资源信息")
    @GetMapping(value = "/get/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<AppPermissionResourceGetResult> getPermissionResource(@PathVariable(value = "id") String id) {
        //返回
        return ApiRestResult.<AppPermissionResourceGetResult> builder()
            .result(appPermissionResourceService.getPermissionResource(id)).build();
    }

    /**
     * 启用资源
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "启用资源")
    @Audit(type = EventType.ENABLE_APP_PERMISSION_RESOURCE)
    @PutMapping(value = "/enable/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> enableOrganization(@PathVariable(value = "id") Long id) {
        return ApiRestResult.<Boolean> builder()
            .result(appPermissionResourceService.updateStatus(id, Boolean.TRUE)).build();
    }

    /**
     * 禁用资源
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "禁用资源")
    @Audit(type = EventType.DISABLE_APP_PERMISSION_RESOURCE)
    @PutMapping(value = "/disable/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> disableOrganization(@PathVariable(value = "id") Long id) {
        return ApiRestResult.<Boolean> builder()
            .result(appPermissionResourceService.updateStatus(id, Boolean.FALSE)).build();
    }

    /**
     * 参数有效性验证
     *
     * @return {@link Boolean}
     */
    @Operation(summary = "参数有效性验证")
    @GetMapping(value = "/param_check")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> resourceParamCheck(@Parameter(description = "验证类型") @NotNull(message = "验证类型不能为空") CheckValidityType type,
                                                     @Parameter(description = "值") @NotEmpty(message = "验证值不能为空") String value,
                                                     @Parameter(description = "应用ID") @NotNull(message = "应用ID不能为空") Long appId,
                                                     @Parameter(description = "ID") Long id) {
        Boolean result = appPermissionResourceService.permissionResourceParamCheck(type, value,
            appId, id);
        //返回
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 资源服务类
     */
    private final AppPermissionResourceService appPermissionResourceService;
}
