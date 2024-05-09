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
package cn.topiam.employee.application.oidc.converter;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.StringSubstitutor;
import org.mapstruct.Mapper;

import cn.topiam.employee.application.oidc.pojo.AppOidcProtocolEndpoint;
import cn.topiam.employee.application.oidc.pojo.AppOidcStandardConfigGetResult;
import cn.topiam.employee.application.oidc.pojo.AppOidcStandardSaveConfigParam;
import cn.topiam.employee.common.constant.ProtocolConstants;
import cn.topiam.employee.common.entity.app.AppOidcConfigEntity;
import cn.topiam.employee.common.entity.app.po.AppOidcConfigPO;
import cn.topiam.employee.core.context.ContextService;
import cn.topiam.employee.support.util.UrlUtils;
import static cn.topiam.employee.common.constant.ProtocolConstants.APP_CODE;
import static cn.topiam.employee.common.constant.ProtocolConstants.OidcEndpointConstants.OIDC_AUTHORIZE_PATH;
import static cn.topiam.employee.common.constant.ProtocolConstants.OidcEndpointConstants.WELL_KNOWN_OPENID_CONFIGURATION;

/**
 * 应用映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/14 22:45
 */
@Mapper(componentModel = "spring")
public interface AppOidcStandardConfigConverter {
    /**
     * 实体转OIDC配置结果
     *
     * @param config {@link AppOidcConfigEntity}
     * @return {@link AppOidcStandardConfigGetResult}
     */
    default AppOidcStandardConfigGetResult entityConverterToOidcConfigResult(AppOidcConfigPO config) {
        AppOidcStandardConfigGetResult result = new AppOidcStandardConfigGetResult();
        if (Objects.isNull(config)) {
            return result;
        }
        //协议端点域
        result.setProtocolEndpoint(getProtocolEndpointDomain(config.getAppCode()));
        //认证授权类型
        result.setAuthGrantTypes(config.getAuthGrantTypes());
        //登录重定向URI
        result.setRedirectUris(config.getRedirectUris());
        //登出重定向URI
        result.setPostLogoutRedirectUris(config.getPostLogoutRedirectUris());
        //授权范围
        result.setGrantScopes(config.getGrantScopes());
        //启用PKCE
        result.setRequireProofKey(config.getRequireProofKey());
        //访问令牌有效时间
        result.setAccessTokenTimeToLive(
            String.valueOf(config.getAccessTokenTimeToLive().toMinutes()));
        //刷新令牌有效时间
        result.setRefreshTokenTimeToLive(
            String.valueOf(config.getRefreshTokenTimeToLive().toMinutes()));
        //ID令牌有效时间
        result.setIdTokenTimeToLive(String.valueOf(config.getIdTokenTimeToLive().toMinutes()));
        //设备授权码有效期
        result
            .setDeviceCodeTimeToLive(String.valueOf(config.getDeviceCodeTimeToLive().toMinutes()));
        //access_token格式
        result.setAccessTokenFormat(config.getAccessTokenFormat());
        //授权码有效期
        result.setAuthorizationCodeTimeToLive(
            String.valueOf(config.getAuthorizationCodeTimeToLive().toMinutes()));
        //ID令牌有效时间
        result.setReuseRefreshToken(config.getReuseRefreshToken());
        //ID令牌有效时间
        result.setIdTokenTimeToLive(String.valueOf(config.getIdTokenTimeToLive().toMinutes()));
        //ID令牌签名算法
        result.setIdTokenSignatureAlgorithm(config.getIdTokenSignatureAlgorithm());
        //登录发起地址
        result.setInitLoginUrl(config.getInitLoginUrl());
        //授权类型
        result.setAuthorizationType(config.getAuthorizationType());
        return result;
    }

