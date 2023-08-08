/*
 * eiam-portal - Employee Identity and Access Management
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
package cn.topiam.employee.portal.configuration.security;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.protocol.form.authentication.FormAuthenticationFailureEventListener;
import cn.topiam.employee.protocol.form.authentication.FormAuthenticationSuccessEventListener;
import cn.topiam.employee.protocol.form.configurers.FormAuthorizationServerConfigurer;
import static cn.topiam.employee.common.constant.ConfigBeanNameConstants.FORM_PROTOCOL_SECURITY_FILTER_CHAIN;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/7/3 21:15
 */
@AutoConfigureBefore(PortalSecurityConfiguration.class)
@Configuration(proxyBeanMethods = false)
public class FormProtocolSecurityConfiguration extends AbstractSecurityConfiguration {

    public FormProtocolSecurityConfiguration(SettingRepository settingRepository) {
        super(settingRepository);
    }

    /**
     * FormProtocolSecurityFilterChain
     *
     * @param httpSecurity {@link HttpSecurity}
     * @return {@link SecurityFilterChain}
     * @throws Exception Exception
     */
    @Bean(value = FORM_PROTOCOL_SECURITY_FILTER_CHAIN)
    @RefreshScope
    public SecurityFilterChain formProtocolSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //@formatter:off
        httpSecurity.getSharedObject(AuthenticationManagerBuilder.class).parentAuthenticationManager(null);
        //Form IDP 配置
        FormAuthorizationServerConfigurer configurer = new FormAuthorizationServerConfigurer();
        RequestMatcher endpointsMatcher = configurer.getEndpointsMatcher();
        httpSecurity.securityMatcher(endpointsMatcher)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
                //安全上下文
                .securityContext(securityContext())
                //CSRF
                .csrf(withCsrfConfigurerDefaults(endpointsMatcher))
                //headers
                .headers(withHeadersConfigurerDefaults())
                //cors
                .cors(withCorsConfigurerDefaults())
                //会话管理器
                .sessionManagement(withSessionManagementConfigurerDefaults())
                .apply(configurer);
        return httpSecurity.build();
        //@formatter:on
    }

    /**
     * Form 成功监听
     *
     * @param auditEventPublish {@link AuditEventPublish}
     * @return {@link AuthenticationSuccessEvent}
     */
    @Bean
    public ApplicationListener<AuthenticationSuccessEvent> formAuthenticationSuccessEventListener(AuditEventPublish auditEventPublish) {
        return new FormAuthenticationSuccessEventListener(auditEventPublish);
    }

    /**
     * Form 失败监听
     *
     * @param auditEventPublish {@link AuditEventPublish}
     * @return {@link FormAuthenticationFailureEventListener}
     */
    @Bean
    public ApplicationListener<AbstractAuthenticationFailureEvent> formAuthenticationFailureEventListener(AuditEventPublish auditEventPublish) {
        return new FormAuthenticationFailureEventListener(auditEventPublish);
    }

}
