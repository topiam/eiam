/*
 * eiam-protocol-form - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.form.configurers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.protocol.code.EndpointMatcher;
import cn.topiam.employee.protocol.code.UnauthorizedAuthenticationEntryPoint;
import cn.topiam.employee.protocol.code.configurer.AbstractConfigurer;
import cn.topiam.employee.protocol.form.context.FormAuthorizationServerContextFilter;
import static cn.topiam.employee.protocol.code.configurer.AuthenticationUtils.getApplicationServiceLoader;

/**
 * 认证配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/01/14 22:58
 */
public final class FormAuthorizationServerConfigurer extends
                                                     AbstractHttpConfigurer<FormAuthorizationServerConfigurer, HttpSecurity> {
    private final Map<Class<? extends AbstractConfigurer>, AbstractConfigurer> configurers      = createConfigurers();

    /**
     * 端点匹配器
     */
    private final List<EndpointMatcher>                                        endpointMatchers = new ArrayList<>();

    public RequestMatcher getEndpointsMatcher() {
        // Return a deferred RequestMatcher
        // since endpointsMatcher is constructed in init(HttpSecurity).
        return request -> new OrRequestMatcher(
            endpointMatchers.stream().map(EndpointMatcher::getRequestMatcher).toList())
            .matches(request);
    }

    @Override
    public void init(HttpSecurity httpSecurity) throws Exception {
        this.configurers.values().forEach(configurer -> {
            configurer.init(httpSecurity);
            endpointMatchers.add(configurer.getEndpointMatcher());
        });
        httpSecurity.exceptionHandling(exceptionHandling -> {
            if (exceptionHandling != null) {
                //身份验证入口点
                exceptionHandling
                    .authenticationEntryPoint(new UnauthorizedAuthenticationEntryPoint());
            }
        });
    }

    @Override
    public void configure(HttpSecurity httpSecurity) {
        //@formatter:off
        //Authorization server context filter
        ApplicationServiceLoader applicationServiceLoader = getApplicationServiceLoader(httpSecurity);
        FormAuthorizationServerContextFilter authorizationServerContextFilter = new FormAuthorizationServerContextFilter(this.endpointMatchers, applicationServiceLoader);
        httpSecurity.addFilterAfter(postProcess(authorizationServerContextFilter), SecurityContextHolderFilter.class);
        this.configurers.values().forEach(configurer -> configurer.configure(httpSecurity));
        //@formatter:on
    }

    /**
     * createConfigurers
     *
     * @return {@link AbstractConfigurer}
     */
    private Map<Class<? extends AbstractConfigurer>, AbstractConfigurer> createConfigurers() {
        //@formatter:off
        Map<Class<? extends AbstractConfigurer>, AbstractConfigurer> configurers = new LinkedHashMap<>();
        configurers.put(FormAuthenticationEndpointConfigurer.class, new FormAuthenticationEndpointConfigurer(this::postProcess));
        //@formatter:on
        return configurers;
    }

}
