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
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/11/9 22:34
 */
@SuppressWarnings("All")
public final class EiamOAuth2AuthorizationImplicitConsentAuthenticationProvider implements
                                                                                AuthenticationProvider {
    private static final String                                                   ERROR_URI        = "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1";
    private static final OAuth2TokenType                                          STATE_TOKEN_TYPE = new OAuth2TokenType(
        OAuth2ParameterNames.STATE);
    private final RegisteredClientRepository                                      registeredClientRepository;
    private final OAuth2AuthorizationService                                      authorizationService;
    private final OAuth2AuthorizationConsentService                               authorizationConsentService;
    private Consumer<EiamOAuth2AuthorizationImplicitConsentAuthenticationContext> authorizationConsentCustomizer;

    /**
     * Constructs an {@code OAuth2AuthorizationConsentAuthenticationProvider} using the provided parameters.
     *
     * @param registeredClientRepository the repository of registered clients
     * @param authorizationService the authorization service
     * @param authorizationConsentService the authorization consent service
     */
    public EiamOAuth2AuthorizationImplicitConsentAuthenticationProvider(RegisteredClientRepository registeredClientRepository,
                                                                        OAuth2AuthorizationService authorizationService,
                                                                        OAuth2AuthorizationConsentService authorizationConsentService) {
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        Assert.notNull(authorizationConsentService, "authorizationConsentService cannot be null");
        this.registeredClientRepository = registeredClientRepository;
        this.authorizationService = authorizationService;
        this.authorizationConsentService = authorizationConsentService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        EiamOAuth2AuthorizationImplicitConsentAuthenticationToken authorizationConsentAuthentication = (EiamOAuth2AuthorizationImplicitConsentAuthenticationToken) authentication;

        OAuth2Authorization authorization = this.authorizationService
            .findByToken(authorizationConsentAuthentication.getState(), STATE_TOKEN_TYPE);
        if (authorization == null) {
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.STATE,
                authorizationConsentAuthentication, null, null);
        }

        // The 'in-flight' authorization must be associated to the current principal
        Authentication principal = (Authentication) authorizationConsentAuthentication
            .getPrincipal();
        if (!isPrincipalAuthenticated(principal)
            || !principal.getName().equals(authorization.getPrincipalName())) {
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.STATE,
                authorizationConsentAuthentication, null, null);
        }

        RegisteredClient registeredClient = this.registeredClientRepository
            .findByClientId(authorizationConsentAuthentication.getClientId());
        if (registeredClient == null
            || !registeredClient.getId().equals(authorization.getRegisteredClientId())) {
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.CLIENT_ID,
                authorizationConsentAuthentication, registeredClient, null);
        }

        OAuth2AuthorizationRequest authorizationRequest = authorization
            .getAttribute(OAuth2AuthorizationRequest.class.getName());
        Set<String> requestedScopes = authorizationRequest.getScopes();
        Set<String> authorizedScopes = new HashSet<>(
            authorizationConsentAuthentication.getScopes());
        if (!requestedScopes.containsAll(authorizedScopes)) {
            throwError(OAuth2ErrorCodes.INVALID_SCOPE, OAuth2ParameterNames.SCOPE,
                authorizationConsentAuthentication, registeredClient, authorizationRequest);
        }

        OAuth2AuthorizationConsent currentAuthorizationConsent = this.authorizationConsentService
            .findById(authorization.getRegisteredClientId(), authorization.getPrincipalName());
        Set<String> currentAuthorizedScopes = currentAuthorizationConsent != null
            ? currentAuthorizationConsent.getScopes()
            : Collections.emptySet();

        if (!currentAuthorizedScopes.isEmpty()) {
            for (String requestedScope : requestedScopes) {
                if (currentAuthorizedScopes.contains(requestedScope)) {
                    authorizedScopes.add(requestedScope);
                }
            }
        }

        if (!authorizedScopes.isEmpty() && requestedScopes.contains(OidcScopes.OPENID)) {
            // 'openid' scope is auto-approved as it does not require consent
            authorizedScopes.add(OidcScopes.OPENID);
        }

        OAuth2AuthorizationConsent.Builder authorizationConsentBuilder;
        if (currentAuthorizationConsent != null) {
            authorizationConsentBuilder = OAuth2AuthorizationConsent
                .from(currentAuthorizationConsent);
        } else {
            authorizationConsentBuilder = OAuth2AuthorizationConsent
                .withId(authorization.getRegisteredClientId(), authorization.getPrincipalName());
        }
        authorizedScopes.forEach(authorizationConsentBuilder::scope);

        if (this.authorizationConsentCustomizer != null) {
            // @formatter:off
			EiamOAuth2AuthorizationImplicitConsentAuthenticationContext authorizationConsentAuthenticationContext =
					EiamOAuth2AuthorizationImplicitConsentAuthenticationContext.with(authorizationConsentAuthentication)
							.authorizationConsent(authorizationConsentBuilder)
							.registeredClient(registeredClient)
							.authorization(authorization)
							.authorizationRequest(authorizationRequest)
							.build();
			// @formatter:on
            this.authorizationConsentCustomizer.accept(authorizationConsentAuthenticationContext);
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorizationConsentBuilder.authorities(authorities::addAll);

        if (authorities.isEmpty()) {
            // Authorization consent denied (or revoked)
            if (currentAuthorizationConsent != null) {
                this.authorizationConsentService.remove(currentAuthorizationConsent);
            }
            this.authorizationService.remove(authorization);
            throwError(OAuth2ErrorCodes.ACCESS_DENIED, OAuth2ParameterNames.CLIENT_ID,
                authorizationConsentAuthentication, registeredClient, authorizationRequest);
        }

        OAuth2AuthorizationConsent authorizationConsent = authorizationConsentBuilder.build();
        if (!authorizationConsent.equals(currentAuthorizationConsent)) {
            this.authorizationConsentService.save(authorizationConsent);
        }

        OAuth2Authorization updatedAuthorization = OAuth2Authorization.from(authorization)
            .authorizedScopes(authorizedScopes)
            .attributes(attrs -> attrs.remove(OAuth2ParameterNames.STATE)).build();
        this.authorizationService.save(updatedAuthorization);

        String redirectUri = authorizationRequest.getRedirectUri();
        if (!StringUtils.hasText(redirectUri)) {
            redirectUri = registeredClient.getRedirectUris().iterator().next();
        }

        return new EiamOAuth2AuthorizationImplicitAuthenticationToken(
            authorizationRequest.getAuthorizationUri(), registeredClient.getClientId(), principal,
            redirectUri, authorizationRequest.getState(), authorizedScopes, null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EiamOAuth2AuthorizationImplicitConsentAuthenticationToken.class
            .isAssignableFrom(authentication);
    }

    /**
     * Sets the {@code Consumer} providing access to the {@link EiamOAuth2AuthorizationImplicitConsentAuthenticationContext}
     * containing an {@link OAuth2AuthorizationConsent.Builder} and additional context information.
     *
     * <p>
     * The following context attributes are available:
     * <ul>
     * <li>The {@link OAuth2AuthorizationConsent.Builder} used to build the authorization consent
     * prior to {@link OAuth2AuthorizationConsentService#save(OAuth2AuthorizationConsent)}.</li>
     * <li>The {@link Authentication} of type
     * {@link EiamOAuth2AuthorizationImplicitConsentAuthenticationToken}.</li>
     * <li>The {@link RegisteredClient} associated with the authorization request.</li>
     * <li>The {@link OAuth2Authorization} associated with the state token presented in the
     * authorization consent request.</li>
     * <li>The {@link OAuth2AuthorizationRequest} associated with the authorization consent request.</li>
     * </ul>
     *
     * @param authorizationConsentCustomizer the {@code Consumer} providing access to the
     * {@link EiamOAuth2AuthorizationImplicitConsentAuthenticationContext} containing an {@link OAuth2AuthorizationConsent.Builder}
     */
    public void setAuthorizationConsentCustomizer(Consumer<EiamOAuth2AuthorizationImplicitConsentAuthenticationContext> authorizationConsentCustomizer) {
        Assert.notNull(authorizationConsentCustomizer,
            "authorizationConsentCustomizer cannot be null");
        this.authorizationConsentCustomizer = authorizationConsentCustomizer;
    }

    private static boolean isPrincipalAuthenticated(Authentication principal) {
        return principal != null
               && !AnonymousAuthenticationToken.class.isAssignableFrom(principal.getClass())
               && principal.isAuthenticated();
    }

    private static void throwError(String errorCode, String parameterName,
                                   EiamOAuth2AuthorizationImplicitConsentAuthenticationToken authorizationConsentAuthentication,
                                   RegisteredClient registeredClient,
                                   OAuth2AuthorizationRequest authorizationRequest) {
        OAuth2Error error = new OAuth2Error(errorCode, "OAuth 2.0 Parameter: " + parameterName,
            ERROR_URI);
        throwError(error, parameterName, authorizationConsentAuthentication, registeredClient,
            authorizationRequest);
    }

    private static void throwError(OAuth2Error error, String parameterName,
                                   EiamOAuth2AuthorizationImplicitConsentAuthenticationToken authorizationConsentAuthentication,
                                   RegisteredClient registeredClient,
                                   OAuth2AuthorizationRequest authorizationRequest) {

        String redirectUri = resolveRedirectUri(authorizationRequest, registeredClient);
        if (error.getErrorCode().equals(OAuth2ErrorCodes.INVALID_REQUEST)
            && (parameterName.equals(OAuth2ParameterNames.CLIENT_ID)
                || parameterName.equals(OAuth2ParameterNames.STATE))) {
            // Prevent redirects
            redirectUri = null;
        }

        String state = authorizationRequest != null ? authorizationRequest.getState()
            : authorizationConsentAuthentication.getState();
        Set<String> requestedScopes = authorizationRequest != null
            ? authorizationRequest.getScopes()
            : authorizationConsentAuthentication.getScopes();

        OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthenticationResult = new OAuth2AuthorizationCodeRequestAuthenticationToken(
            authorizationConsentAuthentication.getAuthorizationUri(),
            authorizationConsentAuthentication.getClientId(),
            (Authentication) authorizationConsentAuthentication.getPrincipal(), redirectUri, state,
            requestedScopes, null);

        throw new OAuth2AuthorizationCodeRequestAuthenticationException(error,
            authorizationCodeRequestAuthenticationResult);
    }

    private static String resolveRedirectUri(OAuth2AuthorizationRequest authorizationRequest,
                                             RegisteredClient registeredClient) {
        if (authorizationRequest != null
            && StringUtils.hasText(authorizationRequest.getRedirectUri())) {
            return authorizationRequest.getRedirectUri();
        }
        if (registeredClient != null) {
            return registeredClient.getRedirectUris().iterator().next();
        }
        return null;
    }

}
