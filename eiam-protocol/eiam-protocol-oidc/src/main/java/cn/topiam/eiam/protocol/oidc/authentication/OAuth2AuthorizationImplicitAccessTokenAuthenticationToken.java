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
import java.util.Collections;
import java.util.Map;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.util.Assert;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/28 22:43
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class OAuth2AuthorizationImplicitAccessTokenAuthenticationToken extends
                                                                       AbstractAuthenticationToken {

    @Serial
    private static final long         serialVersionUID = 3310860855517523150L;

    private final RegisteredClient    registeredClient;
    private final Authentication      clientPrincipal;
    private final OAuth2AccessToken   accessToken;

    private final Map<String, Object> additionalParameters;

    /**
     * Constructs an {@code OAuth2AuthorizationImplicitAccessTokenAuthenticationToken} using the provided parameters.
     *
     * @param registeredClient the registered client
     * @param clientPrincipal  the authenticated client principal
     * @param accessToken      the access token
     */
    public OAuth2AuthorizationImplicitAccessTokenAuthenticationToken(RegisteredClient registeredClient,
                                                                     Authentication clientPrincipal,
                                                                     OAuth2AccessToken accessToken) {
        this(registeredClient, clientPrincipal, accessToken, Collections.emptyMap());
    }

    /**
     * Constructs an {@code OAuth2AuthorizationImplicitAccessTokenAuthenticationToken} using the provided parameters.
     *
     * @param registeredClient     the registered client
     * @param clientPrincipal      the authenticated client principal
     * @param accessToken          the access token
     * @param additionalParameters the additional parameters
     */
    public OAuth2AuthorizationImplicitAccessTokenAuthenticationToken(RegisteredClient registeredClient,
                                                                     Authentication clientPrincipal,
                                                                     OAuth2AccessToken accessToken,
                                                                     Map<String, Object> additionalParameters) {
        super(Collections.emptyList());
        Assert.notNull(registeredClient, "registeredClient cannot be null");
        Assert.notNull(clientPrincipal, "clientPrincipal cannot be null");
        Assert.notNull(additionalParameters, "additionalParameters cannot be null");
        this.registeredClient = registeredClient;
        this.clientPrincipal = clientPrincipal;
        this.accessToken = accessToken;
        this.additionalParameters = additionalParameters;
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
     * Returns the {@link RegisteredClient registered client}.
     *
     * @return the {@link RegisteredClient}
     */
    public RegisteredClient getRegisteredClient() {
        return this.registeredClient;
    }

    /**
     * Returns the {@link OAuth2AccessToken access token}.
     *
     * @return the {@link OAuth2AccessToken}
     */
    public OAuth2AccessToken getAccessToken() {
        return this.accessToken;
    }

    /**
     * Returns the additional parameters.
     *
     * @return a {@code Map} of the additional parameters, may be empty
     */
    public Map<String, Object> getAdditionalParameters() {
        return this.additionalParameters;
    }
}
