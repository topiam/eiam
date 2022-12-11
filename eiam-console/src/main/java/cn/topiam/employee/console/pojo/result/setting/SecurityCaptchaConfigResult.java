/*
 * eiam-console - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.console.pojo.result.setting;

import java.io.Serializable;

import cn.topiam.employee.common.enums.CaptchaProviderType;
import cn.topiam.employee.core.security.captcha.CaptchaProviderConfig;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 行为验证码配置结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/10/4 21:58
 */
@Data
@Schema(description = "行为验证码配置结果")
public class SecurityCaptchaConfigResult implements Serializable {
    /**
     * 验证码提供商
     */
    @Parameter(description = "验证码提供商")
    private CaptchaProviderType   provider;

    /**
     * 验证码配置
     */
    @Parameter(description = "验证码配置")
    private CaptchaProviderConfig config;

    /**
     * 已启用
     */
    @Parameter(description = "已启用")
    private Boolean               enabled;
}
