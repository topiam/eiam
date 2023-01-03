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

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.server.authorization.authentication.*;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.web.OAuth2AuthorizationEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeRequestAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationConsentAuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.protocol.oidc.authentication.implicit.EiamOAuth2AuthorizationImplicitAuthenticationEndpointFilter;

/**
 * OAuth2 授权码端点配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/26 19:12
 */
@SuppressWarnings({ "All" })
public final class EiamOAuth2AuthorizationEndpointConfigurer extends AbstractOAuth2Configurer {
    private RequestMatcher                                                requestMatcher;

    private final List<AuthenticationConverter>                           authorizationRequestConverters         = new ArrayList<>();
    private Consumer<List<AuthenticationConverter>>                       authorizationRequestConvertersConsumer = (authorizationRequestConverters) -> {
                                                                                                                 };
    private final List<AuthenticationProvider>                            authenticationProviders                = new ArrayList<>();
    private Consumer<List<AuthenticationProvider>>                        authenticationProvidersConsumer        = (authenticationProviders) -> {
                                                                                                                 };
    private AuthenticationSuccessHandler                                  authorizationResponseHandler;
    private AuthenticationFailureHandler                                  errorResponseHandler;
    private String                                                        consentPage;

    private Consumer<OAuth2AuthorizationCodeRequestAuthenticationContext> authorizationCodeRequestAuthenticationValidator;

    /**
     * Restrict for internal use only.
     */
    EiamOAuth2AuthorizationEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    /**
     * Adds an {@link AuthenticationConverter} used when attempting to extract an Authorization Request (or Consent) from {@link HttpServletRequest}
     * to an instance of {@link OAuth2AuthorizationCodeRequestAuthenticationToken} or {@link OAuth2AuthorizationConsentAuthenticationToken}
     * used for authenticating the request.
     *
     * @param authorizationRequestConverter an {@link AuthenticationConverter} used when attempting to extract an Authorization Request (or Consent) from {@link HttpServletRequest}
     * @return the {@link EiamOAuth2AuthorizationEndpointConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationEndpointConfigurer authorizationRequestConverter(AuthenticationConverter authorizationRequestConverter) {
        Assert.notNull(authorizationRequestConverter,
            "authorizationRequestConverter cannot be null");
        this.authorizationRequestConverters.add(authorizationRequestConverter);
        return this;
    }

    /**
     * Sets the {@code Consumer} providing access to the {@code List} of default
     * and (optionally) added {@link #authorizationRequestConverter(AuthenticationConverter) AuthenticationConverter}'s
     * allowing the ability to add, remove, or customize a specific {@link AuthenticationConverter}.
     *
     * @param authorizationRequestConvertersConsumer the {@code Consumer} providing access to the {@code List} of default and (optionally) added {@link AuthenticationConverter}'s
     * @return the {@link EiamOAuth2AuthorizationEndpointConfigurer} for further configuration
     * @since 0.4.0
     */
    public EiamOAuth2AuthorizationEndpointConfigurer authorizationRequestConverters(Consumer<List<AuthenticationConverter>> authorizationRequestConvertersConsumer) {
        Assert.notNull(authorizationRequestConvertersConsumer,
            "authorizationRequestConvertersConsumer cannot be null");
        this.authorizationRequestConvertersConsumer = authorizationRequestConvertersConsumer;
        return this;
    }

    /**
     * Adds an {@link AuthenticationProvider} used for authenticating an {@link OAuth2AuthorizationCodeRequestAuthenticationToken}.
     *
     * @param authenticationProvider an {@link AuthenticationProvider} used for authenticating an {@link OAuth2AuthorizationCodeRequestAuthenticationToken}
     * @return the {@link EiamOAuth2AuthorizationEndpointConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationEndpointConfigurer authenticationProvider(AuthenticationProvider authenticationProvider) {
        Assert.notNull(authenticationProvider, "authenticationProvider cannot be null");
        this.authenticationProviders.add(authenticationProvider);
        return this;
    }

    /**
     * Sets the {@code Consumer} providing access to the {@code List} of default
     * and (optionally) added {@link #authenticationProvider(AuthenticationProvider) AuthenticationProvider}'s
     * allowing the ability to add, remove, or customize a specific {@link AuthenticationProvider}.
     *
     * @param authenticationProvidersConsumer the {@code Consumer} providing access to the {@code List} of default and (optionally) added {@link AuthenticationProvider}'s
     * @return the {@link EiamOAuth2AuthorizationEndpointConfigurer} for further configuration
     * @since 0.4.0
     */
    public EiamOAuth2AuthorizationEndpointConfigurer authenticationProviders(Consumer<List<AuthenticationProvider>> authenticationProvidersConsumer) {
        Assert.notNull(authenticationProvidersConsumer,
            "authenticationProvidersConsumer cannot be null");
        this.authenticationProvidersConsumer = authenticationProvidersConsumer;
        return this;
    }

