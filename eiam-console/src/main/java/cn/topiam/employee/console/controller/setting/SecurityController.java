/*
 * eiam-console - Employee Identity and Access Management Program
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
package cn.topiam.employee.console.controller.setting;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.enums.EventType;
import cn.topiam.employee.console.pojo.result.setting.*;
import cn.topiam.employee.console.pojo.save.setting.PasswordPolicySaveParam;
import cn.topiam.employee.console.pojo.save.setting.SecurityBasicSaveParam;
import cn.topiam.employee.console.pojo.save.setting.SecurityCaptchaSaveParam;
import cn.topiam.employee.console.pojo.save.setting.SecurityMfaSaveParam;
import cn.topiam.employee.console.service.setting.PasswordPolicyService;
import cn.topiam.employee.console.service.setting.SecuritySettingService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constants.SettingConstants.SETTING_PATH;

/**
 * 安全设置
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/10/4 20:37
 */
@Validated
@Tag(name = "安全设置")
@RestController
@RequestMapping(value = SETTING_PATH + "/security", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class SecurityController {

    /**
     * 获取高级配置
     *
     * @return {@link SecurityBasicConfigResult}
     */
    @Operation(summary = "获取基础配置")
    @GetMapping(value = "/basic/config")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<SecurityBasicConfigResult> getBasicConfig() {
        SecurityBasicConfigResult result = securitySettingService.getBasicConfig();
        return ApiRestResult.<SecurityBasicConfigResult> builder().result(result).build();
    }

    /**
     * 保存高级配置
     *
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "保存基础配置")
    @Audit(type = EventType.SAVE_LOGIN_SECURITY_BASIC_SETTINGS)
    @PostMapping(value = "/basic/save")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<Boolean> save(@Validated @RequestBody SecurityBasicSaveParam param) {
        return ApiRestResult.<Boolean> builder()
            .result(securitySettingService.saveBasicConfig(param)).build();
    }

    /**
     * 密码策略配置
     *
     * @return {@link List}
     */
    @GetMapping(value = "/password_policy/config")
    @Operation(summary = "获取密码策略")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<PasswordPolicyConfigResult> config() {
        PasswordPolicyConfigResult result = passwordPolicyService.getPasswordPolicyConfig();
        return ApiRestResult.<PasswordPolicyConfigResult> builder().result(result).build();
    }

    /**
     * 保存密码策略
     *
     * @return {@link List}
     */
    @Lock
    @Preview
    @PostMapping(value = "/password_policy/save")
    @Audit(type = EventType.SAVE_PASSWORD_POLICY_SETTINGS)
    @Operation(summary = "保存密码策略")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<Boolean> save(@Validated @RequestBody PasswordPolicySaveParam param) {
        return ApiRestResult.<Boolean> builder()
            .result(passwordPolicyService.savePasswordPolicyConfig(param)).build();
    }

    /**
     * 系统弱密码库
     *
     * @return {@link List}
     */
    @GetMapping(value = "/weak_password_lib/list")
    @Operation(summary = "获取系统弱密码库")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<List<WeakPasswordLibListResult>> getWeakPasswordLibList() {
        List<WeakPasswordLibListResult> result = passwordPolicyService.getWeakPasswordLibList();
        return ApiRestResult.<List<WeakPasswordLibListResult>> builder().result(result).build();
    }

    /**
     * 获取MFA配置
     *
     * @return {@link SecurityMfaConfigResult}
     */
    @Operation(summary = "获取MFA配置")
    @GetMapping(value = "/mfa/config")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<SecurityMfaConfigResult> getMfaConfig() {
        SecurityMfaConfigResult result = securitySettingService.getMfaConfig();
        return ApiRestResult.<SecurityMfaConfigResult> builder().result(result).build();
    }

    /**
     * 保存MFA配置
     *
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "保存MFA配置")
    @Audit(type = EventType.SAVE_MFA_SETTINGS)
    @PostMapping(value = "/mfa/save")
    @Validated
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<Boolean> saveMfaConfig(@RequestBody SecurityMfaSaveParam param) {
        return ApiRestResult.<Boolean> builder().result(securitySettingService.saveMfaConfig(param))
            .build();
    }

    /**
     * 获取行为验证码配置
     *
     * @return {@link SecurityCaptchaConfigResult}
     */
    @Operation(summary = "获取行为验证码配置")
    @GetMapping(value = "/captcha/config")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<SecurityCaptchaConfigResult> getCaptchaProviderConfig() {
        SecurityCaptchaConfigResult result = securitySettingService.getCaptchaProviderConfig();
        return ApiRestResult.<SecurityCaptchaConfigResult> builder().result(result).build();
    }

    /**
     * 保存行为验证码配置
     *
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "保存行为验证码配置")
    @Audit(type = EventType.SAVE_CAPTCHA_PROVIDER)
    @PostMapping(value = "/captcha/save")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<Boolean> saveCaptchaProviderConfig(@Validated @RequestBody SecurityCaptchaSaveParam param) {
        return ApiRestResult.<Boolean> builder()
            .result(securitySettingService.saveCaptchaProviderConfig(param)).build();
    }

    /**
     * 禁用行为验证码
     *
     * @return {@link ApiRestResult}
     */
    @Lock
    @Preview
    @Validated
    @Operation(summary = "禁用行为验证码")
    @Audit(type = EventType.OFF_MAIL_SERVICE)
    @PutMapping(value = "/captcha/disable")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<Boolean> disableCaptchaProvider() {
        Boolean result = securitySettingService.disableCaptchaProvider();
        return ApiRestResult.ok(result);
    }

    /**
     * 密码策略实现类
     */
    private final PasswordPolicyService  passwordPolicyService;
    /**
     * SecuritySettingService
     */
    private final SecuritySettingService securitySettingService;
}
