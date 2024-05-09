/*
 * eiam-protocol-core - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.code;

import java.io.Serializable;
import java.time.Instant;

/**
 * 注册客户端
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/12/18 08:35
 */
public interface RegisteredClient extends Serializable {
    /**
     * 应用ID
     */
    String getId();

    /**
     * 应用code
     */
    String getCode();

    /**
     * 客户端ID
     */
    String getClientId();

    /**
     * 客户端名称
     */
    String getClientName();

    /**
     * 客户端ID创建时间
     */
    Instant getClientIdIssuedAt();

    /**
     * 客户端秘钥
     */
    String getClientSecret();

    /**
     * 客户端秘钥创建时间
     */
    Instant getClientSecretExpiresAt();
}
