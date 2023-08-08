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

import cn.topiam.employee.protocol.code.configurer.AbstractConfigurer;
import cn.topiam.employee.protocol.jwt.authentication.JwtRequestAuthenticationTokenProvider;
import cn.topiam.employee.protocol.jwt.authorization.JwtAuthorizationService;
import cn.topiam.employee.protocol.jwt.endpoint.JwtAuthenticationEndpointFilter;
import static cn.topiam.employee.common.constant.ProtocolConstants.JwtEndpointConstants.IDP_JWT_SSO_INITIATOR;
import static cn.topiam.employee.common.constant.ProtocolConstants.JwtEndpointConstants.JWT_SSO_PATH;
import static cn.topiam.employee.protocol.code.util.ProtocolConfigUtils.getApplicationServiceLoader;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/7/5 21:58
 */
public class JwtAuthorizationEndpointConfigurer extends AbstractConfigurer {

    private RequestMatcher requestMatcher;

    public JwtAuthorizationEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    /**
     * init
     *
     * @param httpSecurity {@link HttpSecurity}
     */
    @Override
    public void init(HttpSecurity httpSecurity) {
        requestMatcher = new OrRequestMatcher(
            new AntPathRequestMatcher(IDP_JWT_SSO_INITIATOR, HttpMethod.POST.name()),
            new AntPathRequestMatcher(JWT_SSO_PATH, HttpMethod.GET.name()),
            new AntPathRequestMatcher(JWT_SSO_PATH, HttpMethod.POST.name()));
        httpSecurity.authenticationProvider(
            new JwtRequestAuthenticationTokenProvider(getApplicationServiceLoader(httpSecurity)));
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
        JwtAuthorizationService authorizationService = JwtAuthenticationUtils
            .getAuthorizationService(httpSecurity);
        JwtAuthenticationEndpointFilter initSingleSignOnEndpointFilter = new JwtAuthenticationEndpointFilter(
            requestMatcher, authenticationManager, authorizationService);
        httpSecurity.addFilterBefore(postProcess(initSingleSignOnEndpointFilter),
            AuthorizationFilter.class);
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
