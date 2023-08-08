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

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;

/**
 * Utility methods for the OAuth 2.0 {@link AuthenticationProvider}'s.
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/28 22:45
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
final class OAuth2AuthenticationProviderUtils {

    private OAuth2AuthenticationProviderUtils() {
    }

    static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class
            .isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }
        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }

    static <T extends OAuth2Token> OAuth2Authorization invalidate(OAuth2Authorization authorization,
                                                                  T token) {

        // @formatter:off
		OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.from(authorization)
				.token(token,
						(metadata) ->
								metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, true));

		if (OAuth2RefreshToken.class.isAssignableFrom(token.getClass())) {
			authorizationBuilder.token(
					authorization.getAccessToken().getToken(),
					(metadata) ->
							metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, true));

			OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode =
					authorization.getToken(OAuth2AuthorizationCode.class);
			if (authorizationCode != null && !authorizationCode.isInvalidated()) {
				authorizationBuilder.token(
						authorizationCode.getToken(),
						(metadata) ->
								metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, true));
			}
		}
		// @formatter:on

        return authorizationBuilder.build();
    }

}
