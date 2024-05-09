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
package cn.topiam.employee.protocol.jwt.configurers;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.protocol.code.configurer.AuthenticationUtils;
import cn.topiam.employee.protocol.jwt.InMemoryJwtAuthorizationService;
import cn.topiam.employee.protocol.jwt.JwtAuthorizationService;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/7/8 23:19
 */
public class JwtAuthenticationUtils {

    public static JwtAuthorizationService getAuthorizationService(HttpSecurity httpSecurity) {
        JwtAuthorizationService authorizationService = httpSecurity
            .getSharedObject(JwtAuthorizationService.class);
        if (authorizationService == null) {
            authorizationService = AuthenticationUtils.getOptionalBean(httpSecurity,
                JwtAuthorizationService.class);
            if (authorizationService == null) {
                ApplicationServiceLoader applicationServiceLoader = AuthenticationUtils
                    .getOptionalBean(httpSecurity, ApplicationServiceLoader.class);
                authorizationService = new InMemoryJwtAuthorizationService(
                    applicationServiceLoader);
            }
            httpSecurity.setSharedObject(JwtAuthorizationService.class, authorizationService);
        }
        return authorizationService;
    }
}
