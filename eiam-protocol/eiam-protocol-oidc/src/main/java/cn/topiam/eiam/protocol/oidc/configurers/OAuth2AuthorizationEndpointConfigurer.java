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
package cn.topiam.eiam.protocol.oidc.configurers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.eiam.protocol.oidc.authentication.OAuth2AuthorizationImplicitRequestAuthenticationProvider;
import cn.topiam.eiam.protocol.oidc.authentication.OAuth2AuthorizationImplicitRequestAuthenticationToken;
import cn.topiam.eiam.protocol.oidc.authorization.client.OidcConfigRegisteredClientRepositoryWrapper;
import cn.topiam.eiam.protocol.oidc.endpoint.OAuth2AuthorizationEndpointFilter;
import cn.topiam.employee.protocol.code.configurer.AbstractConfigurer;
import static cn.topiam.employee.common.constant.ProtocolConstants.OidcEndpointConstants.AUTHORIZATION_ENDPOINT;
import static cn.topiam.employee.protocol.code.util.ProtocolConfigUtils.getAuthenticationDetailsSource;

/**
 * Configurer for the OAuth 2.0 Authorization Endpoint.
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/27 21:42
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public final class OAuth2AuthorizationEndpointConfigurer extends AbstractConfigurer {

    private RequestMatcher                requestMatcher;

    private SessionAuthenticationStrategy sessionAuthenticationStrategy;

    /**
     * Restrict for internal use only.
     */
    OAuth2AuthorizationEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    @Override
    public void init(HttpSecurity httpSecurity) {
        sessionAuthenticationStrategy = (authentication, request, response) -> {
            SessionRegistry sessionRegistry = httpSecurity.getSharedObject(SessionRegistry.class);
            //授权码请求
            if (authentication instanceof OAuth2AuthorizationCodeRequestAuthenticationToken token) {
                if (token.getScopes().contains(OidcScopes.OPENID)) {
                    if (sessionRegistry
                        .getSessionInformation(request.getSession().getId()) == null) {
                        sessionRegistry.registerNewSession(request.getSession().getId(),
                            ((Authentication) token.getPrincipal()).getPrincipal());
                    }
                }
            }
            //简化模式
            if (authentication instanceof OAuth2AuthorizationImplicitRequestAuthenticationToken token) {
                if (token.getScopes().contains(OidcScopes.OPENID)) {
                    if (sessionRegistry
                        .getSessionInformation(request.getSession().getId()) == null) {
                        sessionRegistry.registerNewSession(request.getSession().getId(),
                            ((Authentication) token.getPrincipal()).getPrincipal());
                    }
                }
            }
        };

        this.requestMatcher = new OrRequestMatcher(
            new AntPathRequestMatcher(AUTHORIZATION_ENDPOINT, HttpMethod.GET.name()),
            new AntPathRequestMatcher(AUTHORIZATION_ENDPOINT, HttpMethod.POST.name()));

        List<AuthenticationProvider> authenticationProviders = createDefaultAuthenticationProviders(
            httpSecurity);
        authenticationProviders.forEach(authenticationProvider -> httpSecurity
            .authenticationProvider(postProcess(authenticationProvider)));
    }

    @Override
    public void configure(HttpSecurity httpSecurity) {
        AuthenticationManager authenticationManager = httpSecurity
            .getSharedObject(AuthenticationManager.class);
        OAuth2AuthorizationService authorizationService = httpSecurity
            .getSharedObject(OAuth2AuthorizationService.class);
        OAuth2AuthorizationEndpointFilter authorizationEndpointFilter = new OAuth2AuthorizationEndpointFilter(
            authorizationService, authenticationManager, AUTHORIZATION_ENDPOINT);
        //会话认证策略
        authorizationEndpointFilter
            .setSessionAuthenticationStrategy(this.sessionAuthenticationStrategy);
        //认证详情源
        authorizationEndpointFilter
            .setAuthenticationDetailsSource(getAuthenticationDetailsSource(httpSecurity));

        httpSecurity.addFilterBefore(postProcess(authorizationEndpointFilter),
            AbstractPreAuthenticatedProcessingFilter.class);
    }

    @Override
    public RequestMatcher getRequestMatcher() {
        return this.requestMatcher;
    }

    private List<AuthenticationProvider> createDefaultAuthenticationProviders(HttpSecurity httpSecurity) {
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
        OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator = OAuth2ConfigurerUtils
            .getTokenGenerator(httpSecurity);

        //授权码认证
        OAuth2AuthorizationCodeRequestAuthenticationProvider authorizationCodeRequestAuthenticationProvider = new OAuth2AuthorizationCodeRequestAuthenticationProvider(
            new OidcConfigRegisteredClientRepositoryWrapper(
                OAuth2ConfigurerUtils.getRegisteredClientRepository(httpSecurity)),
            OAuth2ConfigurerUtils.getAuthorizationService(httpSecurity),
            OAuth2ConfigurerUtils.getAuthorizationConsentService(httpSecurity));
        authenticationProviders.add(authorizationCodeRequestAuthenticationProvider);

        //隐式模式认证
        OAuth2AuthorizationImplicitRequestAuthenticationProvider authenticationImplicitRequestAuthenticationProvider = new OAuth2AuthorizationImplicitRequestAuthenticationProvider(
            new OidcConfigRegisteredClientRepositoryWrapper(
                OAuth2ConfigurerUtils.getRegisteredClientRepository(httpSecurity)),
            OAuth2ConfigurerUtils.getAuthorizationService(httpSecurity),
            OAuth2ConfigurerUtils.getAuthorizationConsentService(httpSecurity), tokenGenerator);
        authenticationProviders.add(authenticationImplicitRequestAuthenticationProvider);

        return authenticationProviders;
    }
}
