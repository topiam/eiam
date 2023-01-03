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
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.oidc.OidcClientRegistration;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcClientConfigurationAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcClientRegistrationAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcClientRegistrationAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.oidc.web.OidcClientRegistrationEndpointFilter;
import org.springframework.security.oauth2.server.authorization.oidc.web.authentication.OidcClientRegistrationAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import cn.topiam.employee.common.constants.ProtocolConstants;

/**
 * Configurer for OpenID Connect Dynamic Client Registration 1.0 Endpoint.
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/26 19:21
 */
public final class EiamOidcClientRegistrationEndpointConfigurer extends AbstractOAuth2Configurer {
    private RequestMatcher                          requestMatcher;
    private final List<AuthenticationConverter>     clientRegistrationRequestConverters         = new ArrayList<>();
    private Consumer<List<AuthenticationConverter>> clientRegistrationRequestConvertersConsumer = (clientRegistrationRequestConverters) -> {
                                                                                                };
    private final List<AuthenticationProvider>      authenticationProviders                     = new ArrayList<>();
    private Consumer<List<AuthenticationProvider>>  authenticationProvidersConsumer             = (authenticationProviders) -> {
                                                                                                };
    private AuthenticationSuccessHandler            clientRegistrationResponseHandler;
    private AuthenticationFailureHandler            errorResponseHandler;

    /**
     * Restrict for internal use only.
     */
    EiamOidcClientRegistrationEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    /**
     * Adds an {@link AuthenticationConverter} used when attempting to extract a Client Registration Request from {@link HttpServletRequest}
     * to an instance of {@link OidcClientRegistrationAuthenticationToken} used for authenticating the request.
     *
     * @param clientRegistrationRequestConverter an {@link AuthenticationConverter} used when attempting to extract a Client Registration Request from {@link HttpServletRequest}
     * @return the {@link EiamOidcClientRegistrationEndpointConfigurer} for further configuration
     * @since 0.4.0
     */
    public EiamOidcClientRegistrationEndpointConfigurer clientRegistrationRequestConverter(AuthenticationConverter clientRegistrationRequestConverter) {
        Assert.notNull(clientRegistrationRequestConverter,
            "clientRegistrationRequestConverter cannot be null");
        this.clientRegistrationRequestConverters.add(clientRegistrationRequestConverter);
        return this;
    }

    /**
     * Sets the {@code Consumer} providing access to the {@code List} of default
     * and (optionally) added {@link #clientRegistrationRequestConverter(AuthenticationConverter) AuthenticationConverter}'s
     * allowing the ability to add, remove, or customize a specific {@link AuthenticationConverter}.
     *
     * @param clientRegistrationRequestConvertersConsumer the {@code Consumer} providing access to the {@code List} of default and (optionally) added {@link AuthenticationConverter}'s
     * @return the {@link EiamOidcClientRegistrationEndpointConfigurer} for further configuration
     * @since 0.4.0
     */
    public EiamOidcClientRegistrationEndpointConfigurer clientRegistrationRequestConverters(Consumer<List<AuthenticationConverter>> clientRegistrationRequestConvertersConsumer) {
        Assert.notNull(clientRegistrationRequestConvertersConsumer,
            "clientRegistrationRequestConvertersConsumer cannot be null");
        this.clientRegistrationRequestConvertersConsumer = clientRegistrationRequestConvertersConsumer;
        return this;
    }

