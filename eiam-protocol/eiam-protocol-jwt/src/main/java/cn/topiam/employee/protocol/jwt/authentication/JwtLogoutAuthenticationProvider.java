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
package cn.topiam.employee.protocol.jwt.authentication;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import cn.topiam.employee.application.jwt.model.JwtProtocolConfig;
import cn.topiam.employee.protocol.jwt.exception.JwtAuthenticationException;
import cn.topiam.employee.protocol.jwt.exception.JwtError;
import static cn.topiam.employee.protocol.jwt.constant.JwtProtocolConstants.JWT_ERROR_URI;
import static cn.topiam.employee.protocol.jwt.constant.JwtProtocolConstants.POST_LOGOUT_REDIRECT_URI;
import static cn.topiam.employee.protocol.jwt.exception.JwtErrorCodes.SERVER_ERROR;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/9/4 16:11
 */
public final class JwtLogoutAuthenticationProvider implements AuthenticationProvider {
    private final Logger logger = LoggerFactory.getLogger(JwtLogoutAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtLogoutAuthenticationToken logoutAuthenticationToken = (JwtLogoutAuthenticationToken) authentication;
        JwtProtocolConfig config = logoutAuthenticationToken.getConfig();
        //校验注销后重定向地址
        if (!StringUtils.equals(logoutAuthenticationToken.getPostLogoutRedirectUri(),
            config.getPostLogoutRedirectUri())) {
            logger.info(String.format(
                "Jwt logout: with post_logout_redirect_uri %s does not match supplied post_logout_redirect_uri %s.",
                logoutAuthenticationToken.getPostLogoutRedirectUri(),
                config.getPostLogoutRedirectUri()));
            JwtError error = new JwtError(SERVER_ERROR,
                "Jwt Logout Request Parameter: " + POST_LOGOUT_REDIRECT_URI, JWT_ERROR_URI);
            throw new JwtAuthenticationException(error);
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtLogoutAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
