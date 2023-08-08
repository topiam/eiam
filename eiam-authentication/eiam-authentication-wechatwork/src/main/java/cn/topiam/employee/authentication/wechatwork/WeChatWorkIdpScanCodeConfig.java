/*
 * eiam-authentication-wechatwork - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.wechatwork;

import java.io.Serial;

import cn.topiam.employee.authentication.common.config.IdentityProviderConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;

/**
 * 钉钉扫码配置参数
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/8 21:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WeChatWorkIdpScanCodeConfig extends IdentityProviderConfig {
    @Serial
    private static final long serialVersionUID = -6850223527422243076L;

    /**
     * 企业ID
     */
    @NotBlank(message = "企业ID不能为空")
    private String            corpId;

    /**
     * 应用AgentID
     */
    @NotBlank(message = "应用AgentID不能为空")
    private String            agentId;

    /**
     * 应用Secret
     */
    @NotBlank(message = "应用Secret不能为空")
    private String            appSecret;
}
