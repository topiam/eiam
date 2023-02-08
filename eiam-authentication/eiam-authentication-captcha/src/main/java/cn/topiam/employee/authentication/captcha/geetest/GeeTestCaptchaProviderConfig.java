/*
 * eiam-authentication-captcha - Employee Identity and Access Management Program
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
package cn.topiam.employee.authentication.captcha.geetest;

import java.io.Serial;

import javax.validation.constraints.NotEmpty;

import cn.topiam.employee.core.security.captcha.CaptchaProviderConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 极速验证码
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/14 22:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GeeTestCaptchaProviderConfig extends CaptchaProviderConfig {

    @Serial
    private static final long serialVersionUID = 3279601494863893521L;
    /**
     * 验证码ID
     */
    @NotEmpty(message = "验证码ID不能为空")
    private String            captchaId;

    /**
     * 验证码KEY
     */
    @NotEmpty(message = "验证码KEY不能为空")
    private String            captchaKey;
}
