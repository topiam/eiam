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
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenIntrospectionAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenIntrospectionAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenIntrospectionEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2TokenIntrospectionAuthenticationConverter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import cn.topiam.employee.common.constants.ProtocolConstants;

/**
 * Configurer for the OAuth 2.0 Token Introspection Endpoint.
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/26 19:19
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public final class EiamOAuth2TokenIntrospectionEndpointConfigurer extends AbstractOAuth2Configurer {
    private final List<AuthenticationConverter>     introspectionRequestConverters         = new ArrayList<>();
    private final List<AuthenticationProvider>      authenticationProviders                = new ArrayList<>();
    private RequestMatcher                          requestMatcher;
    private Consumer<List<AuthenticationConverter>> introspectionRequestConvertersConsumer = (introspectionRequestConverters) -> {
                                                                                           };
    private Consumer<List<AuthenticationProvider>>  authenticationProvidersConsumer        = (authenticationProviders) -> {
                                                                                           };
    private AuthenticationSuccessHandler            introspectionResponseHandler;
    private AuthenticationFailureHandler            errorResponseHandler;

    /**
     * Restrict for internal use only.
     */
    EiamOAuth2TokenIntrospectionEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    private static List<AuthenticationConverter> createDefaultAuthenticationConverters() {
        List<AuthenticationConverter> authenticationConverters = new ArrayList<>();

        authenticationConverters.add(new OAuth2TokenIntrospectionAuthenticationConverter());

        return authenticationConverters;
    }

    private static List<AuthenticationProvider> createDefaultAuthenticationProviders(HttpSecurity httpSecurity) {
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();

        OAuth2TokenIntrospectionAuthenticationProvider tokenIntrospectionAuthenticationProvider = new OAuth2TokenIntrospectionAuthenticationProvider(
            OAuth2ConfigurerUtils.getRegisteredClientRepository(httpSecurity),
            OAuth2ConfigurerUtils.getAuthorizationService(httpSecurity));
        authenticationProviders.add(tokenIntrospectionAuthenticationProvider);

        return authenticationProviders;
    }

    /**
     * Adds an {@link AuthenticationConverter} used when attempting to extract an Introspection Request from {@link HttpServletRequest}
     * to an instance of {@link OAuth2TokenIntrospectionAuthenticationToken} used for authenticating the request.
     *
     * @param introspectionRequestConverter an {@link AuthenticationConverter} used when attempting to extract an Introspection Request from {@link HttpServletRequest}
     * @return the {@link EiamOAuth2TokenEndpointConfigurer} for further configuration
     */
    public EiamOAuth2TokenIntrospectionEndpointConfigurer introspectionRequestConverter(AuthenticationConverter introspectionRequestConverter) {
        Assert.notNull(introspectionRequestConverter,
            "introspectionRequestConverter cannot be null");
        this.introspectionRequestConverters.add(introspectionRequestConverter);
        return this;
    }

    /**
     * Sets the {@code Consumer} providing access to the {@code List} of default
     * and (optionally) added {@link #introspectionRequestConverter(AuthenticationConverter) AuthenticationConverter}'s
     * allowing the ability to add, remove, or customize a specific {@link AuthenticationConverter}.
     *
     * @param introspectionRequestConvertersConsumer the {@code Consumer} providing access to the {@code List} of default and (optionally) added {@link AuthenticationConverter}'s
     * @return the {@link EiamOAuth2TokenEndpointConfigurer} for further configuration
     * @since 0.4.0
     */
    public EiamOAuth2TokenIntrospectionEndpointConfigurer introspectionRequestConverters(Consumer<List<AuthenticationConverter>> introspectionRequestConvertersConsumer) {
        Assert.notNull(introspectionRequestConvertersConsumer,
            "introspectionRequestConvertersConsumer cannot be null");
        this.introspectionRequestConvertersConsumer = introspectionRequestConvertersConsumer;
        return this;
    }

    /**
     * Adds an {@link AuthenticationProvider} used for authenticating a type of {@link OAuth2TokenIntrospectionAuthenticationToken}.
     *
     * @param authenticationProvider an {@link AuthenticationProvider} used for authenticating a type of {@link OAuth2TokenIntrospectionAuthenticationToken}
     * @return the {@link EiamOAuth2TokenEndpointConfigurer} for further configuration
     */
    public EiamOAuth2TokenIntrospectionEndpointConfigurer authenticationProvider(AuthenticationProvider authenticationProvider) {
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
    public EiamOAuth2TokenIntrospectionEndpointConfigurer authenticationProviders(Consumer<List<AuthenticationProvider>> authenticationProvidersConsumer) {
        Assert.notNull(authenticationProvidersConsumer,
            "authenticationProvidersConsumer cannot be null");
        this.authenticationProvidersConsumer = authenticationProvidersConsumer;
        return this;
    }

    /**
     * Sets the {@link AuthenticationSuccessHandler} used for handling an {@link OAuth2TokenIntrospectionAuthenticationToken}.
     *
     * @param introspectionResponseHandler the {@link AuthenticationSuccessHandler} used for handling an {@link OAuth2TokenIntrospectionAuthenticationToken}
     * @return the {@link EiamOAuth2TokenEndpointConfigurer} for further configuration
     */
    public EiamOAuth2TokenIntrospectionEndpointConfigurer introspectionResponseHandler(AuthenticationSuccessHandler introspectionResponseHandler) {
        this.introspectionResponseHandler = introspectionResponseHandler;
        return this;
    }

    /**
     * Sets the {@link AuthenticationFailureHandler} used for handling an {@link OAuth2AuthenticationException}
     * and returning the {@link OAuth2Error Error Response}.
     *
     * @param errorResponseHandler the {@link AuthenticationFailureHandler} used for handling an {@link OAuth2AuthenticationException}
     * @return the {@link EiamOAuth2TokenEndpointConfigurer} for further configuration
     */
    public EiamOAuth2TokenIntrospectionEndpointConfigurer errorResponseHandler(AuthenticationFailureHandler errorResponseHandler) {
        this.errorResponseHandler = errorResponseHandler;
        return this;
    }

    @Override
    void init(HttpSecurity httpSecurity) {
        this.requestMatcher = new AntPathRequestMatcher(
            ProtocolConstants.OidcEndpointConstants.TOKEN_INTROSPECTION_ENDPOINT,
            HttpMethod.POST.name());

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

        OAuth2TokenIntrospectionEndpointFilter introspectionEndpointFilter = new OAuth2TokenIntrospectionEndpointFilter(
            authenticationManager,
            ProtocolConstants.OidcEndpointConstants.TOKEN_INTROSPECTION_ENDPOINT);
        List<AuthenticationConverter> authenticationConverters = createDefaultAuthenticationConverters();
        if (!this.introspectionRequestConverters.isEmpty()) {
            authenticationConverters.addAll(0, this.introspectionRequestConverters);
        }
        this.introspectionRequestConvertersConsumer.accept(authenticationConverters);
        introspectionEndpointFilter.setAuthenticationConverter(
            new DelegatingAuthenticationConverter(authenticationConverters));
        if (this.introspectionResponseHandler != null) {
            introspectionEndpointFilter
                .setAuthenticationSuccessHandler(this.introspectionResponseHandler);
        }
        if (this.errorResponseHandler != null) {
            introspectionEndpointFilter.setAuthenticationFailureHandler(this.errorResponseHandler);
        }
        httpSecurity.addFilterAfter(postProcess(introspectionEndpointFilter),
            FilterSecurityInterceptor.class);
    }

    @Override
    public RequestMatcher getRequestMatcher() {
        return this.requestMatcher;
    }

}
