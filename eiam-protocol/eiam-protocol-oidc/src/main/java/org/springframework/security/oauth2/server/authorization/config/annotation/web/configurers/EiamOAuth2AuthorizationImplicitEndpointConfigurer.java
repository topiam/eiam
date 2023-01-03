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
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.server.authorization.authentication.*;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.protocol.oidc.authentication.implicit.*;
import cn.topiam.employee.protocol.oidc.util.EiamOAuth2Utils;

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
    private String                                                         consentPage;

    private Consumer<EiamOAuth2AuthorizationImplicitAuthenticationContext> authorizationImplicitRequestAuthenticationContextConsumer;

    /**
     * Restrict for internal use only.
     */
    EiamOAuth2AuthorizationImplicitEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    /**
     * Adds an {@link AuthenticationConverter} used when attempting to extract an Authorization Request (or Consent) from {@link HttpServletRequest}
     * to an instance of {@link OAuth2AuthorizationCodeRequestAuthenticationToken} or {@link OAuth2AuthorizationConsentAuthenticationToken}
     * used for authenticating the request.
     *
     * @param authorizationRequestConverter an {@link AuthenticationConverter} used when attempting to extract an Authorization Request (or Consent) from {@link HttpServletRequest}
     * @return the {@link EiamOAuth2AuthorizationImplicitEndpointConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationImplicitEndpointConfigurer authorizationRequestConverter(AuthenticationConverter authorizationRequestConverter) {
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
     * @return the {@link EiamOAuth2AuthorizationImplicitEndpointConfigurer} for further configuration
     * @since 0.4.0
     */
    public EiamOAuth2AuthorizationImplicitEndpointConfigurer authorizationRequestConverters(Consumer<List<AuthenticationConverter>> authorizationRequestConvertersConsumer) {
        Assert.notNull(authorizationRequestConvertersConsumer,
            "authorizationRequestConvertersConsumer cannot be null");
        this.authorizationRequestConvertersConsumer = authorizationRequestConvertersConsumer;
        return this;
    }

    /**
     * Adds an {@link AuthenticationProvider} used for authenticating an {@link OAuth2AuthorizationCodeRequestAuthenticationToken}.
     *
     * @param authenticationProvider an {@link AuthenticationProvider} used for authenticating an {@link OAuth2AuthorizationCodeRequestAuthenticationToken}
     * @return the {@link EiamOAuth2AuthorizationImplicitEndpointConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationImplicitEndpointConfigurer authenticationProvider(AuthenticationProvider authenticationProvider) {
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
     * @return the {@link EiamOAuth2AuthorizationImplicitEndpointConfigurer} for further configuration
     * @since 0.4.0
     */
    public EiamOAuth2AuthorizationImplicitEndpointConfigurer authenticationProviders(Consumer<List<AuthenticationProvider>> authenticationProvidersConsumer) {
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
     * @return the {@link EiamOAuth2AuthorizationImplicitEndpointConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationImplicitEndpointConfigurer authorizationResponseHandler(AuthenticationSuccessHandler authorizationResponseHandler) {
        this.authorizationResponseHandler = authorizationResponseHandler;
        return this;
    }

    /**
     * Sets the {@link AuthenticationFailureHandler} used for handling an {@link OAuth2AuthorizationCodeRequestAuthenticationException}
     * and returning the {@link OAuth2Error Error Response}.
     *
     * @param errorResponseHandler the {@link AuthenticationFailureHandler} used for handling an {@link OAuth2AuthorizationCodeRequestAuthenticationException}
     * @return the {@link EiamOAuth2AuthorizationImplicitEndpointConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationImplicitEndpointConfigurer errorResponseHandler(AuthenticationFailureHandler errorResponseHandler) {
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
     * @return the {@link EiamOAuth2AuthorizationImplicitEndpointConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationImplicitEndpointConfigurer consentPage(String consentPage) {
        this.consentPage = consentPage;
        return this;
    }

    void addAuthorizationImplicitRequestAuthenticationValidator(Consumer<EiamOAuth2AuthorizationImplicitAuthenticationContext> authenticationValidator) {
        this.authorizationImplicitRequestAuthenticationContextConsumer = this.authorizationImplicitRequestAuthenticationContextConsumer == null
            ? authenticationValidator
            : this.authorizationImplicitRequestAuthenticationContextConsumer
                .andThen(authenticationValidator);
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
        EiamOAuth2AuthorizationImplicitAuthenticationEndpointFilter authorizationEndpointFilter = new EiamOAuth2AuthorizationImplicitAuthenticationEndpointFilter(
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
            AbstractPreAuthenticatedProcessingFilter.class);
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
            .add(new OAuth2AuthorizationImplicitConsentAuthenticationConverter());
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

}
