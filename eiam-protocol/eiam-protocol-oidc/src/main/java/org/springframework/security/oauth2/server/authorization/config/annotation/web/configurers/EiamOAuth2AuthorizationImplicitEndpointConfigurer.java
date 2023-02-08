/*
 * eiam-protocol-oidc - Employee Identity and Access Management Program
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
package org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.protocol.oidc.authentication.consent.EiamOAuth2AuthorizationConsentEndpointFilter;
import cn.topiam.employee.protocol.oidc.authentication.implicit.*;
import cn.topiam.employee.protocol.oidc.util.EiamOAuth2Utils;
import static cn.topiam.employee.common.constants.ProtocolConstants.OidcEndpointConstants.AUTHORIZATION_ENDPOINT;

/**
 * OAuth2 授权码端点配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/26 19:12
 */
@SuppressWarnings({ "All" })
public final class EiamOAuth2AuthorizationImplicitEndpointConfigurer extends
                                                                     AbstractOAuth2Configurer {
    private RequestMatcher                                                 requestMatcher;

    private final List<AuthenticationConverter>                            authorizationRequestConverters         = new ArrayList<>();
    private Consumer<List<AuthenticationConverter>>                        authorizationRequestConvertersConsumer = (authorizationRequestConverters) -> {
                                                                                                                  };
    private final List<AuthenticationProvider>                             authenticationProviders                = new ArrayList<>();
    private Consumer<List<AuthenticationProvider>>                         authenticationProvidersConsumer        = (authenticationProviders) -> {
                                                                                                                  };
    private AuthenticationSuccessHandler                                   authorizationResponseHandler;
    private AuthenticationFailureHandler                                   errorResponseHandler;

    private Consumer<EiamOAuth2AuthorizationImplicitAuthenticationContext> authorizationImplicitRequestAuthenticationContextConsumer;

    private String                                                         consentPage;

    /**
     * Restrict for internal use only.
     */
    EiamOAuth2AuthorizationImplicitEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    void addAuthorizationImplicitRequestAuthenticationValidator(Consumer<EiamOAuth2AuthorizationImplicitAuthenticationContext> authenticationValidator) {
        this.authorizationImplicitRequestAuthenticationContextConsumer = this.authorizationImplicitRequestAuthenticationContextConsumer == null
            ? authenticationValidator
            : this.authorizationImplicitRequestAuthenticationContextConsumer
                .andThen(authenticationValidator);
    }

    @Override
    void init(HttpSecurity httpSecurity) {
        this.requestMatcher = new OrRequestMatcher(
            new AntPathRequestMatcher(AUTHORIZATION_ENDPOINT, HttpMethod.GET.name()),
            new AntPathRequestMatcher(AUTHORIZATION_ENDPOINT, HttpMethod.POST.name()));

        List<AuthenticationProvider> authenticationProviders = createDefaultAuthenticationProviders(
            httpSecurity);
        if (!this.authenticationProviders.isEmpty()) {
            authenticationProviders.addAll(0, this.authenticationProviders);
        }
        this.authenticationProvidersConsumer.accept(authenticationProviders);
        authenticationProviders.forEach(authenticationProvider -> httpSecurity
            .authenticationProvider(postProcess(authenticationProvider)));
    }

    @Override
    void configure(HttpSecurity httpSecurity) {
        AuthenticationManager authenticationManager = httpSecurity
            .getSharedObject(AuthenticationManager.class);
        EiamOAuth2AuthorizationImplicitAuthenticationEndpointFilter authorizationEndpointFilter = new EiamOAuth2AuthorizationImplicitAuthenticationEndpointFilter(
            authenticationManager, AUTHORIZATION_ENDPOINT);
        List<AuthenticationConverter> authenticationConverters = createDefaultAuthenticationConverters();
        if (!this.authorizationRequestConverters.isEmpty()) {
            authenticationConverters.addAll(0, this.authorizationRequestConverters);
        }
        this.authorizationRequestConvertersConsumer.accept(authenticationConverters);
        authorizationEndpointFilter.setAuthenticationConverter(
            new DelegatingAuthenticationConverter(authenticationConverters));
        //确认请求页面地址
        if (this.consentPage != null) {
            authorizationEndpointFilter.setConsentPage(consentPage);
        }
        httpSecurity.addFilterAfter(postProcess(authorizationEndpointFilter),
            EiamOAuth2AuthorizationConsentEndpointFilter.class);
    }

    @Override
    RequestMatcher getRequestMatcher() {
        return this.requestMatcher;
    }

    /**
     * 创建默认身份验证转换器
     *
     * @return {@link List}
     */
    private static List<AuthenticationConverter> createDefaultAuthenticationConverters() {
        List<AuthenticationConverter> authenticationConverters = new ArrayList<>();
        //隐式模式请求转换器
        authenticationConverters.add(new EiamOAuth2AuthenticationImplicitAuthenticationConverter());
        //OAuth2授权同意认证转换器
        authenticationConverters
            .add(new EiamOAuth2AuthorizationImplicitConsentAuthenticationConverter());
        return authenticationConverters;
    }

    private List<AuthenticationProvider> createDefaultAuthenticationProviders(HttpSecurity httpSecurity) {
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
        //OAuth2 隐式模式请求身份验证程序
        OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator = EiamOAuth2Utils
            .getTokenGenerator(httpSecurity);
        EiamOAuth2AuthenticationImplicitAuthenticationProvider authenticationImplicitAuthenticationProvider = new EiamOAuth2AuthenticationImplicitAuthenticationProvider(
            OAuth2ConfigurerUtils.getRegisteredClientRepository(httpSecurity),
            OAuth2ConfigurerUtils.getAuthorizationService(httpSecurity),
            OAuth2ConfigurerUtils.getAuthorizationConsentService(httpSecurity), tokenGenerator);
        if (this.authorizationImplicitRequestAuthenticationContextConsumer != null) {
            authenticationImplicitAuthenticationProvider.setAuthenticationValidator(
                new EiamOAuth2AuthenticationImplicitAuthenticationValidator()
                    .andThen(this.authorizationImplicitRequestAuthenticationContextConsumer));
        }
        authenticationProviders.add(authenticationImplicitAuthenticationProvider);

        //隐式授权同意身份验证提供程序
        EiamOAuth2AuthorizationImplicitConsentAuthenticationProvider authorizationImplicitConsentAuthenticationProvider = new EiamOAuth2AuthorizationImplicitConsentAuthenticationProvider(
            OAuth2ConfigurerUtils.getRegisteredClientRepository(httpSecurity),
            OAuth2ConfigurerUtils.getAuthorizationService(httpSecurity),
            OAuth2ConfigurerUtils.getAuthorizationConsentService(httpSecurity));
        authenticationProviders.add(authorizationImplicitConsentAuthenticationProvider);
        return authenticationProviders;
    }

    public EiamOAuth2AuthorizationImplicitEndpointConfigurer consentPage(String consentPage) {
        this.consentPage = consentPage;
        return this;
    }

}
