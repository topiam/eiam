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
package cn.topiam.employee.core.security.context;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

/**
 * 封装在应用程序中使用的安全上下文访问。
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/9 23:07
 */
public interface SecurityContextAccessor {

    /**
     * Returns true if the current invocation is being made by
     * a client, not by or on behalf of (in the oauth sense) an end user.
     *
     * @return {@link  Boolean}
     */
    boolean isClient();

    /**
     * Returns true if the current invocation is being made by
     * a user, not by a client app.
     * @return {@link  Boolean}
     */
    boolean isUser();

    /**
     * true if the user has the "admin" role
     *
     * @return {@link  Boolean}
     */
    boolean isAdmin();

    /**
     * the current user identifier (not primary key)
     *
     * @return  {@link  Boolean}
     */
    String getUserId();

    /**
     * the current user name (the thing they login with)
     *
     * @return {@link String}
     */
    String getUserName();

    /**
     * the current client identifier or null
     *
     * @return {@link String}
     */
    String getClientId();

    /**
     *
     * Provides a representation of the current user/client authentication
     * information for use in logs
     *
     * @return  {@link String}
     */
    String getAuthenticationInfo();

    /**
     * the authorities of the current principal (or empty if there is
     * none)
     * @return {@link  Collection}
     */
    Collection<? extends GrantedAuthority> getAuthorities();

    /**
     *
     * the scopes of the current principal (or empty if there is
     * none)
     * @return {@link Collection}
     */
    Collection<String> getScopes();

}
