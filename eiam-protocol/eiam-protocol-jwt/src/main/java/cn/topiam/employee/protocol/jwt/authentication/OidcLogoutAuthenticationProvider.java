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

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

/**
 *
 * @author SanLi
 * Created by qinggang.zuo@gmail.com / 2689170096@qq.com on  2023/9/4 16:11
 */
public final class OidcLogoutAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtLogoutAuthenticationToken logoutAuthenticationToken = (JwtLogoutAuthenticationToken) authentication;
        SessionInformation sessionInformation = sessionRegistry
            .getSessionInformation(logoutAuthenticationToken.getSessionId());
        if (sessionInformation.isExpired()) {

        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtLogoutAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private final SessionRegistry sessionRegistry;

    public OidcLogoutAuthenticationProvider(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }
}
