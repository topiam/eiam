/*
 * eiam-portal - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.core.security.decrypt.DecryptRequestBody;
import cn.topiam.employee.portal.pojo.request.*;
import cn.topiam.employee.portal.pojo.result.PrepareBindMfaResult;
import cn.topiam.employee.portal.service.AccountService;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import static cn.topiam.employee.support.constant.EiamConstants.API_PATH;

/**
 * 账户管理
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/12 21:39
 */
@RestController
@AllArgsConstructor
@RequestMapping(value = API_PATH + "/account")
public class AccountController {

    /**
     * 修改账户信息
     *
     * @return {@link  ApiRestResult}
     */
    @Operation(summary = "修改账户信息")
    @PutMapping("/change_info")
    public ApiRestResult<Boolean> changeInfo(@DecryptRequestBody @RequestBody @Validated UpdateUserInfoRequest param) {
        Boolean result = accountService.changeInfo(param);
        return ApiRestResult.ok(result);
    }

    /**
     * 修改密码
     *
     * @return {@link  ApiRestResult}
     */
    @Operation(summary = "修改账户密码")
    @PutMapping("/change_password")
    public ApiRestResult<Boolean> changePassword(@DecryptRequestBody @RequestBody @Validated ChangePasswordRequest param) {
        return ApiRestResult.ok(accountService.changePassword(param));
    }

    /**
     * 准备修改手机
     *
     * @return {@link  ApiRestResult}
     */
    @Operation(summary = "准备修改手机")
    @PostMapping("/prepare_change_phone")
    public ApiRestResult<Boolean> prepareChangePhone(@DecryptRequestBody @RequestBody @Validated PrepareChangePhoneRequest param) {
        return ApiRestResult.ok(accountService.prepareChangePhone(param));
    }

    /**
     * 修改手机
     *
     * @return {@link  ApiRestResult}
     */
    @Operation(summary = "修改手机")
    @PutMapping("/change_phone")
    public ApiRestResult<Boolean> changePhone(@RequestBody @Validated ChangePhoneRequest param) {
        return ApiRestResult.ok(accountService.changePhone(param));
    }

    /**
     * 准备修改邮箱
     *
     * @return {@link  ApiRestResult}
     */
    @Operation(summary = "准备修改邮箱")
    @PostMapping("/prepare_change_email")
    public ApiRestResult<Boolean> prepareChangeEmail(@DecryptRequestBody @RequestBody @Validated PrepareChangeEmailRequest param) {
        return ApiRestResult.ok(accountService.prepareChangeEmail(param));
    }

    /**
     * 修改邮箱
     *
     * @return {@link  ApiRestResult}
     */
    @Operation(summary = "修改邮箱")
    @PutMapping("/change_email")
    public ApiRestResult<Boolean> changeEmail(@RequestBody @Validated ChangeEmailRequest param) {
        return ApiRestResult.ok(accountService.changeEmail(param));
    }

    /**
     * 准备绑定MFA
     *
     * @return {@link  ApiRestResult}
     */
    @Operation(summary = "准备绑定MFA")
    @PostMapping("/prepare_bind_totp")
    public ApiRestResult<PrepareBindMfaResult> prepareBindTotp(@DecryptRequestBody @RequestBody @Validated PrepareBindTotpRequest param) {
        return ApiRestResult.ok(accountService.prepareBindTotp(param));
    }

    /**
     * 绑定 TOTP
     *
     * @return {@link  ApiRestResult}
     */
    @Operation(summary = "绑定 TOTP")
    @PostMapping("/bind_totp")
    public ApiRestResult<Boolean> bindTotp(@DecryptRequestBody @RequestBody @Validated BindTotpRequest param) {
        return ApiRestResult.ok(accountService.bindTotp(param));
    }

    /**
     * 解绑 TOTP
     *
     * @return {@link  ApiRestResult}
     */
    @Operation(summary = "解绑 TOTP")
    @PutMapping("/unbind_totp")
    public ApiRestResult<Boolean> unbindTotp() {
        return ApiRestResult.ok(accountService.unbindTotp());
    }

    /**
     * 账户服务
     */
    private final AccountService accountService;
}
