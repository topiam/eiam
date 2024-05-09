/*
 * eiam-application-oidc - Employee Identity and Access Management
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
package cn.topiam.employee.application.oidc.pojo;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 协议端点域
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/6/4 23:37
 */
@Data
@Schema(description = "协议端点")
public class AppOidcProtocolEndpoint implements Serializable {

    @Serial
    private static final long serialVersionUID = -2261602995152894964L;
    /**
     * oidcIssuer
     */
    @Schema(description = "Issuer")
    private String            issuer;

    /**
     * discoveryEndpoint
     */
    @Schema(description = "Discovery Endpoint")
    private String            discoveryEndpoint;

    /**
     * UserinfoEndpoint
     */
    @Schema(description = "UserInfo Endpoint")
    private String            userinfoEndpoint;

    /**
     * jwksEndpoint
     */
    @Schema(description = "Jwks Endpoint")
    private String            jwksEndpoint;

    /**
     * revokeEndpoint
     */
    @Schema(description = "Revoke Endpoint")
    private String            revokeEndpoint;

    /**
     * tokenEndpoint
     */
    @Schema(description = "Token Endpoint")
    private String            tokenEndpoint;

    /**
     * authorizationEndpoint
     */
    @Schema(description = "Authorization Endpoint")
    private String            authorizationEndpoint;

    /**
     * endSessionEndpoint
     */
    @Schema(description = "End Session Endpoint")
    private String            endSessionEndpoint;
}
