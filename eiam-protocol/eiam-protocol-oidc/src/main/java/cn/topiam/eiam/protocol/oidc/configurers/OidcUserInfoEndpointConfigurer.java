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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.http.converter.OAuth2ErrorHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.oidc.web.OidcUserInfoEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.common.constant.ProtocolConstants;
import cn.topiam.employee.protocol.code.configurer.AbstractConfigurer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.eiam.protocol.oidc.constant.OidcProtocolConstants.OIDC_ERROR_URI;

/**
 * OIDC 用户信息端点配置器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/27 21:07
 */
public final class OidcUserInfoEndpointConfigurer extends AbstractConfigurer {

    /**
     * 请求匹配器
     */
    private RequestMatcher                     requestMatcher;

    private final AuthenticationFailureHandler authenticationFailureHandler = new AuthenticationFailureHandler() {

                                                                                private final HttpMessageConverter<OAuth2Error> errorHttpResponseConverter = new OAuth2ErrorHttpMessageConverter();

                                                                                @Override
                                                                                public void onAuthenticationFailure(HttpServletRequest request,
                                                                                                                    HttpServletResponse response,
                                                                                                                    AuthenticationException exception) throws IOException,
                                                                                                                                                       ServletException {
            //@formatter:off
            OAuth2Error error = ((OAuth2AuthenticationException) exception).getError();
            HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
            if (error.getErrorCode().equals(OAuth2ErrorCodes.INVALID_TOKEN)) {
                httpStatus = HttpStatus.UNAUTHORIZED;
            } else if (error.getErrorCode().equals(OAuth2ErrorCodes.INSUFFICIENT_SCOPE)) {
                httpStatus = HttpStatus.FORBIDDEN;
            }
            OAuth2Error responseError=new OAuth2Error(error.getErrorCode(),error.getDescription(),OIDC_ERROR_URI);
            ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
            httpResponse.setStatusCode(httpStatus);
            this.errorHttpResponseConverter.write(responseError, null, httpResponse);
            //@formatter:on
                                                                                }
                                                                            };

    OidcUserInfoEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    @Override
    public void init(HttpSecurity httpSecurity) {
        this.requestMatcher = new OrRequestMatcher(new AntPathRequestMatcher(
            ProtocolConstants.OidcEndpointConstants.OIDC_USER_INFO_ENDPOINT, HttpMethod.GET.name()),
            new AntPathRequestMatcher(
                ProtocolConstants.OidcEndpointConstants.OIDC_USER_INFO_ENDPOINT,
                HttpMethod.POST.name()));

        //认证转换器
        List<AuthenticationProvider> authenticationProviders = createDefaultAuthenticationProviders(
            httpSecurity);
        authenticationProviders.forEach(authenticationProvider -> httpSecurity
            .authenticationProvider(postProcess(authenticationProvider)));
    }

    @Override
    public void configure(HttpSecurity httpSecurity) {
        AuthenticationManager authenticationManager = httpSecurity
            .getSharedObject(AuthenticationManager.class);

        OidcUserInfoEndpointFilter oidcUserInfoEndpointFilter = new OidcUserInfoEndpointFilter(
            authenticationManager, ProtocolConstants.OidcEndpointConstants.OIDC_USER_INFO_ENDPOINT);
        oidcUserInfoEndpointFilter
            .setAuthenticationFailureHandler(this.authenticationFailureHandler);
        //认证转换器
        List<AuthenticationConverter> authenticationConverters = createDefaultAuthenticationConverters();
        oidcUserInfoEndpointFilter.setAuthenticationConverter(
            new DelegatingAuthenticationConverter(authenticationConverters));
        httpSecurity.addFilterAfter(postProcess(oidcUserInfoEndpointFilter),
            AuthorizationFilter.class);
    }

    /**
     * 创建默认身份验证转换器
     *
     * @return {@link List}
     */
    private static List<AuthenticationConverter> createDefaultAuthenticationConverters() {
        List<AuthenticationConverter> authenticationConverters = new ArrayList<>();
        authenticationConverters.add((request) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return new OidcUserInfoAuthenticationToken(authentication);
        });
        return authenticationConverters;
    }

    /**
     * 创建默认身份验证提供程序
     *
     * @param httpSecurity {@link HttpSecurity}
     * @return {@link List}
     */
    private List<AuthenticationProvider> createDefaultAuthenticationProviders(HttpSecurity httpSecurity) {
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
        OidcUserInfoAuthenticationProvider oidcUserInfoAuthenticationProvider = new OidcUserInfoAuthenticationProvider(
            OAuth2ConfigurerUtils.getAuthorizationService(httpSecurity));
        authenticationProviders.add(oidcUserInfoAuthenticationProvider);
        return authenticationProviders;
    }

    /**
     * 获取请求匹配器
     *
     * @return {@link RequestMatcher}
     */
    @Override
    public RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }
}
