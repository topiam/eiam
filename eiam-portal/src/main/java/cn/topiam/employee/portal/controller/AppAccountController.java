/*
 * eiam-portal - Employee Identity and Access Management
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
package cn.topiam.employee.portal.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.application.AppAccount;
import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.portal.pojo.request.AppAccountRequest;
import cn.topiam.employee.portal.service.AppAccountService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constant.AppConstants.APP_PATH;

/**
 * 应用账户资源
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/6/4 21:06
 */
@Validated
@Tag(name = "应用账户")
@RestController
@AllArgsConstructor
@RequestMapping(value = APP_PATH + "/account", produces = MediaType.APPLICATION_JSON_VALUE)
public class AppAccountController {

    /**
     * 获取当前登陆者应用账户列表
     *
     * @param appId  {@link String}
     * @return {@link }
     */
    @Operation(summary = "获取当前登陆者应用账户")
    @GetMapping("/{appId}")
    public ApiRestResult<List<AppAccount>> getAppAccountList(@PathVariable String appId) {
        List<AppAccount> appAccounts = appAccountService.getAppAccountList(appId);
        return ApiRestResult.ok(appAccounts);
    }

    /**
     * 创建应用账户
     *
     * @param param {@link AppAccountRequest}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "创建应用账户")
    @Audit(type = EventType.ADD_APP_ACCOUNT)
    @PostMapping(value = "/create")
    public ApiRestResult<Boolean> createAppAccount(@RequestBody @Validated AppAccountRequest param) {
        return ApiRestResult.<Boolean> builder().result(appAccountService.createAppAccount(param))
            .build();
    }

    /**
     * 设置应用账户是否默认
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "设置应用账户为默认")
    @Audit(type = EventType.UPDATE_DEFAULT_APP_ACCOUNT)
    @PutMapping(value = "/activate_default/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> updateAppAccountSetDefault(@PathVariable(value = "id") String id) {
        return ApiRestResult.<Boolean> builder()
            .result(appAccountService.updateAppAccountDefault(id, true)).build();
    }

    /**
     * 设置应用账户是否默认
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "取消应用账户为默认")
    @Audit(type = EventType.UPDATE_DEFAULT_APP_ACCOUNT)
    @PutMapping(value = "/deactivate_default/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> updateAppAccountUnsetDefault(@PathVariable(value = "id") String id) {
        return ApiRestResult.<Boolean> builder()
            .result(appAccountService.updateAppAccountDefault(id, false)).build();
    }

    /**
     * 删除应用账户
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "删除应用账户")
    @Audit(type = EventType.DELETE_APP_ACCOUNT)
    @DeleteMapping(value = "/delete/{id}")
    public ApiRestResult<Boolean> deleteAppAccount(@PathVariable(value = "id") String id) {
        return ApiRestResult.<Boolean> builder().result(appAccountService.deleteAppAccount(id))
            .build();
    }

    /**
     * AppAccountService
     */
    private final AppAccountService appAccountService;

}
