/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.security.authentication;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;

import cn.topiam.employee.core.security.mfa.MfaAuthentication;

/**
 * 身份验证信任解析器实现
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/28 23:13
 */
public class AuthenticationTrustResolverImpl implements AuthenticationTrustResolver {

    private final AuthenticationTrustResolver delegate = new org.springframework.security.authentication.AuthenticationTrustResolverImpl();

    @Override
    public boolean isAnonymous(Authentication authentication) {
        return this.delegate.isAnonymous(authentication)
               || authentication instanceof MfaAuthentication
               //  IDP 提供商，未认证为匿名
               || (authentication instanceof IdpAuthentication
                   && !authentication.isAuthenticated());
    }

    @Override
    public boolean isRememberMe(Authentication authentication) {
        return this.delegate.isRememberMe(authentication);
    }

}
