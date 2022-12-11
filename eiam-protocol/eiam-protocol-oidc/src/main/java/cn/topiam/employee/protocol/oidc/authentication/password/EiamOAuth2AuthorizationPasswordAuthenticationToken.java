/*
 * eiam-protocol-oidc - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.oidc.authentication.password;

import java.io.Serial;
import java.util.*;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.Assert;

/**
 * OAuth2AuthorizationPasswordAuthenticationToken
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/27 22:45
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class EiamOAuth2AuthorizationPasswordAuthenticationToken extends
                                                                AbstractAuthenticationToken {

    @Serial
    private static final long            serialVersionUID = -6067207202119450764L;

    private final AuthorizationGrantType authorizationGrantType;
    private final Authentication         clientPrincipal;
    private final Set<String>            scopes;
    private final Map<String, Object>    additionalParameters;

    public EiamOAuth2AuthorizationPasswordAuthenticationToken(AuthorizationGrantType authorizationGrantType,
                                                              Authentication clientPrincipal,
                                                              @Nullable Set<String> scopes,
                                                              @Nullable Map<String, Object> additionalParameters) {
        super(Collections.emptyList());
        Assert.notNull(authorizationGrantType, "authorizationGrantType cannot be null");
        Assert.notNull(clientPrincipal, "clientPrincipal cannot be null");
        this.authorizationGrantType = authorizationGrantType;
        this.clientPrincipal = clientPrincipal;
        this.scopes = Collections
            .unmodifiableSet(scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
        this.additionalParameters = Collections
            .unmodifiableMap(additionalParameters != null ? new HashMap<>(additionalParameters)
                : Collections.emptyMap());
    }

    /**
     * Returns the authorization grant type.
     *
     * @return the authorization grant type
     */
    public AuthorizationGrantType getGrantType() {
        return this.authorizationGrantType;
    }

    @Override
    public Object getPrincipal() {
        return this.clientPrincipal;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    /**
     * Returns the requested scope(s).
     *
     * @return the requested scope(s), or an empty {@code Set} if not available
     */
    public Set<String> getScopes() {
        return this.scopes;
    }

    /**
     * Returns the additional parameters.
     *
     * @return the additional parameters
     */
    public Map<String, Object> getAdditionalParameters() {
        return this.additionalParameters;
    }

}
