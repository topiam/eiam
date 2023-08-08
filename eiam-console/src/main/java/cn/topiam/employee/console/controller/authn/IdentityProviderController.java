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
package cn.topiam.employee.console.controller.authn;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.common.constant.AuthnConstants;
import cn.topiam.employee.console.pojo.query.authn.IdentityProviderListQuery;
import cn.topiam.employee.console.pojo.result.authn.IdentityProviderCreateResult;
import cn.topiam.employee.console.pojo.result.authn.IdentityProviderListResult;
import cn.topiam.employee.console.pojo.result.authn.IdentityProviderResult;
import cn.topiam.employee.console.pojo.save.authn.IdentityProviderCreateParam;
import cn.topiam.employee.console.pojo.update.authn.IdpUpdateParam;
import cn.topiam.employee.console.service.authn.IdentityProviderService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 身份提供商
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/7/11 21:18
 */
@Tag(name = "身份提供商")
@Validated
@RestController
@RequestMapping(value = AuthnConstants.AUTHN_PATH
                        + "/idp", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class IdentityProviderController {

    /**
     * 获取列表
     *
     * @return {@link IdentityProviderListResult}
     */
    @Operation(summary = "提供商列表")
    @GetMapping(value = "/list")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Page<IdentityProviderListResult>> getIdentityProviderList(PageModel pageModel,
                                                                                   IdentityProviderListQuery query) {
        Page<IdentityProviderListResult> results = identityProviderService
            .getIdentityProviderList(pageModel, query);
        return ApiRestResult.<Page<IdentityProviderListResult>> builder().result(results).build();
    }

    /**
     * 创建提供商
     *
     * @param param {@link IdentityProviderCreateParam}
     * @return {@link IdentityProviderCreateResult}
     */
    @Lock
    @Preview
    @Operation(summary = "创建提供商")
    @Audit(type = EventType.ADD_IDP)
    @PostMapping(value = "/create")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<IdentityProviderCreateResult> createIdentityProvider(@RequestBody @Validated IdentityProviderCreateParam param) {
        IdentityProviderCreateResult result = identityProviderService.createIdp(param);
        return ApiRestResult.<IdentityProviderCreateResult> builder().result(result).build();
    }

    /**
     * 修改提供商
     *
     * @param param {@link IdentityProviderCreateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "修改提供商")
    @Audit(type = EventType.UPDATE_IDP)
    @PutMapping(value = "/update")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> updateIdentityProvider(@RequestBody @Validated IdpUpdateParam param) {
        boolean success = identityProviderService.updateIdentityProvider(param);
        return ApiRestResult.<Boolean> builder().result(success).build();
    }

    /**
     * 详情
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Operation(summary = "获取提供商信息")
    @GetMapping(value = "/get/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<IdentityProviderResult> getIdentityProvider(@PathVariable(value = "id") String id) {
        IdentityProviderResult result = identityProviderService.getIdentityProvider(id);
        return ApiRestResult.<IdentityProviderResult> builder().result(result).build();
    }

    /**
     * 启用提供商
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "启用提供商")
    @Audit(type = EventType.ENABLE_IDP)
    @PutMapping(value = "/enable/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> enableIdentityProvider(@PathVariable(value = "id") String id) {
        boolean result = identityProviderService.updateIdentityProviderStatus(id, true);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 禁用提供商
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "禁用提供商")
    @Audit(type = EventType.DISABLE_IDP)
    @PutMapping(value = "/disable/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> disableIdentityProvider(@PathVariable(value = "id") String id) {
        boolean result = identityProviderService.updateIdentityProviderStatus(id, false);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 删除提供商
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "删除提供商")
    @Audit(type = EventType.DELETE_IDP)
    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> deleteIdentityProvider(@PathVariable(value = "id") String id) {
        boolean result = identityProviderService.deleteIdentityProvider(id);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 身份提供商服务
     */
    private final IdentityProviderService identityProviderService;
}
