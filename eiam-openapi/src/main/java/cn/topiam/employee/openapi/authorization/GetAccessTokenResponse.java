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

import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/23 21:19
 */
@Data
@Schema(description = "获取 access_token 响应")
public class GetAccessTokenResponse {

    /**
     * access_token
     */
    @JsonProperty(value = "access_token")
    @Schema(name = "access_token")
    private String  accessToken;

    /**
     * expires_in
     */
    @JsonProperty(value = "expires_in")
    @Schema(name = "expires_in")
    private Integer expiresIn;

    /**
     * code
     */
    @JsonProperty(value = "code")
    @Schema(name = "code")
    private String  code;

    /**
     * msg
     */
    @JsonProperty(value = "msg")
    @Schema(name = "msg")
    private String  msg;
}
