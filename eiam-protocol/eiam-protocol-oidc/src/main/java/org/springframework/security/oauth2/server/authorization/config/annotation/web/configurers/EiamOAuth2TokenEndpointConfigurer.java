/*
 * eiam-protocol-oidc - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.*;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.protocol.oidc.authentication.password.EiamOAuth2AuthorizationPasswordAuthenticationProvider;
import cn.topiam.employee.protocol.oidc.util.EiamOAuth2Utils;

/**
 * 配置OAuth2 token端点
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/26 19:18
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public final class EiamOAuth2TokenEndpointConfigurer extends AbstractOAuth2Configurer {
    private RequestMatcher                          requestMatcher;
    private final List<AuthenticationConverter>     accessTokenRequestConverters         = new ArrayList<>();
    private Consumer<List<AuthenticationConverter>> accessTokenRequestConvertersConsumer = (accessTokenRequestConverters) -> {
                                                                                         };
    private final List<AuthenticationProvider>      authenticationProviders              = new ArrayList<>();
    private Consumer<List<AuthenticationProvider>>  authenticationProvidersConsumer      = (authenticationProviders) -> {
                                                                                         };
    private AuthenticationSuccessHandler            accessTokenResponseHandler;
    private AuthenticationFailureHandler            errorResponseHandler;

    /**
     * Restrict for internal use only.
     */
    EiamOAuth2TokenEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    /**
     * Adds an {@link AuthenticationConverter} used when attempting to extract an Access Token Request from {@link HttpServletRequest}
     * to an instance of {@link OAuth2AuthorizationGrantAuthenticationToken} used for authenticating the authorization grant.
     *
     * @param accessTokenRequestConverter an {@link AuthenticationConverter} used when attempting to extract an Access Token Request from {@link HttpServletRequest}
     * @return the {@link OAuth2TokenEndpointConfigurer} for further configuration
     */
    public EiamOAuth2TokenEndpointConfigurer accessTokenRequestConverter(AuthenticationConverter accessTokenRequestConverter) {
        Assert.notNull(accessTokenRequestConverter, "accessTokenRequestConverter cannot be null");
        this.accessTokenRequestConverters.add(accessTokenRequestConverter);
        return this;
    }

    /**
     * Sets the {@code Consumer} providing access to the {@code List} of default
     * and (optionally) added {@link #accessTokenRequestConverter(AuthenticationConverter) AuthenticationConverter}'s
     * allowing the ability to add, remove, or customize a specific {@link AuthenticationConverter}.
     *
     * @param accessTokenRequestConvertersConsumer the {@code Consumer} providing access to the {@code List} of default and (optionally) added {@link AuthenticationConverter}'s
     * @return the {@link EiamOAuth2TokenEndpointConfigurer} for further configuration
     * @since 0.4.0
     */
    public EiamOAuth2TokenEndpointConfigurer accessTokenRequestConverters(Consumer<List<AuthenticationConverter>> accessTokenRequestConvertersConsumer) {
        Assert.notNull(accessTokenRequestConvertersConsumer,
            "accessTokenRequestConvertersConsumer cannot be null");
        this.accessTokenRequestConvertersConsumer = accessTokenRequestConvertersConsumer;
        return this;
    }

    /**
     * Adds an {@link AuthenticationProvider} used for authenticating a type of {@link OAuth2AuthorizationGrantAuthenticationToken}.
     *
     * @param authenticationProvider an {@link AuthenticationProvider} used for authenticating a type of {@link OAuth2AuthorizationGrantAuthenticationToken}
     * @return the {@link EiamOAuth2TokenEndpointConfigurer} for further configuration
     */
    public EiamOAuth2TokenEndpointConfigurer authenticationProvider(AuthenticationProvider authenticationProvider) {
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
     * @return the {@link EiamOAuth2TokenEndpointConfigurer} for further configuration
     * @since 0.4.0
     */
    public EiamOAuth2TokenEndpointConfigurer authenticationProviders(Consumer<List<AuthenticationProvider>> authenticationProvidersConsumer) {
        Assert.notNull(authenticationProvidersConsumer,
            "authenticationProvidersConsumer cannot be null");
        this.authenticationProvidersConsumer = authenticationProvidersConsumer;
        return this;
    }

    /**
     * Sets the {@link AuthenticationSuccessHandler} used for handling an {@link OAuth2AccessTokenAuthenticationToken}
     * and returning the {@link OAuth2AccessTokenResponse Access Token Response}.
     *
     * @param accessTokenResponseHandler the {@link AuthenticationSuccessHandler} used for handling an {@link OAuth2AccessTokenAuthenticationToken}
     * @return the {@link EiamOAuth2TokenEndpointConfigurer} for further configuration
     */
    public EiamOAuth2TokenEndpointConfigurer accessTokenResponseHandler(AuthenticationSuccessHandler accessTokenResponseHandler) {
        this.accessTokenResponseHandler = accessTokenResponseHandler;
        return this;
    }

    /**
     * Sets the {@link AuthenticationFailureHandler} used for handling an {@link OAuth2AuthenticationException}
     * and returning the {@link OAuth2Error Error Response}.
     *
     * @param errorResponseHandler the {@link AuthenticationFailureHandler} used for handling an {@link OAuth2AuthenticationException}
     * @return the {@link EiamOAuth2TokenEndpointConfigurer} for further configuration
     */
    public EiamOAuth2TokenEndpointConfigurer errorResponseHandler(AuthenticationFailureHandler errorResponseHandler) {
        this.errorResponseHandler = errorResponseHandler;
        return this;
    }

    @Override
    void init(HttpSecurity httpSecurity) {
        this.requestMatcher = new AntPathRequestMatcher(
            ProtocolConstants.OidcEndpointConstants.TOKEN_ENDPOINT, HttpMethod.POST.name());

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

        OAuth2TokenEndpointFilter tokenEndpointFilter = new OAuth2TokenEndpointFilter(
            authenticationManager, ProtocolConstants.OidcEndpointConstants.TOKEN_ENDPOINT);
        List<AuthenticationConverter> authenticationConverters = createDefaultAuthenticationConverters();
        if (!this.accessTokenRequestConverters.isEmpty()) {
            authenticationConverters.addAll(0, this.accessTokenRequestConverters);
        }
        this.accessTokenRequestConvertersConsumer.accept(authenticationConverters);
        tokenEndpointFilter.setAuthenticationConverter(
            new DelegatingAuthenticationConverter(authenticationConverters));
        if (this.accessTokenResponseHandler != null) {
            tokenEndpointFilter.setAuthenticationSuccessHandler(this.accessTokenResponseHandler);
        }
        if (this.errorResponseHandler != null) {
            tokenEndpointFilter.setAuthenticationFailureHandler(this.errorResponseHandler);
        }
        httpSecurity.addFilterAfter(postProcess(tokenEndpointFilter),
            FilterSecurityInterceptor.class);
    }

    @Override
    RequestMatcher getRequestMatcher() {
        return this.requestMatcher;
    }

    private static List<AuthenticationConverter> createDefaultAuthenticationConverters() {
        List<AuthenticationConverter> authenticationConverters = new ArrayList<>();

        authenticationConverters.add(new OAuth2AuthorizationCodeAuthenticationConverter());
        authenticationConverters.add(new OAuth2RefreshTokenAuthenticationConverter());
        authenticationConverters.add(new OAuth2ClientCredentialsAuthenticationConverter());

        return authenticationConverters;
    }

    private List<AuthenticationProvider> createDefaultAuthenticationProviders(HttpSecurity builder) {
        try {
            //@formatter:off
            List<AuthenticationProvider> authenticationProviders = new ArrayList<>();

            PasswordEncoder passwordEncoder = EiamOAuth2Utils.getPasswordEncoder(builder);

            UserDetailsService userDetailsService = EiamOAuth2Utils.getUserDetailsService(builder);

            OAuth2AuthorizationService authorizationService = OAuth2ConfigurerUtils.getAuthorizationService(builder);

            OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator = EiamOAuth2Utils.getTokenGenerator(builder);

            OAuth2AuthorizationCodeAuthenticationProvider authorizationCodeAuthenticationProvider = new OAuth2AuthorizationCodeAuthenticationProvider(authorizationService, tokenGenerator);
            authenticationProviders.add(authorizationCodeAuthenticationProvider);

            OAuth2RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider = new OAuth2RefreshTokenAuthenticationProvider(authorizationService, tokenGenerator);
            authenticationProviders.add(refreshTokenAuthenticationProvider);

            OAuth2ClientCredentialsAuthenticationProvider clientCredentialsAuthenticationProvider = new OAuth2ClientCredentialsAuthenticationProvider(authorizationService, tokenGenerator);
            authenticationProviders.add(clientCredentialsAuthenticationProvider);

            //密码模式
            EiamOAuth2AuthorizationPasswordAuthenticationProvider auth2AuthorizationCodeRequestAuthenticationProvider = new EiamOAuth2AuthorizationPasswordAuthenticationProvider(userDetailsService, authorizationService, tokenGenerator, passwordEncoder);
            authenticationProviders.add(auth2AuthorizationCodeRequestAuthenticationProvider);

            return authenticationProviders;
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
        //@formatter:on
    }

}
