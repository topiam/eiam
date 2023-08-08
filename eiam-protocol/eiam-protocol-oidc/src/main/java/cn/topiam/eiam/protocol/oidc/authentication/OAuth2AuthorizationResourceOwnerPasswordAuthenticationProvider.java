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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.*;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import cn.topiam.employee.support.security.authentication.WebAuthenticationDetails;

import jakarta.servlet.http.HttpServletRequest;
import static cn.topiam.eiam.protocol.oidc.authentication.OAuth2AuthenticationProviderUtils.getAuthenticatedClientElseThrowInvalidClient;
import static cn.topiam.eiam.protocol.oidc.authentication.OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken.PASSWORD;
import static cn.topiam.employee.support.security.authentication.AuthenticationProvider.USERNAME_PASSWORD;
import static cn.topiam.employee.support.security.util.SecurityUtils.isPrincipalAuthenticated;

/**
 * An {@link AuthenticationProvider} implementation for the OAuth 2.0 Authorization Code Grant.
 *
 * @author TopIAM
 * @see <a target="_blank" href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1">Section 4.1 Authorization Code Grant</a>
 * @see <a target="_blank" href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.3">Section 4.1.3 Access Token Request</a>
 */
@SuppressWarnings({ "AlibabaClassNamingShouldBeCamel", "AlibabaMethodTooLong" })
public final class OAuth2AuthorizationResourceOwnerPasswordAuthenticationProvider extends
                                                                                  DaoAuthenticationProvider {
    private final Log                                                               logger                            = LogFactory
        .getLog(OAuth2AuthorizationResourceOwnerPasswordAuthenticationProvider.class);

    private static final String                                                     ERROR_URI                         = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";
    private static final OAuth2TokenType                                            AUTHORIZATION_PASSWORD_TOKEN_TYPE = new OAuth2TokenType(
        OAuth2ParameterNames.PASSWORD);
    private static final OAuth2TokenType                                            ID_TOKEN_TOKEN_TYPE               = new OAuth2TokenType(
        OidcParameterNames.ID_TOKEN);
    private Consumer<OAuth2AuthorizationResourceOwnerPasswordAuthenticationContext> authenticationValidator           = new OAuth2AuthorizationResourceOwnerPasswordAuthenticationValidator();

    private final OAuth2AuthorizationService                                        authorizationService;
    private final OAuth2TokenGenerator<? extends OAuth2Token>                       tokenGenerator;
    private AuthenticationDetailsSource<HttpServletRequest, ?>                      authenticationDetailsSource       = new WebAuthenticationDetailsSource();
    private SessionRegistry                                                         sessionRegistry;

    /**
     * Constructs an {@code OAuth2authorizationPasswordAuthenticationProvider} using the provided parameters.
     *
     * @param userDetailsService   the user details service
     * @param authorizationService the authorization service
     * @param passwordEncoder      the password encoder
     * @param tokenGenerator       the token generator
     */
    public OAuth2AuthorizationResourceOwnerPasswordAuthenticationProvider(UserDetailsService userDetailsService,
                                                                          OAuth2AuthorizationService authorizationService,
                                                                          OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
                                                                          PasswordEncoder passwordEncoder) {
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
        Assert.notNull(userDetailsService, "userDetailsService cannot be null");
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
        setUserDetailsService(userDetailsService);
        setPasswordEncoder(passwordEncoder);
    }

    public void setSessionRegistry(SessionRegistry sessionRegistry) {
        Assert.notNull(sessionRegistry, "sessionRegistry cannot be null");
        this.sessionRegistry = sessionRegistry;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // @formatter:off
        OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken authorizationPasswordAuthenticationToken = (OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken) authentication;

        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(authorizationPasswordAuthenticationToken);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Retrieved registered client");
        }

        OAuth2AuthorizationResourceOwnerPasswordAuthenticationContext authenticationContext = OAuth2AuthorizationResourceOwnerPasswordAuthenticationContext.with(authorizationPasswordAuthenticationToken).registeredClient(registeredClient).build();

        this.authenticationValidator.accept(authenticationContext);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Validated token request parameters");
        }

        // ---------------
        // Username and password authentication
        // The request is valid - ensure the resource owner is authenticated
        // ---------------

        Authentication principal = getUsernamePasswordAuthentication(authorizationPasswordAuthenticationToken);
        if (!isPrincipalAuthenticated(principal)) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Did not authenticate authorization code request since principal not authenticated");
            }
            // Return the authorization request as-is where isAuthenticated() is false
            return authorizationPasswordAuthenticationToken;
        }

        Set<String> authorizedScopes = authorizationPasswordAuthenticationToken.getScopes();
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(principal)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizedScopes(authorizedScopes)
                .authorizationGrantType(PASSWORD)
                .authorizationGrant(authorizationPasswordAuthenticationToken);

        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .principalName(principal.getName())
                .authorizationGrantType(PASSWORD)
                .authorizedScopes(authorizedScopes)
                .attribute(Principal.class.getName(), principal);

        // ----- Access token -----
        OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
        if (generatedAccessToken == null) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "The token generator failed to generate the access token.", ERROR_URI);
            throw new OAuth2AuthenticationException(error);
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Generated access token");
        }

        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(), generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());

        if (generatedAccessToken instanceof ClaimAccessor) {
            authorizationBuilder.token(accessToken, (metadata) -> metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, ((ClaimAccessor) generatedAccessToken).getClaims()));
        } else {
            authorizationBuilder.accessToken(accessToken);
        }

        // ----- Refresh token -----
        OAuth2RefreshToken refreshToken = null;
        // Do not issue refresh token to public client
        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN) && !clientPrincipal.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE)) {
            tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
            OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
            if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "The token generator failed to generate the refresh token.", ERROR_URI);
                throw new OAuth2AuthenticationException(error);
            }

            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Generated refresh token");
            }

            refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
            authorizationBuilder.refreshToken(refreshToken);
        }

        // ----- ID token -----
        OidcIdToken idToken;
        if (authorizationPasswordAuthenticationToken.getScopes().contains(OidcScopes.OPENID)) {
            SessionInformation sessionInformation = getSessionInformation(principal);
            if (sessionInformation != null) {
                try {
                    // Compute (and use) hash for Session ID
                    sessionInformation = new SessionInformation(sessionInformation.getPrincipal(), createHash(sessionInformation.getSessionId()), sessionInformation.getLastRequest());
                } catch (NoSuchAlgorithmException ex) {
                    OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "Failed to compute hash for Session ID.", ERROR_URI);
                    throw new OAuth2AuthenticationException(error);
                }
                tokenContextBuilder.put(SessionInformation.class, sessionInformation);
            }
            tokenContext = tokenContextBuilder
                    .tokenType(ID_TOKEN_TOKEN_TYPE)
                    // ID token customizer may need access to the access token and/or refresh token
                    .authorization(authorizationBuilder.build())
                    .build();
            OAuth2Token generatedIdToken = this.tokenGenerator.generate(tokenContext);
            if (!(generatedIdToken instanceof Jwt)) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "The token generator failed to generate the ID token.", ERROR_URI);
                throw new OAuth2AuthenticationException(error);
            }

            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Generated id token");
            }

            idToken = new OidcIdToken(generatedIdToken.getTokenValue(), generatedIdToken.getIssuedAt(), generatedIdToken.getExpiresAt(), ((Jwt) generatedIdToken).getClaims());
            authorizationBuilder.token(idToken, (metadata) -> metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, idToken.getClaims()));
        } else {
            idToken = null;
        }
        OAuth2Authorization authorization = authorizationBuilder.build();
        this.authorizationService.save(authorization);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Saved authorization");
        }

        Map<String, Object> additionalParameters = new HashMap<>(16);
        if (idToken != null) {
            additionalParameters.put(OidcParameterNames.ID_TOKEN, idToken.getTokenValue());
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Authenticated token request");
        }

        return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, refreshToken, additionalParameters);
        // @formatter:on
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken.class
            .isAssignableFrom(authentication);
    }

    public void setAuthenticationValidator(Consumer<OAuth2AuthorizationResourceOwnerPasswordAuthenticationContext> authenticationValidator) {
        Assert.notNull(authenticationValidator, "authenticationValidator cannot be null");
        this.authenticationValidator = authenticationValidator;
    }

    private Authentication getUsernamePasswordAuthentication(OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken resourceOwnerPasswordAuthentication) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                resourceOwnerPasswordAuthentication.getUsername(),
                resourceOwnerPasswordAuthentication.getPassword());
            WebAuthenticationDetails details = (WebAuthenticationDetails) resourceOwnerPasswordAuthentication
                .getDetails();
            details.setAuthenticationProvider(USERNAME_PASSWORD);
            usernamePasswordAuthenticationToken.setDetails(details);
            return super.authenticate(usernamePasswordAuthenticationToken);
        } catch (AccountStatusException ase) {
            //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT, ase.getMessage(),
                ERROR_URI);
            throw new OAuth2AuthenticationException(error);
        } catch (BadCredentialsException e) {
            // If the username/password are wrong the spec says we should send 400/invalid grant
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT, e.getMessage(),
                ERROR_URI);
            throw new OAuth2AuthenticationException(error);
        } catch (UsernameNotFoundException e) {
            // If the user is not found, report a generic error message
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT,
                "username not found.", ERROR_URI);
            throw new OAuth2AuthenticationException(error);
        }
    }

    private SessionInformation getSessionInformation(Authentication principal) {
        SessionInformation sessionInformation = null;
        if (this.sessionRegistry != null) {
            List<SessionInformation> sessions = this.sessionRegistry
                .getAllSessions(principal.getPrincipal(), false);
            if (!CollectionUtils.isEmpty(sessions)) {
                sessionInformation = sessions.get(0);
                if (sessions.size() > 1) {
                    // Get the most recent session
                    sessions = new ArrayList<>(sessions);
                    sessions.sort(Comparator.comparing(SessionInformation::getLastRequest));
                    sessionInformation = sessions.get(sessions.size() - 1);
                }
            }
        }
        return sessionInformation;
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal,
                                                         Authentication authentication,
                                                         UserDetails user) {
        return super.createSuccessAuthentication(principal, authentication, user);
    }

    private static String createHash(String value) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(value.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }
}
