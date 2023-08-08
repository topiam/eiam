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
import cn.topiam.employee.common.entity.app.query.AppAccessPolicyQuery;
import cn.topiam.employee.console.pojo.result.app.AppAccessPolicyResult;
import cn.topiam.employee.console.pojo.save.app.AppAccessPolicyCreateParam;
import cn.topiam.employee.console.pojo.save.app.AppAccountCreateParam;
import cn.topiam.employee.console.service.app.AppAccessPolicyService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import static cn.topiam.employee.common.constant.AppConstants.APP_PATH;

/**
 * 应用访问授权策略
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/4 21:58
 */
@Validated
@Tag(name = "应用访问授权策略")
@RestController
@AllArgsConstructor
@RequestMapping(value = APP_PATH + "/access_policy", produces = MediaType.APPLICATION_JSON_VALUE)
public class AppAccessPolicyController {

    /**
     * 获取应用访问授权策略列表
     *
     * @param page  {@link PageModel}
     * @param query {@link AppAccessPolicyQuery}
     * @return {@link AppAccessPolicyResult}
     */
    @Operation(summary = "获取应用访问授权策略列表")
    @GetMapping(value = "/list")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Page<AppAccessPolicyResult>> getAppAccessPolicyList(PageModel page,
                                                                             @Validated AppAccessPolicyQuery query) {
        return ApiRestResult.<Page<AppAccessPolicyResult>> builder()
            .result(appAccessPolicyService.getAppAccessPolicyList(page, query)).build();
    }

    /**
     * 创建应用访问授权策略
     *
     * @param param {@link AppAccountCreateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "创建应用访问授权")
    @Audit(type = EventType.APP_AUTHORIZATION)
    @PostMapping(value = "/create")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> createAppAccessPolicy(@RequestBody @Validated AppAccessPolicyCreateParam param) {
        return ApiRestResult.<Boolean> builder()
            .result(appAccessPolicyService.createAppAccessPolicy(param)).build();
    }

    /**
     * 删除应用访问授权策略
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "取消应用访问授权")
    @Audit(type = EventType.APP_CANCEL_ACCESS_POLICY)
    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> deleteAppAccessPolicy(@PathVariable(value = "id") String id) {
        return ApiRestResult.<Boolean> builder()
            .result(appAccessPolicyService.deleteAppAccessPolicy(id)).build();
    }

    /**
     * 是否具有应用访问权限
     *
     * @param appId {@link Integer}
     * @param userId {@link Integer}
     * @return {@link Boolean}
     */
    @Preview
    @Operation(summary = "是否具有应用访问权限")
    @PostMapping(value = "/has_allow_access")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> hasAllowAccess(@Parameter(description = "应用ID") @NotNull(message = "应用ID不能为空") Long appId,
                                                 @Parameter(description = "用户ID") @NotNull(message = "用户ID不能为空") Long userId) {
        return ApiRestResult.<Boolean> builder()
            .result(appAccessPolicyService.hasAllowAccess(appId, userId)).build();
    }

    /**
     * AppPolicyService
     */
    private final AppAccessPolicyService appAccessPolicyService;
}
