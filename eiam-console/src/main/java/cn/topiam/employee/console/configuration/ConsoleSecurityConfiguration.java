/*
 * eiam-console - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.console.configuration;

import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import cn.topiam.employee.common.constants.AuthorizeConstants;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.geo.GeoLocationService;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.console.security.handler.*;
import cn.topiam.employee.console.security.listener.ConsoleAuthenticationFailureEventListener;
import cn.topiam.employee.console.security.listener.ConsoleAuthenticationSuccessEventListener;
import cn.topiam.employee.console.security.listener.ConsoleLogoutSuccessEventListener;
import cn.topiam.employee.console.security.listener.ConsoleSessionInformationExpiredStrategy;
import cn.topiam.employee.core.endpoint.security.PublicSecretEndpoint;
import cn.topiam.employee.core.security.form.FormLoginSecretFilter;

import lombok.RequiredArgsConstructor;
import static org.springframework.boot.autoconfigure.security.StaticResourceLocation.*;
import static org.springframework.security.config.Customizer.withDefaults;

import static cn.topiam.employee.common.constants.AuthorizeConstants.FE_LOGIN;
import static cn.topiam.employee.common.constants.AuthorizeConstants.LOGIN_PATH;
import static cn.topiam.employee.common.constants.ConfigBeanNameConstants.DEFAULT_SECURITY_FILTER_CHAIN;
import static cn.topiam.employee.common.constants.SessionConstants.CURRENT_STATUS;
import static cn.topiam.employee.core.setting.constant.SecuritySettingConstants.*;
import static cn.topiam.employee.support.constant.EiamConstants.*;

/**
 * ConsoleSecurityConfiguration
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2019/9/27 22:54
 */
