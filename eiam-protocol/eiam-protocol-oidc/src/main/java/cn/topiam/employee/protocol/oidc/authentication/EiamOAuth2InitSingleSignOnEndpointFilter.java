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
package cn.topiam.employee.protocol.oidc.authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.collect.Sets;

import cn.topiam.employee.application.exception.AppNotExistException;
import cn.topiam.employee.core.security.savedredirect.HttpSessionRedirectCache;
import cn.topiam.employee.core.security.savedredirect.RedirectCache;
import cn.topiam.employee.support.exception.TopIamException;
import static cn.topiam.employee.common.constants.ProtocolConstants.APP_CODE;
import static cn.topiam.employee.common.constants.ProtocolConstants.OidcEndpointConstants.AUTHORIZATION_ENDPOINT;
import static cn.topiam.employee.core.security.util.SecurityUtils.isAuthenticated;
import static cn.topiam.employee.protocol.oidc.constant.ProtocolConstants.IDP_OAUTH2_SSO_INITIATOR;

/**
 * IDP 发起单点登录端点
 *
 * 原理就是根据应用配置生成 OAuth2AuthorizationCodeRequestAuthenticationToken，然后调用authenticate，重定向到应用配置的重定向地址。使用的是授权码模式。
 *
 * IDP 发起登录还有一种方式，那就是配置一个应用地址，当应用发现未登录时，将自动跳转到IDP进行授权
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/7 22:46
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class EiamOAuth2InitSingleSignOnEndpointFilter extends OncePerRequestFilter
                                                      implements OrderedFilter {
    private static final String                                ERROR_URI                    = "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1";
    /**
     * 请求路径
     */
    private static final RequestMatcher                        REQUEST_MATCHER              = new AntPathRequestMatcher(
        IDP_OAUTH2_SSO_INITIATOR, HttpMethod.POST.name());
    /**
     * 重定向缓存
     */
    private final RedirectCache                                redirectCache                = new HttpSessionRedirectCache();
    /**
     * 重定向策略
     */
    private final RedirectStrategy                             redirectStrategy             = new DefaultRedirectStrategy();
    /**
     * 授权成功处理器
     */
    private final AuthenticationSuccessHandler                 authenticationSuccessHandler = this::sendAuthorizationResponse;
    /**
     * 授权失败处理器
     */
    private final AuthenticationFailureHandler                 authenticationFailureHandler = this::sendErrorResponse;
    /**
     * 认证管理器
     */
    private final AuthenticationManager                        authenticationManager;

    /**
     * 认证转换器
     */
    private AuthenticationConverter                            authenticationConverter      = this::authenticationConverter;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource  = new WebAuthenticationDetailsSource();

    public EiamOAuth2InitSingleSignOnEndpointFilter(AuthenticationManager authenticationManager,
                                                    RegisteredClientRepository registeredClientRepository) {
        Assert.notNull(authenticationManager, "authenticationManager cannot be null");
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        this.authenticationManager = authenticationManager;
        this.registeredClientRepository = registeredClientRepository;
    }

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request     {@link HttpServletRequest}
     * @param response    {@link HttpServletResponse}
     * @param filterChain {@link FilterChain}
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException,
                                                                      IOException {
        //@formatter:off
        if (REQUEST_MATCHER.matches(request)) {
            try {
                if (!isAuthenticated()) {
                    //Saved Redirect
                    if (!CollectionUtils.isEmpty(request.getParameterMap())) {
                        redirectCache.saveRedirect(request, response, RedirectCache.RedirectType.REQUEST);
                    }
                    filterChain.doFilter(request, response);
                    return;
                }
                //获取 OAuth2AuthorizationCodeRequestAuthenticationToken
                OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication =
                        (OAuth2AuthorizationCodeRequestAuthenticationToken) this.authenticationConverter.convert(request);
                authorizationCodeRequestAuthentication.setDetails(this.authenticationDetailsSource.buildDetails(request));
                //调用 authenticationManager 进行认证操作
                OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthenticationResult = (OAuth2AuthorizationCodeRequestAuthenticationToken) this.authenticationManager.authenticate(authorizationCodeRequestAuthentication);
                //验证成功
                authenticationSuccessHandler.onAuthenticationSuccess(request, response, authorizationCodeRequestAuthenticationResult);
                return;
            } catch (OAuth2AuthenticationException e) {
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }
        filterChain.doFilter(request, response);
        //@formatter:on
    }

    private void sendAuthorizationResponse(HttpServletRequest request, HttpServletResponse response,
                                           Authentication authentication) throws IOException {

        OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication = (OAuth2AuthorizationCodeRequestAuthenticationToken) authentication;
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
            .fromUriString(authorizationCodeRequestAuthentication.getRedirectUri())
            .queryParam(OAuth2ParameterNames.CODE,
                authorizationCodeRequestAuthentication.getAuthorizationCode().getTokenValue());
        if (StringUtils.hasText(authorizationCodeRequestAuthentication.getState())) {
            uriBuilder.queryParam(OAuth2ParameterNames.STATE,
                authorizationCodeRequestAuthentication.getState());
        }
        this.redirectStrategy.sendRedirect(request, response, uriBuilder.toUriString());
    }

    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response,
                                   AuthenticationException exception) throws IOException {

        OAuth2AuthorizationCodeRequestAuthenticationException authorizationCodeRequestAuthenticationException = (OAuth2AuthorizationCodeRequestAuthenticationException) exception;
        OAuth2Error error = authorizationCodeRequestAuthenticationException.getError();
        OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication = authorizationCodeRequestAuthenticationException
            .getAuthorizationCodeRequestAuthentication();

        if (authorizationCodeRequestAuthentication == null
            || !StringUtils.hasText(authorizationCodeRequestAuthentication.getRedirectUri())) {
            // TODO Send default html error response
            response.sendError(HttpStatus.BAD_REQUEST.value(), error.toString());
            return;
        }

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
            .fromUriString(authorizationCodeRequestAuthentication.getRedirectUri())
            .queryParam(OAuth2ParameterNames.ERROR, error.getErrorCode());
        if (StringUtils.hasText(error.getDescription())) {
            uriBuilder.queryParam(OAuth2ParameterNames.ERROR_DESCRIPTION, error.getDescription());
        }
        if (StringUtils.hasText(error.getUri())) {
            uriBuilder.queryParam(OAuth2ParameterNames.ERROR_URI, error.getUri());
        }
        if (StringUtils.hasText(authorizationCodeRequestAuthentication.getState())) {
            uriBuilder.queryParam(OAuth2ParameterNames.STATE,
                authorizationCodeRequestAuthentication.getState());
        }
        this.redirectStrategy.sendRedirect(request, response, uriBuilder.toUriString());
    }

    private Authentication authenticationConverter(HttpServletRequest request) {
        //@formatter:off
        try {
            //获取应用配置
            Map<String, String> variables = REQUEST_MATCHER.matcher(request).getVariables();
            String providerId = variables.get(APP_CODE);
            RegisteredClient client = registeredClientRepository.findById(providerId);
            if (Objects.isNull(client)){
                throw new AppNotExistException();
            }
            Authentication principal = SecurityContextHolder.getContext().getAuthentication();

            return new OAuth2AuthorizationCodeRequestAuthenticationToken(AUTHORIZATION_ENDPOINT,
                    client.getClientId(), principal, new ArrayList<>(client.getRedirectUris()).get(0),
                    "", Sets.newLinkedHashSet(),null);

        } catch (TopIamException e) {
            OAuth2Error error = new OAuth2Error(e.getErrorCode(), e.getMessage(), ERROR_URI);
            throw new OAuth2AuthorizationCodeRequestAuthenticationException(error, null);
        }
        //@formatter:on
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    public static RequestMatcher getRequestMatcher() {
        return REQUEST_MATCHER;
    }

    /**
     * RegisteredClientRepository
     */
    private final RegisteredClientRepository registeredClientRepository;
}
