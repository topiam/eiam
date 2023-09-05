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

import cn.topiam.employee.common.entity.app.query.AppPolicyQuery;
import cn.topiam.employee.console.pojo.result.app.AppPermissionPolicyGetResult;
import cn.topiam.employee.console.pojo.result.app.AppPermissionPolicyListResult;
import cn.topiam.employee.console.pojo.result.app.AppPermissionRoleListResult;
import cn.topiam.employee.console.pojo.save.app.AppPermissionPolicyCreateParam;
import cn.topiam.employee.console.pojo.save.app.AppPermissionRoleCreateParam;
import cn.topiam.employee.console.pojo.update.app.AppPermissionPolicyUpdateParam;
import cn.topiam.employee.console.pojo.update.app.PermissionRoleUpdateParam;
import cn.topiam.employee.console.service.app.AppPermissionPolicyService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constant.AppConstants.APP_PATH;

/**
 * 应用权限
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/12 22:30
 */
@Validated
@Tag(name = "应用权限-授权策略")
@RequestMapping(value = APP_PATH
                        + "/permission/policy", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class AppPermissionPolicyController {

    /**
     * 获取所有策略（分页）
     *
     * @param page {@link PageModel}
     * @return {@link AppPermissionRoleListResult}
     */
    @Operation(summary = "获取策略列表")
    @GetMapping(value = "/list")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Page<AppPermissionPolicyListResult>> getPermissionPolicyList(PageModel page,
                                                                                      @Validated AppPolicyQuery query) {
        Page<AppPermissionPolicyListResult> result = permissionPolicyService
            .getPermissionPolicyList(page, query);
        return ApiRestResult.<Page<AppPermissionPolicyListResult>> builder().result(result).build();
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
    @PostMapping(value = "/create")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> createPolicy(@Validated @RequestBody AppPermissionPolicyCreateParam param) {
        return ApiRestResult.<Boolean> builder()
            .result(permissionPolicyService.createPermissionPolicy(param)).build();
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
    @PutMapping(value = "/update/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> updatePolicy(@Validated AppPermissionPolicyUpdateParam param) {
        return ApiRestResult.<Boolean> builder()
            .result(permissionPolicyService.updatePermissionPolicy(param)).build();
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
    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> deletePermissionPolicy(@PathVariable(value = "id") String id) {
        return ApiRestResult.<Boolean> builder()
            .result(permissionPolicyService.deletePermissionPolicy(id)).build();
    }

    /**
     * 获取资源
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "获取资源信息")
    @GetMapping(value = "/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<AppPermissionPolicyGetResult> getPermissionPolicy(@PathVariable(value = "id") String id) {
        //返回
        return ApiRestResult.<AppPermissionPolicyGetResult> builder()
            .result(permissionPolicyService.getPermissionPolicy(id)).build();
    }

    private final AppPermissionPolicyService permissionPolicyService;
}
