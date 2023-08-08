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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.protocol.code.UnauthorizedAuthenticationEntryPoint;
import cn.topiam.employee.protocol.code.configurer.AbstractConfigurer;
import cn.topiam.employee.protocol.jwt.context.JwtAuthorizationServerContextFilter;

import jakarta.servlet.http.HttpServletRequest;
import static cn.topiam.employee.protocol.code.util.ProtocolConfigUtils.getApplicationServiceLoader;

/**
 * 认证配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/01/14 22:58
 */
public final class JwtAuthorizationServerConfigurer extends
                                                    AbstractHttpConfigurer<JwtAuthorizationServerConfigurer, HttpSecurity> {
    private final Map<Class<? extends AbstractConfigurer>, AbstractConfigurer> configurers = createConfigurers();

    /**
     * 端点匹配器
     */
    private RequestMatcher                                                     endpointsMatcher;

    /**
     * Returns a {@link RequestMatcher} for the authorization server endpoints.
     *
     * @return a {@link RequestMatcher} for the authorization server endpoints
     */
    public RequestMatcher getEndpointsMatcher() {
        // Return a deferred RequestMatcher
        // since endpointsMatcher is constructed in init(HttpSecurity).
        return new RequestMatcher() {
            @Override
            public boolean matches(HttpServletRequest request) {
                return endpointsMatcher.matches(request);
            }

            @Override
            public MatchResult matcher(HttpServletRequest request) {
                return endpointsMatcher.matcher(request);
            }
        };
    }

    @Override
    public void init(HttpSecurity httpSecurity) throws Exception {
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        this.configurers.values().forEach(configurer -> {
            configurer.init(httpSecurity);
            requestMatchers.add(configurer.getRequestMatcher());
        });
        this.endpointsMatcher = new OrRequestMatcher(requestMatchers);

        ExceptionHandlingConfigurer<HttpSecurity> exceptionHandling = httpSecurity
            .getConfigurer(ExceptionHandlingConfigurer.class);
        if (exceptionHandling != null) {
            //身份验证入口点
            exceptionHandling.authenticationEntryPoint(new UnauthorizedAuthenticationEntryPoint());
        }
    }

    @Override
    public void configure(HttpSecurity httpSecurity) {
        this.configurers.values().forEach(configurer -> configurer.configure(httpSecurity));
        //Authorization server context filter
        ApplicationServiceLoader applicationServiceLoader = getApplicationServiceLoader(
            httpSecurity);
        JwtAuthorizationServerContextFilter authorizationServerContextFilter = new JwtAuthorizationServerContextFilter(
            getEndpointsMatcher(), applicationServiceLoader);
        httpSecurity.addFilterAfter(postProcess(authorizationServerContextFilter),
            SecurityContextHolderFilter.class);
    }

    /**
     * createConfigurers
     *
     * @return {@link AbstractConfigurer}
     */
    private Map<Class<? extends AbstractConfigurer>, AbstractConfigurer> createConfigurers() {
        //@formatter:off
        Map<Class<? extends AbstractConfigurer>, AbstractConfigurer> configurers = new LinkedHashMap<>();
        configurers.put(JwtAuthorizationEndpointConfigurer.class, new JwtAuthorizationEndpointConfigurer(this::postProcess));
        //@formatter:on
        return configurers;
    }

}
