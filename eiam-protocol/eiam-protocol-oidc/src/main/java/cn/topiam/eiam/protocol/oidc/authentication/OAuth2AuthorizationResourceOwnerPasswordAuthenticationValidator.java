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

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.util.CollectionUtils;
import static cn.topiam.eiam.protocol.oidc.authentication.OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken.PASSWORD;

/**
 * 验证器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/11/9 23:54
 */
@SuppressWarnings({ "AlibabaClassNamingShouldBeCamel", "AlibabaUndefineMagicConstant" })
public final class OAuth2AuthorizationResourceOwnerPasswordAuthenticationValidator implements
                                                                                   Consumer<OAuth2AuthorizationResourceOwnerPasswordAuthenticationContext> {
    private static final String                                                                 ERROR_URI                    = "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1";

    /**
     * The default validator for {@link OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken#getScopes()}.
     */
    public static final Consumer<OAuth2AuthorizationResourceOwnerPasswordAuthenticationContext> DEFAULT_SCOPE_VALIDATOR      = OAuth2AuthorizationResourceOwnerPasswordAuthenticationValidator::validateScope;

    public static final Consumer<OAuth2AuthorizationResourceOwnerPasswordAuthenticationContext> DEFAULT_GRANT_TYPE_VALIDATOR = OAuth2AuthorizationResourceOwnerPasswordAuthenticationValidator::validateGrantType;

    private final Consumer<OAuth2AuthorizationResourceOwnerPasswordAuthenticationContext>       authenticationValidator      = DEFAULT_SCOPE_VALIDATOR
        .andThen(DEFAULT_GRANT_TYPE_VALIDATOR);

    /**
     * 验证 Scope
     *
     * @param authenticationContext {@link OAuth2AuthorizationResourceOwnerPasswordAuthenticationContext}
     */
    private static void validateScope(OAuth2AuthorizationResourceOwnerPasswordAuthenticationContext authenticationContext) {
        RegisteredClient registeredClient = authenticationContext.getRegisteredClient();
        OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken authorizationPasswordAuthenticationToken = authenticationContext
            .getAuthentication();
        Set<String> requestedScopes = authorizationPasswordAuthenticationToken.getScopes();
        if (!CollectionUtils.isEmpty(requestedScopes)) {
            Set<String> unauthorizedScopes = requestedScopes.stream()
                .filter(requestedScope -> !registeredClient.getScopes().contains(requestedScope))
                .collect(Collectors.toSet());
            if (!CollectionUtils.isEmpty(unauthorizedScopes)) {
                throwError(OAuth2ErrorCodes.INVALID_SCOPE, OAuth2ParameterNames.SCOPE,
                    authorizationPasswordAuthenticationToken);
            }
        }
    }

    /**
     * 验证授权类型
     *
     * @param authenticationContext {@link OAuth2AuthorizationResourceOwnerPasswordAuthenticationContext}
     */
    private static void validateGrantType(OAuth2AuthorizationResourceOwnerPasswordAuthenticationContext authenticationContext) {
        OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken authorizationCodeRequestAuthentication = authenticationContext
            .getAuthentication();
        RegisteredClient registeredClient = authenticationContext.getRegisteredClient();
        //校验 grant type
        if (!registeredClient.getAuthorizationGrantTypes().contains(PASSWORD)) {
            throwError(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT, OAuth2ParameterNames.CLIENT_ID,
                authorizationCodeRequestAuthentication);
        }
    }

    private static void throwError(String errorCode, String parameterName,
                                   OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken authorizationCodeRequestAuthentication) {
        OAuth2Error error = new OAuth2Error(errorCode, "OAuth 2.0 Parameter: " + parameterName,
            ERROR_URI);
        throwError(error, authorizationCodeRequestAuthentication);
    }

    private static void throwError(OAuth2Error error,
                                   OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken authorizationPasswordAuthenticationToken) {
        authorizationPasswordAuthenticationToken.setAuthenticated(true);
        throw new OAuth2AuthorizationResourceOwnerPasswordAuthenticationException(error,
            authorizationPasswordAuthenticationToken);
    }

    @Override
    public void accept(OAuth2AuthorizationResourceOwnerPasswordAuthenticationContext authenticationContext) {
        this.authenticationValidator.accept(authenticationContext);
    }

}
