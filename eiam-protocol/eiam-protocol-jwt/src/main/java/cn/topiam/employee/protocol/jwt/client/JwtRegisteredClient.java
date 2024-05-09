/*
 * eiam-protocol-jwt - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.jwt.client;

import java.time.Instant;

import cn.topiam.employee.protocol.code.RegisteredClient;

import lombok.Builder;
import lombok.Data;

/**
 * JWT 已注册的客户端
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/12/17 20:10
 */
@Data
@Builder
public class JwtRegisteredClient implements RegisteredClient {
    /**
     * 应用ID
     */
    private String  id;

    /**
     * 应用code
     */
    private String  code;

    /**
     * 客户端ID
     */
    private String  clientId;

    /**
     * 客户端名称
     */
    private String  clientName;

    /**
     * 客户端ID创建时间
     */
    private Instant clientIdIssuedAt;

    /**
     * 客户端秘钥
     */
    private String  clientSecret;

    /**
     * 客户端秘钥创建时间
     */
    private Instant clientSecretExpiresAt;

}
