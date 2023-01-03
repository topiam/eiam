/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.security.captcha;

import java.io.Serial;
import java.io.Serializable;

import javax.validation.constraints.NotNull;

import cn.topiam.employee.common.enums.CaptchaProviderType;

/**
 * 验证码配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/14 22:43
 */
public class CaptchaProviderConfig implements Serializable {

    @Serial
    private static final long   serialVersionUID = -4070578811152498950L;

    /**
     * 提供商
     */
    @NotNull(message = "验证码提供商不能为空")
    private CaptchaProviderType provider;

    public CaptchaProviderType getProvider() {
        return provider;
    }

    public void setProvider(CaptchaProviderType provider) {
        this.provider = provider;
    }
}
