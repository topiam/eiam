/*
 * eiam-authentication-github - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.github;

import java.io.Serial;

import cn.topiam.employee.authentication.common.config.IdentityProviderConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;

/**
 * GITHUB 认证配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/4 23:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GithubIdpOauthConfig extends IdentityProviderConfig {
    @Serial
    private static final long serialVersionUID = -6850223527422243176L;

    /**
     * Client ID
     */
    @NotBlank(message = "Client ID 不能为空")
    private String            clientId;

    /**
     * Client Secret
     */
    @NotBlank(message = "Client Secret 不能为空")
    private String            clientSecret;
}
