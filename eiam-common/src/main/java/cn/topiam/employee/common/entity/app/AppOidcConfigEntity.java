/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.entity.app;

import java.time.Duration;
import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.type.SqlTypes;

import cn.topiam.employee.support.repository.SoftDeleteConverter;
import cn.topiam.employee.support.repository.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import static cn.topiam.employee.support.repository.base.BaseEntity.IS_DELETED_COLUMN;

/**
 * APP OIDC 配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/5/22 22:31
 */
@Getter
@Setter
@ToString
@Entity
@Accessors(chain = true)
@Table(name = "eiam_app_oidc_config")
@SoftDelete(columnName = IS_DELETED_COLUMN, converter = SoftDeleteConverter.class)
public class AppOidcConfigEntity extends BaseEntity {

    /**
     * APP ID
     */
    @Column(name = "app_id")
    private String      appId;

    /**
     * 客户端认证方式
     */
    @Column(name = "client_auth_methods")
    @JdbcTypeCode(SqlTypes.JSON)
    private Set<String> clientAuthMethods;

    /**
     * 授权类型
     */
    @Column(name = "auth_grant_types")
    @JdbcTypeCode(SqlTypes.JSON)
    private Set<String> authGrantTypes;

    /**
     * 响应类型
     */
    @Column(name = "response_types")
    @JdbcTypeCode(SqlTypes.JSON)
    private Set<String> responseTypes;

    /**
     * 重定向URIs
     */
    @Column(name = "redirect_uris")
    @JdbcTypeCode(SqlTypes.JSON)
    private Set<String> redirectUris;

    /**
     * 登出重定向URIs
     */
    @Column(name = "post_logout_redirect_uris")
    @JdbcTypeCode(SqlTypes.JSON)
    private Set<String> postLogoutRedirectUris;

    /**
     * scopes
     */
    @Column(name = "grant_scopes")
    @JdbcTypeCode(SqlTypes.JSON)
    private Set<String> grantScopes;

    /**
     * 是否需要授权同意
     */
    @Column(name = "require_auth_consent")
    private Boolean     requireAuthConsent;

    /**
     * 需要PKCE
     */
    @Column(name = "require_proof_key")
    private Boolean     requireProofKey;

    /**
     * 令牌 Endpoint 身份验证签名算法
     */
    @Column(name = "token_endpoint_auth_signing_algorithm")
    private String      tokenEndpointAuthSigningAlgorithm;

    /**
     * 刷新 Token生存时间（分钟）
     */
    @Column(name = "refresh_token_time_to_live")
    private Duration    refreshTokenTimeToLive;

    /**
     * 授权码 生存时间（分钟）
     */
    @Column(name = "authorization_code_time_to_live")
    private Duration    authorizationCodeTimeToLive;

    /**
     * 设备CODE 生存时间（分钟）
     */
    @Column(name = "device_code_time_to_live")
    private Duration    deviceCodeTimeToLive;

    /**
     * ID Token生存时间（分钟）
     */
    @Column(name = "id_token_time_to_live")
    private Duration    idTokenTimeToLive;

    /**
     * 访问 Token生存时间（分钟）
     */
    @Column(name = "access_token_time_to_live")
    private Duration    accessTokenTimeToLive;

    /**
     * Id Token 签名算法
     */
    @Column(name = "id_token_signature_algorithm")
    private String      idTokenSignatureAlgorithm;

    /**
     * Access Token 格式
     */
    @Column(name = "access_token_format")
    private String      accessTokenFormat;

    /**
     * 是否重用刷新令牌
     */
    @Column(name = "reuse_refresh_token")
    private Boolean     reuseRefreshToken;
}
