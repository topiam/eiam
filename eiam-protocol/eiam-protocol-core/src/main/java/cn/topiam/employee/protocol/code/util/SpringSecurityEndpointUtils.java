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

import java.util.Optional;

import org.springframework.security.web.SecurityFilterChain;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/11/25 14:03
 */
public final class SpringSecurityEndpointUtils<T> {

    /**
     * The Oauth 2 endpoint filter.
     */
    private final T oauth2EndpointFilter;

    /**
     * Instantiates a new Spring doc security o auth 2 endpoint utils.
     *
     * @param oauth2EndpointFilter the oauth 2 endpoint filter
     */
    public SpringSecurityEndpointUtils(T oauth2EndpointFilter) {
        this.oauth2EndpointFilter = oauth2EndpointFilter;
    }

    /**
     * Find endpoint object.
     *
     * @param filterChain the filter chain
     * @return the object
     */
    public Object findEndpoint(SecurityFilterChain filterChain) {
        Optional<?> oAuth2EndpointFilterOptional = filterChain.getFilters().stream()
            .filter(((Class<?>) oauth2EndpointFilter)::isInstance)
            .map(((Class<?>) oauth2EndpointFilter)::cast).findAny();
        return oAuth2EndpointFilterOptional.orElse(null);
    }
}
