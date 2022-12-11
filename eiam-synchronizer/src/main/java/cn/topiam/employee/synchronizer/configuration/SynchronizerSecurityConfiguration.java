/*
 * eiam-synchronizer - Employee Identity and Access Management Program
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
package cn.topiam.employee.synchronizer.configuration;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import static cn.topiam.employee.common.constants.ConfigBeanNameConstants.DEFAULT_SECURITY_FILTER_CHAIN;
import static cn.topiam.employee.synchronizer.constants.SynchronizerConstants.EVENT_RECEIVE_PATH;

/**
 * SynchronizerSecurityConfiguration
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2019/9/27 22:54
 */
@EnableWebSecurity
@RequiredArgsConstructor
public class SynchronizerSecurityConfiguration {

    /**
     * SecurityFilterChain
     *
     * @param http {@link  HttpSecurity}
     * @return {@link  SecurityFilterChain}
     * @throws Exception Exception
     */
    @RefreshScope
    @Bean(name = DEFAULT_SECURITY_FILTER_CHAIN)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                //认证请求
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry.antMatchers(EVENT_RECEIVE_PATH+"/*").permitAll()
                        .antMatchers(webEndpointProperties.getBasePath()+"/**").permitAll().anyRequest().authenticated())
                //csrf过滤器
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.ignoringAntMatchers(EVENT_RECEIVE_PATH+"/*"));
        // @formatter:on
        return http.build();
    }

    private final WebEndpointProperties webEndpointProperties;
}
