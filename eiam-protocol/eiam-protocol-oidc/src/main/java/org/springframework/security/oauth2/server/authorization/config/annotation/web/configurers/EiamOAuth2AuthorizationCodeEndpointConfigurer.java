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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationValidator;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationConsentAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.web.OAuth2AuthorizationEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeRequestAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationConsentAuthenticationConverter;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.protocol.oidc.authentication.implicit.EiamOAuth2AuthorizationImplicitAuthenticationEndpointFilter;
import static cn.topiam.employee.common.constants.ProtocolConstants.OidcEndpointConstants.AUTHORIZATION_ENDPOINT;

/**
 * OAuth2 授权码端点配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/26 19:12
 */
@SuppressWarnings({ "All" })
public final class EiamOAuth2AuthorizationCodeEndpointConfigurer extends AbstractOAuth2Configurer {

    protected final Log                                                   logger                                 = LogFactory
        .getLog(getClass());

    private RequestMatcher                                                requestMatcher;
    private final RedirectStrategy                                        redirectStrategy                       = new DefaultRedirectStrategy();
    private final List<AuthenticationConverter>                           authorizationRequestConverters         = new ArrayList<>();
    private Consumer<List<AuthenticationConverter>>                       authorizationRequestConvertersConsumer = (authorizationRequestConverters) -> {
                                                                                                                 };
    private final List<AuthenticationProvider>                            authenticationProviders                = new ArrayList<>();
    private Consumer<List<AuthenticationProvider>>                        authenticationProvidersConsumer        = (authenticationProviders) -> {
                                                                                                                 };
    private String                                                        consentPage;

    private Consumer<OAuth2AuthorizationCodeRequestAuthenticationContext> authorizationCodeRequestAuthenticationValidator;

    /**
     * Restrict for internal use only.
     */
    EiamOAuth2AuthorizationCodeEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    void addAuthorizationCodeRequestAuthenticationValidator(Consumer<OAuth2AuthorizationCodeRequestAuthenticationContext> authenticationValidator) {
        this.authorizationCodeRequestAuthenticationValidator = this.authorizationCodeRequestAuthenticationValidator == null
            ? authenticationValidator
            : this.authorizationCodeRequestAuthenticationValidator.andThen(authenticationValidator);
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
        OAuth2AuthorizationEndpointFilter authorizationEndpointFilter = new OAuth2AuthorizationEndpointFilter(
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
            EiamOAuth2AuthorizationImplicitAuthenticationEndpointFilter.class);
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
        //授权码模式请求转换器
        authenticationConverters.add(new OAuth2AuthorizationCodeRequestAuthenticationConverter());
        //OAuth2授权同意认证转换器
        authenticationConverters.add(new OAuth2AuthorizationConsentAuthenticationConverter());
        return authenticationConverters;
    }

    private List<AuthenticationProvider> createDefaultAuthenticationProviders(HttpSecurity httpSecurity) {
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
        //OAuth2授权码请求身份验证程序
        OAuth2AuthorizationCodeRequestAuthenticationProvider authorizationCodeRequestAuthenticationProvider = new OAuth2AuthorizationCodeRequestAuthenticationProvider(
            OAuth2ConfigurerUtils.getRegisteredClientRepository(httpSecurity),
            OAuth2ConfigurerUtils.getAuthorizationService(httpSecurity),
            OAuth2ConfigurerUtils.getAuthorizationConsentService(httpSecurity));
        if (this.authorizationCodeRequestAuthenticationValidator != null) {
            authorizationCodeRequestAuthenticationProvider.setAuthenticationValidator(
                new OAuth2AuthorizationCodeRequestAuthenticationValidator()
                    .andThen(this.authorizationCodeRequestAuthenticationValidator));
        }
        authenticationProviders.add(authorizationCodeRequestAuthenticationProvider);

        //OAuth2授权码同意身份验证提供程序
        OAuth2AuthorizationConsentAuthenticationProvider authorizationConsentAuthenticationProvider = new OAuth2AuthorizationConsentAuthenticationProvider(
            OAuth2ConfigurerUtils.getRegisteredClientRepository(httpSecurity),
            OAuth2ConfigurerUtils.getAuthorizationService(httpSecurity),
            OAuth2ConfigurerUtils.getAuthorizationConsentService(httpSecurity));
        authenticationProviders.add(authorizationConsentAuthenticationProvider);
        return authenticationProviders;
    }

    public EiamOAuth2AuthorizationCodeEndpointConfigurer consentPage(String consentPage) {
        this.consentPage = consentPage;
        return this;
    }

}
