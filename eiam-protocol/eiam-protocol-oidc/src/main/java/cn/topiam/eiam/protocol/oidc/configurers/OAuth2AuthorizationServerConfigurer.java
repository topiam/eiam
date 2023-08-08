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
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.context.DelegatingApplicationListener;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.web.NimbusJwkSetEndpointFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
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

import cn.topiam.eiam.protocol.oidc.context.OidcAuthorizationServerContextFilter;
import cn.topiam.employee.common.constant.ProtocolConstants;
import cn.topiam.employee.protocol.code.UnauthorizedAuthenticationEntryPoint;
import cn.topiam.employee.protocol.code.configurer.AbstractConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import static cn.topiam.employee.protocol.code.util.ProtocolConfigUtils.getApplicationServiceLoader;
import static cn.topiam.employee.support.security.util.HttpSecurityConfigUtils.getOptionalBean;

/**
 * OAuth2 授权服务器配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/27 21:43
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public final class OAuth2AuthorizationServerConfigurer extends
                                                       AbstractHttpConfigurer<OAuth2AuthorizationServerConfigurer, HttpSecurity> {

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
    public void init(HttpSecurity httpSecurity) {
        initSessionRegistry(httpSecurity);
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        this.configurers.values().forEach(configurer -> {
            configurer.init(httpSecurity);
            requestMatchers.add(configurer.getRequestMatcher());
        });
        //Get jwk endpoint
        requestMatchers.add(new AntPathRequestMatcher(
            ProtocolConstants.OidcEndpointConstants.JWK_SET_ENDPOINT, HttpMethod.GET.name()));
        this.endpointsMatcher = new OrRequestMatcher(requestMatchers);

        ExceptionHandlingConfigurer<HttpSecurity> exceptionHandling = httpSecurity
            .getConfigurer(ExceptionHandlingConfigurer.class);
        if (exceptionHandling != null) {
            //身份验证入口点
            exceptionHandling.defaultAuthenticationEntryPointFor(
                new UnauthorizedAuthenticationEntryPoint(), new OrRequestMatcher(
                    getRequestMatcher(OAuth2AuthorizationEndpointConfigurer.class)));
            exceptionHandling.defaultAuthenticationEntryPointFor(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                new OrRequestMatcher(getRequestMatcher(OAuth2TokenEndpointConfigurer.class),
                    getRequestMatcher(OAuth2TokenIntrospectionEndpointConfigurer.class),
                    getRequestMatcher(OAuth2TokenRevocationEndpointConfigurer.class)));
        }
    }

    @Override
    public void configure(HttpSecurity httpSecurity) {
        //@formatter:off
        this.configurers.values().forEach(configurer -> configurer.configure(httpSecurity));
        //Authorization server context filter
        OidcAuthorizationServerContextFilter oidcAuthorizationServerContextFilter = new OidcAuthorizationServerContextFilter(getEndpointsMatcher(), getApplicationServiceLoader(httpSecurity));
        httpSecurity.addFilterAfter(postProcess(oidcAuthorizationServerContextFilter), SecurityContextHolderFilter.class);
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
        return configurer != null ? configurer.getRequestMatcher() : null;
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
