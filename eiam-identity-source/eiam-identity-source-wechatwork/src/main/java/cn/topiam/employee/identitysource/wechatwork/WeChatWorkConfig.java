/*
 * eiam-identity-source-wechatwork - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.wechatwork;

import java.io.Serial;

import cn.topiam.employee.identitysource.core.IdentitySourceConfig;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;

/**
 * 企业微信配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/28 23:17
 */
@Getter
@Setter
@RequiredArgsConstructor
public class WeChatWorkConfig extends IdentitySourceConfig {

    @Serial
    private static final long serialVersionUID = 3060072695822804238L;
    /**
     * 企业 ID
     */
    @NotEmpty(message = "企业ID不能为空")
    private final String      corpId;

    /**
     * 企业 secret
     */
    @NotEmpty(message = "secret不能为空")
    private final String      secret;

    /**
     * 加密 aes key。实时同步，此参数必填。
     */
    private final String      encodingAESKey;
    /**
     * 签名token
     */
    private final String      token;
}
