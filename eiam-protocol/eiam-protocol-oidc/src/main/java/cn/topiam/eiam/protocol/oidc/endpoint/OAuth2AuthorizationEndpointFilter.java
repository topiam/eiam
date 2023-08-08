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
package cn.topiam.eiam.protocol.oidc.endpoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationConsentAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeRequestAuthenticationConverter;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.*;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import cn.topiam.eiam.protocol.oidc.authentication.OAuth2AuthorizationImplicitAccessTokenAuthenticationToken;
import cn.topiam.eiam.protocol.oidc.authentication.OAuth2AuthorizationImplicitRequestAuthenticationException;
import cn.topiam.eiam.protocol.oidc.authentication.OAuth2AuthorizationImplicitRequestAuthenticationToken;
import cn.topiam.eiam.protocol.oidc.endpoint.authentication.OAuth2AuthorizationImplicitRequestAuthenticationConverter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.UNSUPPORTED_RESPONSE_TYPE;
import static org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames.TOKEN_TYPE;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.*;

import static cn.topiam.eiam.protocol.oidc.endpoint.OAuth2EndpointUtils.appendUrl;
import static cn.topiam.eiam.protocol.oidc.endpoint.authentication.OAuth2AuthorizationImplicitRequestAuthenticationConverter.ID_TOKEN;
import static cn.topiam.eiam.protocol.oidc.endpoint.authentication.OAuth2AuthorizationImplicitRequestAuthenticationConverter.TOKEN;

