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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2DeviceAuthorizationConsentAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2DeviceVerificationAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.web.OAuth2DeviceVerificationEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2DeviceAuthorizationConsentAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2DeviceVerificationAuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.common.constant.ProtocolConstants;
import cn.topiam.employee.protocol.code.EndpointMatcher;
import cn.topiam.employee.protocol.code.configurer.AbstractConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.eiam.protocol.oidc.constant.OidcProtocolConstants.OIDC_ERROR_URI;

/**
 * Configurer for the OAuth 2.0 Device Verification Endpoint.
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/6/27 21:29
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public final class OAuth2DeviceVerificationEndpointConfigurer extends AbstractConfigurer {

    private RequestMatcher                     requestMatcher;

    private final AuthenticationFailureHandler authenticationFailureHandler = this::sendErrorResponse;

    /**
     * Restrict for internal use only.
     */
    OAuth2DeviceVerificationEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    @Override
    public void init(HttpSecurity builder) {
        this.requestMatcher = new OrRequestMatcher(
            new AntPathRequestMatcher(
                ProtocolConstants.OidcEndpointConstants.DEVICE_VERIFICATION_ENDPOINT,
                HttpMethod.GET.name()),
            new AntPathRequestMatcher(
                ProtocolConstants.OidcEndpointConstants.DEVICE_VERIFICATION_ENDPOINT,
                HttpMethod.POST.name()));

        List<AuthenticationProvider> authenticationProviders = createDefaultAuthenticationProviders(
            builder);
        authenticationProviders.forEach(authenticationProvider -> builder
            .authenticationProvider(postProcess(authenticationProvider)));
    }

    @Override
    public void configure(HttpSecurity builder) {
        AuthenticationManager authenticationManager = builder
            .getSharedObject(AuthenticationManager.class);

        OAuth2DeviceVerificationEndpointFilter deviceVerificationEndpointFilter = new OAuth2DeviceVerificationEndpointFilter(
            authenticationManager,
            ProtocolConstants.OidcEndpointConstants.DEVICE_VERIFICATION_ENDPOINT);
        deviceVerificationEndpointFilter
            .setAuthenticationFailureHandler(this.authenticationFailureHandler);
        List<AuthenticationConverter> authenticationConverters = createDefaultAuthenticationConverters();
        deviceVerificationEndpointFilter.setAuthenticationConverter(
            new DelegatingAuthenticationConverter(authenticationConverters));
        builder.addFilterBefore(postProcess(deviceVerificationEndpointFilter),
            AbstractPreAuthenticatedProcessingFilter.class);
    }

    @Override
    public EndpointMatcher getEndpointMatcher() {
        return new EndpointMatcher(this.requestMatcher, false);
    }

    private static List<AuthenticationConverter> createDefaultAuthenticationConverters() {
        List<AuthenticationConverter> authenticationConverters = new ArrayList<>();

        authenticationConverters.add(new OAuth2DeviceVerificationAuthenticationConverter());
        authenticationConverters.add(new OAuth2DeviceAuthorizationConsentAuthenticationConverter());

        return authenticationConverters;
    }

    private static List<AuthenticationProvider> createDefaultAuthenticationProviders(HttpSecurity builder) {
        RegisteredClientRepository registeredClientRepository = OAuth2ConfigurerUtils
            .getRegisteredClientRepository(builder);
        OAuth2AuthorizationService authorizationService = OAuth2ConfigurerUtils
            .getAuthorizationService(builder);
        OAuth2AuthorizationConsentService authorizationConsentService = OAuth2ConfigurerUtils
            .getAuthorizationConsentService(builder);

        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();

        // @formatter:off
        OAuth2DeviceVerificationAuthenticationProvider deviceVerificationAuthenticationProvider =
                new OAuth2DeviceVerificationAuthenticationProvider(
                        registeredClientRepository, authorizationService, authorizationConsentService);
        // @formatter:on
        authenticationProviders.add(deviceVerificationAuthenticationProvider);

        // @formatter:off
        OAuth2DeviceAuthorizationConsentAuthenticationProvider deviceAuthorizationConsentAuthenticationProvider =
                new OAuth2DeviceAuthorizationConsentAuthenticationProvider(
                        registeredClientRepository, authorizationService, authorizationConsentService);
        // @formatter:on
        authenticationProviders.add(deviceAuthorizationConsentAuthenticationProvider);

        return authenticationProviders;
    }

    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response,
                                   AuthenticationException exception) throws IOException {
        //@formatter:off
        OAuth2Error error = ((OAuth2AuthenticationException) exception).getError();
        OAuth2Error responseError=new OAuth2Error(error.getErrorCode(),error.getDescription(),OIDC_ERROR_URI);
        response.sendError(HttpStatus.BAD_REQUEST.value(), responseError.toString());
        //@formatter:on
    }
}
