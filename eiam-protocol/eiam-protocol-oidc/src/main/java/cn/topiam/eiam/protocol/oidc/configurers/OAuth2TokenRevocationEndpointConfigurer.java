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
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.http.converter.OAuth2ErrorHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenRevocationAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenRevocationEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2TokenRevocationAuthenticationConverter;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.common.constant.ProtocolConstants;
import cn.topiam.employee.protocol.code.configurer.AbstractConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.eiam.protocol.oidc.constant.OidcProtocolConstants.OIDC_ERROR_URI;

/**
 * 配置令牌撤销端点
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/27 21:25
 */
@SuppressWarnings({ "AlibabaClassNamingShouldBeCamel" })
public final class OAuth2TokenRevocationEndpointConfigurer extends AbstractConfigurer {
    private RequestMatcher                     requestMatcher;

    private final AuthenticationFailureHandler authenticationFailureHandler = new AuthenticationFailureHandler() {

                                                                                private final HttpMessageConverter<OAuth2Error> errorHttpResponseConverter = new OAuth2ErrorHttpMessageConverter();

                                                                                @Override
                                                                                public void onAuthenticationFailure(HttpServletRequest request,
                                                                                                                    HttpServletResponse response,
                                                                                                                    AuthenticationException exception) throws IOException {
            //@formatter:off
            OAuth2Error error = ((OAuth2AuthenticationException) exception).getError();
            ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
            httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
            OAuth2Error responseError=new OAuth2Error(error.getErrorCode(),error.getDescription(),OIDC_ERROR_URI);
            this.errorHttpResponseConverter.write(responseError, null, httpResponse);
            //@formatter:on
                                                                                }
                                                                            };

    OAuth2TokenRevocationEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    @Override
    public void init(HttpSecurity httpSecurity) {
        this.requestMatcher = new AntPathRequestMatcher(
            ProtocolConstants.OidcEndpointConstants.TOKEN_REVOCATION_ENDPOINT,
            HttpMethod.POST.name());

        List<AuthenticationProvider> authenticationProviders = createDefaultAuthenticationProviders(
            httpSecurity);

        authenticationProviders.forEach(authenticationProvider -> httpSecurity
            .authenticationProvider(postProcess(authenticationProvider)));
    }

    @Override
    public void configure(HttpSecurity httpSecurity) {
        AuthenticationManager authenticationManager = httpSecurity
            .getSharedObject(AuthenticationManager.class);

        OAuth2TokenRevocationEndpointFilter revocationEndpointFilter = new OAuth2TokenRevocationEndpointFilter(
            authenticationManager,
            ProtocolConstants.OidcEndpointConstants.TOKEN_REVOCATION_ENDPOINT);
        List<AuthenticationConverter> authenticationConverters = createDefaultAuthenticationConverters();
        revocationEndpointFilter.setAuthenticationFailureHandler(this.authenticationFailureHandler);
        revocationEndpointFilter.setAuthenticationConverter(
            new DelegatingAuthenticationConverter(authenticationConverters));
        httpSecurity.addFilterAfter(postProcess(revocationEndpointFilter),
            AuthorizationFilter.class);
    }

    @Override
    public RequestMatcher getRequestMatcher() {
        return this.requestMatcher;
    }

    private static List<AuthenticationConverter> createDefaultAuthenticationConverters() {
        List<AuthenticationConverter> authenticationConverters = new ArrayList<>();

        authenticationConverters.add(new OAuth2TokenRevocationAuthenticationConverter());

        return authenticationConverters;
    }

    private static List<AuthenticationProvider> createDefaultAuthenticationProviders(HttpSecurity httpSecurity) {
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();

        OAuth2TokenRevocationAuthenticationProvider tokenRevocationAuthenticationProvider = new OAuth2TokenRevocationAuthenticationProvider(
            OAuth2ConfigurerUtils.getAuthorizationService(httpSecurity));
        authenticationProviders.add(tokenRevocationAuthenticationProvider);

        return authenticationProviders;
    }
}
