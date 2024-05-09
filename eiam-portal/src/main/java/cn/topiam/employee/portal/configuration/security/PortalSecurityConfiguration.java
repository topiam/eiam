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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.NonNull;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.authentication.alipay.configurer.AlipayAuthenticationConfigurer;
import cn.topiam.employee.authentication.common.IdentityProviderAuthenticationService;
import cn.topiam.employee.authentication.common.client.RegisteredIdentityProviderClientRepository;
import cn.topiam.employee.authentication.common.configurer.IdentityProviderBindAuthenticationConfigurer;
import cn.topiam.employee.authentication.common.jackjson.AuthenticationJacksonModule;
import cn.topiam.employee.authentication.dingtalk.configurer.DingTalkAuthenticationConfigurer;
import cn.topiam.employee.authentication.feishu.configurer.FeiShuAuthenticationConfigurer;
import cn.topiam.employee.authentication.gitee.configurer.GiteeAuthenticationConfigurer;
import cn.topiam.employee.authentication.github.configurer.GithubAuthenticationConfigurer;
import cn.topiam.employee.authentication.otp.mail.configurer.MailOtpAuthenticationConfigurer;
import cn.topiam.employee.authentication.otp.sms.configurer.SmsOtpAuthenticationConfigurer;
import cn.topiam.employee.authentication.qq.configurer.QqAuthenticationConfigurer;
import cn.topiam.employee.authentication.wechat.configurer.WeChatAuthenticationConfigurer;
import cn.topiam.employee.authentication.wechatwork.configurer.WeChatWorkAuthenticationConfigurer;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.core.message.mail.MailMsgEventPublish;
import cn.topiam.employee.core.message.sms.SmsMsgEventPublish;
import cn.topiam.employee.core.security.otp.OtpContextHelp;
import cn.topiam.employee.core.security.password.task.PasswordExpireTask;
import cn.topiam.employee.core.security.password.task.impl.PasswordExpireLockTask;
import cn.topiam.employee.core.security.password.task.impl.PasswordExpireWarnTask;
import cn.topiam.employee.core.security.task.UserExpireLockTask;
import cn.topiam.employee.core.security.task.UserUnlockTask;
import cn.topiam.employee.portal.authentication.*;
import cn.topiam.employee.support.geo.GeoLocationService;
import cn.topiam.employee.support.jackjson.SupportJackson2Module;
import cn.topiam.employee.support.security.authentication.WebAuthenticationDetailsSource;
import cn.topiam.employee.support.security.configurer.FormLoginConfigurer;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.Customizer.withDefaults;

import static cn.topiam.employee.authentication.alipay.configurer.AlipayAuthenticationConfigurer.alipayOauth;
import static cn.topiam.employee.authentication.dingtalk.configurer.DingTalkAuthenticationConfigurer.dingTalkOAuth2;
import static cn.topiam.employee.authentication.feishu.configurer.FeiShuAuthenticationConfigurer.feiShuOAuth2;
import static cn.topiam.employee.authentication.gitee.configurer.GiteeAuthenticationConfigurer.giteeOauth;
import static cn.topiam.employee.authentication.github.configurer.GithubAuthenticationConfigurer.githubOAuth2;
import static cn.topiam.employee.authentication.otp.mail.configurer.MailOtpAuthenticationConfigurer.mailOtp;
import static cn.topiam.employee.authentication.otp.sms.configurer.SmsOtpAuthenticationConfigurer.smsOtp;
import static cn.topiam.employee.authentication.qq.configurer.QqAuthenticationConfigurer.qqOAuth2;
import static cn.topiam.employee.authentication.wechat.configurer.WeChatAuthenticationConfigurer.weChatOauth;
import static cn.topiam.employee.authentication.wechatwork.configurer.WeChatWorkAuthenticationConfigurer.weChatWorkOAuth2;
import static cn.topiam.employee.common.constant.AuthnConstants.LOGIN_CONFIG;
import static cn.topiam.employee.common.constant.ConfigBeanNameConstants.*;
import static cn.topiam.employee.common.constant.SessionConstants.CURRENT_STATUS;
import static cn.topiam.employee.core.security.PublicSecretEndpoint.PUBLIC_SECRET_PATH;
import static cn.topiam.employee.portal.constant.PortalConstants.*;
import static cn.topiam.employee.protocol.code.configurer.AuthenticationUtils.getAuthenticationDetailsSource;
import static cn.topiam.employee.support.constant.EiamConstants.API_PATH;

