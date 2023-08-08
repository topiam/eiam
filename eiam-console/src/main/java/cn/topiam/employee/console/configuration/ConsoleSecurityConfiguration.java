/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.configuration;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.authentication.common.jackjson.AuthenticationJacksonModule;
import cn.topiam.employee.common.constant.AuthorizeConstants;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.repository.account.OrganizationRepository;
import cn.topiam.employee.common.repository.account.UserElasticSearchRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.setting.AdministratorRepository;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.console.handler.*;
import cn.topiam.employee.console.listener.ConsoleAuthenticationFailureEventListener;
import cn.topiam.employee.console.listener.ConsoleAuthenticationSuccessEventListener;
import cn.topiam.employee.console.listener.ConsoleLogoutSuccessEventListener;
import cn.topiam.employee.console.listener.ConsoleSessionInformationExpiredStrategy;
import cn.topiam.employee.core.dynamic.UserSyncTask;
import cn.topiam.employee.core.security.form.FormLoginSecretFilter;
import cn.topiam.employee.support.autoconfiguration.SupportProperties;
import cn.topiam.employee.support.geo.GeoLocationService;
import cn.topiam.employee.support.jackjson.SupportJackson2Module;
import cn.topiam.employee.support.security.authentication.WebAuthenticationDetailsSource;
import cn.topiam.employee.support.security.csrf.SpaCsrfTokenRequestHandler;

import lombok.RequiredArgsConstructor;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK;
import static org.springframework.web.cors.CorsConfiguration.ALL;

import static cn.topiam.employee.common.constant.AuthorizeConstants.FE_LOGIN;
import static cn.topiam.employee.common.constant.AuthorizeConstants.FORM_LOGIN;
import static cn.topiam.employee.common.constant.ConfigBeanNameConstants.DEFAULT_SECURITY_FILTER_CHAIN;
import static cn.topiam.employee.common.constant.SessionConstants.CURRENT_STATUS;
import static cn.topiam.employee.core.endpoint.security.PublicSecretEndpoint.PUBLIC_SECRET_PATH;
import static cn.topiam.employee.core.setting.constant.SecuritySettingConstants.*;
import static cn.topiam.employee.protocol.code.util.ProtocolConfigUtils.getAuthenticationDetailsSource;
import static cn.topiam.employee.support.constant.EiamConstants.*;

/**
 * ConsoleSecurityConfiguration
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2019/9/27 22:54
 */
