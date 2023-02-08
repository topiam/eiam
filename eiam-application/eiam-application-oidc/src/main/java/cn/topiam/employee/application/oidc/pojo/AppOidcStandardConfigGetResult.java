/*
 * eiam-application-oidc - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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
import cn.topiam.employee.common.enums.app.InitLoginType;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OIDC 配置返回
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/31 22:46
 */
@Data
@Schema(description = "OIDC 配置返回结果")
public class AppOidcStandardConfigGetResult implements Serializable {

    @Serial
    private static final long       serialVersionUID = 4177874005424703372L;

    /**
     * APP ID
     */
    @Parameter(description = "appId")
    private Long                    appId;

    /**
     * SSO 发起方
     */
    @Parameter(description = "SSO 发起方")
    private InitLoginType           initLoginType;

    /**
     * SSO 登录链接
     */
    @Parameter(description = "SSO 登录链接")
    private String                  initLoginUrl;

    /**
     * 授权范围
     */
    @Parameter(description = "SSO 授权范围")
    private AuthorizationType       authorizationType;
    /**
     * authorizationGrantTypes
     */
    @Parameter(description = "认证授权类型")
    private Set<String>             authGrantTypes;

    /**
     * 客户端认证方式
     */
    @Parameter(description = "客户端认证方式")
    private Set<String>             clientAuthMethods;

    /**
     * 重定向URI
     */
    @Parameter(description = "重定向URI")
    private Set<String>             redirectUris;
    /**
     * scopes
     */
    @Parameter(description = "授权范围")
    private Set<String>             grantScopes;

    /**
     * 启用PKCE
     */
    @Parameter(description = "启用PKCE")
    private Boolean                 requireProofKey;

    /**
     * 令牌 Endpoint 身份验证签名算法
     */
    @Parameter(description = "令牌 Endpoint 身份验证签名算法")
    private String                  tokenEndpointAuthSigningAlgorithm;

    /**
     * 是否需要授权同意
     */
    @Parameter(description = "是否需要授权同意")
    private Boolean                 requireAuthConsent;
    /**
     * 访问令牌有效时间
     */
    @Parameter(description = "访问令牌有效时间")
    private String                  accessTokenTimeToLive;
    /**
     * 刷新令牌有效时间
     */
    @Parameter(description = "刷新令牌有效时间")
    private String                  refreshTokenTimeToLive;

    /**
     * ID token 有效时间
     */
    @Parameter(description = "ID 令牌有效时间")
    private String                  idTokenTimeToLive;
    /**
     * id 令牌签名算法
     */
    @Parameter(description = "Id令牌签名算法")
    private String                  idTokenSignatureAlgorithm;

    /**
     * 协议端点域
     */
    @Parameter(description = "协议端点域")
    private AppOidcProtocolEndpoint protocolEndpoint;

    /**
     * Access Token 格式
     */
    @Parameter(description = "Access Token 格式")
    private String                  accessTokenFormat;

    /**
     * 是否重用刷新令牌
     */
    @Parameter(description = "是否重用刷新令牌")
    private Boolean                 reuseRefreshToken;

}
