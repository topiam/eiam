/*
 * eiam-identity-source-dingtalk - Employee Identity and Access Management Program
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
package cn.topiam.employee.identitysource.dingtalk;

import java.io.Serial;

import javax.validation.constraints.NotEmpty;

import cn.topiam.employee.identitysource.core.IdentitySourceConfig;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 钉钉配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/28 23:17
 */
@Getter
@Setter
@RequiredArgsConstructor
public class DingTalkConfig extends IdentitySourceConfig {

    @Serial
    private static final long serialVersionUID = 8537679521873685897L;
    /**
     * 企业 ID
     */
    private final String      corpId;
    /**
     * 应用App key
     */
    @NotEmpty(message = "AppKey不能为空")
    private final String      appKey;
    /**
     * 应用AppSecret
     */
    @NotEmpty(message = "AppSecret不能为空")
    private final String      appSecret;
    /**
     * 加密 aes key。实时同步，此参数必填。
     */
    private final String      aesKey;
    /**
     * 签名token
     */
    private final String      token;
}
