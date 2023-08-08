/*
 * eiam-openapi - Employee Identity and Access Management
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
package cn.topiam.employee.openapi.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Authorization
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/25 21:53
 */
@Data
public class AccessToken {
    /**
     * 客户端ID
     */
    @JsonProperty(value = "client_id")
    private String  clientId;

    /**
     * token 值
     */
    @JsonProperty(value = "value")
    private String  value;

    /**
     * 过期时间
     */
    @JsonProperty(value = "expires_in")
    private Integer expiresIn;

    public AccessToken() {
    }

    public AccessToken(String clientId, String value, Integer expiresIn) {
        this.clientId = clientId;
        this.value = value;
        this.expiresIn = expiresIn;
    }
}