/**
 * PortalSecurityConfiguration
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2019/9/27 21:54
 */
@EnableMethodSecurity
@Configuration(proxyBeanMethods = false)
public class PortalSecurityConfiguration extends AbstractSecurityConfiguration
                                         implements BeanClassLoaderAware {

    private final AuthenticationFailureHandler failureHandler = new PortalAuthenticationFailureHandler();

    /**
     * webSecurityCustomizer
     *
     * @return {@link WebSecurityCustomizer} WebSecurityCustomizer
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            new AntPathRequestMatcher("/css/**", GET.name()),
            new AntPathRequestMatcher("/js/**", GET.name()),
            new AntPathRequestMatcher("/webjars/**", GET.name()),
            new AntPathRequestMatcher("/images/**", GET.name()),
            new AntPathRequestMatcher("/favicon.ico", GET.name()));
    }

    /**
     * IDP SecurityFilterChain
     *
     * @param httpSecurity {@link  HttpSecurity}
     * @return {@link  SecurityFilterChain}
     * @throws Exception Exception
     */
    @RefreshScope
    @Bean(name = IDP_SECURITY_FILTER_CHAIN)
    public SecurityFilterChain idpAuthenticationSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // @formatter:off
        WebAuthenticationDetailsSource authenticationDetailsSource = getAuthenticationDetailsSource(httpSecurity);
        AuthenticationSuccessHandler successHandler = new PortalAuthenticationSuccessHandler(userRepository,  auditEventPublish );
        List<RequestMatcher> requestMatchers = new ArrayList<>();

        //QQ
        QqAuthenticationConfigurer qq = qqOAuth2(registeredIdentityProviderClientRepository ,identityProviderAuthenticationService)
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .authenticationDetailsSource(authenticationDetailsSource);
        requestMatchers.add(qq.getRequestMatcher());
        httpSecurity.with(qq,configurer-> {});

        //微信扫码
        WeChatAuthenticationConfigurer chatScanCode = weChatOauth(registeredIdentityProviderClientRepository ,identityProviderAuthenticationService)
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .authenticationDetailsSource(authenticationDetailsSource);
        requestMatchers.add(chatScanCode.getRequestMatcher());
        httpSecurity.with(chatScanCode,configurer-> {});

        //GITHUB
        GithubAuthenticationConfigurer github = githubOAuth2(registeredIdentityProviderClientRepository ,identityProviderAuthenticationService)
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .authenticationDetailsSource(authenticationDetailsSource);
        requestMatchers.add(github.getRequestMatcher());
        httpSecurity.with(github,configurer-> {});

        //企业微信
        WeChatWorkAuthenticationConfigurer weChatWork = weChatWorkOAuth2(registeredIdentityProviderClientRepository ,identityProviderAuthenticationService)
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .authenticationDetailsSource(authenticationDetailsSource);
        requestMatchers.add(weChatWork.getRequestMatcher());
        httpSecurity.with(weChatWork,configurer-> {});

        //钉钉OAuth2
        DingTalkAuthenticationConfigurer dingtalkOauth2 = dingTalkOAuth2(registeredIdentityProviderClientRepository ,identityProviderAuthenticationService)
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .authenticationDetailsSource(authenticationDetailsSource);
        requestMatchers.add(dingtalkOauth2.getRequestMatcher());
        httpSecurity.with(dingtalkOauth2,configurer-> {});

        //飞书
        FeiShuAuthenticationConfigurer feiShuScanCode = feiShuOAuth2(registeredIdentityProviderClientRepository ,identityProviderAuthenticationService)
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .authenticationDetailsSource(authenticationDetailsSource);
        requestMatchers.add(feiShuScanCode.getRequestMatcher());
        httpSecurity.with(feiShuScanCode,configurer-> {});


        //Gitee
        GiteeAuthenticationConfigurer giteeCode = giteeOauth(registeredIdentityProviderClientRepository ,identityProviderAuthenticationService)
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .authenticationDetailsSource(authenticationDetailsSource);
        requestMatchers.add(giteeCode.getRequestMatcher());
        httpSecurity.with(giteeCode,configurer-> {});

        //支付宝
        AlipayAuthenticationConfigurer alipayOauth = alipayOauth(registeredIdentityProviderClientRepository ,identityProviderAuthenticationService)
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .authenticationDetailsSource(authenticationDetailsSource);
        requestMatchers.add(alipayOauth.getRequestMatcher());
        httpSecurity.with(alipayOauth,configurer-> {});

        //RequestMatcher
        OrRequestMatcher requestMatcher = new OrRequestMatcher(requestMatchers);
        //社交授权请求重定向匹配器
        httpSecurity
            .securityMatcher(requestMatcher)
            .authorizeHttpRequests(registry -> registry.anyRequest().authenticated())
            //安全上下文
            .securityContext(securityContext())
            //异常处理器
            .exceptionHandling(withExceptionConfigurerDefaults())
            //CSRF
            .csrf(withCsrfConfigurerDefaults(requestMatcher))
            //headers
            .headers(withHeadersConfigurerDefaults())
            //cors
            .cors(withCorsConfigurerDefaults())
            //会话管理器
            .sessionManagement(withSessionManagementConfigurerDefaults());
        return httpSecurity.build();
        // @formatter:on
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
    @DependsOn({ IDP_SECURITY_FILTER_CHAIN, OIDC_PROTOCOL_SECURITY_FILTER_CHAIN,
                 FORM_PROTOCOL_SECURITY_FILTER_CHAIN, JWT_PROTOCOL_SECURITY_FILTER_CHAIN })
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // @formatter:off
        WebAuthenticationDetailsSource authenticationDetailsSource = getAuthenticationDetailsSource(httpSecurity);
        AuthenticationSuccessHandler successHandler = new PortalAuthenticationSuccessHandler(userRepository,auditEventPublish);
        // 系统配置
        httpSecurity
                .securityMatcher(API_PATH+"/**")
                //认证请求
                .authorizeHttpRequests(withHttpAuthorizeRequests())
                //安全上下文
                .securityContext(securityContext())
                //请求缓存
                .requestCache(withRequestCacheConfigurer())
                //x509
                .x509(withDefaults())
                //异常处理
                .exceptionHandling(withExceptionConfigurerDefaults())
                //记住我
                .rememberMe(withRememberMeConfigurerDefaults())
                //CSRF
                .csrf(withCsrfConfigurerDefaults())
                //headers
                .headers(withHeadersConfigurerDefaults())
                //cors
                .cors(withCorsConfigurerDefaults())
                //退出配置
                .logout(withLogoutConfigurerDefaults())
                //会话管理器
                .sessionManagement(withSessionManagementConfigurerDefaults())
                .with(withFormLoginConfigurer(),configurer-> {});
        //邮件验证码登录认证
        MailOtpAuthenticationConfigurer mailOtpAuthenticationConfigurer = mailOtp(userRepository, userDetailsService, otpContextHelp)
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .authenticationDetailsSource(authenticationDetailsSource);
        httpSecurity.with(mailOtpAuthenticationConfigurer,configurer-> {});
        //短信验证码登录认证
        SmsOtpAuthenticationConfigurer smsAuthenticationConfigurer = smsOtp(userRepository, userDetailsService, otpContextHelp)
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .authenticationDetailsSource(authenticationDetailsSource);
        httpSecurity.with(smsAuthenticationConfigurer,configurer-> {});
        //IDP 绑定用户
        IdentityProviderBindAuthenticationConfigurer identityProviderBindAuthenticationConfigurer = IdentityProviderBindAuthenticationConfigurer.idpBind(identityProviderAuthenticationService, passwordEncoder)
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .authenticationDetailsSource(authenticationDetailsSource);
        httpSecurity.with(identityProviderBindAuthenticationConfigurer,configurer-> {});
        // @formatter:on
        return httpSecurity.build();
    }

    /**
     * 使用 Http 授权请求
     *
     * @return {@link AuthorizeHttpRequestsConfigurer}
     */
    public Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> withHttpAuthorizeRequests() {
        //@formatter:off
        return registry -> {
            registry.requestMatchers(new AntPathRequestMatcher(LOGIN_CONFIG, GET.name())).permitAll();
            registry.requestMatchers(new AntPathRequestMatcher(PUBLIC_SECRET_PATH, GET.name())).permitAll();
            registry.requestMatchers(new AntPathRequestMatcher(CURRENT_STATUS, GET.name())).permitAll();
            registry.requestMatchers(new AntPathRequestMatcher(ACCOUNT_PATH + PREPARE_FORGET_PASSWORD, POST.name())).permitAll();
            registry.requestMatchers(new AntPathRequestMatcher(ACCOUNT_PATH + FORGET_PASSWORD_CODE, GET.name())).permitAll();
            registry.requestMatchers(new AntPathRequestMatcher(ACCOUNT_PATH + FORGET_PASSWORD, PUT.name())).permitAll();
            registry.anyRequest().authenticated();
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
        return new PortalAuthenticationSuccessEventListener();
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
     * 表单登录
     *
     * @return {@link FormLoginConfigurer}
     */
    public FormLoginConfigurer<HttpSecurity> withFormLoginConfigurer() {
        // @formatter:off
        AuthenticationSuccessHandler successHandler = new PortalAuthenticationSuccessHandler(userRepository,  auditEventPublish );
        FormLoginConfigurer<HttpSecurity> configurer=new FormLoginConfigurer<>();
        configurer.successHandler(successHandler)
                .failureHandler(new PortalAuthenticationFailureHandler());
        return configurer;
        // @formatter:on
    }

    /**
     * withRequestCacheConfigurer
     *
     * @return {@link RequestCacheConfigurer}
     */
    public static Customizer<RequestCacheConfigurer<HttpSecurity>> withRequestCacheConfigurer() {
        return configurer -> {
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
    public UserExpireLockTask userExpireLockTask(UserRepository userRepository) {
        return new UserExpireLockTask(userRepository);
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

    /**
     * UserRepository
     */
    private final UserRepository                             userRepository;

    /**
     * UserDetailsService
     */
    private final UserDetailsService                         userDetailsService;

    /**
     * OtpContextHelp
     */
    private final OtpContextHelp                             otpContextHelp;

    /**
     * PasswordEncoder
     */
    private final PasswordEncoder                            passwordEncoder;

    /**
     * AuditEventPublish
     */
    private final AuditEventPublish                          auditEventPublish;

    private final RegisteredIdentityProviderClientRepository registeredIdentityProviderClientRepository;
    private final IdentityProviderAuthenticationService      identityProviderAuthenticationService;

    private ClassLoader                                      loader;

    @Override
    public void setBeanClassLoader(@NonNull ClassLoader classLoader) {
        this.loader = classLoader;
    }

    public PortalSecurityConfiguration(UserRepository userRepository,
                                       UserDetailsService userDetailsService,
                                       OtpContextHelp otpContextHelp,
                                       PasswordEncoder passwordEncoder,
                                       AuditEventPublish auditEventPublish,
                                       SettingRepository settingRepository,
                                       RegisteredIdentityProviderClientRepository registeredIdentityProviderClientRepository,
                                       IdentityProviderAuthenticationService identityProviderAuthenticationService) {
        super(settingRepository);
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.otpContextHelp = otpContextHelp;
        this.passwordEncoder = passwordEncoder;
        this.auditEventPublish = auditEventPublish;
        this.registeredIdentityProviderClientRepository = registeredIdentityProviderClientRepository;
        this.identityProviderAuthenticationService = identityProviderAuthenticationService;
    }

}
