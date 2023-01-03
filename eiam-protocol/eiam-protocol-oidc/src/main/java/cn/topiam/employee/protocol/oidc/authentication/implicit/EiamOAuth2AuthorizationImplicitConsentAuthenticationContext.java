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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthenticationContext;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.util.Assert;

/**
 * An {@link OAuth2AuthenticationContext} that holds an {@link OAuth2AuthorizationConsent.Builder} and additional information
 * and is used when customizing the building of the {@link OAuth2AuthorizationConsent}.
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/31 19:15
 */
@SuppressWarnings("All")
public final class EiamOAuth2AuthorizationImplicitConsentAuthenticationContext implements
                                                                               OAuth2AuthenticationContext {

    private final Map<Object, Object> context;

    private EiamOAuth2AuthorizationImplicitConsentAuthenticationContext(Map<Object, Object> context) {
        this.context = Collections.unmodifiableMap(new HashMap<>(context));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <V> V get(Object key) {
        return hasKey(key) ? (V) this.context.get(key) : null;
    }

    @Override
    public boolean hasKey(Object key) {
        Assert.notNull(key, "key cannot be null");
        return this.context.containsKey(key);
    }

    /**
     * Returns the {@link OAuth2AuthorizationConsent.Builder authorization consent builder}.
     *
     * @return the {@link OAuth2AuthorizationConsent.Builder}
     */
    public OAuth2AuthorizationConsent.Builder getAuthorizationConsent() {
        return get(OAuth2AuthorizationConsent.Builder.class);
    }

    /**
     * Returns the {@link RegisteredClient registered client}.
     *
     * @return the {@link RegisteredClient}
     */
    public RegisteredClient getRegisteredClient() {
        return get(RegisteredClient.class);
    }

    /**
     * Returns the {@link OAuth2Authorization authorization}.
     *
     * @return the {@link OAuth2Authorization}
     */
    public OAuth2Authorization getAuthorization() {
        return get(OAuth2Authorization.class);
    }

    /**
     * Returns the {@link OAuth2AuthorizationRequest authorization request}.
     *
     * @return the {@link OAuth2AuthorizationRequest}
     */
    public OAuth2AuthorizationRequest getAuthorizationRequest() {
        return get(OAuth2AuthorizationRequest.class);
    }

    /**
     * Constructs a new {@link EiamOAuth2AuthorizationImplicitConsentAuthenticationContext.Builder} with the provided {@link EiamOAuth2AuthorizationImplicitConsentAuthenticationToken}.
     *
     * @param authentication the {@link EiamOAuth2AuthorizationImplicitConsentAuthenticationToken}
     * @return the {@link EiamOAuth2AuthorizationImplicitConsentAuthenticationContext.Builder}
     */
    public static EiamOAuth2AuthorizationImplicitConsentAuthenticationContext.Builder with(EiamOAuth2AuthorizationImplicitConsentAuthenticationToken authentication) {
        return new EiamOAuth2AuthorizationImplicitConsentAuthenticationContext.Builder(
            authentication);
    }

    /**
     * A builder for {@link EiamOAuth2AuthorizationImplicitConsentAuthenticationContext}.
     */
    public static final class Builder extends
                                      AbstractBuilder<EiamOAuth2AuthorizationImplicitConsentAuthenticationContext, EiamOAuth2AuthorizationImplicitConsentAuthenticationContext.Builder> {

        private Builder(EiamOAuth2AuthorizationImplicitConsentAuthenticationToken authentication) {
            super(authentication);
        }

        /**
         * Sets the {@link OAuth2AuthorizationConsent.Builder authorization consent builder}.
         *
         * @param authorizationConsent the {@link OAuth2AuthorizationConsent.Builder}
         * @return the {@link EiamOAuth2AuthorizationImplicitConsentAuthenticationContext.Builder} for further configuration
         */
        public EiamOAuth2AuthorizationImplicitConsentAuthenticationContext.Builder authorizationConsent(OAuth2AuthorizationConsent.Builder authorizationConsent) {
            return put(OAuth2AuthorizationConsent.Builder.class, authorizationConsent);
        }

        /**
         * Sets the {@link RegisteredClient registered client}.
         *
         * @param registeredClient the {@link RegisteredClient}
         * @return the {@link EiamOAuth2AuthorizationImplicitConsentAuthenticationContext.Builder} for further configuration
         */
        public EiamOAuth2AuthorizationImplicitConsentAuthenticationContext.Builder registeredClient(RegisteredClient registeredClient) {
            return put(RegisteredClient.class, registeredClient);
        }

        /**
         * Sets the {@link OAuth2Authorization authorization}.
         *
         * @param authorization the {@link OAuth2Authorization}
         * @return the {@link EiamOAuth2AuthorizationImplicitConsentAuthenticationContext.Builder} for further configuration
         */
        public EiamOAuth2AuthorizationImplicitConsentAuthenticationContext.Builder authorization(OAuth2Authorization authorization) {
            return put(OAuth2Authorization.class, authorization);
        }

        /**
         * Sets the {@link OAuth2AuthorizationRequest authorization request}.
         *
         * @param authorizationRequest the {@link OAuth2AuthorizationRequest}
         * @return the {@link EiamOAuth2AuthorizationImplicitConsentAuthenticationContext.Builder} for further configuration
         */
        public EiamOAuth2AuthorizationImplicitConsentAuthenticationContext.Builder authorizationRequest(OAuth2AuthorizationRequest authorizationRequest) {
            return put(OAuth2AuthorizationRequest.class, authorizationRequest);
        }

        /**
         * Builds a new {@link EiamOAuth2AuthorizationImplicitConsentAuthenticationContext}.
         *
         * @return the {@link EiamOAuth2AuthorizationImplicitConsentAuthenticationContext}
         */
        @Override
        public EiamOAuth2AuthorizationImplicitConsentAuthenticationContext build() {
            Assert.notNull(get(OAuth2AuthorizationConsent.Builder.class),
                "authorizationConsentBuilder cannot be null");
            Assert.notNull(get(RegisteredClient.class), "registeredClient cannot be null");
            Assert.notNull(get(OAuth2Authorization.class), "authorization cannot be null");
            Assert.notNull(get(OAuth2AuthorizationRequest.class),
                "authorizationRequest cannot be null");
            return new EiamOAuth2AuthorizationImplicitConsentAuthenticationContext(getContext());
        }

    }
}
