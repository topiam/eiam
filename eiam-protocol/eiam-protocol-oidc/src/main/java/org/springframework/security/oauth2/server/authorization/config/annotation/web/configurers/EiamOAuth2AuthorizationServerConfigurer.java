/*
 * eiam-protocol-oidc - Employee Identity and Access Management Program
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
package org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers;

import java.util.*;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.NimbusJwkSetEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.OAuth2AuthorizationEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.protocol.oidc.authentication.EiamOAuth2InitSingleSignOnEndpointFilter;
import cn.topiam.employee.protocol.oidc.authentication.EiamOidcAuthorizationServerContextFilter;
import cn.topiam.employee.protocol.oidc.authentication.password.EiamOAuth2AuthorizationPasswordAuthenticationConverter;
import cn.topiam.employee.protocol.oidc.handler.PortalOAuth2AuthenticationEntryPoint;
import cn.topiam.employee.protocol.oidc.util.EiamOAuth2Utils;
import static cn.topiam.employee.protocol.oidc.util.EiamOAuth2Utils.getAppOidcConfigRepository;

/**
 * An {@link AbstractHttpConfigurer} for OAuth 2.0 Authorization Server support.
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/26 19:32
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public final class EiamOAuth2AuthorizationServerConfigurer extends
                                                           AbstractHttpConfigurer<EiamOAuth2AuthorizationServerConfigurer, HttpSecurity> {

    private final Map<Class<? extends AbstractOAuth2Configurer>, AbstractOAuth2Configurer> configurers = createConfigurers();
    private RequestMatcher                                                                 endpointsMatcher;

    /**
     * Sets the repository of registered clients.
     *
     * @param registeredClientRepository the repository of registered clients
     * @return the {@link OAuth2AuthorizationServerConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationServerConfigurer registeredClientRepository(RegisteredClientRepository registeredClientRepository) {
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        getBuilder().setSharedObject(RegisteredClientRepository.class, registeredClientRepository);
        return this;
    }

    /**
     * Sets the authorization service.
     *
     * @param authorizationService the authorization service
     * @return the {@link OAuth2AuthorizationServerConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationServerConfigurer authorizationService(OAuth2AuthorizationService authorizationService) {
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        getBuilder().setSharedObject(OAuth2AuthorizationService.class, authorizationService);
        return this;
    }

    /**
     * Sets the authorization consent service.
     *
     * @param authorizationConsentService the authorization consent service
     * @return the {@link OAuth2AuthorizationServerConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationServerConfigurer authorizationConsentService(OAuth2AuthorizationConsentService authorizationConsentService) {
        Assert.notNull(authorizationConsentService, "authorizationConsentService cannot be null");
        getBuilder().setSharedObject(OAuth2AuthorizationConsentService.class,
            authorizationConsentService);
        return this;
    }

    /**
     * Sets the authorization server settings.
     *
     * @param authorizationServerSettings the authorization server settings
     * @return the {@link OAuth2AuthorizationServerConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationServerConfigurer authorizationServerSettings(AuthorizationServerSettings authorizationServerSettings) {
        Assert.notNull(authorizationServerSettings, "authorizationServerSettings cannot be null");
        getBuilder().setSharedObject(AuthorizationServerSettings.class,
            authorizationServerSettings);
        return this;
    }

    /**
     * Sets the token generator.
     *
     * @param tokenGenerator the token generator
     * @return the {@link OAuth2AuthorizationServerConfigurer} for further configuration
     * @since 0.2.3
     */
    public EiamOAuth2AuthorizationServerConfigurer tokenGenerator(OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
        getBuilder().setSharedObject(OAuth2TokenGenerator.class, tokenGenerator);
        return this;
    }

    /**
     * Configures OAuth 2.0 Client Authentication.
     *
     * @param clientAuthenticationCustomizer the {@link Customizer} providing access to the {@link OAuth2ClientAuthenticationConfigurer}
     * @return the {@link OAuth2AuthorizationServerConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationServerConfigurer clientAuthentication(Customizer<OAuth2ClientAuthenticationConfigurer> clientAuthenticationCustomizer) {
        clientAuthenticationCustomizer
            .customize(getConfigurer(OAuth2ClientAuthenticationConfigurer.class));
        return this;
    }

    /**
     * Configures the OAuth 2.0 Authorization Endpoint.
     *
     * @param authorizationEndpointCustomizer the {@link Customizer} providing access to the {@link OAuth2AuthorizationEndpointConfigurer}
     * @return the {@link OAuth2AuthorizationServerConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationServerConfigurer authorizationEndpoint(Customizer<OAuth2AuthorizationEndpointConfigurer> authorizationEndpointCustomizer) {
        authorizationEndpointCustomizer
            .customize(getConfigurer(OAuth2AuthorizationEndpointConfigurer.class));
        return this;
    }

    /**
     * Configures the OAuth 2.0 Token Endpoint.
     *
     * @param tokenEndpointCustomizer the {@link Customizer} providing access to the {@link OAuth2TokenEndpointConfigurer}
     * @return the {@link OAuth2AuthorizationServerConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationServerConfigurer tokenEndpoint(Customizer<OAuth2TokenEndpointConfigurer> tokenEndpointCustomizer) {
        tokenEndpointCustomizer.customize(getConfigurer(OAuth2TokenEndpointConfigurer.class));
        return this;
    }

    /**
     * Configures the OAuth 2.0 Token Introspection Endpoint.
     *
     * @param tokenIntrospectionEndpointCustomizer the {@link Customizer} providing access to the {@link OAuth2TokenIntrospectionEndpointConfigurer}
     * @return the {@link EiamOAuth2AuthorizationServerConfigurer} for further configuration
     * @since 0.2.3
     */
    public EiamOAuth2AuthorizationServerConfigurer tokenIntrospectionEndpoint(Customizer<OAuth2TokenIntrospectionEndpointConfigurer> tokenIntrospectionEndpointCustomizer) {
        tokenIntrospectionEndpointCustomizer
            .customize(getConfigurer(OAuth2TokenIntrospectionEndpointConfigurer.class));
        return this;
    }

    /**
     * Configures the OAuth 2.0 Token Revocation Endpoint.
     *
     * @param tokenRevocationEndpointCustomizer the {@link Customizer} providing access to the {@link OAuth2TokenRevocationEndpointConfigurer}
     * @return the {@link EiamOAuth2AuthorizationServerConfigurer} for further configuration
     * @since 0.2.2
     */
    public EiamOAuth2AuthorizationServerConfigurer tokenRevocationEndpoint(Customizer<OAuth2TokenRevocationEndpointConfigurer> tokenRevocationEndpointCustomizer) {
        tokenRevocationEndpointCustomizer
            .customize(getConfigurer(OAuth2TokenRevocationEndpointConfigurer.class));
        return this;
    }

    /**
     * Configures OpenID Connect 1.0 support (disabled by default).
     *
     * @param oidcCustomizer the {@link Customizer} providing access to the {@link OidcConfigurer}
     * @return the {@link EiamOAuth2AuthorizationServerConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationServerConfigurer oidc(Customizer<EiamOidcConfigurer> oidcCustomizer) {
        EiamOidcConfigurer oidcConfigurer = getConfigurer(EiamOidcConfigurer.class);
        if (oidcConfigurer == null) {
            addConfigurer(EiamOidcConfigurer.class, new EiamOidcConfigurer(this::postProcess));
            oidcConfigurer = getConfigurer(EiamOidcConfigurer.class);
        }
        oidcCustomizer.customize(oidcConfigurer);
        return this;
    }

    /**
     * Returns a {@link RequestMatcher} for the authorization server endpoints.
     *
     * @return a {@link RequestMatcher} for the authorization server endpoints
     */
    public RequestMatcher getEndpointsMatcher() {
        // Return a deferred RequestMatcher
        // since endpointsMatcher is constructed in init(HttpSecurity).
        return (request) -> this.endpointsMatcher.matches(request);
    }

    @Override
    public void init(HttpSecurity httpSecurity) {
        //@formatter:off
        EiamOidcConfigurer oidcConfigurer = getConfigurer(EiamOidcConfigurer.class);
        if (oidcConfigurer == null) {
            // OpenID Connect is disabled.
            // Add an authentication validator that rejects authentication requests.
            EiamOAuth2AuthorizationEndpointConfigurer authorizationEndpointConfigurer =
                    getConfigurer(EiamOAuth2AuthorizationEndpointConfigurer.class);
            //添加授权码请求身份验证验证器
            authorizationEndpointConfigurer.addAuthorizationCodeRequestAuthenticationValidator((authenticationContext) -> {
                OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication =
                        authenticationContext.getAuthentication();
                if (authorizationCodeRequestAuthentication.getScopes().contains(OidcScopes.OPENID)) {
                    OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_SCOPE,
                            "OpenID Connect 1.0 authentication requests are restricted.",
                            "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1");
                    throw new OAuth2AuthorizationCodeRequestAuthenticationException(
                            error, authorizationCodeRequestAuthentication);
                }
            });

            EiamOAuth2AuthorizationImplicitEndpointConfigurer authorizationImplicitEndpointConfigurer =
                    getConfigurer(EiamOAuth2AuthorizationImplicitEndpointConfigurer.class);
            //添加授权隐式请求身份验证验证器
            authorizationImplicitEndpointConfigurer.addAuthorizationImplicitRequestAuthenticationValidator((authenticationContext) -> {
                OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication =
                        authenticationContext.getAuthentication();
                if (authorizationCodeRequestAuthentication.getScopes().contains(OidcScopes.OPENID)) {
                    OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_SCOPE,
                            "OpenID Connect 1.0 authentication requests are restricted.",
                            "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1");
                    throw new OAuth2AuthorizationCodeRequestAuthenticationException(
                            error, authorizationCodeRequestAuthentication);
                }
            });
        }
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        //配置
        this.configurers.values().forEach(configurer -> {
            configurer.init(httpSecurity);
            configurer.init(httpSecurity);
            requestMatchers.add(configurer.getRequestMatcher());
        });
        requestMatchers.add(new AntPathRequestMatcher(ProtocolConstants.OidcEndpointConstants.JWK_SET_ENDPOINT, HttpMethod.GET.name()));
        this.endpointsMatcher = new OrRequestMatcher(requestMatchers);

        ExceptionHandlingConfigurer<HttpSecurity> exceptionHandling = httpSecurity.getConfigurer(ExceptionHandlingConfigurer.class);
        if (exceptionHandling != null) {
            //身份验证入口点
            exceptionHandling.defaultAuthenticationEntryPointFor(
                    new PortalOAuth2AuthenticationEntryPoint(),
                    new OrRequestMatcher(getRequestMatcher(EiamOAuth2AuthorizationEndpointConfigurer.class))
            );
            exceptionHandling.defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    new OrRequestMatcher(
                            getRequestMatcher(EiamOAuth2TokenEndpointConfigurer.class),
                            getRequestMatcher(EiamOAuth2TokenIntrospectionEndpointConfigurer.class),
                            getRequestMatcher(EiamOAuth2TokenRevocationEndpointConfigurer.class)
                    )
            );
        }
        //@formatter:on
    }

    @Override
    public void configure(HttpSecurity httpSecurity) {
        //@formatter:off
        this.configurers.values().forEach(configurer -> configurer.configure(httpSecurity));
        //授权服务器上下文过滤器
        httpSecurity.addFilterAfter(postProcess(new EiamOidcAuthorizationServerContextFilter(endpointsMatcher, getAppOidcConfigRepository(httpSecurity))), SecurityContextHolderFilter.class);
        //配置JWK端点设置器
        NimbusJwkSetEndpointFilter jwkSetEndpointFilter = new NimbusJwkSetEndpointFilter(EiamOAuth2Utils.getJwkSource(httpSecurity), ProtocolConstants.OidcEndpointConstants.JWK_SET_ENDPOINT);
        httpSecurity.addFilterBefore(postProcess(jwkSetEndpointFilter), AbstractPreAuthenticatedProcessingFilter.class);
        //设置 SSO 发起过滤器
        httpSecurity.addFilterBefore(new EiamOAuth2InitSingleSignOnEndpointFilter(httpSecurity.getSharedObject(AuthenticationManager.class),
                httpSecurity.getSharedObject(RegisteredClientRepository.class)), OAuth2AuthorizationEndpointFilter.class);
        //@formatter:on
    }

    private Map<Class<? extends AbstractOAuth2Configurer>, AbstractOAuth2Configurer> createConfigurers() {
        //@formatter:off
        Map<Class<? extends AbstractOAuth2Configurer>, AbstractOAuth2Configurer> configurers = new LinkedHashMap<>();
        configurers.put(EiamOAuth2ClientAuthenticationConfigurer.class,
            new EiamOAuth2ClientAuthenticationConfigurer(this::postProcess));
        //OAuth2 隐式模式端点配置器
        configurers.put(EiamOAuth2AuthorizationImplicitEndpointConfigurer.class,new EiamOAuth2AuthorizationImplicitEndpointConfigurer(this::postProcess) );
        //OAuth2 授权码端点配置器
        configurers.put(EiamOAuth2AuthorizationEndpointConfigurer.class,new EiamOAuth2AuthorizationEndpointConfigurer(this::postProcess) );
        //token端点配置器
        EiamOAuth2TokenEndpointConfigurer configurer = new EiamOAuth2TokenEndpointConfigurer(this::postProcess);
        DelegatingAuthenticationConverter authenticationConverter = new DelegatingAuthenticationConverter(
                Arrays.asList(
                        //密码模式认证转换器
                        new EiamOAuth2AuthorizationPasswordAuthenticationConverter(),
                        new OAuth2AuthorizationCodeAuthenticationConverter(),
                        new OAuth2RefreshTokenAuthenticationConverter(),
                        new OAuth2ClientCredentialsAuthenticationConverter()));
        configurer.accessTokenRequestConverter(authenticationConverter);
        configurers.put(EiamOAuth2TokenEndpointConfigurer.class, configurer);
        configurers.put(EiamOAuth2TokenIntrospectionEndpointConfigurer.class, new EiamOAuth2TokenIntrospectionEndpointConfigurer(this::postProcess));
        configurers.put(EiamOAuth2TokenRevocationEndpointConfigurer.class, new EiamOAuth2TokenRevocationEndpointConfigurer(this::postProcess));
        //@formatter:no
        return configurers;
    }

    @SuppressWarnings("unchecked")
    private <T> T getConfigurer(Class<T> type) {
        return (T) this.configurers.get(type);
    }

    private <T extends AbstractOAuth2Configurer> void addConfigurer(Class<T> configurerType, T configurer) {
        this.configurers.put(configurerType, configurer);
    }
    private <T extends AbstractOAuth2Configurer> RequestMatcher getRequestMatcher(Class<T> configurerType) {
        return getConfigurer(configurerType).getRequestMatcher();
    }

}
