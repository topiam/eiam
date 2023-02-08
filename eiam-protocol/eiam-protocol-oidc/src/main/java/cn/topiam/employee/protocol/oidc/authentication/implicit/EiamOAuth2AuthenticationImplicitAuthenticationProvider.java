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

import java.security.Principal;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationValidator;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;

import cn.topiam.employee.audit.context.AuditContext;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REDIRECT_URI;

/**
 * OAuth2AuthenticationImplicitAuthenticationProvider
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/27 22:48
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class EiamOAuth2AuthenticationImplicitAuthenticationProvider implements
                                                                    AuthenticationProvider {
    public static final AuthorizationGrantType                             IMPLICIT                = new AuthorizationGrantType(
        "implicit");

    private static final String                                            ERROR_URI               = "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1";
    private static final StringKeyGenerator                                DEFAULT_STATE_GENERATOR = new Base64StringKeyGenerator(
        Base64.getUrlEncoder());
    private final RegisteredClientRepository                               registeredClientRepository;
    private final OAuth2AuthorizationService                               authorizationService;
    private final OAuth2AuthorizationConsentService                        authorizationConsentService;
    private Consumer<EiamOAuth2AuthorizationImplicitAuthenticationContext> authenticationValidator = new EiamOAuth2AuthenticationImplicitAuthenticationValidator();
    private static final OAuth2TokenType                                   ID_TOKEN_TOKEN_TYPE     = new OAuth2TokenType(
        OidcParameterNames.ID_TOKEN);
    private final OAuth2TokenGenerator<? extends OAuth2Token>              tokenGenerator;

    /**
     * Constructs an {@code EiamOAuth2AuthenticationImplicitAuthenticationProvider} using the provided parameters.
     *
     * @param registeredClientRepository  the repository of registered clients
     * @param authorizationService        the authorization service
     * @param authorizationConsentService the authorization consent service
     * @param tokenGenerator              OAuth2TokenGenerator
     */
    public EiamOAuth2AuthenticationImplicitAuthenticationProvider(RegisteredClientRepository registeredClientRepository,
                                                                  OAuth2AuthorizationService authorizationService,
                                                                  OAuth2AuthorizationConsentService authorizationConsentService,
                                                                  OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        Assert.notNull(authorizationConsentService, "authorizationConsentService cannot be null");
        Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
        this.registeredClientRepository = registeredClientRepository;
        this.authorizationService = authorizationService;
        this.authorizationConsentService = authorizationConsentService;
        this.tokenGenerator = tokenGenerator;
    }

    @SuppressWarnings("AlibabaMethodTooLong")
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        EiamOAuth2AuthorizationImplicitAuthenticationToken authorizationImplicitRequestAuthentication = (EiamOAuth2AuthorizationImplicitAuthenticationToken) authentication;
        RegisteredClient registeredClient = this.registeredClientRepository
            .findByClientId(authorizationImplicitRequestAuthentication.getClientId());
        if (registeredClient == null) {
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.CLIENT_ID,
                authorizationImplicitRequestAuthentication, null);
        }
        if (registeredClient == null) {
            throwInvalidClient(OAuth2ParameterNames.CLIENT_ID);
        }

        EiamOAuth2AuthorizationImplicitAuthenticationContext authenticationContext = EiamOAuth2AuthorizationImplicitAuthenticationContext
            .with(authorizationImplicitRequestAuthentication).registeredClient(registeredClient)
            .build();
        this.authenticationValidator.accept(authenticationContext);

        if (!registeredClient.getAuthorizationGrantTypes().contains(IMPLICIT)) {
            throwError(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT, OAuth2ParameterNames.CLIENT_ID,
                authorizationImplicitRequestAuthentication, registeredClient);
        }

        // ---------------
        // The request is valid - ensure the resource owner is authenticated
        // ---------------

        Authentication principal = (Authentication) authorizationImplicitRequestAuthentication
            .getPrincipal();
        if (!isPrincipalAuthenticated(principal)) {
            // Return the authorization request as-is where isAuthenticated() is false
            return authorizationImplicitRequestAuthentication;
        }
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest
            .authorizationCode()
            .authorizationUri(authorizationImplicitRequestAuthentication.getAuthorizationUri())
            .clientId(registeredClient.getClientId())
            .redirectUri(authorizationImplicitRequestAuthentication.getRedirectUri())
            .scopes(authorizationImplicitRequestAuthentication.getScopes())
            .state(authorizationImplicitRequestAuthentication.getState()).additionalParameters(
                authorizationImplicitRequestAuthentication.getAdditionalParameters())
            .build();

        OAuth2AuthorizationConsent currentAuthorizationConsent = this.authorizationConsentService
            .findById(registeredClient.getId(), principal.getName());

        //是否需要确认授权
        if (requireAuthorizationConsent(registeredClient, authorizationRequest,
            currentAuthorizationConsent)) {
            String state = DEFAULT_STATE_GENERATOR.generateKey();
            OAuth2Authorization authorization = authorizationBuilder(registeredClient, principal,
                authorizationRequest).attribute(OAuth2ParameterNames.STATE, state).build();
            this.authorizationService.save(authorization);

            Set<String> currentAuthorizedScopes = currentAuthorizationConsent != null
                ? currentAuthorizationConsent.getScopes()
                : null;

            return new EiamOAuth2AuthorizationImplicitConsentAuthenticationToken(
                authorizationRequest.getAuthorizationUri(), registeredClient.getClientId(),
                principal, state, currentAuthorizedScopes, null);
        }

        OAuth2Authorization authorization = authorizationBuilder(registeredClient, principal,
            authorizationRequest).authorizedScopes(authorizationRequest.getScopes()).build();

        OAuth2ClientAuthenticationToken clientPrincipal = new OAuth2ClientAuthenticationToken(
            registeredClient, ClientAuthenticationMethod.NONE, null);

        // @formatter:off
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(authorization.getAttribute(Principal.class.getName()))
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorization(authorization)
                .authorizedScopes(authorization.getAuthorizedScopes())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrant(authorizationImplicitRequestAuthentication);
        // @formatter:on

        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.from(authorization);

        // ----- Access token -----
        OAuth2TokenContext tokenContext = tokenContextBuilder
            .tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
        if (generatedAccessToken == null) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                "The token generator failed to generate the access token.", ERROR_URI);
            throw new OAuth2AuthenticationException(error);
        }
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
            generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
            generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());
        if (generatedAccessToken instanceof ClaimAccessor) {
            authorizationBuilder.token(accessToken,
                (metadata) -> metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
                    ((ClaimAccessor) generatedAccessToken).getClaims()));
        } else {
            authorizationBuilder.accessToken(accessToken);
        }

        // ----- Refresh token -----
        OAuth2RefreshToken refreshToken = null;
        if (registeredClient.getAuthorizationGrantTypes()
            .contains(AuthorizationGrantType.REFRESH_TOKEN) &&
        // Do not issue refresh token to public client
            !clientPrincipal.getClientAuthenticationMethod()
                .equals(ClientAuthenticationMethod.NONE)) {

            tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
            OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
            if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                    "The token generator failed to generate the refresh token.", ERROR_URI);
                throw new OAuth2AuthenticationException(error);
            }
            refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
            authorizationBuilder.refreshToken(refreshToken);
        }

        // ----- ID token -----
        OidcIdToken idToken;
        if (authorizationRequest.getScopes().contains(OidcScopes.OPENID)) {
            // @formatter:off
            tokenContext = tokenContextBuilder
                    .tokenType(ID_TOKEN_TOKEN_TYPE)
                    // ID token customizer may need access to the access token and/or refresh token
                    .authorization(authorizationBuilder.build())
                    .build();
            // @formatter:on
            OAuth2Token generatedIdToken = this.tokenGenerator.generate(tokenContext);
            if (!(generatedIdToken instanceof Jwt)) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                    "The token generator failed to generate the ID token.", ERROR_URI);
                throw new OAuth2AuthenticationException(error);
            }
            idToken = new OidcIdToken(generatedIdToken.getTokenValue(),
                generatedIdToken.getIssuedAt(), generatedIdToken.getExpiresAt(),
                ((Jwt) generatedIdToken).getClaims());
            authorizationBuilder.token(idToken, (metadata) -> metadata
                .put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, idToken.getClaims()));
        } else {
            idToken = null;
        }

        authorization = authorizationBuilder.build();

        this.authorizationService.save(authorization);

        Map<String, Object> additionalParameters = Maps.newHashMap();
        additionalParameters.put(REDIRECT_URI,
            authorizationImplicitRequestAuthentication.getRedirectUri());
        if (idToken != null) {
            additionalParameters.put(OidcParameterNames.ID_TOKEN, idToken.getTokenValue());
        }

        OAuth2AccessTokenAuthenticationToken token = new OAuth2AccessTokenAuthenticationToken(
            registeredClient, clientPrincipal, accessToken, refreshToken, additionalParameters);
        token.setAuthenticated(true);
        //放入审计上下文中
        AuditContext.setAuthorization(authorization.getAttribute(Principal.class.getName()));
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication
            .isAssignableFrom(EiamOAuth2AuthorizationImplicitAuthenticationToken.class);
    }

    private static boolean requireAuthorizationConsent(RegisteredClient registeredClient,
                                                       OAuth2AuthorizationRequest authorizationRequest,
                                                       OAuth2AuthorizationConsent authorizationConsent) {

        if (!registeredClient.getClientSettings().isRequireAuthorizationConsent()) {
            return false;
        }
        // 'openid' scope does not require consent
        if (authorizationRequest.getScopes().contains(OidcScopes.OPENID)
            && authorizationRequest.getScopes().size() == 1) {
            return false;
        }

        return authorizationConsent == null
               || !authorizationConsent.getScopes().containsAll(authorizationRequest.getScopes());
    }

    private static boolean isPrincipalAuthenticated(Authentication principal) {
        return principal != null
               && !AnonymousAuthenticationToken.class.isAssignableFrom(principal.getClass())
               && principal.isAuthenticated();
    }

    private static void throwError(String errorCode, String parameterName,
                                   EiamOAuth2AuthorizationImplicitAuthenticationToken authorizationCodeRequestAuthentication,
                                   RegisteredClient registeredClient) {
        throwError(errorCode, parameterName, ERROR_URI, authorizationCodeRequestAuthentication,
            registeredClient, null);
    }

    private static void throwError(String errorCode, String parameterName, String errorUri,
                                   EiamOAuth2AuthorizationImplicitAuthenticationToken authorizationCodeRequestAuthentication,
                                   RegisteredClient registeredClient,
                                   OAuth2AuthorizationRequest authorizationRequest) {
        OAuth2Error error = new OAuth2Error(errorCode, "OAuth 2.0 Parameter: " + parameterName,
            errorUri);
        throwError(error, parameterName, authorizationCodeRequestAuthentication, registeredClient,
            authorizationRequest);
    }

    private static void throwError(OAuth2Error error, String parameterName,
                                   EiamOAuth2AuthorizationImplicitAuthenticationToken authorizationCodeRequestAuthentication,
                                   RegisteredClient registeredClient,
                                   OAuth2AuthorizationRequest authorizationRequest) {

        String redirectUri = resolveRedirectUri(authorizationRequest, registeredClient);
        if (error.getErrorCode().equals(OAuth2ErrorCodes.INVALID_REQUEST)
            && (parameterName.equals(OAuth2ParameterNames.CLIENT_ID)
                || parameterName.equals(OAuth2ParameterNames.STATE))) {
            // Prevent redirects
            redirectUri = null;
        }

        EiamOAuth2AuthorizationImplicitAuthenticationToken authorizationCodeRequestAuthenticationResult = new EiamOAuth2AuthorizationImplicitAuthenticationToken(
            authorizationCodeRequestAuthentication.getAuthorizationUri(),
            authorizationCodeRequestAuthentication.getClientId(),
            (Authentication) authorizationCodeRequestAuthentication.getPrincipal(), redirectUri,
            authorizationCodeRequestAuthentication.getState(),
            authorizationCodeRequestAuthentication.getScopes(),
            authorizationCodeRequestAuthentication.getAdditionalParameters());

        throw new EiamOAuth2AuthorizationImplicitAuthenticationException(error,
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

    /**
     * Sets the {@code Consumer} providing access to the {@link EiamOAuth2AuthenticationImplicitAuthenticationValidator}
     * and is responsible for validating specific OAuth 2.0 Authorization Request parameters
     * associated in the {@link EiamOAuth2AuthorizationImplicitAuthenticationToken}.
     * The default authentication validator is {@link OAuth2AuthorizationCodeRequestAuthenticationValidator}.
     *
     * <p>
     * <b>NOTE:</b> The authentication validator MUST throw {@link OAuth2AuthorizationCodeRequestAuthenticationException} if validation fails.
     *
     * @param authenticationValidator the {@code Consumer} providing access to the {@link EiamOAuth2AuthenticationImplicitAuthenticationValidator} and is responsible for validating specific OAuth 2.0 Authorization Request parameters
     * @since 0.4.0
     */
    public void setAuthenticationValidator(Consumer<EiamOAuth2AuthorizationImplicitAuthenticationContext> authenticationValidator) {
        Assert.notNull(authenticationValidator, "authenticationValidator cannot be null");
        this.authenticationValidator = authenticationValidator;
    }

    private static OAuth2Authorization.Builder authorizationBuilder(RegisteredClient registeredClient,
                                                                    Authentication principal,
                                                                    OAuth2AuthorizationRequest authorizationRequest) {
        return OAuth2Authorization.withRegisteredClient(registeredClient)
            .principalName(principal.getName())
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .attribute(Principal.class.getName(), principal)
            .attribute(OAuth2AuthorizationRequest.class.getName(), authorizationRequest);
    }

    private static void throwInvalidClient(String parameterName) {
        OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_CLIENT,
            "Client authentication failed: " + parameterName, ERROR_URI);
        throw new OAuth2AuthenticationException(error);
    }

}