    /**
     * save 转 entity
     *
     * @param config {@link AppOidcConfigEntity}
     * @return {@link AppOidcConfigEntity}
     */
    default AppOidcConfigEntity appOidcStandardSaveConfigParamToEntity(AppOidcStandardSaveConfigParam config) {
        if (config == null) {
            return null;
        }
        AppOidcConfigEntity entity = new AppOidcConfigEntity();
        if (CollectionUtils.isNotEmpty(config.getClientAuthMethods())) {
            entity.setClientAuthMethods(new LinkedHashSet<>(config.getClientAuthMethods()));
        }
        if (CollectionUtils.isNotEmpty(config.getAuthGrantTypes())) {
            entity.setAuthGrantTypes(new LinkedHashSet<>(config.getAuthGrantTypes()));
        }
        if (CollectionUtils.isNotEmpty(config.getRedirectUris())) {
            entity.setRedirectUris(new LinkedHashSet<>(config.getRedirectUris()));
        }
        if (CollectionUtils.isNotEmpty(config.getPostLogoutRedirectUris())) {
            entity
                .setPostLogoutRedirectUris(new LinkedHashSet<>(config.getPostLogoutRedirectUris()));
        }
        if (CollectionUtils.isNotEmpty(config.getGrantScopes())) {
            entity.setGrantScopes(new LinkedHashSet<>(config.getGrantScopes()));
        }
        entity.setRequireAuthConsent(config.getRequireAuthConsent());
        entity.setRequireProofKey(config.getRequireProofKey());
        entity.setTokenEndpointAuthSigningAlgorithm(config.getTokenEndpointAuthSigningAlgorithm());
        if (config.getRefreshTokenTimeToLive() != null) {
            entity
                .setRefreshTokenTimeToLive(Duration.ofMinutes(config.getRefreshTokenTimeToLive()));
        }
        if (config.getAuthorizationCodeTimeToLive() != null) {
            entity.setAuthorizationCodeTimeToLive(
                Duration.ofMinutes(config.getAuthorizationCodeTimeToLive()));
        }
        if (config.getDeviceCodeTimeToLive() != null) {
            entity.setDeviceCodeTimeToLive(Duration.ofMinutes(config.getDeviceCodeTimeToLive()));
        }
        if (config.getIdTokenTimeToLive() != null) {
            entity.setIdTokenTimeToLive(Duration.ofMinutes(config.getIdTokenTimeToLive()));
        }
        if (config.getAccessTokenTimeToLive() != null) {
            entity.setAccessTokenTimeToLive(Duration.ofMinutes(config.getAccessTokenTimeToLive()));
        }
        entity.setIdTokenSignatureAlgorithm(config.getIdTokenSignatureAlgorithm());
        entity.setAccessTokenFormat(config.getAccessTokenFormat());
        entity.setReuseRefreshToken(config.getReuseRefreshToken());
        return entity;
    }

    /**
     * 获取协议端点
     *
     * @param appCode {@link String}
     * @return {@link AppOidcProtocolEndpoint}
     */
    private AppOidcProtocolEndpoint getProtocolEndpointDomain(String appCode) {
        //@formatter:off
        AppOidcProtocolEndpoint domain = new AppOidcProtocolEndpoint();
        //issues
        Map<String,String> variables = new HashMap<>(16);
        variables.put(APP_CODE,appCode);
        StringSubstitutor sub = new StringSubstitutor(variables, "{", "}");
        //Issuer
        domain.setIssuer(sub.replace(ContextService.getPortalPublicBaseUrl() + OIDC_AUTHORIZE_PATH));
        //发现端点
        domain.setDiscoveryEndpoint(UrlUtils.format(ContextService.getPortalPublicBaseUrl() + sub.replace(WELL_KNOWN_OPENID_CONFIGURATION)));
        //认证端点
        domain.setAuthorizationEndpoint(UrlUtils.format(ContextService.getPortalPublicBaseUrl() +  sub.replace(ProtocolConstants.OidcEndpointConstants.AUTHORIZATION_ENDPOINT)));
        //Token端点
        domain.setTokenEndpoint(UrlUtils.format(ContextService.getPortalPublicBaseUrl() + sub.replace( ProtocolConstants.OidcEndpointConstants.TOKEN_ENDPOINT)));
        //Jwks端点
        domain.setJwksEndpoint(UrlUtils.format(ContextService.getPortalPublicBaseUrl() +  sub.replace(ProtocolConstants.OidcEndpointConstants.JWK_SET_ENDPOINT)));
        //撤销端点
        domain.setRevokeEndpoint(UrlUtils.format(ContextService.getPortalPublicBaseUrl()+  sub.replace(ProtocolConstants.OidcEndpointConstants.TOKEN_REVOCATION_ENDPOINT)));
        //UserInfo端点
        domain.setUserinfoEndpoint(UrlUtils.format(ContextService.getPortalPublicBaseUrl() +  sub.replace(ProtocolConstants.OidcEndpointConstants.OIDC_USER_INFO_ENDPOINT)));
        //登出端点
        domain.setEndSessionEndpoint(UrlUtils.format(ContextService.getPortalPublicBaseUrl() +  sub.replace(ProtocolConstants.OidcEndpointConstants.OIDC_LOGOUT_ENDPOINT)));
        return domain;
        //@formatter:on
    }
}
