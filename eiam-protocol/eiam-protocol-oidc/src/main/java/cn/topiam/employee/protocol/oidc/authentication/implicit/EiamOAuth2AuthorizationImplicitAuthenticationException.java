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
package cn.topiam.employee.protocol.oidc.authentication.implicit;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

/**
 * This exception is thrown by {@link EiamOAuth2AuthenticationImplicitAuthenticationProvider}
 * when an attempt to authenticate the OAuth 2.0 Authorization Request (or Consent) fails.
 *
 * @author TopIAM
 * @see EiamOAuth2AuthorizationImplicitAuthenticationToken
 * @see EiamOAuth2AuthenticationImplicitAuthenticationProvider
 */
@SuppressWarnings("All")
public class EiamOAuth2AuthorizationImplicitAuthenticationException extends
                                                                    OAuth2AuthenticationException {
    private final EiamOAuth2AuthorizationImplicitAuthenticationToken authorizationCodeRequestAuthentication;

    /**
     * Constructs an {@code OAuth2AuthorizationCodeRequestAuthenticationException} using the provided parameters.
     *
     * @param error                                  the {@link OAuth2Error OAuth 2.0 Error}
     * @param authorizationCodeRequestAuthentication the {@link Authentication} instance of the OAuth 2.0 Authorization Request (or Consent)
     */
    public EiamOAuth2AuthorizationImplicitAuthenticationException(OAuth2Error error,
                                                                  @Nullable EiamOAuth2AuthorizationImplicitAuthenticationToken authorizationCodeRequestAuthentication) {
        super(error);
        this.authorizationCodeRequestAuthentication = authorizationCodeRequestAuthentication;
    }

    /**
     * Constructs an {@code OAuth2AuthorizationCodeRequestAuthenticationException} using the provided parameters.
     *
     * @param error                                  the {@link OAuth2Error OAuth 2.0 Error}
     * @param cause                                  the root cause
     * @param authorizationCodeRequestAuthentication the {@link Authentication} instance of the OAuth 2.0 Authorization Request (or Consent)
     */
    public EiamOAuth2AuthorizationImplicitAuthenticationException(OAuth2Error error,
                                                                  Throwable cause,
                                                                  @Nullable EiamOAuth2AuthorizationImplicitAuthenticationToken authorizationCodeRequestAuthentication) {
        super(error, cause);
        this.authorizationCodeRequestAuthentication = authorizationCodeRequestAuthentication;
    }

    /**
     * Returns the {@link Authentication} instance of the OAuth 2.0 Authorization Request (or Consent), or {@code null} if not available.
     *
     * @return the {@link EiamOAuth2AuthorizationImplicitAuthenticationToken}
     */
    @Nullable
    public EiamOAuth2AuthorizationImplicitAuthenticationToken getAuthorizationCodeRequestAuthentication() {
        return this.authorizationCodeRequestAuthentication;
    }

}
