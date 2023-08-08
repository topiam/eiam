/*
 * eiam-openapi - Employee Identity and Access Management
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
package cn.topiam.employee.openapi.authorization;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Token认证过滤器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/14 21:22
 */
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {
    private final Logger                logger = LoggerFactory
        .getLogger(AccessTokenAuthenticationFilter.class);
    private final AuthenticationManager authenticationManager;

    public AccessTokenAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException,
                                                                      IOException {
        String token = resolveFromAuthorizationHeader(request);
        if (!StringUtils.isBlank(token)) {
            try {
                Authentication authentication = authenticationManager
                    .authenticate(new AccessTokenAuthenticationToken(token));
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            } catch (InvalidBearerTokenException ignored) {
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     *ACCESS_TOKEN_HEADER
     */
    public static final String ACCESS_TOKEN_HEADER = "x-topiam-access-token";

    private String resolveFromAuthorizationHeader(HttpServletRequest request) {
        return request.getHeader(ACCESS_TOKEN_HEADER);
    }

}
