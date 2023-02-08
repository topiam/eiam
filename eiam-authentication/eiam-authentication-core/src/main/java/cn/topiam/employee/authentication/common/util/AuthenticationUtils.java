/*
 * eiam-authentication-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.authentication.common.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import cn.topiam.employee.core.security.authentication.IdpAuthentication;
import cn.topiam.employee.core.security.authentication.SmsAuthentication;
import cn.topiam.employee.core.security.mfa.MfaAuthentication;
import static cn.topiam.employee.authentication.common.IdentityProviderType.SMS;
import static cn.topiam.employee.authentication.common.IdentityProviderType.USERNAME_PASSWORD;

/**
 *
 * @author SanLi
 * Created by qinggang.zuo@gmail.com / 2689170096@qq.com on  2022/12/31 14:29
 */
public class AuthenticationUtils {
    /**
     * 获取认证类型
     *
     * @param authentication {@link Authentication}
     * @return {@link String}
     */
    public static String geAuthType(Authentication authentication) {
        //用户名密码
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return USERNAME_PASSWORD.value();
        }
        //身份提供商
        if (authentication instanceof IdpAuthentication) {
            return ((IdpAuthentication) authentication).getProviderType();
        }
        //短信登录
        if (authentication instanceof SmsAuthentication) {
            return SMS.value();
        }
        //MFA
        if (authentication instanceof MfaAuthentication) {
            return geAuthType(((MfaAuthentication) authentication).getFirst());
        }
        throw new IllegalArgumentException("未知认证对象");
    }
}
