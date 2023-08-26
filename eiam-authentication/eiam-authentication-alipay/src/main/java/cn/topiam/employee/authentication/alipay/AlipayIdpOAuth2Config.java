/*
 * eiam-authentication-alipay - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.alipay;

import cn.topiam.employee.authentication.common.config.IdentityProviderConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;

/**
 * 支付宝 登录配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/8/19 16:09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AlipayIdpOAuth2Config extends IdentityProviderConfig {

    /**
     * 商户ID
     */
    @NotBlank(message = "商户ID不能为空")
    private String appId;

    /**
     * 应用私钥
     */
    @NotBlank(message = "应用私钥")
    private String appPrivateKey;

    /**
     * 支付宝公钥
     */
    @NotBlank(message = "支付宝公钥")
    private String alipayPublicKey;

}
