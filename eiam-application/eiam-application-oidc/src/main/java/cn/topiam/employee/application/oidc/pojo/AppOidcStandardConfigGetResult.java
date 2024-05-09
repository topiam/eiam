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
import java.util.Set;

import cn.topiam.employee.common.enums.app.AuthorizationType;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OIDC 配置返回
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/5/31 22:46
 */
@Data
@Schema(description = "OIDC 配置返回响应")
public class AppOidcStandardConfigGetResult implements Serializable {

    @Serial
    private static final long       serialVersionUID = 4177874005424703372L;

    /**
     * SSO 登录链接
     */
    @Schema(description = "SSO 登录链接")
    private String                  initLoginUrl;

    /**
     * 授权范围
     */
    @Schema(description = "SSO 授权范围")
    private AuthorizationType       authorizationType;

    /**
     * authorizationGrantTypes
     */
    @Schema(description = "认证授权类型")
    private Set<String>             authGrantTypes;

    /**
     * 客户端认证方式
     */
    @Schema(description = "客户端认证方式")
    private Set<String>             clientAuthMethods;

    /**
     * 重定向URI
     */
    @Schema(description = "重定向URI")
    private Set<String>             redirectUris;

    /**
     * 登出重定向URI
     */
    @Schema(description = "登出重定向URI")
    private Set<String>             postLogoutRedirectUris;
    /**
     * scopes
     */
    @Schema(description = "授权范围")
    private Set<String>             grantScopes;

    /**
     * 启用PKCE
     */
    @Schema(description = "启用PKCE")
    private Boolean                 requireProofKey;

    /**
     * 令牌 Endpoint 身份验证签名算法
     */
    @Schema(description = "令牌 Endpoint 身份验证签名算法")
    private String                  tokenEndpointAuthSigningAlgorithm;

    /**
     * 是否需要授权同意
     */
    @Schema(description = "是否需要授权同意")
    private Boolean                 requireAuthConsent;

    /**
     * 访问令牌有效时间
     */
    @Schema(description = "访问令牌有效时间")
    private String                  accessTokenTimeToLive;

    /**
     * 刷新令牌有效时间
     */
    @Schema(description = "刷新令牌有效时间")
    private String                  refreshTokenTimeToLive;

    /**
     * 授权码 生存时间（分钟）
     */
    @Schema(description = "授权码 生存时间（分钟）")
    private String                  authorizationCodeTimeToLive;

    /**
     * 设备CODE 生存时间（分钟）
     */
    @Schema(description = "设备CODE 生存时间（分钟）")
    private String                  deviceCodeTimeToLive;

    /**
     * ID token 有效时间
     */
    @Schema(description = "ID 令牌有效时间")
    private String                  idTokenTimeToLive;

    /**
     * id 令牌签名算法
     */
    @Schema(description = "Id令牌签名算法")
    private String                  idTokenSignatureAlgorithm;

    /**
     * 协议端点域
     */
    @Schema(description = "协议端点域")
    private AppOidcProtocolEndpoint protocolEndpoint;

    /**
     * Access Token 格式
     */
    @Schema(description = "Access Token 格式")
    private String                  accessTokenFormat;

    /**
     * 是否重用刷新令牌
     */
    @Schema(description = "是否重用刷新令牌")
    private Boolean                 reuseRefreshToken;
}
