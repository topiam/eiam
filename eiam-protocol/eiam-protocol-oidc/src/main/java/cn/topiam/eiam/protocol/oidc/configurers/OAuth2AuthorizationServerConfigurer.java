/*
 * eiam-protocol-oidc - Employee Identity and Access Management
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
package cn.topiam.eiam.protocol.oidc.configurers;

import java.util.*;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.GenericApplicationListenerAdapter;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.context.DelegatingApplicationListener;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.web.NimbusJwkSetEndpointFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import cn.topiam.eiam.protocol.oidc.authentication.ClientAuthenticationRequiredEntryPoint;
import cn.topiam.eiam.protocol.oidc.context.OidcAuthorizationServerContextFilter;
import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.common.constant.ProtocolConstants;
import cn.topiam.employee.protocol.code.EndpointMatcher;
import cn.topiam.employee.protocol.code.UnauthorizedAuthenticationEntryPoint;
import cn.topiam.employee.protocol.code.configurer.AbstractConfigurer;
import static cn.topiam.employee.protocol.code.configurer.AuthenticationUtils.getApplicationServiceLoader;
import static cn.topiam.employee.support.security.util.HttpSecurityConfigUtils.getOptionalBean;

/**
 * OAuth2 授权服务器配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/6/27 21:43
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public final class OAuth2AuthorizationServerConfigurer extends
                                                       AbstractHttpConfigurer<OAuth2AuthorizationServerConfigurer, HttpSecurity> {

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
        initSessionRegistry(httpSecurity);
        this.configurers.values().forEach(configurer -> {
            configurer.init(httpSecurity);
            this.endpointMatchers.add(configurer.getEndpointMatcher());
        });
        //Get jwk endpoint
        this.endpointMatchers.add(new EndpointMatcher(
            new AntPathRequestMatcher(ProtocolConstants.OidcEndpointConstants.JWK_SET_ENDPOINT,
                HttpMethod.GET.name()),
            false));
        //异常处理
        httpSecurity.exceptionHandling(exceptionHandling -> {
            if (exceptionHandling != null) {
                //认证端点异常
                exceptionHandling.defaultAuthenticationEntryPointFor(
                    new UnauthorizedAuthenticationEntryPoint(), new OrRequestMatcher(
                        getRequestMatcher(OAuth2AuthorizationEndpointConfigurer.class)));
                exceptionHandling.defaultAuthenticationEntryPointFor(
                    new ClientAuthenticationRequiredEntryPoint(),
                    new OrRequestMatcher(getRequestMatcher(OAuth2TokenEndpointConfigurer.class),
                        getRequestMatcher(OAuth2TokenIntrospectionEndpointConfigurer.class),
                        getRequestMatcher(OAuth2TokenRevocationEndpointConfigurer.class),
                        getRequestMatcher(OAuth2DeviceAuthorizationEndpointConfigurer.class)));
            }
        });
    }

    @Override
    public void configure(HttpSecurity httpSecurity) {
        //@formatter:off
        //OIDC 授权服务器上下文过滤器
        ApplicationServiceLoader applicationServiceLoader = getApplicationServiceLoader(httpSecurity);
        OidcAuthorizationServerContextFilter oidcAuthorizationServerContextFilter = new OidcAuthorizationServerContextFilter(this.endpointMatchers, applicationServiceLoader);
        httpSecurity.addFilterAfter(postProcess(oidcAuthorizationServerContextFilter), SecurityContextHolderFilter.class);
        this.configurers.values().forEach(configurer -> configurer.configure(httpSecurity));
        //Jwk filter
        NimbusJwkSetEndpointFilter jwkSetEndpointFilter = new NimbusJwkSetEndpointFilter(OAuth2ConfigurerUtils.getJwkSource(httpSecurity), ProtocolConstants.OidcEndpointConstants.JWK_SET_ENDPOINT);
        httpSecurity.addFilterBefore(postProcess(jwkSetEndpointFilter), AbstractPreAuthenticatedProcessingFilter.class);
        //@formatter:on
    }

    private static void initSessionRegistry(HttpSecurity httpSecurity) {
        SessionRegistry sessionRegistry = getOptionalBean(httpSecurity, SessionRegistry.class);
        if (sessionRegistry == null) {
            sessionRegistry = new SessionRegistryImpl();
            registerDelegateApplicationListener(httpSecurity,
                (SessionRegistryImpl) sessionRegistry);
        }
        httpSecurity.setSharedObject(SessionRegistry.class, sessionRegistry);
    }

    public static JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        Set<JWSAlgorithm> jwsAlgs = new HashSet<>();
        jwsAlgs.addAll(JWSAlgorithm.Family.RSA);
        jwsAlgs.addAll(JWSAlgorithm.Family.EC);
        jwsAlgs.addAll(JWSAlgorithm.Family.HMAC_SHA);
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        JWSKeySelector<SecurityContext> jwsKeySelector = new JWSVerificationKeySelector<>(jwsAlgs,
            jwkSource);
        jwtProcessor.setJWSKeySelector(jwsKeySelector);
        // Override the default Nimbus claims set verifier as NimbusJwtDecoder handles it instead
        jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
        });
        return new NimbusJwtDecoder(jwtProcessor);
    }

    private static void registerDelegateApplicationListener(HttpSecurity httpSecurity,
                                                            ApplicationListener<?> delegate) {
        DelegatingApplicationListener delegatingApplicationListener = getOptionalBean(httpSecurity,
            DelegatingApplicationListener.class);
        if (delegatingApplicationListener == null) {
            return;
        }
        SmartApplicationListener smartListener = new GenericApplicationListenerAdapter(delegate);
        delegatingApplicationListener.addListener(smartListener);
    }

    private <T extends AbstractConfigurer> RequestMatcher getRequestMatcher(Class<T> configurerType) {
        T configurer = getConfigurer(configurerType);
        return configurer != null ? configurer.getEndpointMatcher().getRequestMatcher() : null;
    }

    private <T> T getConfigurer(Class<T> type) {
        return (T) this.configurers.get(type);
    }

    /**
     * createConfigurers
     *
     * @return {@link AbstractConfigurer}
     */
    private Map<Class<? extends AbstractConfigurer>, AbstractConfigurer> createConfigurers() {
        //@formatter:off
        Map<Class<? extends AbstractConfigurer>, AbstractConfigurer> configurers = new LinkedHashMap<>();
        //客户端认证端点
        configurers.put(OAuth2ClientAuthenticationConfigurer.class, new OAuth2ClientAuthenticationConfigurer(this::postProcess));
        //授权端点
        configurers.put(OAuth2AuthorizationEndpointConfigurer.class, new OAuth2AuthorizationEndpointConfigurer(this::postProcess));
        //令牌端点
        configurers.put(OAuth2TokenEndpointConfigurer.class, new OAuth2TokenEndpointConfigurer(this::postProcess));
        //令牌内省端点配置
        configurers.put(OAuth2TokenIntrospectionEndpointConfigurer.class, new OAuth2TokenIntrospectionEndpointConfigurer(this::postProcess));
        //令牌吊销端点配置
        configurers.put(OAuth2TokenRevocationEndpointConfigurer.class, new OAuth2TokenRevocationEndpointConfigurer(this::postProcess));
        //设备授权端点配置
        configurers.put(OAuth2DeviceAuthorizationEndpointConfigurer.class, new OAuth2DeviceAuthorizationEndpointConfigurer(this::postProcess));
        //设备验证端点配置
        configurers.put(OAuth2DeviceVerificationEndpointConfigurer.class, new OAuth2DeviceVerificationEndpointConfigurer(this::postProcess));
        //OIDC提供商端点配置
        configurers.put(OidcProviderConfigurationEndpointConfigurer.class, new OidcProviderConfigurationEndpointConfigurer(this::postProcess));
        //OIDC登出端点配置
        configurers.put(OidcLogoutEndpointConfigurer.class, new OidcLogoutEndpointConfigurer(this::postProcess));
        //用户信息端点配置
        configurers.put(OidcUserInfoEndpointConfigurer.class, new OidcUserInfoEndpointConfigurer(this::postProcess));
        //@formatter:on
        return configurers;
    }

}
