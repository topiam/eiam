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

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.console.pojo.result.setting.SecurityDefensePolicyConfigResult;
import cn.topiam.employee.console.pojo.save.setting.SecurityDefensePolicyParam;
import cn.topiam.employee.console.service.setting.SecurityDefensePolicyService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constant.SettingConstants.SETTING_PATH;

/**
 * 安全防御策略
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023-03-09
 */
@Validated
@Tag(name = "安全防御策略")
@RestController
@AllArgsConstructor
@RequestMapping(value = SETTING_PATH
                        + "/security/defense_policy", produces = MediaType.APPLICATION_JSON_VALUE)
public class SecurityDefensePolicyController {

    /**
     * 保存安全策略
     *
     * @param param {@link SecurityDefensePolicyParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "保存安全防御策略")
    @Audit(type = EventType.SAVE_SECURITY_POLICY_SETTINGS)
    @PostMapping(value = "/save")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> saveSecurityDefenseStrategyConfig(@RequestBody @Validated SecurityDefensePolicyParam param) {
        Boolean result = securityDefenseStrategyService.saveSecurityDefensePolicyConfig(param);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 获取安全策略
     *
     * @return {@link SecurityDefensePolicyConfigResult}
     */
    @Operation(summary = "获取安全策略")
    @GetMapping(value = "/config")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<SecurityDefensePolicyConfigResult> getSecurityPolicyConfig() {
        SecurityDefensePolicyConfigResult result = securityDefenseStrategyService
            .getSecurityPolicyConfig();
        //返回
        return ApiRestResult.<SecurityDefensePolicyConfigResult> builder().result(result).build();
    }

    /**
     * SecurityDefenseStrategy
     */
    private final SecurityDefensePolicyService securityDefenseStrategyService;
}
