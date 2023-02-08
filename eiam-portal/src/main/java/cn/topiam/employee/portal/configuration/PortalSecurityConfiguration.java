/*
 * eiam-portal - Employee Identity and Access Management Program
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
package cn.topiam.employee.portal.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.EiamOAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.authentication.captcha.CaptchaValidator;
import cn.topiam.employee.authentication.captcha.filter.CaptchaValidatorFilter;
import cn.topiam.employee.authentication.common.service.UserIdpService;
import cn.topiam.employee.authentication.dingtalk.configurer.DingtalkOAuth2AuthenticationConfigurer;
import cn.topiam.employee.authentication.dingtalk.configurer.DingtalkScanCodeAuthenticationConfigurer;
import cn.topiam.employee.authentication.feishu.configurer.FeiShuScanCodeAuthenticationConfigurer;
import cn.topiam.employee.authentication.mfa.MfaAuthenticationConfigurer;
import cn.topiam.employee.authentication.mfa.MfaAuthenticationHandler;
import cn.topiam.employee.authentication.qq.configurer.QqOauthAuthenticationConfigurer;
import cn.topiam.employee.authentication.sms.SmsAuthenticationConfigurer;
import cn.topiam.employee.authentication.sms.SmsAuthenticationFilter;
import cn.topiam.employee.authentication.wechat.configurer.WeChatScanCodeAuthenticationConfigurer;
import cn.topiam.employee.authentication.wechatwork.configurer.WeChatWorkScanCodeAuthenticationConfigurer;
import cn.topiam.employee.common.constants.AuthorizeConstants;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.geo.GeoLocationService;
import cn.topiam.employee.common.repository.account.UserIdpRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.core.endpoint.security.PublicSecretEndpoint;
import cn.topiam.employee.core.message.mail.MailMsgEventPublish;
import cn.topiam.employee.core.message.sms.SmsMsgEventPublish;
import cn.topiam.employee.core.security.authentication.AuthenticationTrustResolverImpl;
import cn.topiam.employee.core.security.form.FormLoginSecretFilter;
import cn.topiam.employee.core.security.otp.OtpContextHelp;
import cn.topiam.employee.core.security.password.task.PasswordExpireTask;
import cn.topiam.employee.core.security.password.task.impl.PasswordExpireLockTask;
import cn.topiam.employee.core.security.password.task.impl.PasswordExpireWarnTask;
import cn.topiam.employee.core.security.savedredirect.LoginRedirectParameterFilter;
import cn.topiam.employee.core.security.task.UserExpireLockTask;
import cn.topiam.employee.core.security.task.UserUnlockTask;
import cn.topiam.employee.core.setting.constant.SecuritySettingConstants;
import cn.topiam.employee.portal.handler.*;
import cn.topiam.employee.portal.idp.IdpRedirectParameterMatcher;
import cn.topiam.employee.portal.idp.bind.IdpAuthenticationConfigurer;
import cn.topiam.employee.portal.listener.PortalAuthenticationFailureEventListener;
import cn.topiam.employee.portal.listener.PortalAuthenticationSuccessEventListener;
import cn.topiam.employee.portal.listener.PortalLogoutSuccessEventListener;
import cn.topiam.employee.portal.listener.PortalSessionInformationExpiredStrategy;
import cn.topiam.employee.protocol.cas.idp.CasIdpConfigurer;
import cn.topiam.employee.protocol.form.FormProtocolConfigurer;
import cn.topiam.employee.protocol.oidc.token.EiamOpaqueTokenIntrospector;
import cn.topiam.employee.protocol.saml2.idp.Saml2IdpConfigurer;

import lombok.RequiredArgsConstructor;
import static org.springframework.boot.autoconfigure.security.StaticResourceLocation.*;
import static org.springframework.security.config.Customizer.withDefaults;

import static cn.topiam.employee.common.constants.AuthorizeConstants.*;
import static cn.topiam.employee.common.constants.ConfigBeanNameConstants.*;
import static cn.topiam.employee.common.constants.SessionConstants.CURRENT_STATUS;
import static cn.topiam.employee.core.setting.constant.SecuritySettingConstants.SECURITY_BASIC_REMEMBER_ME_VALID_TIME;
import static cn.topiam.employee.core.setting.constant.SecuritySettingConstants.SECURITY_SESSION_MAXIMUM;
import static cn.topiam.employee.support.constant.EiamConstants.*;

/**
 * PortalSecurityConfiguration
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2019/9/27 19:54
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class PortalSecurityConfiguration {

    private final AuthenticationSuccessHandler successHandler = new PortalAuthenticationSuccessHandler();

    private final AuthenticationFailureHandler failureHandler = new PortalAuthenticationFailureHandler();

    /**
     * IDP SecurityFilterChain
     *
     * @param http {@link  HttpSecurity}
     * @return {@link  SecurityFilterChain}
     * @throws Exception Exception
     */
    @Order(1)
    @RefreshScope
    @Bean(name = IDP_SECURITY_FILTER_CHAIN)
    public SecurityFilterChain idpAuthenticationSecurityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        //QQ
        QqOauthAuthenticationConfigurer<HttpSecurity> qqOauthAuthenticationConfigurer = new QqOauthAuthenticationConfigurer<>(identityProviderRepository, userIdpService);
        requestMatchers.add(qqOauthAuthenticationConfigurer.getRequestMatcher());
        qqOauthAuthenticationConfigurer.successHandler(successHandler);
        qqOauthAuthenticationConfigurer.failureHandler(failureHandler);
        http.apply(qqOauthAuthenticationConfigurer);
        //微信扫码
        WeChatScanCodeAuthenticationConfigurer<HttpSecurity> weChatScanCodeAuthenticationConfigurer = new WeChatScanCodeAuthenticationConfigurer<>(identityProviderRepository, userIdpService);
        requestMatchers.add(weChatScanCodeAuthenticationConfigurer.getRequestMatcher());
        weChatScanCodeAuthenticationConfigurer.successHandler(successHandler);
        weChatScanCodeAuthenticationConfigurer.failureHandler(failureHandler);
        http.apply(weChatScanCodeAuthenticationConfigurer);
        //企业微信
        WeChatWorkScanCodeAuthenticationConfigurer<HttpSecurity> weChatWorkScanCodeAuthenticationConfigurer = new WeChatWorkScanCodeAuthenticationConfigurer<>(identityProviderRepository, userIdpService);
        requestMatchers.add(weChatWorkScanCodeAuthenticationConfigurer.getRequestMatcher());
        weChatWorkScanCodeAuthenticationConfigurer.successHandler(successHandler);
        weChatWorkScanCodeAuthenticationConfigurer.failureHandler(failureHandler);
        http.apply(weChatWorkScanCodeAuthenticationConfigurer);
        //钉钉OAuth2
        DingtalkOAuth2AuthenticationConfigurer<HttpSecurity> dingtalkOauth2AuthenticationConfigurer = new DingtalkOAuth2AuthenticationConfigurer<>(identityProviderRepository, userIdpService);
        requestMatchers.add(dingtalkOauth2AuthenticationConfigurer.getRequestMatcher());
        dingtalkOauth2AuthenticationConfigurer.successHandler(successHandler);
        dingtalkOauth2AuthenticationConfigurer.failureHandler(failureHandler);
        http.apply(dingtalkOauth2AuthenticationConfigurer);
        //钉钉扫码
        DingtalkScanCodeAuthenticationConfigurer<HttpSecurity> dingtalkScanCodeAuthenticationConfigurer = new DingtalkScanCodeAuthenticationConfigurer<>(identityProviderRepository, userIdpService);
        requestMatchers.add(dingtalkScanCodeAuthenticationConfigurer.getRequestMatcher());
        dingtalkScanCodeAuthenticationConfigurer.successHandler(successHandler);
        dingtalkScanCodeAuthenticationConfigurer.failureHandler(failureHandler);
        http.apply(dingtalkScanCodeAuthenticationConfigurer);
        //飞书扫码
        FeiShuScanCodeAuthenticationConfigurer<HttpSecurity> feiShuScanCodeAuthenticationConfigurer = new FeiShuScanCodeAuthenticationConfigurer<>(identityProviderRepository, userIdpService);
        requestMatchers.add(feiShuScanCodeAuthenticationConfigurer.getRequestMatcher());
        feiShuScanCodeAuthenticationConfigurer.successHandler(successHandler);
        feiShuScanCodeAuthenticationConfigurer.failureHandler(failureHandler);
        http.apply(feiShuScanCodeAuthenticationConfigurer);

        //RequestMatcher
        OrRequestMatcher requestMatcher = new OrRequestMatcher(requestMatchers);
        //社交授权请求重定向匹配器
        http.addFilterBefore(new LoginRedirectParameterFilter(new IdpRedirectParameterMatcher()), OAuth2AuthorizationRequestRedirectFilter.class);

        http.requestMatcher(requestMatcher).authorizeHttpRequests().anyRequest().authenticated();
        //异常处理器
        http.exceptionHandling(withExceptionConfigurerDefaults());
        //CSRF
        http.csrf(withCsrfConfigurerDefaults(requestMatcher));
        //headers
        http.headers(withHeadersConfigurerDefaults());
        //cors
        http.cors(withCorsConfigurerDefaults());
        //会话管理器
        http.sessionManagement(withSessionManagementConfigurerDefaults(settingRepository));
        // @formatter:on
        return http.build();
    }

    /**
     * OIDC 协议
     *
     * @param http {@link HttpSecurity}
     * @return {@link SecurityFilterChain}
     * @throws Exception Exception
     */
    @Order(2)
    @Bean(value = OIDC_PROTOCOL_SECURITY_FILTER_CHAIN)
    @RefreshScope
    public SecurityFilterChain oidcProtocolSecurityFilterChain(HttpSecurity http) throws Exception {
        EiamOAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new EiamOAuth2AuthorizationServerConfigurer();
        authorizationServerConfigurer.oidc(configurer -> {
        });
        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
        OrRequestMatcher requestMatcher = new OrRequestMatcher(endpointsMatcher);
        http.requestMatcher(requestMatcher)
            .authorizeHttpRequests(
                authorizeRequests -> authorizeRequests.anyRequest().authenticated())
            .oauth2ResourceServer(configurer -> configurer.opaqueToken()
                .introspector(new EiamOpaqueTokenIntrospector(oAuth2AuthorizationService)))
            //CSRF
            .csrf(withCsrfConfigurerDefaults(requestMatcher))
            //headers
            .headers(withHeadersConfigurerDefaults())
            //cors
            .cors(withCorsConfigurerDefaults())
            //会话管理器
            .sessionManagement(withSessionManagementConfigurerDefaults(settingRepository))
            .apply(authorizationServerConfigurer);
        return http.build();
    }

    /**
     * CAS 协议
     *
     * @param http {@link HttpSecurity}
     * @return {@link SecurityFilterChain}
     * @throws Exception Exception
     */
    @Order(3)
    @Bean(value = CAS_PROTOCOL_SECURITY_FILTER_CHAIN)
    @RefreshScope
    public SecurityFilterChain casProtocolSecurityFilterChain(HttpSecurity http) throws Exception {
        CasIdpConfigurer<HttpSecurity> configurer = new CasIdpConfigurer<>();
        RequestMatcher endpointsMatcher = configurer.getEndpointsMatcher();
        http.requestMatcher(endpointsMatcher)
            .authorizeHttpRequests(
                authorizeRequests -> authorizeRequests.anyRequest().authenticated())
            //异常处理
            .exceptionHandling(withExceptionConfigurerDefaults())
            //CSRF
            .csrf(withCsrfConfigurerDefaults(endpointsMatcher))
            //headers
            .headers(withHeadersConfigurerDefaults())
            //cors
            .cors(withCorsConfigurerDefaults())
            //会话管理器
            .sessionManagement(withSessionManagementConfigurerDefaults(settingRepository))
            .apply(configurer);
        return http.build();
    }

    /**
     * SAML 协议
     *
     * @param http {@link HttpSecurity}
     * @return {@link SecurityFilterChain}
     * @throws Exception Exception
     */
    @Order(4)
    @Bean(value = SAML2_PROTOCOL_SECURITY_FILTER_CHAIN)
    @RefreshScope
    public SecurityFilterChain saml2ProtocolSecurityFilterChain(HttpSecurity http) throws Exception {
        //@formatter:off
        //SAML2 IDP 配置
        Saml2IdpConfigurer<HttpSecurity> configurer = new Saml2IdpConfigurer<>();
        RequestMatcher endpointsMatcher = configurer.getEndpointsMatcher();
        http.requestMatcher(endpointsMatcher)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
                //异常处理
                .exceptionHandling(withExceptionConfigurerDefaults())
                //CSRF
                .csrf(withCsrfConfigurerDefaults(endpointsMatcher))
                //headers
                .headers(withHeadersConfigurerDefaults())
                //cors
                .cors(withCorsConfigurerDefaults())
                //会话管理器
                .sessionManagement(withSessionManagementConfigurerDefaults(settingRepository))
                .apply(configurer);
        return http.build();
        //@formatter:on
    }

    /**
     * FormProtocolSecurityFilterChain
     *
     * @param http {@link HttpSecurity}
     * @return {@link SecurityFilterChain}
     * @throws Exception Exception
     */
    @Order(5)
    @Bean(value = FORM_PROTOCOL_SECURITY_FILTER_CHAIN)
    @RefreshScope
    public SecurityFilterChain formProtocolSecurityFilterChain(HttpSecurity http) throws Exception {
        //@formatter:off
            //Form IDP 配置
            FormProtocolConfigurer<HttpSecurity> configurer = new FormProtocolConfigurer<>();
            RequestMatcher endpointsMatcher = configurer.getEndpointsMatcher();
            http.requestMatcher(endpointsMatcher)
                    .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
                    //异常处理
                    .exceptionHandling(withExceptionConfigurerDefaults())
                    //CSRF
                    .csrf(withCsrfConfigurerDefaults(endpointsMatcher))
                    //headers
                    .headers(withHeadersConfigurerDefaults())
                    //cors
                    .cors(withCorsConfigurerDefaults())
                    //会话管理器
                    .sessionManagement(withSessionManagementConfigurerDefaults(settingRepository))
                    .apply(configurer);
            return http.build();
            //@formatter:on
    }

    /**
     * SecurityFilterChain
     *
     * @param http {@link  HttpSecurity}
     * @return {@link  SecurityFilterChain}
     * @throws Exception Exception
     */
    @Order(7)
    @RefreshScope
    @Bean(name = DEFAULT_SECURITY_FILTER_CHAIN)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        // 系统配置
        http
                //认证请求
                .authorizeHttpRequests(withHttpAuthorizeRequests())
                //请求缓存
                .requestCache(withRequestCacheConfigurer(http))
                // 表单登录配置
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
        //短信OTP认证
        SmsAuthenticationConfigurer<HttpSecurity> smsAuthenticationConfigurer = new SmsAuthenticationConfigurer<>(userRepository, userDetailsService, otpContextHelp);
        smsAuthenticationConfigurer.successHandler(successHandler);
        smsAuthenticationConfigurer.failureHandler(failureHandler);
        http.apply(smsAuthenticationConfigurer);
        //MFA
        http.apply(new MfaAuthenticationConfigurer<>(otpContextHelp, successHandler,failureHandler));
        //IDP 绑定用户
        http.apply(new IdpAuthenticationConfigurer<>(userIdpService, userIdpRepository, passwordEncoder,auditEventPublish));
        //Form 、SMS 授权请求重定向参数过滤器
        http.addFilterBefore(new LoginRedirectParameterFilter(new OrRequestMatcher(new AntPathRequestMatcher(FORM_LOGIN), SmsAuthenticationFilter.getRequestMatcher(),MfaAuthenticationConfigurer.getRequestMatcher())), OAuth2AuthorizationRequestRedirectFilter.class);
        //验证码验证过滤器
        http.addFilterBefore(new CaptchaValidatorFilter(captchaValidator), FormLoginSecretFilter.class);
        // @formatter:on
        return http.build();
    }

    /**
     * 使用 Http 授权请求
     *
     * @return {@link AuthorizeHttpRequestsConfigurer}
     */
    public Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> withHttpAuthorizeRequests() {
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
            //登录配置
            registry.antMatchers(HttpMethod.GET, LOGIN_CONFIG).permitAll();
            //健康检查端点
            registry.antMatchers(webEndpointProperties.getBasePath()+"/**").permitAll();
            //其他请求认证
            registry.antMatchers(API_PATH+"/**").authenticated();
        };
    }

    /**
     * Cors 过滤器
     *
     * @return {@link HeadersConfigurer}
     */
    public  Customizer<CorsConfigurer<HttpSecurity>> withCorsConfigurerDefaults() {
        return configurer -> configurer.configurationSource(new UrlBasedCorsConfigurationSource());
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
            String defaultSessionMaximum = SecuritySettingConstants.SECURITY_BASIC_DEFAULT_SETTINGS
                    .get(SECURITY_SESSION_MAXIMUM);
            String sessionMaximum = Objects.isNull(setting) ? defaultSessionMaximum
                    : "0".equals(setting.getValue()) ? defaultSessionMaximum : setting.getValue();
            configurer.maximumSessions(Integer.parseInt(sessionMaximum))
                    .expiredSessionStrategy(new PortalSessionInformationExpiredStrategy());
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
            configurer.logoutSuccessHandler(new PortalLogoutSuccessHandler());
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
                            "frame-ancestors 'self' eiam.topiam.cn data:; " +
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
    public Customizer<RememberMeConfigurer<HttpSecurity>> withRememberMeConfigurerDefaults(SettingRepository settingRepository) {
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
            csrf.csrfTokenRepository(repository);
            csrf.ignoringRequestMatchers(requestMatcher);
        };
    }

    /**
     * 身份验证成功事件监听器
     *
     * @return {@link  PortalAuthenticationSuccessEventListener}
     */
    @Bean
    @ConditionalOnMissingBean
    public PortalAuthenticationSuccessEventListener authenticationSuccessEventListener() {
        return new PortalAuthenticationSuccessEventListener(geoLocationService);
    }

    /**
     * 身份验证失败事件监听器
     *
     * @return {@link  PortalAuthenticationFailureEventListener}
     */
    @Bean
    @ConditionalOnMissingBean
    public PortalAuthenticationFailureEventListener authenticationFailureEventListener() {
        return new PortalAuthenticationFailureEventListener();
    }

    /**
     * 退出成功事件监听器
     *
     * @return {@link  PortalLogoutSuccessEventListener}
     */
    @Bean
    @ConditionalOnMissingBean
    public PortalLogoutSuccessEventListener logoutSuccessEventListener() {
        return new PortalLogoutSuccessEventListener();
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
            configurer.loginProcessingUrl(FORM_LOGIN);
            MfaAuthenticationHandler authenticationHandler = new MfaAuthenticationHandler(successHandler, failureHandler);
            configurer.successHandler(authenticationHandler);
            configurer.failureHandler(authenticationHandler);
        };
        // @formatter:on
    }

    /**
     * withRequestCacheConfigurer
     *
     * @return {@link RequestCacheConfigurer}
     */
    public static Customizer<RequestCacheConfigurer<HttpSecurity>> withRequestCacheConfigurer(HttpSecurity http) {
        return httpSecurityRequestCacheConfigurer -> {
        };
    }

    /**
     * 密码过期锁定任务
     *
     * @param settingRepository {@link  SettingRepository}
     * @param userRepository    {@link  UserRepository}
     * @return {@link  PasswordExpireTask}
     */
    @Bean
    public PasswordExpireTask passwordExpireLockTask(SettingRepository settingRepository,
                                                     UserRepository userRepository) {
        return new PasswordExpireLockTask(settingRepository, userRepository);
    }

    /**
     * 密码过期警告任务
     *
     * @param settingRepository   {@link  SettingRepository}
     * @param userRepository      {@link  UserRepository}
     * @param mailMsgEventPublish {@link  MailMsgEventPublish}
     * @param smsMsgEventPublish {@link  SmsMsgEventPublish}
     * @return {@link  PasswordExpireTask}
     */
    @Bean
    public PasswordExpireTask passwordExpireWarnTask(SettingRepository settingRepository,
                                                     UserRepository userRepository,
                                                     MailMsgEventPublish mailMsgEventPublish,
                                                     SmsMsgEventPublish smsMsgEventPublish) {
        return new PasswordExpireWarnTask(settingRepository, userRepository, mailMsgEventPublish,
            smsMsgEventPublish);
    }

    /**
     * 密码过期锁定任务
     *
     * @param userRepository    {@link  UserRepository}
     * @return {@link  PasswordExpireTask}
     */
    @Bean
    public UserUnlockTask userUnlockTask(UserRepository userRepository) {
        return new UserUnlockTask(userRepository);
    }

    /**
     * 用户过期锁定任务
     *
     * @param userRepository    {@link  UserRepository}
     * @return {@link  PasswordExpireTask}
     */
    @Bean
    public UserExpireLockTask userExpireLockTask(UserRepository userRepository,
                                                 SettingRepository settingRepository) {
        return new UserExpireLockTask(settingRepository, userRepository);
    }

    private final OAuth2AuthorizationService oAuth2AuthorizationService;
    /**
     * WebEndpointProperties
     */
    private final WebEndpointProperties      webEndpointProperties;

    /**
     * CaptchaValidator
     */
    private final CaptchaValidator           captchaValidator;

    /**
     * UserRepository
     */
    private final UserRepository             userRepository;

    /**
     * UserDetailsService
     */
    private final UserDetailsService         userDetailsService;

    /**
     * OtpContextHelp
     */
    private final OtpContextHelp             otpContextHelp;

    /**
     * PasswordEncoder
     */
    private final PasswordEncoder            passwordEncoder;

    /**
     * AuditEventPublish
     */
    private final AuditEventPublish          auditEventPublish;

    /**
     * SettingRepository
     */
    private final SettingRepository          settingRepository;

    /**
     * 账户认证
     */
    private final UserIdpService             userIdpService;

    /**
     * UserIdpRepositoryCustomizedImpl
     */
    private final UserIdpRepository          userIdpRepository;

    /**
     * 认证源repository
     */
    private final IdentityProviderRepository identityProviderRepository;

    /**
     * GeoLocationService
     */
    private final GeoLocationService         geoLocationService;
}
