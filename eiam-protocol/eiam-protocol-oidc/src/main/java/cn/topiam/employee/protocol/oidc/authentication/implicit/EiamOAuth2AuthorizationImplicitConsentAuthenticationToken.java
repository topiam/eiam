/*
 * eiam-protocol-oidc - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.oidc.authentication.implicit;

import java.util.*;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.util.SpringAuthorizationServerVersion;
import org.springframework.util.Assert;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/11/9 22:41
 */
@SuppressWarnings("All")
public class EiamOAuth2AuthorizationImplicitConsentAuthenticationToken extends
                                                                       AbstractAuthenticationToken {
    private static final long         serialVersionUID = SpringAuthorizationServerVersion.SERIAL_VERSION_UID;
    private final String              authorizationUri;
    private final String              clientId;
    private final Authentication      principal;
    private final String              state;
    private final Set<String>         scopes;
    private final Map<String, Object> additionalParameters;

    /**
     * Constructs an {@code OAuth2AuthorizationConsentAuthenticationToken} using the provided parameters.
     *
     * @param authorizationUri the authorization URI
     * @param clientId the client identifier
     * @param principal the {@code Principal} (Resource Owner)
     * @param state the state
     * @param scopes the requested (or authorized) scope(s)
     * @param additionalParameters the additional parameters
     */
    public EiamOAuth2AuthorizationImplicitConsentAuthenticationToken(String authorizationUri,
                                                                     String clientId,
                                                                     Authentication principal,
                                                                     String state,
                                                                     @Nullable Set<String> scopes,
                                                                     @Nullable Map<String, Object> additionalParameters) {
        super(Collections.emptyList());
        Assert.hasText(authorizationUri, "authorizationUri cannot be empty");
        Assert.hasText(clientId, "clientId cannot be empty");
        Assert.notNull(principal, "principal cannot be null");
        Assert.hasText(state, "state cannot be empty");
        this.authorizationUri = authorizationUri;
        this.clientId = clientId;
        this.principal = principal;
        this.state = state;
        this.scopes = Collections
            .unmodifiableSet(scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
        this.additionalParameters = Collections
            .unmodifiableMap(additionalParameters != null ? new HashMap<>(additionalParameters)
                : Collections.emptyMap());
        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    /**
     * Returns the authorization URI.
     *
     * @return the authorization URI
     */
    public String getAuthorizationUri() {
        return this.authorizationUri;
    }

    /**
     * Returns the client identifier.
     *
     * @return the client identifier
     */
    public String getClientId() {
        return this.clientId;
    }

    /**
     * Returns the state.
     *
     * @return the state
     */
    public String getState() {
        return this.state;
    }

    /**
     * Returns the requested (or authorized) scope(s).
     *
     * @return the requested (or authorized) scope(s), or an empty {@code Set} if not available
     */
    public Set<String> getScopes() {
        return this.scopes;
    }

    /**
     * Returns the additional parameters.
     *
     * @return the additional parameters, or an empty {@code Map} if not available
     */
    public Map<String, Object> getAdditionalParameters() {
        return this.additionalParameters;
    }

}
