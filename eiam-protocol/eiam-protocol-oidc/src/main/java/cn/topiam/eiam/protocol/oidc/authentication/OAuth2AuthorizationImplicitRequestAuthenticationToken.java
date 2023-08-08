/*
 * eiam-protocol-oidc - Employee Identity and Access Management
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
package cn.topiam.eiam.protocol.oidc.authentication;

import java.io.Serial;
import java.util.*;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

/**
 * An {@link Authentication} implementation for the OAuth 2.0 Authorization Request
 * used in the Authorization Code Grant.
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/26 21:07
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class OAuth2AuthorizationImplicitRequestAuthenticationToken extends
                                                                   AbstractAuthenticationToken {

    @Serial
    private static final long         serialVersionUID = -5662861007848991326L;

    private final String              authorizationUri;
    private final String              clientId;
    private final Authentication      principal;
    private final String              redirectUri;
    private final String              state;
    private final Set<String>         scopes;

    private final Set<String>         responseTypes;
    private final Map<String, Object> additionalParameters;

    /**
     * Constructs an {@code OAuth2AuthorizationCodeRequestAuthenticationToken} using the provided parameters.
     *
     * @param authorizationUri     the authorization URI
     * @param clientId             the client identifier
     * @param principal            the {@code Principal} (Resource Owner)
     * @param redirectUri          the redirect uri
     * @param state                the state
     * @param scopes               the requested scope(s)
     * @param responseTypes        the response type(s)
     * @param additionalParameters the additional parameters
     */
    public OAuth2AuthorizationImplicitRequestAuthenticationToken(String authorizationUri,
                                                                 String clientId,
                                                                 Authentication principal,
                                                                 @Nullable String redirectUri,
                                                                 @Nullable String state,
                                                                 @Nullable Set<String> scopes,
                                                                 Set<String> responseTypes,
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
        this.responseTypes = Collections.unmodifiableSet(
            scopes != null ? new HashSet<>(responseTypes) : Collections.emptySet());
        this.additionalParameters = Collections
            .unmodifiableMap(additionalParameters != null ? new HashMap<>(additionalParameters)
                : Collections.emptyMap());
    }

    /**
     * Constructs an {@code OAuth2AuthorizationCodeRequestAuthenticationToken} using the provided parameters.
     *
     * @param authorizationUri the authorization URI
     * @param clientId         the client identifier
     * @param principal        the {@code Principal} (Resource Owner)
     * @param redirectUri      the redirect uri
     * @param state            the state
     * @param scopes           the authorized scope(s)
     * @param responseTypes    the response type(s)
     */
    public OAuth2AuthorizationImplicitRequestAuthenticationToken(String authorizationUri,
                                                                 String clientId,
                                                                 Authentication principal,
                                                                 @Nullable String redirectUri,
                                                                 @Nullable String state,
                                                                 @Nullable Set<String> scopes,
                                                                 Set<String> responseTypes) {
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
        this.responseTypes = Collections.unmodifiableSet(
            scopes != null ? new HashSet<>(responseTypes) : Collections.emptySet());
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
     * Returns the response types.
     *
     * @return the requested (or response) type(s), or an empty {@code Set} if not available
     */
    public Set<String> getResponseTypes() {
        return responseTypes;
    }
}
