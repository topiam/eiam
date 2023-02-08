/*
 * eiam-protocol-oidc - Employee Identity and Access Management Program
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
package org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.web.NimbusJwkSetEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.OAuth2AuthorizationEndpointFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.core.security.savedredirect.HttpSessionRedirectCache;
import cn.topiam.employee.core.security.savedredirect.RedirectCache;
import cn.topiam.employee.protocol.oidc.authentication.EiamOAuth2InitSingleSignOnEndpointFilter;
import cn.topiam.employee.protocol.oidc.authentication.EiamOidcAuthorizationServerContextFilter;
import cn.topiam.employee.protocol.oidc.util.EiamOAuth2Utils;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.HttpResponseUtils;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import static cn.topiam.employee.common.constants.AuthorizeConstants.FE_LOGIN;
import static cn.topiam.employee.protocol.oidc.util.EiamOAuth2Utils.getAppOidcConfigRepository;
import static cn.topiam.employee.support.context.ServletContextHelp.acceptIncludeTextHtml;

/**
 * An {@link AbstractHttpConfigurer} for OAuth 2.0 Authorization Server support.
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/26 19:32
 */
@SuppressWarnings({ "AlibabaClassNamingShouldBeCamel", "DuplicatedCode" })
public final class EiamOAuth2AuthorizationServerConfigurer extends
                                                           AbstractHttpConfigurer<EiamOAuth2AuthorizationServerConfigurer, HttpSecurity> {

    private final Map<Class<? extends AbstractOAuth2Configurer>, AbstractOAuth2Configurer> configurers = createConfigurers();
    private RequestMatcher                                                                 endpointsMatcher;

    /**
     * Configures OpenID Connect 1.0 support (disabled by default).
     *
     * @param oidcCustomizer the {@link Customizer} providing access to the {@link OidcConfigurer}
     * @return the {@link EiamOAuth2AuthorizationServerConfigurer} for further configuration
     */
    public EiamOAuth2AuthorizationServerConfigurer oidc(Customizer<EiamOidcConfigurer> oidcCustomizer) {
        EiamOidcConfigurer oidcConfigurer = getConfigurer(EiamOidcConfigurer.class);
        if (oidcConfigurer == null) {
            this.configurers.put(EiamOidcConfigurer.class,
                new EiamOidcConfigurer(this::postProcess));
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
            EiamOAuth2AuthorizationCodeEndpointConfigurer authorizationEndpointConfigurer =
                    getConfigurer(EiamOAuth2AuthorizationCodeEndpointConfigurer.class);
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
            //添加 RequestMatchers
            requestMatchers.add(configurer.getRequestMatcher());
        });
        requestMatchers.add(new AntPathRequestMatcher(ProtocolConstants.OidcEndpointConstants.JWK_SET_ENDPOINT, HttpMethod.GET.name()));
        this.endpointsMatcher = new OrRequestMatcher(requestMatchers);

        ExceptionHandlingConfigurer<HttpSecurity> exceptionHandling = httpSecurity.getConfigurer(ExceptionHandlingConfigurer.class);
        if (exceptionHandling != null) {
            //身份验证入口点
            exceptionHandling.defaultAuthenticationEntryPointFor(
                    authenticationEntryPoint,
                    new OrRequestMatcher(getRequestMatcher(EiamOAuth2AuthorizationCodeEndpointConfigurer.class))
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
        //OAuth2 同意端点配置器
        String consentEndpoint = ProtocolConstants.OidcEndpointConstants.AUTHORIZATION_CONSENT_ENDPOINT;
        EiamOAuth2ConsentEndpointConfigurer consentEndpointConfigurer = new EiamOAuth2ConsentEndpointConfigurer(this::postProcess);
        consentEndpointConfigurer.consentPage(consentEndpoint);
        configurers.put(EiamOAuth2ConsentEndpointConfigurer.class,consentEndpointConfigurer);
        //OAuth2 隐式模式端点配置器
        EiamOAuth2AuthorizationImplicitEndpointConfigurer implicitEndpointConfigurer = new EiamOAuth2AuthorizationImplicitEndpointConfigurer(this::postProcess);
        implicitEndpointConfigurer.consentPage(consentEndpoint);
        configurers.put(EiamOAuth2AuthorizationImplicitEndpointConfigurer.class,implicitEndpointConfigurer);
        //OAuth2 授权码端点配置器
        EiamOAuth2AuthorizationCodeEndpointConfigurer codeEndpointConfigurer = new EiamOAuth2AuthorizationCodeEndpointConfigurer(this::postProcess);
        codeEndpointConfigurer.consentPage(consentEndpoint);
        configurers.put(EiamOAuth2AuthorizationCodeEndpointConfigurer.class,codeEndpointConfigurer);
        //token端点配置器
        EiamOAuth2TokenEndpointConfigurer tokenEndpointConfigurer = new EiamOAuth2TokenEndpointConfigurer(this::postProcess);
        configurers.put(EiamOAuth2TokenEndpointConfigurer.class, tokenEndpointConfigurer);
        configurers.put(EiamOAuth2TokenIntrospectionEndpointConfigurer.class, new EiamOAuth2TokenIntrospectionEndpointConfigurer(this::postProcess));
        configurers.put(EiamOAuth2TokenRevocationEndpointConfigurer.class, new EiamOAuth2TokenRevocationEndpointConfigurer(this::postProcess));
        //@formatter:no
        return configurers;
    }

    @SuppressWarnings("unchecked")
    private <T> T getConfigurer(Class<T> type) {
        return (T) this.configurers.get(type);
    }

    private <T extends AbstractOAuth2Configurer> RequestMatcher getRequestMatcher(Class<T> configurerType) {
        return getConfigurer(configurerType).getRequestMatcher();
    }

    private final AuthenticationEntryPoint authenticationEntryPoint= new AuthenticationEntryPoint() {
        /**
         * 日志
         */
        private final Logger logger        = LoggerFactory.getLogger(this.getClass());

        private final RedirectCache redirectCache = new HttpSessionRedirectCache();

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            logger.info("----------------------------------------------------------");
            logger.info("未登录, 或登录过期");
            //记录
            redirectCache.saveRedirect(request, response, RedirectCache.RedirectType.REQUEST);
            //判断请求
            boolean isTextHtml = acceptIncludeTextHtml(request);
            //JSON
            if (!isTextHtml) {
                ApiRestResult<Object> result = ApiRestResult.builder()
                        .status(String.valueOf(UNAUTHORIZED.value())).message(StringUtils
                                .defaultString(authException.getMessage(), UNAUTHORIZED.getReasonPhrase()))
                        .build();
                HttpResponseUtils.flushResponseJson(response, UNAUTHORIZED.value(), result);
            }
            // HTML
            else {
                //跳转前端SESSION过期路由
                response.sendRedirect(ServerContextHelp.getPortalPublicBaseUrl() + FE_LOGIN);
            }
            logger.info("----------------------------------------------------------");
        }
    };
}
