/*
 * eiam-protocol-core - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.code.util;

import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.core.session.SessionRegistry;

import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.support.security.authentication.WebAuthenticationDetailsSource;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/21 21:35
 */
public class ProtocolConfigUtils {

    public static <B extends HttpSecurityBuilder<B>> SessionRegistry getSessionRegistry(B builder) {
        SessionRegistry sessionRegistry = builder.getSharedObject(SessionRegistry.class);
        if (sessionRegistry == null) {
            sessionRegistry = getBean(builder, SessionRegistry.class);
            builder.setSharedObject(SessionRegistry.class, sessionRegistry);
        }
        return sessionRegistry;
    }

    public static <B extends HttpSecurityBuilder<B>> ApplicationServiceLoader getApplicationServiceLoader(B builder) {
        ApplicationServiceLoader applicationServiceLoader = builder
            .getSharedObject(ApplicationServiceLoader.class);
        if (applicationServiceLoader == null) {
            applicationServiceLoader = getBean(builder, ApplicationServiceLoader.class);
            builder.setSharedObject(ApplicationServiceLoader.class, applicationServiceLoader);
        }
        return applicationServiceLoader;
    }

    public static <B extends HttpSecurityBuilder<B>> WebAuthenticationDetailsSource getAuthenticationDetailsSource(B builder) {
        WebAuthenticationDetailsSource authenticationDetailsSource = builder
            .getSharedObject(WebAuthenticationDetailsSource.class);
        if (authenticationDetailsSource == null) {
            authenticationDetailsSource = getBean(builder, WebAuthenticationDetailsSource.class);
            builder.setSharedObject(WebAuthenticationDetailsSource.class,
                authenticationDetailsSource);
        }
        return authenticationDetailsSource;
    }

    public static <B extends HttpSecurityBuilder<B>, T> T getBean(B builder, Class<T> type) {
        return builder.getSharedObject(ApplicationContext.class).getBean(type);
    }
}
