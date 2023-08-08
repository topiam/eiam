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

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcLogoutAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.oidc.web.OidcLogoutEndpointFilter;
import org.springframework.security.oauth2.server.authorization.oidc.web.authentication.OidcLogoutAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.eiam.protocol.oidc.authorization.client.OidcConfigRegisteredClientRepositoryWrapper;
import cn.topiam.employee.common.constant.ProtocolConstants;
import cn.topiam.employee.protocol.code.configurer.AbstractConfigurer;
import static cn.topiam.eiam.protocol.oidc.constant.OidcProtocolConstants.OIDC_ERROR_URI;

/**
 * Configurer for OpenID Connect 1.0 RP-Initiated Logout Endpoint.
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/27 21:02
 */
public final class OidcLogoutEndpointConfigurer extends AbstractConfigurer {

    private RequestMatcher                     requestMatcher;

    private final AuthenticationFailureHandler authenticationFailureHandler = (request, response,
                                                                               exception) -> {
//@formatter:off
OAuth2Error error = ((OAuth2AuthenticationException) exception).getError();
OAuth2Error responseError=new OAuth2Error(error.getErrorCode(),error.getDescription(),OIDC_ERROR_URI);
response.sendError(HttpStatus.BAD_REQUEST.value(), responseError.toString());
//@formatter:on
                                                                            };

    OidcLogoutEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    @Override
    public void init(HttpSecurity httpSecurity) {
        this.requestMatcher = new OrRequestMatcher(
            new AntPathRequestMatcher(ProtocolConstants.OidcEndpointConstants.OIDC_LOGOUT_ENDPOINT,
                HttpMethod.GET.name()),
            new AntPathRequestMatcher(ProtocolConstants.OidcEndpointConstants.OIDC_LOGOUT_ENDPOINT,
                HttpMethod.POST.name()));

        List<AuthenticationProvider> authenticationProviders = createDefaultAuthenticationProviders(
            httpSecurity);
        authenticationProviders.forEach(authenticationProvider -> httpSecurity
            .authenticationProvider(postProcess(authenticationProvider)));
    }

    @Override
    public void configure(HttpSecurity httpSecurity) {
        AuthenticationManager authenticationManager = httpSecurity
            .getSharedObject(AuthenticationManager.class);
        OidcLogoutEndpointFilter oidcLogoutEndpointFilter = new OidcLogoutEndpointFilter(
            authenticationManager, ProtocolConstants.OidcEndpointConstants.OIDC_LOGOUT_ENDPOINT);
        oidcLogoutEndpointFilter.setAuthenticationFailureHandler(this.authenticationFailureHandler);
        List<AuthenticationConverter> authenticationConverters = createDefaultAuthenticationConverters();
        oidcLogoutEndpointFilter.setAuthenticationConverter(
            new DelegatingAuthenticationConverter(authenticationConverters));
        httpSecurity.addFilterBefore(postProcess(oidcLogoutEndpointFilter), LogoutFilter.class);
    }

    @Override
    public RequestMatcher getRequestMatcher() {
        return this.requestMatcher;
    }

    private static List<AuthenticationConverter> createDefaultAuthenticationConverters() {
        List<AuthenticationConverter> authenticationConverters = new ArrayList<>();

        authenticationConverters.add(new OidcLogoutAuthenticationConverter());

        return authenticationConverters;
    }

    private static List<AuthenticationProvider> createDefaultAuthenticationProviders(HttpSecurity httpSecurity) {
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();

        OidcLogoutAuthenticationProvider oidcLogoutAuthenticationProvider = new OidcLogoutAuthenticationProvider(
            new OidcConfigRegisteredClientRepositoryWrapper(
                OAuth2ConfigurerUtils.getRegisteredClientRepository(httpSecurity)),
            OAuth2ConfigurerUtils.getAuthorizationService(httpSecurity),
            httpSecurity.getSharedObject(SessionRegistry.class));
        authenticationProviders.add(oidcLogoutAuthenticationProvider);

        return authenticationProviders;
    }
}
