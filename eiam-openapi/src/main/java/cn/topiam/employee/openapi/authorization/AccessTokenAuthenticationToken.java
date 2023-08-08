/*
 * eiam-openapi - Employee Identity and Access Management
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
package cn.topiam.employee.openapi.authorization;

import java.io.Serial;
import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.Assert;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/25 23:29
 */
public class AccessTokenAuthenticationToken extends AbstractAuthenticationToken {

    @Serial
    private static final long serialVersionUID = -1075066894919649480L;

    private final String      clientId;
    private final String      token;

    /**
     * Create a {@code BearerTokenAuthenticationToken} using the provided parameter(s)
     *
     * @param token    - the bearer token
     */
    public AccessTokenAuthenticationToken(String token) {
        super(Collections.emptyList());
        Assert.hasText(token, "token cannot be empty");
        this.token = token;
        this.clientId = "";
    }

    /**
     * Create a {@code BearerTokenAuthenticationToken} using the provided parameter(s)
     *
     * @param clientId clientId
     * @param token    - the bearer token
     */
    public AccessTokenAuthenticationToken(String clientId, String token, boolean authenticated) {
        super(Collections.emptyList());
        Assert.hasText(clientId, "clientId cannot be empty");
        Assert.hasText(token, "token cannot be empty");
        this.token = token;
        this.clientId = clientId;
        setAuthenticated(authenticated);
    }

    /**
     * Get the
     * <a href="https://tools.ietf.org/html/rfc6750#section-1.2" target="_blank">Bearer
     * Token</a>
     * @return the token that proves the caller's authority to perform the
     * {@link jakarta.servlet.http.HttpServletRequest}
     */
    public String getToken() {
        return this.token;
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public Object getCredentials() {
        return this.getToken();
    }

    @Override
    public Object getPrincipal() {
        return this.getToken();
    }

}
