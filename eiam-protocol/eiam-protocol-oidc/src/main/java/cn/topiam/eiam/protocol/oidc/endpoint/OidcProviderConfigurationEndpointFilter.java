/*
 * eiam-protocol-oidc - Employee Identity and Access Management
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
package cn.topiam.eiam.protocol.oidc.endpoint;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponseType;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContext;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.oidc.OidcProviderConfiguration;
import org.springframework.security.oauth2.server.authorization.oidc.http.converter.OidcProviderConfigurationHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import cn.topiam.employee.core.help.ServerHelp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.eiam.protocol.oidc.endpoint.authentication.OAuth2AuthorizationImplicitRequestAuthenticationConverter.ID_TOKEN;
import static cn.topiam.eiam.protocol.oidc.endpoint.authentication.OAuth2AuthorizationImplicitRequestAuthenticationConverter.TOKEN;
import static cn.topiam.employee.common.constant.ProtocolConstants.OidcEndpointConstants.WELL_KNOWN_OPENID_CONFIGURATION;

/**
 * A {@code Filter} that processes OpenID Provider Configuration Requests.
 *
 * @author TopIAM
 * @see <a target="_blank" href="https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfigurationRequest">4.1. OpenID Provider Configuration Request</a>
 */
public final class OidcProviderConfigurationEndpointFilter extends OncePerRequestFilter {
    /**
     * The default endpoint {@code URI} for OpenID Provider Configuration requests.
     */
    private static final String  DEFAULT_OIDC_PROVIDER_CONFIGURATION_ENDPOINT_URI = WELL_KNOWN_OPENID_CONFIGURATION;

    private final RequestMatcher requestMatcher;

    public OidcProviderConfigurationEndpointFilter() {
        requestMatcher = new AntPathRequestMatcher(DEFAULT_OIDC_PROVIDER_CONFIGURATION_ENDPOINT_URI,
            HttpMethod.GET.name());
    }

    public OidcProviderConfigurationEndpointFilter(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    private final OidcProviderConfigurationHttpMessageConverter providerConfigurationHttpMessageConverter = new OidcProviderConfigurationHttpMessageConverter();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException,
                                                                      IOException {

        if (!this.requestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        AuthorizationServerContext authorizationServerContext = AuthorizationServerContextHolder
            .getContext();
        String issuer = authorizationServerContext.getIssuer();
        AuthorizationServerSettings authorizationServerSettings = authorizationServerContext
            .getAuthorizationServerSettings();

        OidcProviderConfiguration.Builder providerConfiguration = OidcProviderConfiguration
            .builder().issuer(issuer)
            //认证端点
            .authorizationEndpoint(asUrl(ServerHelp.getPortalPublicBaseUrl(),
                authorizationServerSettings.getAuthorizationEndpoint()))
            //设备授权端点
            .deviceAuthorizationEndpoint(asUrl(ServerHelp.getPortalPublicBaseUrl(),
                authorizationServerSettings.getDeviceAuthorizationEndpoint()))
            //令牌端点
            .tokenEndpoint(asUrl(ServerHelp.getPortalPublicBaseUrl(),
                authorizationServerSettings.getTokenEndpoint()))
            //令牌端点身份验证方法
            .tokenEndpointAuthenticationMethods(clientAuthenticationMethods())
            //JWK
            .jwkSetUrl(asUrl(ServerHelp.getPortalPublicBaseUrl(),
                authorizationServerSettings.getJwkSetEndpoint()))
            //用户信息端点
            .userInfoEndpoint(asUrl(ServerHelp.getPortalPublicBaseUrl(),
                authorizationServerSettings.getOidcUserInfoEndpoint()))
            //退出端点
            .endSessionEndpoint(asUrl(ServerHelp.getPortalPublicBaseUrl(),
                authorizationServerSettings.getOidcLogoutEndpoint()))
            //动态处理影响类型
            .responseType(OAuth2AuthorizationResponseType.CODE.getValue())
            .responseType(TOKEN.getValue()).responseType(ID_TOKEN.getValue())
            //动态处理授权类型
            .grantTypes(grantTypes())
            //令牌吊销端点
            .tokenRevocationEndpoint(asUrl(ServerHelp.getPortalPublicBaseUrl(),
                authorizationServerSettings.getTokenRevocationEndpoint()))
            //令牌吊销端点身份验证方法
            .tokenRevocationEndpointAuthenticationMethods(clientAuthenticationMethods())
            //令牌解析端点
            .tokenIntrospectionEndpoint(asUrl(ServerHelp.getPortalPublicBaseUrl(),
                authorizationServerSettings.getTokenIntrospectionEndpoint()))
            //令牌解析身份验证方法
            .tokenIntrospectionEndpointAuthenticationMethods(clientAuthenticationMethods())
            .subjectType("public")
            //ID_TOKEN签名端点
            .idTokenSigningAlgorithm(SignatureAlgorithm.RS256.getName()).scope(OidcScopes.OPENID);

        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        this.providerConfigurationHttpMessageConverter.write(providerConfiguration.build(),
            MediaType.APPLICATION_JSON, httpResponse);
    }

    /**
     * 授权类型
     *
     * @return {@link List}
     */
    private static Consumer<List<String>> grantTypes() {
        return strings -> {
            strings.add(AuthorizationGrantType.AUTHORIZATION_CODE.getValue());
            strings.add(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue());
            strings.add(AuthorizationGrantType.REFRESH_TOKEN.getValue());
            strings.add(AuthorizationGrantType.DEVICE_CODE.getValue());
        };
    }

    /**
     * 客户端身份验证方法
     *
     * @return {@link List}
     */
    private static Consumer<List<String>> clientAuthenticationMethods() {
        return (authenticationMethods) -> {
            authenticationMethods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue());
            authenticationMethods.add(ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue());
            authenticationMethods.add(ClientAuthenticationMethod.CLIENT_SECRET_JWT.getValue());
            authenticationMethods.add(ClientAuthenticationMethod.PRIVATE_KEY_JWT.getValue());
        };
    }

    private static String asUrl(String issuer, String endpoint) {
        return UriComponentsBuilder.fromUriString(issuer).path(endpoint).build().toUriString();
    }

}
