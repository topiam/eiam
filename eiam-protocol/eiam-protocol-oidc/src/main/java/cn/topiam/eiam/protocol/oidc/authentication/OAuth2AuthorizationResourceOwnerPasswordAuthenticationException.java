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

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

/**
 * This exception is thrown by {@link OAuth2AuthorizationResourceOwnerPasswordAuthenticationProvider}
 * when an attempt to authenticate the OAuth 2.0 Authorization Request (or Consent) fails.
 *
 * @author TopIAM
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class OAuth2AuthorizationResourceOwnerPasswordAuthenticationException extends
                                                                             OAuth2AuthenticationException {
    private final OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken authorizationPasswordAuthenticationToken;

    /**
     * Constructs an {@code OAuth2AuthorizationPasswordAuthenticationException} using the provided parameters.
     *
     * @param error                                    the {@link OAuth2Error OAuth 2.0 Error}
     * @param authorizationPasswordAuthenticationToken the {@link Authentication} instance of the OAuth 2.0 Authorization Request (or Consent)
     */
    public OAuth2AuthorizationResourceOwnerPasswordAuthenticationException(OAuth2Error error,
                                                                           @Nullable OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken authorizationPasswordAuthenticationToken) {
        super(error);
        this.authorizationPasswordAuthenticationToken = authorizationPasswordAuthenticationToken;
    }

    /**
     * Constructs an {@code OAuth2AuthorizationPasswordAuthenticationException} using the provided parameters.
     *
     * @param error                                  the {@link OAuth2Error OAuth 2.0 Error}
     * @param cause                                  the root cause
     * @param authorizationCodeRequestAuthentication the {@link Authentication} instance of the OAuth 2.0 Authorization Request (or Consent)
     */
    public OAuth2AuthorizationResourceOwnerPasswordAuthenticationException(OAuth2Error error,
                                                                           Throwable cause,
                                                                           @Nullable OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken authorizationCodeRequestAuthentication) {
        super(error, cause);
        this.authorizationPasswordAuthenticationToken = authorizationCodeRequestAuthentication;
    }

    /**
     * Returns the {@link Authentication} instance of the OAuth 2.0 Authorization Request (or Consent), or {@code null} if not available.
     *
     * @return the {@link OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken}
     */
    @Nullable
    public OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken getAuthorizationCodeRequestAuthentication() {
        return this.authorizationPasswordAuthenticationToken;
    }

}