@EnableWebSecurity
@RequiredArgsConstructor
public class ConsoleSecurityConfiguration {
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
        // 系统配置
        http
                //认证请求
                .authorizeHttpRequests(authorizeRequests())
                //表单登录配置
                .formLogin(withFormLoginConfigurerDefaults())
                //x509
                .x509(withDefaults())
                //异常处理
                .exceptionHandling(withExceptionConfigurerDefaults())
                //记住我
                .rememberMe(withRememberMeConfigurerDefaults(settingRepository))
                //CSRF
                .csrf(withCsrfConfigurerDefaults())
                //headers
                .headers(withHeadersConfigurerDefaults())
                //cors
                .cors(withCorsConfigurerDefaults())
                //退出配置
                .logout(withLogoutConfigurerDefaults())
                //会话管理器
                .sessionManagement(withSessionManagementConfigurerDefaults(settingRepository));
        //表单登录解密过滤器
        http.addFilterBefore(new FormLoginSecretFilter(), UsernamePasswordAuthenticationFilter.class);
        // @formatter:on
        return http.build();
    }

    /**
     * 认证请求
     *
     * @return {@link AuthorizeHttpRequestsConfigurer}
     */
    public Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authorizeRequests() {
        //@formatter:off
        return registry -> {
            //静态资源
            registry.antMatchers(
                    CSS.getPatterns().collect(Collectors.joining()),
                    JAVA_SCRIPT.getPatterns().collect(Collectors.joining()),
                    IMAGES.getPatterns().collect(Collectors.joining()),
                    WEB_JARS.getPatterns().collect(Collectors.joining()),
                    FAVICON.getPatterns().collect(Collectors.joining())).permitAll();
            //当前会话状态
            registry.antMatchers(HttpMethod.GET, CURRENT_STATUS).permitAll();
            //公钥
            registry.antMatchers(HttpMethod.GET, PublicSecretEndpoint.PUBLIC_SECRET_PATH).permitAll();
            //发送OPT
            registry.antMatchers(HttpMethod.POST, LOGIN_PATH + "/opt/send").permitAll();
            //健康检查端点
            registry.antMatchers(webEndpointProperties.getBasePath()+"/**").permitAll();
            //其他全部认证
            registry.antMatchers(API_PATH+"/**").authenticated();
        };
    }

    /**
     * session 管理器
     *
     * @return {@link SessionManagementConfigurer}
     */
    public  Customizer<SessionManagementConfigurer<HttpSecurity>> withSessionManagementConfigurerDefaults(SettingRepository settingRepository) {
        SettingEntity setting = settingRepository.findByName(SECURITY_SESSION_MAXIMUM);
        return configurer -> {
            configurer.sessionFixation().changeSessionId();
            //用户并发
            String defaultSessionMaximum = SECURITY_BASIC_DEFAULT_SETTINGS
                    .get(SECURITY_SESSION_MAXIMUM);
            String sessionMaximum = Objects.isNull(setting) ? defaultSessionMaximum
                    : "0".equals(setting.getValue()) ? defaultSessionMaximum : setting.getValue();
            configurer.maximumSessions(Integer.parseInt(sessionMaximum))
                    .expiredSessionStrategy(new ConsoleSessionInformationExpiredStrategy());
        };
    }

    /**
     * session 退出过滤器
     *
     * @return {@link LogoutConfigurer}
     */
    public  Customizer<LogoutConfigurer<HttpSecurity>> withLogoutConfigurerDefaults() {
        return configurer -> {
            configurer.logoutUrl(AuthorizeConstants.LOGOUT);
            configurer.logoutSuccessHandler(new ConsoleLogoutSuccessHandler());
            configurer.permitAll();
        };
    }

    /**
     * headers 过滤器
     *
     * @return {@link HeadersConfigurer}
     */
    public  Customizer<HeadersConfigurer<HttpSecurity>> withHeadersConfigurerDefaults() {
        //@formatter:off
        return configurer -> {
            configurer.xssProtection(xssProtection -> xssProtection.block(false));
            configurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin);
            configurer.contentSecurityPolicy(
                    "default-src 'self' data:; " +
                            "frame-src 'self' login.dingtalk.com open.weixin.qq.com open.work.weixin.qq.com passport.feishu.cn data:; " +
                            "frame-ancestors 'self' https://eiam.topiam.cn data:; " +
                            "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com sf3-cn.feishucdn.com;" +
                            "style-src 'self' https://fonts.googleapis.com https://cdn.jsdelivr.net 'unsafe-inline'; " +
                            "img-src 'self' https://img.alicdn.com https://static-legacy.dingtalk.com  https://joeschmoe.io https://api.multiavatar.com data:; " +
                            "font-src 'self' https://fonts.gstatic.com data:; "+
                            "worker-src 'self' https://storage.googleapis.com blob:;");
            configurer.referrerPolicy(
                    ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN);
            configurer
                    .referrerPolicy(
                            ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                    .and().permissionsPolicy().policy(
                            "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()");
            configurer.frameOptions().deny();
        };
        //@formatter:on
    }

    /**
     * Cors 过滤器
     *
     * @return {@link HeadersConfigurer}
     */
    public Customizer<CorsConfigurer<HttpSecurity>> withCorsConfigurerDefaults() {
        return configurer -> configurer.configurationSource(new UrlBasedCorsConfigurationSource());
    }

    /**
     * 异常处理器
     *
     * @return {@link ExceptionHandlingConfigurer}
     */
    public Customizer<ExceptionHandlingConfigurer<HttpSecurity>> withExceptionConfigurerDefaults() {
        return configurer -> {
            configurer.authenticationEntryPoint(new ConsoleAuthenticationEntryPoint());
            configurer.accessDeniedHandler(new ConsoleAccessDeniedHandler());
        };
    }

    /**
     * withRememberMeConfigurerDefaults
     *
     * @return {@link RememberMeConfigurer}
     */
    public Customizer<RememberMeConfigurer<HttpSecurity>> withRememberMeConfigurerDefaults(SettingRepository settingRepository) {
        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(false);
        SettingEntity setting = settingRepository.findByName(SECURITY_BASIC_REMEMBER_ME_VALID_TIME);
        String rememberMeValiditySeconds = Objects.isNull(setting)
            ? SECURITY_BASIC_DEFAULT_SETTINGS.get(SECURITY_BASIC_REMEMBER_ME_VALID_TIME)
            : setting.getValue();
        rememberMeServices.setValiditySeconds(Integer.parseInt(rememberMeValiditySeconds));
        return configurer -> configurer.rememberMeServices(rememberMeServices);
    }

    /**
     * csrf
     *
     * @return {@link CsrfConfigurer}
     */
    public Customizer<CsrfConfigurer<HttpSecurity>> withCsrfConfigurerDefaults(RequestMatcher... ignoringRequestMatchers) {
        return csrf -> {
            CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
            repository.setCookieName(DEFAULT_CSRF_COOKIE_NAME);
            repository.setHeaderName(DEFAULT_CSRF_HEADER_NAME);
            csrf.ignoringRequestMatchers(ignoringRequestMatchers).csrfTokenRepository(repository);
        };
    }

    /**
     * form
     *
     * @return {@link FormLoginConfigurer}
     */
    public Customizer<FormLoginConfigurer<HttpSecurity>> withFormLoginConfigurerDefaults() {
        // @formatter:off
        return configurer -> {
            configurer.loginPage(FE_LOGIN);
            configurer.loginProcessingUrl(AuthorizeConstants.FORM_LOGIN);
            configurer.successHandler(new ConsoleAuthenticationSuccessHandler());
            configurer.failureHandler(new ConsoleAuthenticationFailureHandler());
        };
        // @formatter:on
    }

    /**
     * 身份验证成功事件监听器
     *
     * @return {@link  ConsoleAuthenticationSuccessEventListener}
     */
    @Bean
    @ConditionalOnMissingBean
    public ConsoleAuthenticationSuccessEventListener authenticationSuccessEventListener() {
        return new ConsoleAuthenticationSuccessEventListener(geoLocationService);
    }

    /**
     * 身份验证失败事件监听器
     *
     * @return {@link  ConsoleAuthenticationFailureEventListener}
     */
    @Bean
    @ConditionalOnMissingBean
    public ConsoleAuthenticationFailureEventListener authenticationFailureEventListener() {
        return new ConsoleAuthenticationFailureEventListener();
    }

    /**
     * 退出成功事件监听器
     *
     * @return {@link  ConsoleLogoutSuccessEventListener}
     */
    @Bean
    @ConditionalOnMissingBean
    public ConsoleLogoutSuccessEventListener logoutSuccessEventListener() {
        return new ConsoleLogoutSuccessEventListener();
    }

    private final SettingRepository     settingRepository;

    private final GeoLocationService    geoLocationService;

    private final WebEndpointProperties webEndpointProperties;
}
