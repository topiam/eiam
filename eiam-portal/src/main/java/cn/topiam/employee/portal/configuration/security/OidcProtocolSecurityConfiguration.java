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

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import cn.topiam.eiam.protocol.oidc.authentication.*;
import cn.topiam.eiam.protocol.oidc.authorization.client.OidcConfigRegisteredClientRepository;
import cn.topiam.eiam.protocol.oidc.authorization.token.OAuth2TokenCustomizer;
import cn.topiam.eiam.protocol.oidc.configurers.ClientJwkSource;
import cn.topiam.eiam.protocol.oidc.configurers.OAuth2AuthorizationServerConfigurer;
import cn.topiam.eiam.protocol.oidc.token.OpaqueTokenIntrospector;
import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.app.AppOidcConfigRepository;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.support.redis.KeyStringRedisSerializer;
import static cn.topiam.employee.common.constant.ConfigBeanNameConstants.OIDC_PROTOCOL_SECURITY_FILTER_CHAIN;

/**
 * OIDC 协议配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/7/2 21:16
 */
@AutoConfigureBefore(PortalSecurityConfiguration.class)
@Configuration(proxyBeanMethods = false)
public class OidcProtocolSecurityConfiguration extends AbstractSecurityConfiguration {

    public OidcProtocolSecurityConfiguration(SettingRepository settingRepository) {
        super(settingRepository);
    }

    /**
     * OIDC 协议
     *
     * @return {@link SecurityFilterChain}
     * @throws Exception Exception
     */
    @Bean(value = OIDC_PROTOCOL_SECURITY_FILTER_CHAIN)
    @RefreshScope
    public SecurityFilterChain oidcProtocolSecurityFilterChain(HttpSecurity httpSecurity,
                                                               AccessTokenAuthenticationManagerResolver authenticationManagerResolver) throws Exception {
        //@formatter:off
        httpSecurity.getSharedObject(AuthenticationManagerBuilder.class).parentAuthenticationManager(null);
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
        OrRequestMatcher requestMatcher = new OrRequestMatcher(endpointsMatcher);
        httpSecurity
                .securityMatcher(endpointsMatcher)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2ResourceServer(
                        configurer -> configurer.authenticationManagerResolver(authenticationManagerResolver))
                //安全上下文
                .securityContext(securityContext())
                //CSRF
                .csrf(withCsrfConfigurerDefaults(requestMatcher))
                //headers
                .headers(withHeadersConfigurerDefaults())
                //cors
                .cors(withCorsConfigurerDefaults())
                //会话管理器
                .sessionManagement(withSessionManagementConfigurerDefaults())
                .apply(authorizationServerConfigurer);
        return httpSecurity.build();
        //@formatter:on
    }

    /**
     * 令牌定制器
     *
     * @return {@link OAuth2TokenCustomizer}
     */
    @Bean
    public org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(UserRepository userRepository) {
        return new OAuth2TokenCustomizer(userRepository);
    }

    /**
     * Token 解析器
     *
     * @param jwtDecoder {@link JwtDecoder}
     * @param authorizationService {@link OAuth2AuthorizationService}
     * @return {@link AccessTokenAuthenticationManagerResolver}
     */
    @Bean
    public AccessTokenAuthenticationManagerResolver authenticationManagerResolver(JwtDecoder jwtDecoder,
                                                                                  OAuth2AuthorizationService authorizationService) {
        return new AccessTokenAuthenticationManagerResolver(
            new JwtAuthenticationProvider(jwtDecoder), new OpaqueTokenAuthenticationProvider(
                new OpaqueTokenIntrospector(authorizationService)));
    }

    /**
     * 认证服务
     *
     * @param redisConnectionFactory {@link RedisConnectionFactory}
     * @param cacheProperties {@link CacheProperties}
     * @param clientRepository {@link RedisConnectionFactory}
     * @param beanFactory {@link RegisteredClientRepository}
     * @return {@link AutowireCapableBeanFactory}
     */
    @Bean
    public OAuth2AuthorizationService authorizationService(RedisConnectionFactory redisConnectionFactory,
                                                           CacheProperties cacheProperties,
                                                           RegisteredClientRepository clientRepository,
                                                           AutowireCapableBeanFactory beanFactory) {
        return new RedisOAuth2AuthorizationServiceWrapper(
            getRedisTemplate(redisConnectionFactory, cacheProperties), clientRepository,
            beanFactory);
    }

    /**
     * JWT 解码器
     *
     * @return {@link JwtDecoder}
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfigurer.jwtDecoder(jwkSource);
    }

    /**
     * JWKSource
     *
     * @return {@link JWKSource}
     */
    @Bean
    JWKSource<SecurityContext> jwkSource() {
        return new ClientJwkSource();
    }

    /**
     * 客户端Repository
     *
     * @return {@link RegisteredClientRepository}
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(AppOidcConfigRepository appOidcConfigRepository) {
        return new OidcConfigRegisteredClientRepository(appOidcConfigRepository);
    }

    /**
     * 授权同意service
     *
     * @return {@link OAuth2AuthorizationConsentService}
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(RedisConnectionFactory redisConnectionFactory,
                                                                         CacheProperties cacheProperties) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        KeyStringRedisSerializer keyStringRedisSerializer = new KeyStringRedisSerializer(
            cacheProperties.getRedis().getKeyPrefix());
        redisTemplate.setKeySerializer(keyStringRedisSerializer);
        redisTemplate.setValueSerializer(StringRedisSerializer.UTF_8);
        redisTemplate.afterPropertiesSet();
        return new RedisOAuth2AuthorizationConsentService(redisTemplate);
    }

    /**
     * OAuth2 登录成功监听器
     *
     * @return {@link ApplicationListener}
     */
    @Bean
    public ApplicationListener<AuthenticationSuccessEvent> oauth2ProtocolAuthenticationSuccessEventListener(AuditEventPublish auditEventPublish,
                                                                                                            OAuth2AuthorizationService authorizationService) {
        return new OAuth2AuthenticationSuccessEventListener(auditEventPublish,
            authorizationService);
    }

    /**
     * OAuth2 失败监听
     *
     * @param auditEventPublish {@link AuditEventPublish}
     * @return {@link OAuth2AuthenticationFailureEventListener}
     */
    @Bean
    public ApplicationListener<AbstractAuthenticationFailureEvent> oauth2AuthenticationFailureEventListener(AuditEventPublish auditEventPublish) {
        return new OAuth2AuthenticationFailureEventListener(auditEventPublish);
    }
}