/**
 * 处理授权请求
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/26 21:52
 * @see <a target="_blank" href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1">Section 4.1 Authorization Code Grant</a>
 * @see <a target="_blank" href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.1">Section 4.1.1 Authorization Request</a>
 * @see <a target="_blank" href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2">Section 4.1.2 Authorization Response</a>
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public final class OAuth2AuthorizationEndpointFilter extends OncePerRequestFilter {

    /**
     * The default endpoint {@code URI} for authorization requests.
     */
    private static final String DEFAULT_AUTHORIZATION_ENDPOINT_URI = "/oauth2/authorize";

    /**
     * Constructs an {@code OAuth2AuthorizationEndpointFilter} using the provided parameters.
     *
     * @param authenticationManager the authentication manager
     * @param authorizationService  the authorization service
     */
    public OAuth2AuthorizationEndpointFilter(AuthenticationManager authenticationManager,
                                             OAuth2AuthorizationService authorizationService) {
        this(authorizationService, authenticationManager, DEFAULT_AUTHORIZATION_ENDPOINT_URI);
    }

    /**
     * Constructs an {@code OAuth2AuthorizationEndpointFilter} using the provided parameters.
     *
     * @param authorizationService     the authorization service
     * @param authenticationManager    the authentication manager
     * @param authorizationEndpointUri the endpoint {@code URI} for authorization requests
     */
    public OAuth2AuthorizationEndpointFilter(OAuth2AuthorizationService authorizationService,
                                             AuthenticationManager authenticationManager,
                                             String authorizationEndpointUri) {
        this.authorizationService = authorizationService;
        Assert.notNull(authenticationManager, "authenticationManager cannot be null");
        Assert.hasText(authorizationEndpointUri, "authorizationEndpointUri cannot be empty");
        this.authenticationManager = authenticationManager;
        this.authorizationEndpointMatcher = createDefaultRequestMatcher(authorizationEndpointUri);
        this.authenticationConverter = request -> {
            try {
                //先走授权码模式转换器
                return new OAuth2AuthorizationCodeRequestAuthenticationConverter().convert(request);
            } catch (OAuth2AuthorizationCodeRequestAuthenticationException e) {
                //不支持响应类型异常：unsupported_response_type，尝试走简化模式转换器
                if (UNSUPPORTED_RESPONSE_TYPE.equals(e.getError().getErrorCode())) {
                    return new OAuth2AuthorizationImplicitRequestAuthenticationConverter()
                        .convert(request);
                }
                throw e;
            }
        };
    }

    /**
     * 创建默认请求匹配器
     *
     * @param authorizationEndpointUri {@link String} 授权端点URI
     * @return {@link RequestMatcher}
     */
    private static RequestMatcher createDefaultRequestMatcher(String authorizationEndpointUri) {
        RequestMatcher authorizationRequestGetMatcher = new AntPathRequestMatcher(
            authorizationEndpointUri, HttpMethod.GET.name());
        RequestMatcher authorizationRequestPostMatcher = new AntPathRequestMatcher(
            authorizationEndpointUri, HttpMethod.POST.name());
        RequestMatcher openidScopeMatcher = request -> {
            String scope = request.getParameter(OAuth2ParameterNames.SCOPE);
            return StringUtils.isNotBlank(scope) && scope.contains(OidcScopes.OPENID);
        };
        RequestMatcher responseTypeParameterMatcher = request -> request
            .getParameter(OAuth2ParameterNames.RESPONSE_TYPE) != null;

        RequestMatcher authorizationRequestMatcher = new OrRequestMatcher(
            authorizationRequestGetMatcher, new AndRequestMatcher(authorizationRequestPostMatcher,
                responseTypeParameterMatcher, openidScopeMatcher));
        RequestMatcher authorizationConsentMatcher = new AndRequestMatcher(
            authorizationRequestPostMatcher,
            new NegatedRequestMatcher(responseTypeParameterMatcher));

        return new OrRequestMatcher(authorizationRequestMatcher, authorizationConsentMatcher);
    }

    /**
     * Filter
     *
     * @param request     {@link HttpServletRequest}
     * @param response    {@link HttpServletResponse}
     * @param filterChain {@link FilterChain}
     * @throws ServletException ServletException
     * @throws IOException      IOException
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException,
                                                                      IOException {

        if (!this.authorizationEndpointMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            //通过身份认证转换器转换
            Authentication authentication = this.authenticationConverter.convert(request);
            if (authentication instanceof AbstractAuthenticationToken) {
                ((AbstractAuthenticationToken) authentication)
                    .setDetails(this.authenticationDetailsSource.buildDetails(request));
            }

            //调用认证管理器进行认证
            Authentication authenticationResult = this.authenticationManager
                .authenticate(authentication);

            if (!authenticationResult.isAuthenticated()) {
                // If the Principal (Resource Owner) is not authenticated then
                // pass through the chain with the expectation that the authentication process
                // will commence via AuthenticationEntryPoint
                filterChain.doFilter(request, response);
                return;
            }
            this.sessionAuthenticationStrategy.onAuthentication(authenticationResult, request,
                response);
            this.authenticationSuccessHandler.onAuthenticationSuccess(request, response,
                authenticationResult);
        } catch (OAuth2AuthenticationException ex) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace(
                    LogMessage.format("Authorization request failed: %s", ex.getError()), ex);
            }
            this.authenticationFailureHandler.onAuthenticationFailure(request, response, ex);
        }
    }

    /**
     * 发送成功响应
     *
     * @param request        {@link HttpServletRequest}
     * @param response       {@link HttpServletResponse}
     * @param authentication {@link Authentication}
     * @throws IOException IOException
     */
    private void sendAuthorizationResponse(HttpServletRequest request, HttpServletResponse response,
                                           Authentication authentication) throws IOException {
        //授权码请求
        if (authentication instanceof OAuth2AuthorizationCodeRequestAuthenticationToken authenticationToken) {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(StringUtils.defaultString(authenticationToken.getRedirectUri()))
                .queryParam(OAuth2ParameterNames.CODE,
                    ObjectUtils.isNotEmpty(authenticationToken.getAuthorizationCode())
                        ? authenticationToken.getAuthorizationCode().getTokenValue()
                        : "");
            if (StringUtils.isNotBlank(authenticationToken.getState())) {
                uriBuilder.queryParam(OAuth2ParameterNames.STATE,
                    UriUtils.encode(authenticationToken.getState(), StandardCharsets.UTF_8));
            }
            // build(true) -> Components are explicitly encoded
            String redirectUri = uriBuilder.build(true).toUriString();
            this.redirectStrategy.sendRedirect(request, response, redirectUri);
            return;
        }
        //简化模式
        if (authentication instanceof OAuth2AuthorizationImplicitAccessTokenAuthenticationToken authenticationToken) {
            Map<String, Object> vars = new LinkedHashMap<>();
            Map<String, String> keys = new HashMap<>();
            //响应类型
            String responseType = (String) authenticationToken.getAdditionalParameters()
                .get(RESPONSE_TYPE);
            Set<String> responseTypes = new HashSet<>(
                Arrays.asList(org.springframework.util.StringUtils
                    .delimitedListToStringArray(responseType, " ")));
            //响应类型包含 token
            if (responseTypes.contains(TOKEN.getValue())) {
                OAuth2AccessToken accessToken = authenticationToken.getAccessToken();
                vars.put(TOKEN_TYPE, accessToken.getTokenType().getValue());
                vars.put(ACCESS_TOKEN, accessToken.getTokenValue());
                if (accessToken.getIssuedAt() != null && accessToken.getExpiresAt() != null) {
                    vars.put(EXPIRES_IN, ChronoUnit.SECONDS.between(accessToken.getIssuedAt(),
                        accessToken.getExpiresAt()));
                }
            }
            //id_token
            if (responseTypes.contains(ID_TOKEN.getValue())) {
                vars.put(OidcParameterNames.ID_TOKEN,
                    authenticationToken.getAdditionalParameters().get(OidcParameterNames.ID_TOKEN));
            }
            //state
            String state = (String) authenticationToken.getAdditionalParameters().get(STATE);
            //redirectUri
            String redirectUri = (String) authenticationToken.getAdditionalParameters()
                .get(REDIRECT_URI);
            if (org.springframework.util.StringUtils.hasText(state)) {
                vars.put(OAuth2ParameterNames.STATE, state);
            }
            redirectUri = appendUrl(redirectUri, vars, keys, true);
            this.redirectStrategy.sendRedirect(request, response, redirectUri);
        }
    }

    /**
     * 发送异常响应
     *
     * @param request   {@link HttpServletRequest}
     * @param response  {@link HttpServletResponse}
     * @param exception {@link AuthenticationException}
     * @throws IOException IOException
     */
    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response,
                                   AuthenticationException exception) throws IOException {

        //隐式授权模式
        if (exception instanceof OAuth2AuthorizationImplicitRequestAuthenticationException authorizationImplicitRequestAuthenticationException) {
            OAuth2Error error = authorizationImplicitRequestAuthenticationException.getError();
            OAuth2Error responseError = new OAuth2Error(error.getErrorCode(),
                error.getDescription(), "https://eiam.topiam.cn");
            OAuth2AuthorizationImplicitRequestAuthenticationToken authorizationImplicitRequestAuthenticationToken = authorizationImplicitRequestAuthenticationException
                .getAuthorizationImplicitRequestAuthenticationToken();
            if (authorizationImplicitRequestAuthenticationToken == null || !StringUtils
                .isNotBlank(authorizationImplicitRequestAuthenticationToken.getRedirectUri())) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), responseError.toString());
                return;
            }

            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Redirecting to client with error");
            }
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(authorizationImplicitRequestAuthenticationToken.getRedirectUri())
                .queryParam(OAuth2ParameterNames.ERROR, responseError.getErrorCode());
            if (StringUtils.isNotBlank(responseError.getDescription())) {
                uriBuilder.queryParam(OAuth2ParameterNames.ERROR_DESCRIPTION,
                    UriUtils.encode(responseError.getDescription(), StandardCharsets.UTF_8));
            }
            if (StringUtils.isNotBlank(responseError.getUri())) {
                uriBuilder.queryParam(OAuth2ParameterNames.ERROR_URI,
                    UriUtils.encode(responseError.getUri(), StandardCharsets.UTF_8));
            }
            if (StringUtils
                .isNotBlank(authorizationImplicitRequestAuthenticationToken.getState())) {
                uriBuilder.queryParam(OAuth2ParameterNames.STATE,
                    UriUtils.encode(authorizationImplicitRequestAuthenticationToken.getState(),
                        StandardCharsets.UTF_8));
            }
            // build(true) -> Components are explicitly encoded
            String redirectUri = uriBuilder.build(true).toUriString();
            this.redirectStrategy.sendRedirect(request, response, redirectUri);
        }
        //授权码模式
        if (exception instanceof OAuth2AuthorizationCodeRequestAuthenticationException authorizationCodeRequestAuthenticationException) {
            OAuth2Error error = authorizationCodeRequestAuthenticationException.getError();
            OAuth2Error responseError = new OAuth2Error(error.getErrorCode(),
                error.getDescription(), "https://eiam.topiam.cn");
            OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication = authorizationCodeRequestAuthenticationException
                .getAuthorizationCodeRequestAuthentication();
            if (authorizationCodeRequestAuthentication == null || !StringUtils
                .isNotBlank(authorizationCodeRequestAuthentication.getRedirectUri())) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), responseError.toString());
                return;
            }

            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Redirecting to client with error");
            }
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(authorizationCodeRequestAuthentication.getRedirectUri())
                .queryParam(OAuth2ParameterNames.ERROR, responseError.getErrorCode());
            if (StringUtils.isNotBlank(responseError.getDescription())) {
                uriBuilder.queryParam(OAuth2ParameterNames.ERROR_DESCRIPTION,
                    UriUtils.encode(responseError.getDescription(), StandardCharsets.UTF_8));
            }
            if (StringUtils.isNotBlank(responseError.getUri())) {
                uriBuilder.queryParam(OAuth2ParameterNames.ERROR_URI,
                    UriUtils.encode(responseError.getUri(), StandardCharsets.UTF_8));
            }
            if (StringUtils.isNotBlank(authorizationCodeRequestAuthentication.getState())) {
                uriBuilder.queryParam(OAuth2ParameterNames.STATE, UriUtils.encode(
                    authorizationCodeRequestAuthentication.getState(), StandardCharsets.UTF_8));
            }
            // build(true) -> Components are explicitly encoded
            String redirectUri = uriBuilder.build(true).toUriString();
            this.redirectStrategy.sendRedirect(request, response, redirectUri);
        }

    }

    private final OAuth2AuthorizationService                   authorizationService;

    /**
     * 认证管理器
     */
    private final AuthenticationManager                        authenticationManager;

    /**
     * 授权端点匹配器
     */
    private final RequestMatcher                               authorizationEndpointMatcher;

    /**
     * 重定向策略
     */
    private final RedirectStrategy                             redirectStrategy              = new DefaultRedirectStrategy();

    /**
     * AuthenticationDetailsSource
     */
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource   = new WebAuthenticationDetailsSource();

    /**
     * 认证转换器
     */
    private AuthenticationConverter                            authenticationConverter;

    /**
     * 身份验证成功处理程序
     */
    private AuthenticationSuccessHandler                       authenticationSuccessHandler  = this::sendAuthorizationResponse;

    /**
     * 身份验证失败处理程序
     */
    private AuthenticationFailureHandler                       authenticationFailureHandler  = this::sendErrorResponse;

    /**
     * 会话身份策略
     */
    private SessionAuthenticationStrategy                      sessionAuthenticationStrategy = (authentication,
                                                                                                request,
                                                                                                response) -> {
                                                                                             };

    /**
     * Sets the {@link AuthenticationDetailsSource} used for building an authentication details instance from {@link HttpServletRequest}.
     *
     * @param authenticationDetailsSource the {@link AuthenticationDetailsSource} used for building an authentication details instance from {@link HttpServletRequest}
     */
    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        Assert.notNull(authenticationDetailsSource, "authenticationDetailsSource cannot be null");
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    /**
     * Sets the {@link AuthenticationConverter} used when attempting to extract an Authorization Request (or Consent) from {@link HttpServletRequest}
     * to an instance of {@link OAuth2AuthorizationCodeRequestAuthenticationToken} or {@link OAuth2AuthorizationConsentAuthenticationToken}
     * used for authenticating the request.
     *
     * @param authenticationConverter the {@link AuthenticationConverter} used when attempting to extract an Authorization Request (or Consent) from {@link HttpServletRequest}
     */
    public void setAuthenticationConverter(AuthenticationConverter authenticationConverter) {
        Assert.notNull(authenticationConverter, "authenticationConverter cannot be null");
        this.authenticationConverter = authenticationConverter;
    }

    /**
     * Sets the {@link AuthenticationSuccessHandler} used for handling an {@link OAuth2AuthorizationCodeRequestAuthenticationToken}
     * and returning the {@link OAuth2AuthorizationResponse Authorization Response}.
     *
     * @param authenticationSuccessHandler the {@link AuthenticationSuccessHandler} used for handling an {@link OAuth2AuthorizationCodeRequestAuthenticationToken}
     */
    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        Assert.notNull(authenticationSuccessHandler, "authenticationSuccessHandler cannot be null");
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    /**
     * Sets the {@link AuthenticationFailureHandler} used for handling an {@link OAuth2AuthorizationCodeRequestAuthenticationException}
     * and returning the {@link OAuth2Error Error Response}.
     *
     * @param authenticationFailureHandler the {@link AuthenticationFailureHandler} used for handling an {@link OAuth2AuthorizationCodeRequestAuthenticationException}
     */
    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        Assert.notNull(authenticationFailureHandler, "authenticationFailureHandler cannot be null");
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    /**
     * Sets the {@link SessionAuthenticationStrategy} used for handling an {@link OAuth2AuthorizationCodeRequestAuthenticationToken}
     * before calling the {@link AuthenticationSuccessHandler}.
     * If OpenID Connect is enabled, the default implementation tracks OpenID Connect sessions using a {@link SessionRegistry}.
     *
     * @param sessionAuthenticationStrategy the {@link SessionAuthenticationStrategy} used for handling an {@link OAuth2AuthorizationCodeRequestAuthenticationToken}
     */
    public void setSessionAuthenticationStrategy(SessionAuthenticationStrategy sessionAuthenticationStrategy) {
        Assert.notNull(sessionAuthenticationStrategy,
            "sessionAuthenticationStrategy cannot be null");
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
    }
}
