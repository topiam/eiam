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
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenRevocationAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenRevocationAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenRevocationEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2TokenRevocationAuthenticationConverter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import cn.topiam.employee.common.constants.ProtocolConstants;

/**
 * Configurer for the OAuth 2.0 Token Revocation Endpoint.
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/26 19:20
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public final class EiamOAuth2TokenRevocationEndpointConfigurer extends AbstractOAuth2Configurer {
    private RequestMatcher                          requestMatcher;
    private final List<AuthenticationConverter>     revocationRequestConverters         = new ArrayList<>();
    private Consumer<List<AuthenticationConverter>> revocationRequestConvertersConsumer = (revocationRequestConverters) -> {
                                                                                        };
    private final List<AuthenticationProvider>      authenticationProviders             = new ArrayList<>();
    private Consumer<List<AuthenticationProvider>>  authenticationProvidersConsumer     = (authenticationProviders) -> {
                                                                                        };
    private AuthenticationSuccessHandler            revocationResponseHandler;
    private AuthenticationFailureHandler            errorResponseHandler;

    /**
     * Restrict for internal use only.
     */
    EiamOAuth2TokenRevocationEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    /**
     * Adds an {@link AuthenticationConverter} used when attempting to extract a Revoke Token Request from {@link HttpServletRequest}
     * to an instance of {@link OAuth2TokenRevocationAuthenticationToken} used for authenticating the request.
     *
     * @param revocationRequestConverter an {@link AuthenticationConverter} used when attempting to extract a Revoke Token Request from {@link HttpServletRequest}
     * @return the {@link EiamOAuth2TokenRevocationEndpointConfigurer} for further configuration
     */
    public EiamOAuth2TokenRevocationEndpointConfigurer revocationRequestConverter(AuthenticationConverter revocationRequestConverter) {
        Assert.notNull(revocationRequestConverter, "revocationRequestConverter cannot be null");
        this.revocationRequestConverters.add(revocationRequestConverter);
        return this;
    }

    /**
     * Sets the {@code Consumer} providing access to the {@code List} of default
     * and (optionally) added {@link #revocationRequestConverter(AuthenticationConverter) AuthenticationConverter}'s
     * allowing the ability to add, remove, or customize a specific {@link AuthenticationConverter}.
     *
     * @param revocationRequestConvertersConsumer the {@code Consumer} providing access to the {@code List} of default and (optionally) added {@link AuthenticationConverter}'s
     * @return the {@link EiamOAuth2TokenRevocationEndpointConfigurer} for further configuration
     * @since 0.4.0
     */
    public EiamOAuth2TokenRevocationEndpointConfigurer revocationRequestConverters(Consumer<List<AuthenticationConverter>> revocationRequestConvertersConsumer) {
        Assert.notNull(revocationRequestConvertersConsumer,
            "revocationRequestConvertersConsumer cannot be null");
        this.revocationRequestConvertersConsumer = revocationRequestConvertersConsumer;
        return this;
    }

    /**
     * Adds an {@link AuthenticationProvider} used for authenticating a type of {@link OAuth2TokenRevocationAuthenticationToken}.
     *
     * @param authenticationProvider an {@link AuthenticationProvider} used for authenticating a type of {@link OAuth2TokenRevocationAuthenticationToken}
     * @return the {@link EiamOAuth2TokenRevocationEndpointConfigurer} for further configuration
     */
    public EiamOAuth2TokenRevocationEndpointConfigurer authenticationProvider(AuthenticationProvider authenticationProvider) {
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
     * @return the {@link EiamOAuth2TokenRevocationEndpointConfigurer} for further configuration
     * @since 0.4.0
     */
    public EiamOAuth2TokenRevocationEndpointConfigurer authenticationProviders(Consumer<List<AuthenticationProvider>> authenticationProvidersConsumer) {
        Assert.notNull(authenticationProvidersConsumer,
            "authenticationProvidersConsumer cannot be null");
        this.authenticationProvidersConsumer = authenticationProvidersConsumer;
        return this;
    }

    /**
     * Sets the {@link AuthenticationSuccessHandler} used for handling an {@link OAuth2TokenRevocationAuthenticationToken}.
     *
     * @param revocationResponseHandler the {@link AuthenticationSuccessHandler} used for handling an {@link OAuth2TokenRevocationAuthenticationToken}
     * @return the {@link EiamOAuth2TokenRevocationEndpointConfigurer} for further configuration
     */
    public EiamOAuth2TokenRevocationEndpointConfigurer revocationResponseHandler(AuthenticationSuccessHandler revocationResponseHandler) {
        this.revocationResponseHandler = revocationResponseHandler;
        return this;
    }

    /**
     * Sets the {@link AuthenticationFailureHandler} used for handling an {@link OAuth2AuthenticationException}
     * and returning the {@link OAuth2Error Error Response}.
     *
     * @param errorResponseHandler the {@link AuthenticationFailureHandler} used for handling an {@link OAuth2AuthenticationException}
     * @return the {@link EiamOAuth2TokenRevocationEndpointConfigurer} for further configuration
     */
    public EiamOAuth2TokenRevocationEndpointConfigurer errorResponseHandler(AuthenticationFailureHandler errorResponseHandler) {
        this.errorResponseHandler = errorResponseHandler;
        return this;
    }

    @Override
    void init(HttpSecurity httpSecurity) {
        this.requestMatcher = new AntPathRequestMatcher(
            ProtocolConstants.OidcEndpointConstants.TOKEN_REVOCATION_ENDPOINT,
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

        OAuth2TokenRevocationEndpointFilter revocationEndpointFilter = new OAuth2TokenRevocationEndpointFilter(
            authenticationManager,
            ProtocolConstants.OidcEndpointConstants.TOKEN_REVOCATION_ENDPOINT);
        List<AuthenticationConverter> authenticationConverters = createDefaultAuthenticationConverters();
        if (!this.revocationRequestConverters.isEmpty()) {
            authenticationConverters.addAll(0, this.revocationRequestConverters);
        }
        this.revocationRequestConvertersConsumer.accept(authenticationConverters);
        revocationEndpointFilter.setAuthenticationConverter(
            new DelegatingAuthenticationConverter(authenticationConverters));
        if (this.revocationResponseHandler != null) {
            revocationEndpointFilter
                .setAuthenticationSuccessHandler(this.revocationResponseHandler);
        }
        if (this.errorResponseHandler != null) {
            revocationEndpointFilter.setAuthenticationFailureHandler(this.errorResponseHandler);
        }
        httpSecurity.addFilterAfter(postProcess(revocationEndpointFilter),
            FilterSecurityInterceptor.class);
    }

    @Override
    RequestMatcher getRequestMatcher() {
        return this.requestMatcher;
    }

    private static List<AuthenticationConverter> createDefaultAuthenticationConverters() {
        List<AuthenticationConverter> authenticationConverters = new ArrayList<>();

        authenticationConverters.add(new OAuth2TokenRevocationAuthenticationConverter());

        return authenticationConverters;
    }

    private List<AuthenticationProvider> createDefaultAuthenticationProviders(HttpSecurity builder) {
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();

        OAuth2TokenRevocationAuthenticationProvider tokenRevocationAuthenticationProvider = new OAuth2TokenRevocationAuthenticationProvider(
            OAuth2ConfigurerUtils.getAuthorizationService(builder));
        authenticationProviders.add(tokenRevocationAuthenticationProvider);

        return authenticationProviders;
    }

}