    /**
     * Adds an {@link AuthenticationProvider} used for authenticating an {@link OidcClientRegistrationAuthenticationToken}.
     *
     * @param authenticationProvider an {@link AuthenticationProvider} used for authenticating an {@link OidcClientRegistrationAuthenticationToken}
     * @return the {@link EiamOidcClientRegistrationEndpointConfigurer} for further configuration
     * @since 0.4.0
     */
    public EiamOidcClientRegistrationEndpointConfigurer authenticationProvider(AuthenticationProvider authenticationProvider) {
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
     * @return the {@link EiamOidcClientRegistrationEndpointConfigurer} for further configuration
     * @since 0.4.0
     */
    public EiamOidcClientRegistrationEndpointConfigurer authenticationProviders(Consumer<List<AuthenticationProvider>> authenticationProvidersConsumer) {
        Assert.notNull(authenticationProvidersConsumer,
            "authenticationProvidersConsumer cannot be null");
        this.authenticationProvidersConsumer = authenticationProvidersConsumer;
        return this;
    }

    /**
     * Sets the {@link AuthenticationSuccessHandler} used for handling an {@link OidcClientRegistrationAuthenticationToken}
     * and returning the {@link OidcClientRegistration Client Registration Response}.
     *
     * @param clientRegistrationResponseHandler the {@link AuthenticationSuccessHandler} used for handling an {@link OidcClientRegistrationAuthenticationToken}
     * @return the {@link EiamOidcClientRegistrationEndpointConfigurer} for further configuration
     * @since 0.4.0
     */
    public EiamOidcClientRegistrationEndpointConfigurer clientRegistrationResponseHandler(AuthenticationSuccessHandler clientRegistrationResponseHandler) {
        this.clientRegistrationResponseHandler = clientRegistrationResponseHandler;
        return this;
    }

    /**
     * Sets the {@link AuthenticationFailureHandler} used for handling an {@link OAuth2AuthenticationException}
     * and returning the {@link OAuth2Error Error Response}.
     *
     * @param errorResponseHandler the {@link AuthenticationFailureHandler} used for handling an {@link OAuth2AuthenticationException}
     * @return the {@link EiamOidcClientRegistrationEndpointConfigurer} for further configuration
     * @since 0.4.0
     */
    public EiamOidcClientRegistrationEndpointConfigurer errorResponseHandler(AuthenticationFailureHandler errorResponseHandler) {
        this.errorResponseHandler = errorResponseHandler;
        return this;
    }

    @Override
    void init(HttpSecurity httpSecurity) {
        this.requestMatcher = new OrRequestMatcher(
            new AntPathRequestMatcher(
                ProtocolConstants.OidcEndpointConstants.OIDC_CLIENT_REGISTRATION_ENDPOINT,
                HttpMethod.POST.name()),
            new AntPathRequestMatcher(
                ProtocolConstants.OidcEndpointConstants.OIDC_CLIENT_REGISTRATION_ENDPOINT,
                HttpMethod.GET.name()));

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

        OidcClientRegistrationEndpointFilter oidcClientRegistrationEndpointFilter = new OidcClientRegistrationEndpointFilter(
            authenticationManager,
            ProtocolConstants.OidcEndpointConstants.OIDC_CLIENT_REGISTRATION_ENDPOINT);

        List<AuthenticationConverter> authenticationConverters = createDefaultAuthenticationConverters();
        if (!this.clientRegistrationRequestConverters.isEmpty()) {
            authenticationConverters.addAll(0, this.clientRegistrationRequestConverters);
        }
        this.clientRegistrationRequestConvertersConsumer.accept(authenticationConverters);
        oidcClientRegistrationEndpointFilter.setAuthenticationConverter(
            new DelegatingAuthenticationConverter(authenticationConverters));
        if (this.clientRegistrationResponseHandler != null) {
            oidcClientRegistrationEndpointFilter
                .setAuthenticationSuccessHandler(this.clientRegistrationResponseHandler);
        }
        if (this.errorResponseHandler != null) {
            oidcClientRegistrationEndpointFilter
                .setAuthenticationFailureHandler(this.errorResponseHandler);
        }
        httpSecurity.addFilterAfter(postProcess(oidcClientRegistrationEndpointFilter),
            FilterSecurityInterceptor.class);
    }

    @Override
    RequestMatcher getRequestMatcher() {
        return this.requestMatcher;
    }

    private static List<AuthenticationConverter> createDefaultAuthenticationConverters() {
        List<AuthenticationConverter> authenticationConverters = new ArrayList<>();

        authenticationConverters.add(new OidcClientRegistrationAuthenticationConverter());

        return authenticationConverters;
    }

    private static List<AuthenticationProvider> createDefaultAuthenticationProviders(HttpSecurity httpSecurity) {
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();

        OidcClientRegistrationAuthenticationProvider oidcClientRegistrationAuthenticationProvider = new OidcClientRegistrationAuthenticationProvider(
            OAuth2ConfigurerUtils.getRegisteredClientRepository(httpSecurity),
            OAuth2ConfigurerUtils.getAuthorizationService(httpSecurity),
            OAuth2ConfigurerUtils.getTokenGenerator(httpSecurity));
        authenticationProviders.add(oidcClientRegistrationAuthenticationProvider);

        OidcClientConfigurationAuthenticationProvider oidcClientConfigurationAuthenticationProvider = new OidcClientConfigurationAuthenticationProvider(
            OAuth2ConfigurerUtils.getRegisteredClientRepository(httpSecurity),
            OAuth2ConfigurerUtils.getAuthorizationService(httpSecurity));
        authenticationProviders.add(oidcClientConfigurationAuthenticationProvider);

        return authenticationProviders;
    }

}
