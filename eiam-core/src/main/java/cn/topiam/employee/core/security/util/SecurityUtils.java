/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.security.util;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.event.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import cn.topiam.employee.common.enums.UserType;
import cn.topiam.employee.core.security.userdetails.UserDetails;

/**
 * Spring Security的实用程序类。
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2019/5/7
 */
public final class SecurityUtils {
    /**
     * 匿名用户
     */
    public static final String ANONYMOUS_USER = "anonymousUser";

    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static String getCurrentUserId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Optional<String> optional = Optional.ofNullable(securityContext.getAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof UserDetails) {
                    return ((UserDetails) authentication.getPrincipal()).getId();
                }
                return null;
            });
        return optional.orElse(ANONYMOUS_USER);
    }

    /**
     * Get SecurityContext
     *
     * @return {@link  SecurityContext}
     */
    public static SecurityContext getSecurityContext() {
        return SecurityContextHolder.getContext();
    }

    /**
     * Get CurrentUser
     *
     * @return {@link  UserDetails}
     */
    public static UserDetails getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Optional<UserDetails> optional = Optional.ofNullable(securityContext.getAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof UserDetails) {
                    return (UserDetails) authentication.getPrincipal();
                }
                return null;
            });
        return optional.orElse(null);
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static String getCurrentUserName() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Optional<String> optional = Optional.ofNullable(securityContext.getAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof UserDetails) {
                    return ((UserDetails) authentication.getPrincipal()).getUsername();
                }
                //UserDetails
                if (authentication
                    .getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
                    return ((org.springframework.security.core.userdetails.UserDetails) authentication
                        .getPrincipal()).getUsername();
                }
                // String
                else if (authentication.getPrincipal() instanceof String) {
                    return (String) authentication.getPrincipal();
                }
                return null;
            });
        return optional.orElse(ANONYMOUS_USER);
    }

    /**
     * Get the login of the current user authorities.
     *
     * @return the login of the current user authorities.
     */
    public static Collection<? extends GrantedAuthority> getCurrentUserAuthorities() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Optional<? extends Collection<? extends GrantedAuthority>> authorities = Optional
            .ofNullable(securityContext.getAuthentication()).map(Authentication::getAuthorities);
        return authorities.orElse(null);
    }

    public static boolean isCurrentUserInRole(String authority) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
            .map(authentication -> authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority)))
            .orElse(false);
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityUtils.getSecurityContext().getAuthentication();
        return !Objects.isNull(authentication) && !TRUST_RESOLVER.isAnonymous(authentication)
               && authentication.isAuthenticated();
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static UserType getCurrentUserType() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Optional<UserType> optional = Optional.ofNullable(securityContext.getAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof UserDetails) {
                    return (((UserDetails) authentication.getPrincipal()).getUserType());
                }
                return null;
            });
        return optional.orElse(UserType.UNKNOWN);
    }

    /**
     * 获取错误信息
     *
     * @param event {@link AbstractAuthenticationFailureEvent}
     * @return {@link String}
     */
    public static String getFailureMessage(AbstractAuthenticationFailureEvent event) {
        String message = "未知错误";
        if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            message = "登录密码错误";
        }
        if (event instanceof AuthenticationFailureCredentialsExpiredEvent) {
            message = "账户密码过期";
        }
        if (event instanceof AuthenticationFailureDisabledEvent) {
            message = "账户已禁用";
        }
        if (event instanceof AuthenticationFailureExpiredEvent) {
            message = "帐户已过期";
        }
        if (event instanceof AuthenticationFailureLockedEvent) {
            message = "账户已锁定";
        }
        if (event instanceof AuthenticationFailureProxyUntrustedEvent) {
            message = "代理不受信任";
        }
        if (event instanceof AuthenticationFailureProviderNotFoundEvent) {
            message = "提供商配置错误";
        }
        if (event instanceof AuthenticationFailureServiceExceptionEvent) {
            message = "发生内部异常";
        }
        return message;
    }

    private static final AuthenticationTrustResolver TRUST_RESOLVER = new AuthenticationTrustResolverImpl();

}