@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class ConsoleSecurityConfiguration implements BeanClassLoaderAware {

    /**
     * webSecurityCustomizer
     *
     * @return {@link WebSecurityCustomizer} WebSecurityCustomizer
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            new AntPathRequestMatcher("/css/**", HttpMethod.GET.name()),
            new AntPathRequestMatcher("/js/**", HttpMethod.GET.name()),
            new AntPathRequestMatcher("/webjars/**", HttpMethod.GET.name()),
            new AntPathRequestMatcher("/images/**", HttpMethod.GET.name()),
            new AntPathRequestMatcher("/favicon.ico", HttpMethod.GET.name()));
    }

    /**
     * SecurityFilterChain
     *
     * @param httpSecurity {@link  HttpSecurity}
     * @return {@link  SecurityFilterChain}
     * @throws Exception Exception
     */
    @RefreshScope
    @Bean(name = DEFAULT_SECURITY_FILTER_CHAIN)
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // @formatter:off
        // 系统配置
        httpSecurity
                .securityMatcher(API_PATH+"/**")
                //认证请求
                .authorizeHttpRequests(authorizeHttpRequests())
                //安全上下文
                .securityContext(securityContext())
                //表单登录配置
                .formLogin(withFormLoginConfigurerDefaults(httpSecurity))
                //x509
                .x509(withDefaults())
                //异常处理
                .exceptionHandling(withExceptionConfigurerDefaults())
                //记住我
                .rememberMe(withRememberMeConfigurerDefaults(settingRepository))
                //CSRF
                .csrf(withCsrfConfigurerDefaults())
                //headers
                .headers(withHeadersConfigurerDefaults(settingRepository))
                //cors
                .cors(withCorsConfigurerDefaults())
                //退出配置
                .logout(withLogoutConfigurerDefaults())
                //会话管理器
                .sessionManagement(withSessionManagementConfigurerDefaults(settingRepository))
                //表单登录解密过滤器
                .addFilterBefore(new FormLoginSecretFilter(), UsernamePasswordAuthenticationFilter.class);
        // @formatter:on
        return httpSecurity.build();
    }

    /**
     * 认证请求
     *
     * @return {@link AuthorizeHttpRequestsConfigurer}
     */
    public Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authorizeHttpRequests() {
        //@formatter:off
        return registry -> {
            registry.requestMatchers(new AntPathRequestMatcher(CURRENT_STATUS, HttpMethod.GET.name())).permitAll();
            registry.requestMatchers(new AntPathRequestMatcher(PUBLIC_SECRET_PATH, HttpMethod.GET.name())).permitAll();
            registry.anyRequest().authenticated();
        };
        //@formatter:on
    }

    /**
     *  安全上下文
     *
     * @return {@link SecurityContextConfigurer}
     */
    public Customizer<SecurityContextConfigurer<HttpSecurity>> securityContext() {
        return configurer -> configurer.requireExplicitSave(false);
    }

    /**
     * session 管理器
     *
     * @return {@link SessionManagementConfigurer}
     */
    public Customizer<SessionManagementConfigurer<HttpSecurity>> withSessionManagementConfigurerDefaults(SettingRepository settingRepository) {
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
    public Customizer<LogoutConfigurer<HttpSecurity>> withLogoutConfigurerDefaults() {
        return configurer -> {
            configurer.logoutUrl(AuthorizeConstants.LOGOUT)
                .logoutSuccessHandler(new ConsoleLogoutSuccessHandler()).permitAll();
        };
    }

    /**
     * headers 过滤器
     *
     * @param settingRepository {@link SettingRepository}
     * @return {@link HeadersConfigurer}
     */
    public Customizer<HeadersConfigurer<HttpSecurity>> withHeadersConfigurerDefaults(SettingRepository settingRepository) {
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
     * Cors 过滤器
     *
     * @return {@link HeadersConfigurer}
     */
    public Customizer<CorsConfigurer<HttpSecurity>> withCorsConfigurerDefaults() {
        CorsConfiguration configuration = new CorsConfiguration()
            .setAllowedOriginPatterns(Lists.newArrayList(ALL)).applyPermitDefaultValues();
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return configurer -> configurer.configurationSource(source);
    }

    /**
     * 异常处理器
     *
     * @return {@link ExceptionHandlingConfigurer}
     */
    public Customizer<ExceptionHandlingConfigurer<HttpSecurity>> withExceptionConfigurerDefaults() {
        return configurer -> {
            configurer.authenticationEntryPoint(new ConsoleAuthenticationEntryPoint())
                .accessDeniedHandler(new ConsoleAccessDeniedHandler());
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
            csrf.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler());
            csrf.ignoringRequestMatchers(ignoringRequestMatchers).csrfTokenRepository(repository);
        };
    }

    /**
     * form
     *
     * @return {@link FormLoginConfigurer}
     */
    public Customizer<FormLoginConfigurer<HttpSecurity>> withFormLoginConfigurerDefaults(HttpSecurity httpSecurity) {
        // @formatter:off
        return configurer -> {
            configurer.loginPage(FE_LOGIN)
            .loginProcessingUrl(FORM_LOGIN)
            .successHandler(new ConsoleAuthenticationSuccessHandler(administratorRepository,  auditEventPublish))
            .failureHandler(new ConsoleAuthenticationFailureHandler())
            .authenticationDetailsSource(getAuthenticationDetailsSource(httpSecurity));
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
        return new ConsoleAuthenticationSuccessEventListener();
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

    @Bean
    @ConditionalOnMissingBean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(SupportJackson2Module.getModules(this.loader));
        mapper.registerModules(new AuthenticationJacksonModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return new GenericJackson2JsonRedisSerializer(mapper);
    }

    /**
     * 同步es用户数据定时任务
     *
     * @param supportProperties {@link SupportProperties}
     * @param userElasticSearchRepository {@link UserElasticSearchRepository}
     * @param userRepository {@link UserRepository}
     * @param organizationRepository {@link OrganizationRepository}
     * @return {@link UserSyncTask}
     */
    @Bean
    public UserSyncTask userSyncTask(SupportProperties supportProperties,
                                     UserElasticSearchRepository userElasticSearchRepository,
                                     UserRepository userRepository,
                                     OrganizationRepository organizationRepository) {
        return new UserSyncTask(supportProperties, userElasticSearchRepository, userRepository,
            organizationRepository);
    }

    /**
     * WebAuthenticationDetailsSource
     *
     * @param geoLocationService {@link GeoLocationService}
     * @return {@link WebAuthenticationDetailsSource}
     */
    @Bean
    public WebAuthenticationDetailsSource authenticationDetailsSource(GeoLocationService geoLocationService) {
        return new WebAuthenticationDetailsSource(geoLocationService);
    }

    private ClassLoader loader;

    @Override
    public void setBeanClassLoader(@NonNull ClassLoader classLoader) {
        this.loader = classLoader;
    }

    /**
     * AdministratorRepository
     */
    private final AdministratorRepository administratorRepository;

    /**
     * SettingRepository
     */
    private final SettingRepository       settingRepository;

    /**
     * AuditEventPublish
     */
    private final AuditEventPublish       auditEventPublish;

}
