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
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import com.google.common.collect.Sets;

import cn.topiam.eiam.protocol.oidc.authentication.OAuth2AuthorizationImplicitAccessTokenAuthenticationToken;
import cn.topiam.eiam.protocol.oidc.authentication.OAuth2AuthorizationImplicitRequestAuthenticationException;
import cn.topiam.eiam.protocol.oidc.authentication.OAuth2AuthorizationImplicitRequestAuthenticationToken;
import cn.topiam.eiam.protocol.oidc.endpoint.authentication.OAuth2AuthorizationImplicitRequestAuthenticationConverter;
import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.application.oidc.model.OidcProtocolConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.UNSUPPORTED_RESPONSE_TYPE;
import static org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames.TOKEN_TYPE;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.*;

import static cn.topiam.eiam.protocol.oidc.constant.OidcProtocolConstants.OIDC_ERROR_URI;
import static cn.topiam.eiam.protocol.oidc.endpoint.OAuth2EndpointUtils.appendUrl;
import static cn.topiam.eiam.protocol.oidc.endpoint.OAuth2ParameterNames.FRAGMENT;
import static cn.topiam.eiam.protocol.oidc.endpoint.OAuth2ParameterNames.RESPONSE_MODE;
import static cn.topiam.eiam.protocol.oidc.endpoint.authentication.OAuth2AuthorizationImplicitRequestAuthenticationConverter.ID_TOKEN;
import static cn.topiam.eiam.protocol.oidc.endpoint.authentication.OAuth2AuthorizationImplicitRequestAuthenticationConverter.TOKEN;

