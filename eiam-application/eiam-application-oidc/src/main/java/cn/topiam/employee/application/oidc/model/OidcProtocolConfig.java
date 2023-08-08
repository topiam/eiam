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
package cn.topiam.employee.application.oidc.model;

import java.io.Serial;
import java.util.List;
import java.util.Set;

import com.nimbusds.jose.jwk.JWK;

import cn.topiam.employee.application.AbstractProtocolConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * Oidc 协议配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/28 21:43
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class OidcProtocolConfig extends AbstractProtocolConfig {

    @Serial
    private static final long serialVersionUID = -3671812647788723766L;

    /**
     * 客户端认证方式
     */
    private Set<String>       clientAuthMethods;

    /**
     * 授权类型
     */
    private Set<String>       authGrantTypes;

    /**
     * 响应类型
     */
    private Set<String>       responseTypes;

    /**
     * 重定向URIs
     */
    private Set<String>       redirectUris;

    /**
     * 登出重定向URIs
     */
    private Set<String>       postLogoutRedirectUris;

    /**
     * scopes
     */
    private Set<String>       grantScopes;

    /**
     * 是否需要授权同意
     */
    private Boolean           requireAuthConsent;

    /**
     * 需要PKCE
     */
    private Boolean           requireProofKey;

    /**
     * 令牌 Endpoint 身份验证签名算法
     */
    private String            tokenEndpointAuthSigningAlgorithm;

    /**
     * 刷新 Token生存时间（分钟）
     */
    private Integer           refreshTokenTimeToLive;

    /**
     * ID Token生存时间（分钟）
     */
    private Integer           idTokenTimeToLive;

    /**
     * 访问 Token生存时间（分钟）
     */
    private Integer           accessTokenTimeToLive;

    /**
     * Id Token 签名算法
     */
    private String            idTokenSignatureAlgorithm;

    /**
     * Access Token 格式
     */
    private String            accessTokenFormat;

    /**
     * 是否重用刷新令牌
     */
    private Boolean           reuseRefreshToken;

    /**
     * jwks
     */
    private List<JWK>         jwks;
}
