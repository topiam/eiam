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
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/9 23:12
 */
public final class DefaultSecurityContextAccessor implements SecurityContextAccessor {

    /**
     * Returns true if the current invocation is being made by
     * a client, not by or on behalf of (in the oauth sense) an end user.
     *
     * @return {@link  Boolean}
     */
    @Override
    public boolean isClient() {
        return false;
    }

    /**
     * Returns true if the current invocation is being made by
     * a user, not by a client app.
     *
     * @return {@link  Boolean}
     */
    @Override
    public boolean isUser() {
        return false;
    }

    /**
     * true if the user has the "admin" role
     *
     * @return {@link  Boolean}
     */
    @Override
    public boolean isAdmin() {
        return false;
    }

    /**
     * the current user identifier (not primary key)
     *
     * @return {@link  Boolean}
     */
    @Override
    public String getUserId() {
        return null;
    }

    /**
     * the current user name (the thing they login with)
     *
     * @return {@link String}
     */
    @Override
    public String getUserName() {
        return null;
    }

    /**
     * the current client identifier or null
     *
     * @return {@link String}
     */
    @Override
    public String getClientId() {
        return null;
    }

    /**
     * Provides a representation of the current user/client authentication
     * information for use in logs
     *
     * @return {@link String}
     */
    @Override
    public String getAuthenticationInfo() {
        return null;
    }

    /**
     * the authorities of the current principal (or empty if there is
     * none)
     *
     * @return {@link  Collection}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /**
     * the scopes of the current principal (or empty if there is
     * none)
     *
     * @return {@link Collection}
     */
    @Override
    public Collection<String> getScopes() {
        return null;
    }
}
