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

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.google.common.collect.Lists;

import cn.topiam.employee.common.constant.AuthorizeConstants;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.core.setting.constant.SecuritySettingConstants;
import cn.topiam.employee.portal.authentication.AuthenticationTrustResolverImpl;
import cn.topiam.employee.portal.handler.PortalAccessDeniedHandler;
import cn.topiam.employee.portal.handler.PortalAuthenticationEntryPoint;
import cn.topiam.employee.portal.handler.PortalLogoutSuccessHandler;
import cn.topiam.employee.portal.listener.PortalSessionInformationExpiredStrategy;
import cn.topiam.employee.support.security.csrf.SpaCsrfTokenRequestHandler;
import static org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK;
import static org.springframework.web.cors.CorsConfiguration.ALL;

import static cn.topiam.employee.core.setting.constant.SecuritySettingConstants.*;
import static cn.topiam.employee.support.constant.EiamConstants.DEFAULT_CSRF_COOKIE_NAME;
import static cn.topiam.employee.support.constant.EiamConstants.DEFAULT_CSRF_HEADER_NAME;

/**
 * AbstractConfiguration
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/7/3 21:08
 */
public class AbstractSecurityConfiguration {

    /**
     * Cors 过滤器
     *
     * @return {@link HeadersConfigurer}
     */
    public Customizer<CorsConfigurer<HttpSecurity>> withCorsConfigurerDefaults() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Lists.newArrayList(ALL));
        configuration.applyPermitDefaultValues();
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return configurer -> configurer.configurationSource(source);
    }

    /**
     * session 管理器
     *
     * @return {@link SessionManagementConfigurer}
     */
    public Customizer<SessionManagementConfigurer<HttpSecurity>> withSessionManagementConfigurerDefaults() {
        //@formatter:off
        SettingEntity setting = settingRepository.findByName(SECURITY_SESSION_MAXIMUM);
        return configurer -> {
            configurer.sessionFixation().changeSessionId();
            //用户并发
            String defaultSessionMaximum = SecuritySettingConstants.SECURITY_BASIC_DEFAULT_SETTINGS
                .get(SECURITY_SESSION_MAXIMUM);
            String sessionMaximum = Objects.isNull(setting) ? defaultSessionMaximum
                : "0".equals(setting.getValue()) ? defaultSessionMaximum : setting.getValue();
            configurer.maximumSessions(Integer.parseInt(sessionMaximum))
                .expiredSessionStrategy(new PortalSessionInformationExpiredStrategy());
        };
        //@formatter:on
    }

    /**
     * session 退出过滤器
     *
     * @return {@link LogoutConfigurer}
     */
    public Customizer<LogoutConfigurer<HttpSecurity>> withLogoutConfigurerDefaults() {
        return configurer -> {
            configurer.logoutUrl(AuthorizeConstants.LOGOUT);
            configurer.logoutSuccessHandler(new PortalLogoutSuccessHandler());
            configurer.permitAll();
        };
    }

    /**
     * headers 过滤器
     *
     * @return {@link HeadersConfigurer}
     */
    public Customizer<HeadersConfigurer<HttpSecurity>> withHeadersConfigurerDefaults() {
        List<SettingEntity> list = settingRepository.findByNameIn(SECURITY_DEFENSE_POLICY_KEY);
        // 转MAP
        Map<String, String> map = list.stream().collect(Collectors.toMap(SettingEntity::getName,
            SettingEntity::getValue, (key1, key2) -> key2));
        //内容安全策略
        String contentSecurityPolicy = map
            .containsKey(SECURITY_DEFENSE_POLICY_CONTENT_SECURITY_POLICY)
                ? map.get(SECURITY_DEFENSE_POLICY_CONTENT_SECURITY_POLICY).replace("\n", "")
                    .replace("\r\n", "")
                : SECURITY_DEFENSE_POLICY_DEFAULT_SETTINGS
                    .get(SECURITY_DEFENSE_POLICY_CONTENT_SECURITY_POLICY);

        //@formatter:off
        return configurer -> {
            configurer.xssProtection(xssProtection -> xssProtection.headerValue(ENABLED_MODE_BLOCK))
                    .contentSecurityPolicy(config-> config.policyDirectives(contentSecurityPolicy))
                    .referrerPolicy(referrerPolicyConfig -> referrerPolicyConfig.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                    .contentTypeOptions(contentTypeOptionsConfig-> {})
                    .permissionsPolicy(permissionsPolicyConfig -> permissionsPolicyConfig.policy("camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"));
        };
        //@formatter:on
    }

    /**
     * 异常处理器
     *
     * @return {@link ExceptionHandlingConfigurer}
     */
    public Customizer<ExceptionHandlingConfigurer<HttpSecurity>> withExceptionConfigurerDefaults() {
        return configurer -> {
            configurer.authenticationEntryPoint(new PortalAuthenticationEntryPoint());
            configurer.accessDeniedHandler(new PortalAccessDeniedHandler());
            configurer
                .withObjectPostProcessor(new ObjectPostProcessor<ExceptionTranslationFilter>() {
                    @Override
                    public <O extends ExceptionTranslationFilter> O postProcess(O filter) {
                        filter
                            .setAuthenticationTrustResolver(new AuthenticationTrustResolverImpl());
                        return filter;
                    }
                });
        };
    }

    /**
     * withRememberMeConfigurerDefaults
     *
     * @return {@link RememberMeConfigurer}
     */
    public Customizer<RememberMeConfigurer<HttpSecurity>> withRememberMeConfigurerDefaults() {
        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(false);
        SettingEntity setting = settingRepository.findByName(SECURITY_BASIC_REMEMBER_ME_VALID_TIME);
        String rememberMeValiditySeconds = Objects.isNull(setting)
            ? SecuritySettingConstants.SECURITY_BASIC_DEFAULT_SETTINGS
                .get(SECURITY_BASIC_REMEMBER_ME_VALID_TIME)
            : setting.getValue();
        rememberMeServices.setValiditySeconds(Integer.parseInt(rememberMeValiditySeconds));
        return configurer -> configurer.rememberMeServices(rememberMeServices);
    }

    /**
     * csrf
     *
     * @return {@link CsrfConfigurer}
     */
    public Customizer<CsrfConfigurer<HttpSecurity>> withCsrfConfigurerDefaults(RequestMatcher... requestMatcher) {
        return csrf -> {
            CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
            repository.setCookieName(DEFAULT_CSRF_COOKIE_NAME);
            repository.setHeaderName(DEFAULT_CSRF_HEADER_NAME);
            csrf.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler());
            csrf.csrfTokenRepository(repository);
            csrf.ignoringRequestMatchers(requestMatcher);
        };
    }

    /**
     *  安全上下文
     *
     * @return {@link SecurityContextConfigurer}
     */
    public Customizer<SecurityContextConfigurer<HttpSecurity>> securityContext() {
        return configurer -> configurer.requireExplicitSave(false);
    }

    private final SettingRepository settingRepository;

    public AbstractSecurityConfiguration(SettingRepository settingRepository) {
        Assert.notNull(settingRepository, "The settingRepository cannot be null");
        this.settingRepository = settingRepository;
    }

}
