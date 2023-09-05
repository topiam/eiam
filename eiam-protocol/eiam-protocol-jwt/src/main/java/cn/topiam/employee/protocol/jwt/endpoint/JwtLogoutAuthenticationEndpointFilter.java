/*
 * eiam-protocol-jwt - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.jwt.endpoint;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.topiam.employee.protocol.jwt.authentication.JwtAuthenticationFailureHandler;
import cn.topiam.employee.protocol.jwt.authentication.JwtLogoutAuthenticationToken;
import cn.topiam.employee.protocol.jwt.endpoint.authentication.JwtLogoutAuthenticationConverter;
import cn.topiam.employee.protocol.jwt.exception.JwtAuthenticationException;
import cn.topiam.employee.protocol.jwt.exception.JwtError;
import cn.topiam.employee.protocol.jwt.exception.JwtErrorCodes;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.employee.protocol.jwt.constant.JwtProtocolConstants.JWT_ERROR_URI;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/9/4 20:14
 */
public final class JwtLogoutAuthenticationEndpointFilter extends OncePerRequestFilter {

    /**
     * 端点匹配器
     */
    private final RequestMatcher                               requestMatcher;

    /**
     * 身份验证失败处理程序
     */
    private AuthenticationFailureHandler                       authenticationFailureHandler = new JwtAuthenticationFailureHandler();

    /**
     * AuthenticationSuccessHandler
     */
    private AuthenticationSuccessHandler                       authenticationSuccessHandler = this::sendAuthorizationResponse;

    /**
     * LogoutHandler
     */
    private final LogoutHandler                                logoutHandler;

    /**
     * 认证转换器
     */
    private AuthenticationConverter                            authenticationConverter;

    /**
     * AuthenticationDetailsSource
     */
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource  = new WebAuthenticationDetailsSource();

    /**
     * 认证管理器
     */
    private final AuthenticationManager                        authenticationManager;

    public JwtLogoutAuthenticationEndpointFilter(RequestMatcher requestMatcher,
                                                 SessionRegistry sessionRegistry,
                                                 AuthenticationManager authenticationManager) {
        Assert.notNull(requestMatcher, "requestMatcher cannot be empty");
        Assert.notNull(sessionRegistry, "sessionRegistry cannot be empty");
        Assert.notNull(sessionRegistry, "authenticationManager cannot be empty");
        this.authenticationManager = authenticationManager;
        this.logoutHandler = new SecurityContextLogoutHandler();
        this.requestMatcher = requestMatcher;
        authenticationConverter = new JwtLogoutAuthenticationConverter();
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        Assert.notNull(authenticationFailureHandler, "authenticationFailureHandler cannot be null");
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        Assert.notNull(authenticationFailureHandler, "authenticationSuccessHandler cannot be null");
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    public void setAuthenticationConverter(AuthenticationConverter authenticationConverter) {
        Assert.notNull(authenticationConverter, "authenticationConverter cannot be null");
        this.authenticationConverter = authenticationConverter;
    }

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
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @param filterChain {@link FilterChain}
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException,
                                                                      IOException {
        if (!requestMatcher.matches(request)) {
            doFilter(request, response, filterChain);
            return;
        }
        try {
            Authentication authentication = authenticationConverter.convert(request);
            if (authentication instanceof AbstractAuthenticationToken) {
                ((AbstractAuthenticationToken) authentication)
                    .setDetails(this.authenticationDetailsSource.buildDetails(request));
            }
            Authentication authenticationResult = authenticationManager
                .authenticate(authentication);
            authenticationSuccessHandler.onAuthenticationSuccess(request, response,
                authenticationResult);
        } catch (JwtAuthenticationException ex) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace(LogMessage.format("JWT logout request failed: %s", ex.getError()),
                    ex);
            }
            this.authenticationFailureHandler.onAuthenticationFailure(request, response, ex);
        } catch (Exception ex) {
            JwtError error = new JwtError(JwtErrorCodes.SERVER_ERROR, ex.getMessage(),
                JWT_ERROR_URI);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace(error, ex);
            }
            this.authenticationFailureHandler.onAuthenticationFailure(request, response,
                new JwtAuthenticationException(error));
        }
    }

    /**
     * 发送成功响应
     *
     * @param request        {@link HttpServletRequest}
     * @param response       {@link HttpServletResponse}
     * @param authentication {@link Authentication}
     */
    private void sendAuthorizationResponse(HttpServletRequest request, HttpServletResponse response,
                                           Authentication authentication) {
        JwtLogoutAuthenticationToken jwtLogoutAuthentication = (JwtLogoutAuthenticationToken) authentication;
        // Check for active user session
        if (jwtLogoutAuthentication.isPrincipalAuthenticated()
            && StringUtils.hasText(jwtLogoutAuthentication.getSessionId())) {
            // Perform logout
            this.logoutHandler.logout(request, response,
                (Authentication) jwtLogoutAuthentication.getPrincipal());
        }
    }

}