/**
 * 处理授权请求
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/6/26 21:52
 * @see <a target="_blank" href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1">Section 4.1 Authorization Code Grant</a>
 * @see <a target="_blank" href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.1">Section 4.1.1 Authorization Request</a>
 * @see <a target="_blank" href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2">Section 4.1.2 Authorization Response</a>
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public final class OAuth2AuthorizationEndpointFilter extends OncePerRequestFilter {

    /**
     * The default endpoint {@code URI} for authorization requests.
     */
    private static final String         DEFAULT_AUTHORIZATION_ENDPOINT_URI = "/oauth2/authorize";
    private static final Authentication ANONYMOUS_AUTHENTICATION           = new AnonymousAuthenticationToken(
        "anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

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
            //@formatter:off
            //sept1：只允许GET 和POST 请求，并判断是否存在请求参数，没有请求对象，走自发起认证模式
            if (isRequestWithoutParams(request)) {
                //获取应用配置
                ApplicationContext context = ApplicationContextHolder.getApplicationContext();
                String clientId = context.getClientId();
                OidcProtocolConfig config = (OidcProtocolConfig) context.getConfig().get(OidcProtocolConfig.class.getName());

                String authorizationUri = request.getRequestURL().toString();

                Authentication principal = SecurityContextHolder.getContext().getAuthentication();
                if (principal == null) {
                    principal = ANONYMOUS_AUTHENTICATION;
                }
                //重定向地址未配置
                if (CollectionUtils.isEmpty(config.getRedirectUris())){
                    OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, "OAuth 2.0 Parameter: " + OAuth2ParameterNames.REDIRECT_URI,null);
                    throw new OAuth2AuthorizationCodeRequestAuthenticationException(error, null);
                }
                return new OAuth2AuthorizationCodeRequestAuthenticationToken(authorizationUri, clientId, principal, new ArrayList<>(config.getRedirectUris()).get(0), RandomStringUtils.randomAlphanumeric(32).toLowerCase(), config.getGrantScopes(),null);
            }
            try {
                //step2: 如果step1返回值为null，尝试走授权码模式转换器（此为Spring授权服务器内置）
                return new OAuth2AuthorizationCodeRequestAuthenticationConverter().convert(request);
            } catch (OAuth2AuthorizationCodeRequestAuthenticationException e) {
                //step3: 抛出不支持响应类型异常：unsupported_response_type，尝试走简化模式转换器
                if (UNSUPPORTED_RESPONSE_TYPE.equals(e.getError().getErrorCode())) {
                    return new OAuth2AuthorizationImplicitRequestAuthenticationConverter().convert(request);
                }
                throw e;
            }
            //@formatter:on
        };
    }

    /**
     * 创建默认请求匹配器
     *
     * @param authorizationEndpointUri {@link String} 授权端点URI
     * @return {@link RequestMatcher}
     */
    private static RequestMatcher createDefaultRequestMatcher(String authorizationEndpointUri) {
        //Get 请求
        RequestMatcher authorizationRequestGetMatcher = new AntPathRequestMatcher(
            authorizationEndpointUri, HttpMethod.GET.name());
        //Post 请求
        RequestMatcher authorizationRequestPostMatcher = new AntPathRequestMatcher(
            authorizationEndpointUri, HttpMethod.POST.name());
        //openid scope 匹配
        RequestMatcher openidScopeMatcher = request -> {
            String scope = request.getParameter(OAuth2ParameterNames.SCOPE);
            return StringUtils.isNotBlank(scope) && scope.contains(OidcScopes.OPENID);
        };
        //response_type 参数匹配
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
                .fromUriString(Objects.toString(authenticationToken.getRedirectUri()))
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
            String responseMode = (String) authenticationToken.getAdditionalParameters()
                .get(RESPONSE_MODE);
            //如果该参数为空，或者值为 fragment，则将参数拼接到url后面
            if (StringUtils.isBlank(responseMode) || StringUtils.equals(responseMode, FRAGMENT)) {
                redirectUri = appendUrl(redirectUri, vars, keys, true);
            }
            //query 模式
            else {
                redirectUri = appendUrl(redirectUri, vars, keys, false);
            }
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
        OAuth2Error error = null;
        AbstractAuthenticationToken requestAuthenticationToken = null;
        String redirectUri = null;
        String state = null;
        //隐式授权模式
        if (exception instanceof OAuth2AuthorizationImplicitRequestAuthenticationException e) {
            error = e.getError();
            //获取请求对象
            OAuth2AuthorizationImplicitRequestAuthenticationToken authenticationToken = e
                .getAuthorizationImplicitRequestAuthenticationToken();
            if (!Objects.isNull(authenticationToken)) {
                requestAuthenticationToken = authenticationToken;
                //获取重定向地址
                redirectUri = authenticationToken.getRedirectUri();
                //获取state
                state = authenticationToken.getState();
            }
        }
        //授权码模式
        if (exception instanceof OAuth2AuthorizationCodeRequestAuthenticationException e) {
            error = e.getError();

            //获取请求对象
            OAuth2AuthorizationCodeRequestAuthenticationToken authenticationToken = e
                .getAuthorizationCodeRequestAuthentication();
            if (!Objects.isNull(authenticationToken)) {
                requestAuthenticationToken = authenticationToken;
                //获取重定向地址
                redirectUri = authenticationToken.getRedirectUri();
                //获取state
                state = authenticationToken.getState();
            }

        }

        if (Objects.isNull(error)) {
            return;
        }

        // 包装错误响应
        OAuth2Error responseError = new OAuth2Error(error.getErrorCode(), error.getDescription(),
            OIDC_ERROR_URI);

        if (Objects.isNull(requestAuthenticationToken) || !StringUtils.isNotBlank(redirectUri)) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), responseError.toString());
            return;
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Redirecting to client with error");
        }

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(redirectUri)
            .queryParam(OAuth2ParameterNames.ERROR, responseError.getErrorCode());
        if (StringUtils.isNotBlank(responseError.getDescription())) {
            uriBuilder.queryParam(OAuth2ParameterNames.ERROR_DESCRIPTION,
                UriUtils.encode(responseError.getDescription(), StandardCharsets.UTF_8));
        }
        if (StringUtils.isNotBlank(responseError.getUri())) {
            uriBuilder.queryParam(OAuth2ParameterNames.ERROR_URI,
                UriUtils.encode(responseError.getUri(), StandardCharsets.UTF_8));
        }
        if (StringUtils.isNotBlank(state)) {
            uriBuilder.queryParam(OAuth2ParameterNames.STATE,
                UriUtils.encode(state, StandardCharsets.UTF_8));
        }
        // build(true) -> Components are explicitly encoded
        redirectUri = uriBuilder.build(true).toUriString();
        this.redirectStrategy.sendRedirect(request, response, redirectUri);

    }

    private boolean isRequestWithoutParams(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        return paramMap.isEmpty();
    }

    /**
     * OAuth2AuthorizationService
     */
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
