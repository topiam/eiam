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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.http.converter.OAuth2ErrorHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenRevocationAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenRevocationEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2TokenRevocationAuthenticationConverter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

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
    private final HttpMessageConverter<OAuth2Error> errorHttpResponseConverter          = new OAuth2ErrorHttpMessageConverter();
    private final AuthenticationSuccessHandler      authenticationSuccessHandler        = this::sendRevocationSuccessResponse;
    private final AuthenticationFailureHandler      authenticationFailureHandler        = this::sendErrorResponse;

    /**
     * Restrict for internal use only.
     */
    EiamOAuth2TokenRevocationEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
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
        revocationEndpointFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        revocationEndpointFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        List<AuthenticationConverter> authenticationConverters = createDefaultAuthenticationConverters();
        if (!this.revocationRequestConverters.isEmpty()) {
            authenticationConverters.addAll(0, this.revocationRequestConverters);
        }
        this.revocationRequestConvertersConsumer.accept(authenticationConverters);
        revocationEndpointFilter.setAuthenticationConverter(
            new DelegatingAuthenticationConverter(authenticationConverters));
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

    /**
     * 撤销成功
     *
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @param authentication {@link Authentication}
     */
    private void sendRevocationSuccessResponse(HttpServletRequest request,
                                               HttpServletResponse response,
                                               Authentication authentication) {
        response.setStatus(HttpStatus.OK.value());
    }

    /**
     * 响应失败
     *
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @param exception {@link AuthenticationException}
     * @throws IOException IOException
     */
    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response,
                                   AuthenticationException exception) throws IOException {
        OAuth2Error error = ((OAuth2AuthenticationException) exception).getError();
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
        this.errorHttpResponseConverter.write(error, null, httpResponse);
    }
}
