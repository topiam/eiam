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

import java.util.Set;

import org.hibernate.annotations.*;

import cn.topiam.employee.support.repository.domain.LogicDeleteEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_WHERE;

/**
 * APP OIDC 配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/22 22:31
 */
@Getter
@Setter
@ToString
@Entity
@Accessors(chain = true)
@Table(name = "app_oidc_config")
@SQLDelete(sql = "update app_oidc_config set " + SOFT_DELETE_SET + " where id_ = ?")
@Where(clause = SOFT_DELETE_WHERE)
public class AppOidcConfigEntity extends LogicDeleteEntity<Long> {

    /**
     * APP ID
     */
    @Column(name = "app_id")
    private Long        appId;

    /**
     * 客户端认证方式
     */
    @Column(name = "client_auth_methods")
    @Type(JsonType.class)
    private Set<String> clientAuthMethods;

    /**
     * 授权类型
     */
    @Column(name = "auth_grant_types")
    @Type(JsonType.class)
    private Set<String> authGrantTypes;

    /**
     * 响应类型
     */
    @Column(name = "response_types")
    @Type(JsonType.class)
    private Set<String> responseTypes;

    /**
     * 重定向URIs
     */
    @Column(name = "redirect_uris")
    @Type(JsonType.class)
    private Set<String> redirectUris;

    /**
     * 登出重定向URIs
     */
    @Column(name = "post_logout_redirect_uris")
    @Type(JsonType.class)
    private Set<String> postLogoutRedirectUris;

    /**
     * scopes
     */
    @Column(name = "grant_scopes")
    @Type(JsonType.class)
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
    private Integer     refreshTokenTimeToLive;

    /**
     * ID Token生存时间（分钟）
     */
    @Column(name = "id_token_time_to_live")
    private Integer     idTokenTimeToLive;

    /**
     * 访问 Token生存时间（分钟）
     */
    @Column(name = "access_token_time_to_live")
    private Integer     accessTokenTimeToLive;

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
