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
package cn.topiam.employee.console.controller.setting;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.console.pojo.result.setting.PasswordPolicyConfigResult;
import cn.topiam.employee.console.pojo.result.setting.WeakPasswordLibListResult;
import cn.topiam.employee.console.pojo.save.setting.PasswordPolicySaveParam;
import cn.topiam.employee.console.service.setting.PasswordPolicyService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constant.SettingConstants.SECURITY_PATH;

/**
 * 密码策略配置 Controller
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/05/05 21:18
 */
@Validated
@Tag(name = "密码策略配置")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = SECURITY_PATH
                        + "/password_policy", produces = MediaType.APPLICATION_JSON_VALUE)
public class SecurityPasswordPolicyController {

    /**
     * 密码策略配置
     *
     * @return {@link List}
     */
    @GetMapping(value = "/config")
    @Operation(summary = "获取密码策略")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
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
    @PostMapping(value = "/save")
    @Audit(type = EventType.SAVE_PASSWORD_POLICY)
    @Operation(summary = "保存密码策略")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> save(@Validated @RequestBody PasswordPolicySaveParam param) {
        return ApiRestResult.<Boolean> builder()
            .result(passwordPolicyService.savePasswordPolicyConfig(param)).build();
    }

    /**
     * 系统弱密码库
     *
     * @return {@link List}
     */
    @GetMapping(value = "/weak_password_lib")
    @Operation(summary = "获取系统弱密码库")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<List<WeakPasswordLibListResult>> getWeakPasswordLibList() {
        List<WeakPasswordLibListResult> result = passwordPolicyService.getWeakPasswordLibList();

        return ApiRestResult.<List<WeakPasswordLibListResult>> builder().result(result).build();
    }

    /**
     * 密码策略实现类
     */
    private final PasswordPolicyService passwordPolicyService;
}
