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
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.util.SpringAuthorizationServerVersion;
import org.springframework.util.Assert;

/**
 * 授权隐式请求身份验证令牌
 *
 * @see OAuth2AuthorizationCodeRequestAuthenticationToken 参考
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/28 22:01
 */
@SuppressWarnings({ "All" })
public final class EiamOAuth2AuthorizationImplicitAuthenticationToken extends
                                                                      AbstractAuthenticationToken {
    private static final long             serialVersionUID = SpringAuthorizationServerVersion.SERIAL_VERSION_UID;
    private final String                  authorizationUri;
    private final String                  clientId;
    private final Authentication          principal;
    private final String                  redirectUri;
    private final String                  state;
    private final Set<String>             scopes;
    private final Map<String, Object>     additionalParameters;
    private final OAuth2AuthorizationCode authorizationCode;

    /**
     * Constructs an {@code EiamOAuth2AuthorizationImplicitRequestAuthenticationToken} using the provided parameters.
     *
     * @param authorizationUri the authorization URI
     * @param clientId the client identifier
     * @param principal the {@code Principal} (Resource Owner)
     * @param redirectUri the redirect uri
     * @param state the state
     * @param scopes the requested scope(s)
     * @param additionalParameters the additional parameters
     * @since 0.4.0
     */
    public EiamOAuth2AuthorizationImplicitAuthenticationToken(String authorizationUri,
                                                              String clientId,
                                                              Authentication principal,
                                                              @Nullable String redirectUri,
                                                              @Nullable String state,
                                                              @Nullable Set<String> scopes,
                                                              @Nullable Map<String, Object> additionalParameters) {
        super(Collections.emptyList());
        Assert.hasText(authorizationUri, "authorizationUri cannot be empty");
        Assert.hasText(clientId, "clientId cannot be empty");
        Assert.notNull(principal, "principal cannot be null");
        this.authorizationUri = authorizationUri;
        this.clientId = clientId;
        this.principal = principal;
        this.redirectUri = redirectUri;
        this.state = state;
        this.scopes = Collections
            .unmodifiableSet(scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
        this.additionalParameters = Collections
            .unmodifiableMap(additionalParameters != null ? new HashMap<>(additionalParameters)
                : Collections.emptyMap());
        this.authorizationCode = null;
    }

    /**
     * Constructs an {@code EiamOAuth2AuthorizationImplicitRequestAuthenticationToken} using the provided parameters.
     *
     * @param authorizationUri the authorization URI
     * @param clientId the client identifier
     * @param principal the {@code Principal} (Resource Owner)
     * @param authorizationCode the {@link OAuth2AuthorizationCode}
     * @param redirectUri the redirect uri
     * @param state the state
     * @param scopes the authorized scope(s)
     * @since 0.4.0
     */
    public EiamOAuth2AuthorizationImplicitAuthenticationToken(String authorizationUri,
                                                              String clientId,
                                                              Authentication principal,
                                                              OAuth2AuthorizationCode authorizationCode,
                                                              @Nullable String redirectUri,
                                                              @Nullable String state,
                                                              @Nullable Set<String> scopes) {
        super(Collections.emptyList());
        Assert.hasText(authorizationUri, "authorizationUri cannot be empty");
        Assert.hasText(clientId, "clientId cannot be empty");
        Assert.notNull(principal, "principal cannot be null");
        Assert.notNull(authorizationCode, "authorizationCode cannot be null");
        this.authorizationUri = authorizationUri;
        this.clientId = clientId;
        this.principal = principal;
        this.authorizationCode = authorizationCode;
        this.redirectUri = redirectUri;
        this.state = state;
        this.scopes = Collections
            .unmodifiableSet(scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
        this.additionalParameters = Collections.emptyMap();
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
     * Returns the redirect uri.
     *
     * @return the redirect uri
     */
    @Nullable
    public String getRedirectUri() {
        return this.redirectUri;
    }

    /**
     * Returns the state.
     *
     * @return the state
     */
    @Nullable
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

    /**
     * Returns the {@link OAuth2AuthorizationCode}.
     *
     * @return the {@link OAuth2AuthorizationCode}
     */
    @Nullable
    public OAuth2AuthorizationCode getAuthorizationCode() {
        return this.authorizationCode;
    }

}
