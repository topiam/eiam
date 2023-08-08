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

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.portal.pojo.request.*;
import cn.topiam.employee.portal.pojo.result.BoundIdpListResult;
import cn.topiam.employee.portal.service.AccountService;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.web.decrypt.DecryptRequestBody;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import static cn.topiam.employee.portal.constant.PortalConstants.*;

/**
 * 账户管理
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/12 21:39
 */
@RestController
@AllArgsConstructor
@RequestMapping(value = ACCOUNT_PATH)
public class AccountController {

    /**
     * 修改账户信息
     *
     * @return {@link  ApiRestResult}
     */
    @Audit(type = EventType.MODIFY_ACCOUNT_INFO_PORTAL)
    @Operation(summary = "修改账户信息")
    @PutMapping("/change_info")
    public ApiRestResult<Boolean> changeInfo(@DecryptRequestBody @RequestBody @Validated UpdateUserInfoRequest param) {
        Boolean result = accountService.changeInfo(param);
        return ApiRestResult.ok(result);
    }

    /**
     * 准备修改密码
     *
     * @return {@link  ApiRestResult}
     */
    @Audit(type = EventType.PREPARE_MODIFY_PASSWORD)
    @Operation(summary = "准备修改账户密码")
    @PostMapping("/prepare_change_password")
    public ApiRestResult<Boolean> prepareChangePassword(@DecryptRequestBody @RequestBody @Validated PrepareChangePasswordRequest param) {
        return ApiRestResult.ok(accountService.prepareChangePassword(param));
    }

    /**
     * 修改密码
     *
     * @return {@link  ApiRestResult}
     */
    @Audit(type = EventType.MODIFY_USER_PASSWORD_PORTAL)
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
    @Audit(type = EventType.PREPARE_MODIFY_PHONE)
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
    @Audit(type = EventType.MODIFY_USER_PHONE_PORTAL)
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
    @Audit(type = EventType.PREPARE_MODIFY_EMAIL)
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
    @Audit(type = EventType.MODIFY_USER_EMAIL_PORTAL)
    @Operation(summary = "修改邮箱")
    @PutMapping("/change_email")
    public ApiRestResult<Boolean> changeEmail(@RequestBody @Validated ChangeEmailRequest param) {
        return ApiRestResult.ok(accountService.changeEmail(param));
    }

    /**
     * 忘记密码发送验证码
     *
     * @return {@link  ApiRestResult}
     */
    @Operation(summary = "忘记密码发送验证码")
    @GetMapping(FORGET_PASSWORD_CODE)
    public ApiRestResult<Boolean> forgetPasswordCode(@Parameter(description = "验证码接收者（邮箱/手机号）") @RequestParam String recipient) {
        return ApiRestResult.ok(accountService.forgetPasswordCode(recipient));
    }

    /**
     * 忘记密码预认证
     *
     * @return {@link  ApiRestResult}
     */
    @Operation(summary = "忘记密码预认证")
    @PostMapping(PREPARE_FORGET_PASSWORD)
    public ApiRestResult<Boolean> prepareForgetPassword(@DecryptRequestBody @RequestBody @Validated PrepareForgetPasswordRequest param) {
        return ApiRestResult
            .ok(accountService.prepareForgetPassword(param.getRecipient(), param.getCode()));
    }

    /**
     * 忘记密码
     *
     * @return {@link  ApiRestResult}
     */
    @Operation(summary = "忘记密码")
    @PutMapping(FORGET_PASSWORD)
    public ApiRestResult<Boolean> forgetPassword(@DecryptRequestBody @RequestBody @Validated ForgetPasswordRequest forgetPasswordRequest) {
        return ApiRestResult.ok(accountService.forgetPassword(forgetPasswordRequest));
    }

    /**
     * 查询账号绑定
     *
     * @return {@link  ApiRestResult}
     */
    @Operation(summary = "查询已绑定IDP")
    @GetMapping("/bound_idp")
    public ApiRestResult<List<BoundIdpListResult>> getBoundIdpList() {
        return ApiRestResult.ok(accountService.getBoundIdpList());
    }

    /**
     * 解除账号绑定
     *
     * @return {@link  ApiRestResult}
     */
    @Audit(type = EventType.UNBIND_IDP_USER)
    @Operation(summary = "IDP账号解绑")
    @DeleteMapping("/unbind_idp/{id}")
    public ApiRestResult<Boolean> unbindIdp(@Parameter(description = "IDP ID") @PathVariable("id") String id) {
        return ApiRestResult.ok(accountService.unbindIdp(id));
    }

    /**
     * 账户服务
     */
    private final AccountService accountService;
}