    /**
     * Sets the {@link AuthenticationSuccessHandler} used for handling an {@link OAuth2AuthorizationCodeRequestAuthenticationToken}
     * and returning the {@link OAuth2AuthorizationResponse Authorization Response}.
     *
     * @param authorizationResponseHandler the {@link AuthenticationSuccessHandler} used for handling an {@link OAuth2AuthorizationCodeRequestAuthenticationToken}
     * @return the {@link EiamOAuth2AuthorizationEndpointConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationEndpointConfigurer authorizationResponseHandler(AuthenticationSuccessHandler authorizationResponseHandler) {
        this.authorizationResponseHandler = authorizationResponseHandler;
        return this;
    }

    /**
     * Sets the {@link AuthenticationFailureHandler} used for handling an {@link OAuth2AuthorizationCodeRequestAuthenticationException}
     * and returning the {@link OAuth2Error Error Response}.
     *
     * @param errorResponseHandler the {@link AuthenticationFailureHandler} used for handling an {@link OAuth2AuthorizationCodeRequestAuthenticationException}
     * @return the {@link EiamOAuth2AuthorizationEndpointConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationEndpointConfigurer errorResponseHandler(AuthenticationFailureHandler errorResponseHandler) {
        this.errorResponseHandler = errorResponseHandler;
        return this;
    }

    /**
     * Specify the URI to redirect Resource Owners to if consent is required during
     * the {@code authorization_code} flow. A default consent page will be generated when
     * this attribute is not specified.
     *
     * If a URI is specified, applications are required to process the specified URI to generate
     * a consent page. The query string will contain the following parameters:
     *
     * <ul>
     * <li>{@code client_id} - the client identifier</li>
     * <li>{@code scope} - a space-delimited list of scopes present in the authorization request</li>
     * <li>{@code state} - a CSRF protection token</li>
     * </ul>
     *
     * In general, the consent page should create a form that submits
     * a request with the following requirements:
     *
     * <ul>
     * <li>It must be an HTTP POST</li>
     * <li>It must be submitted to {@link AuthorizationServerSettings#getAuthorizationEndpoint()}</li>
     * <li>It must include the received {@code client_id} as an HTTP parameter</li>
     * <li>It must include the received {@code state} as an HTTP parameter</li>
     * <li>It must include the list of {@code scope}s the {@code Resource Owner}
     * consented to as an HTTP parameter</li>
     * </ul>
     *
     * @param consentPage the URI of the custom consent page to redirect to if consent is required (e.g. "/oauth2/consent")
     * @return the {@link EiamOAuth2AuthorizationEndpointConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationEndpointConfigurer consentPage(String consentPage) {
        this.consentPage = consentPage;
        return this;
    }

    void addAuthorizationCodeRequestAuthenticationValidator(Consumer<OAuth2AuthorizationCodeRequestAuthenticationContext> authenticationValidator) {
        this.authorizationCodeRequestAuthenticationValidator = this.authorizationCodeRequestAuthenticationValidator == null
            ? authenticationValidator
            : this.authorizationCodeRequestAuthenticationValidator.andThen(authenticationValidator);
    }

    @Override
    void init(HttpSecurity httpSecurity) {
        this.requestMatcher = new OrRequestMatcher(new AntPathRequestMatcher(
            ProtocolConstants.OidcEndpointConstants.AUTHORIZATION_ENDPOINT, HttpMethod.GET.name()),
            new AntPathRequestMatcher(
                ProtocolConstants.OidcEndpointConstants.AUTHORIZATION_ENDPOINT,
                HttpMethod.POST.name()));

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
            authenticationManager, ProtocolConstants.OidcEndpointConstants.AUTHORIZATION_ENDPOINT);
        List<AuthenticationConverter> authenticationConverters = createDefaultAuthenticationConverters();
        if (!this.authorizationRequestConverters.isEmpty()) {
            authenticationConverters.addAll(0, this.authorizationRequestConverters);
        }
        this.authorizationRequestConvertersConsumer.accept(authenticationConverters);
        authorizationEndpointFilter.setAuthenticationConverter(
            new DelegatingAuthenticationConverter(authenticationConverters));
        if (this.authorizationResponseHandler != null) {
            authorizationEndpointFilter
                .setAuthenticationSuccessHandler(this.authorizationResponseHandler);
        }
        if (this.errorResponseHandler != null) {
            authorizationEndpointFilter.setAuthenticationFailureHandler(this.errorResponseHandler);
        }
        if (StringUtils.hasText(this.consentPage)) {
            authorizationEndpointFilter.setConsentPage(this.consentPage);
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

}
