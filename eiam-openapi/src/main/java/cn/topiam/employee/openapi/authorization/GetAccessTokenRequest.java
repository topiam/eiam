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

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 获取 access_token 授权
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/23 21:09
 */
@Data
@Schema(description = "获取 access_token 授权")
public class GetAccessTokenRequest implements Serializable {
    /**
     * 客户端ID
     */
    @JsonProperty(value = "client_id")
    @Schema(description = "客户端ID")
    private String clientId;

    /**
     * 客户端秘钥
     */
    @JsonProperty(value = "client_secret")
    @Schema(description = "客户端秘钥")
    private String clientSecret;
}
