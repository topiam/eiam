/*
 * eiam-openapi - Employee Identity and Access Management
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
package cn.topiam.employee.openapi.endpoint.permission;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.topiam.employee.common.entity.app.po.AppPermissionPolicyPO;
import cn.topiam.employee.openapi.pojo.request.app.query.OpenApiPolicyQuery;
import cn.topiam.employee.openapi.service.AppPermissionPolicyService;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import static cn.topiam.employee.openapi.constants.OpenApiV1Constants.OPEN_API_PERMISSION_PATH;

/**
 * 应用权限-策略开放API
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/9/5 21:04
 */
@RestController
@RequestMapping(value = OPEN_API_PERMISSION_PATH + "/policy")
@RequiredArgsConstructor
public class AppPermissionPolicyEndpoint {
    /**
     * 获取所有策略（分页）
     *
     * @param page {@link PageModel}
     * @return {@link AppPermissionPolicyPO}
     */
    @Operation(summary = "获取策略列表")
    @GetMapping(value = "/list")
    public ApiRestResult<Page<AppPermissionPolicyPO>> getPermissionPolicyList(PageModel page,
                                                                              @Validated OpenApiPolicyQuery query) {
        Page<AppPermissionPolicyPO> result = permissionPolicyService.getPermissionPolicyList(page,
            query);
        return ApiRestResult.<Page<AppPermissionPolicyPO>> builder().result(result).build();
    }

    private final AppPermissionPolicyService permissionPolicyService;
}
