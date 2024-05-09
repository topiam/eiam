/*
 * eiam-protocol-jwt - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.jwt.configurers;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.protocol.code.EndpointMatcher;
import cn.topiam.employee.protocol.code.configurer.AbstractConfigurer;
import cn.topiam.employee.protocol.code.configurer.AuthenticationUtils;
import cn.topiam.employee.protocol.jwt.JwtAuthorizationService;
import cn.topiam.employee.protocol.jwt.authentication.JwtLoginAuthenticationTokenProvider;
import cn.topiam.employee.protocol.jwt.endpoint.JwtLoginAuthenticationEndpointFilter;
import static cn.topiam.employee.common.constant.ProtocolConstants.JwtEndpointConstants.IDP_JWT_SSO_INITIATOR;
import static cn.topiam.employee.common.constant.ProtocolConstants.JwtEndpointConstants.JWT_SSO_PATH;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/7/5 21:58
 */
public class JwtLoginAuthorizationEndpointConfigurer extends AbstractConfigurer {

    private RequestMatcher requestMatcher;

    public JwtLoginAuthorizationEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    /**
     * init
     *
     * @param httpSecurity {@link HttpSecurity}
     */
    @Override
    public void init(HttpSecurity httpSecurity) {
        JwtAuthorizationService authorizationService = JwtAuthenticationUtils
            .getAuthorizationService(httpSecurity);
        requestMatcher = new OrRequestMatcher(
            new AntPathRequestMatcher(IDP_JWT_SSO_INITIATOR, HttpMethod.POST.name()),
            new AntPathRequestMatcher(IDP_JWT_SSO_INITIATOR, HttpMethod.GET.name()),
            new AntPathRequestMatcher(JWT_SSO_PATH, HttpMethod.GET.name()),
            new AntPathRequestMatcher(JWT_SSO_PATH, HttpMethod.POST.name()));
        httpSecurity
            .authenticationProvider(new JwtLoginAuthenticationTokenProvider(authorizationService));
    }

    /**
     * configure
     *
     * @param httpSecurity {@link HttpSecurity}
     */
    @Override
    public void configure(HttpSecurity httpSecurity) {
        AuthenticationManager authenticationManager = httpSecurity
            .getSharedObject(AuthenticationManager.class);
        JwtLoginAuthenticationEndpointFilter jwtLoginAuthenticationEndpointFilter = new JwtLoginAuthenticationEndpointFilter(
            requestMatcher, authenticationManager);
        jwtLoginAuthenticationEndpointFilter.setAuthenticationDetailsSource(
            AuthenticationUtils.getAuthenticationDetailsSource(httpSecurity));
        httpSecurity.addFilterBefore(postProcess(jwtLoginAuthenticationEndpointFilter),
            AuthorizationFilter.class);
    }

    @Override
    public EndpointMatcher getEndpointMatcher() {
        return new EndpointMatcher(this.requestMatcher, true);
    }
}
