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
package cn.topiam.employee.protocol.oidc.token;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.core.security.util.UserUtils;
import static org.springframework.security.oauth2.core.oidc.OidcScopes.*;

/**
 * EiamOAuth2TokenGenerator
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/27 00:06
 */
@SuppressWarnings({ "unused", "AlibabaClassNamingShouldBeCamel", "AlibabaAvoidComplexCondition",
                    "AlibabaMethodTooLong" })
public final class EiamOAuth2TokenGenerator implements OAuth2TokenGenerator<Jwt> {
    private final JwtEncoder                          jwtEncoder;
    private OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer;

    /**
     * Constructs a {@code JwtGenerator} using the provided parameters.
     *
     * @param jwtEncoder the jwt encoder
     */
    public EiamOAuth2TokenGenerator(JwtEncoder jwtEncoder) {
        Assert.notNull(jwtEncoder, "jwtEncoder cannot be null");
        this.jwtEncoder = jwtEncoder;
    }

    @Nullable
    @Override
    public Jwt generate(OAuth2TokenContext context) {
        // @formatter:off
        if (context.getTokenType() == null || (!OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType()) && !OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue()))) {
            return null;
        }
        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType()) && !OAuth2TokenFormat.SELF_CONTAINED.equals(context.getRegisteredClient().getTokenSettings().getAccessTokenFormat())) {
            return null;
        }

        String issuer = null;
        if (context.getAuthorizationServerContext() != null) {
            issuer = context.getAuthorizationServerContext().getIssuer();
        }
        RegisteredClient registeredClient = context.getRegisteredClient();

        Instant issuedAt = Instant.now();
        Instant expiresAt;
        if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
            // TODO Allow configuration for ID Token time-to-live
            // TODO ID token 默认为 30 分钟，通过上下文拿配置倒是也可以，但是更想从 RegisteredClient  拿配置，
            //  等 https://github.com/spring-projects/spring-authorization-server/issues/790 支持后支持
            expiresAt = issuedAt.plus(30, ChronoUnit.MINUTES);
        } else {
            expiresAt = issuedAt.plus(registeredClient.getTokenSettings().getAccessTokenTimeToLive());
        }

        // @formatter:off
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder();
        if (StringUtils.hasText(issuer)) {
            claimsBuilder.issuer(issuer);
        }
        UserDetails principal = (UserDetails) context.getPrincipal().getPrincipal();
        UserEntity user = UserUtils.getUser(principal.getId());
        Set<String> scopes = context.getAuthorizedScopes();
        claimsBuilder
                .subject(principal.getId())
                .audience(Collections.singletonList(registeredClient.getClientId()))
                .issuedAt(issuedAt)
                .expiresAt(expiresAt);
        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
            claimsBuilder.notBefore(issuedAt);
            if (!CollectionUtils.isEmpty(scopes)) {
                claimsBuilder.claim(OAuth2ParameterNames.SCOPE, scopes);
            }
        }
        //根据配置封装ID Token
        else if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
            claimsBuilder.claim(IdTokenClaimNames.AZP, registeredClient.getClientId());
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            //手机号
            if (scopes.contains(PHONE) && StringUtils.hasText(user.getPhone())){
                claimsBuilder.claim(PHONE, user.getPhone());
            }
            //邮箱
            if (scopes.contains(EMAIL) && StringUtils.hasText(user.getEmail())){
                claimsBuilder.claim(EMAIL, user.getEmail());
            }
            //profile
            if (scopes.contains(PROFILE)){

            }
            if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(context.getAuthorizationGrantType())) {
                OAuth2AuthorizationRequest authorizationRequest = context.getAuthorization().getAttribute(OAuth2AuthorizationRequest.class.getName());
                String nonce = (String) authorizationRequest.getAdditionalParameters().get(OidcParameterNames.NONCE);
                if (StringUtils.hasText(nonce)) {
                    claimsBuilder.claim(IdTokenClaimNames.NONCE, nonce);
                }
            }
            // TODO Add 'auth_time' claim
        }
        // @formatter:on

        JwsHeader.Builder headersBuilder = JwsHeader.with(SignatureAlgorithm.RS256);

        if (this.jwtCustomizer != null) {
            // @formatter:off
            JwtEncodingContext.Builder jwtContextBuilder = JwtEncodingContext.with(headersBuilder, claimsBuilder)
                    .registeredClient(context.getRegisteredClient())
                    .principal(context.getPrincipal())
                    .authorizationServerContext(context.getAuthorizationServerContext())
                    .authorizedScopes(context.getAuthorizedScopes())
                    .tokenType(context.getTokenType())
                    .authorizationGrantType(context.getAuthorizationGrantType());
            if (context.getAuthorization() != null) {
                jwtContextBuilder.authorization(context.getAuthorization());
            }
            if (context.getAuthorizationGrant() != null) {
                jwtContextBuilder.authorizationGrant(context.getAuthorizationGrant());
            }
            // @formatter:on

            JwtEncodingContext jwtContext = jwtContextBuilder.build();
            this.jwtCustomizer.customize(jwtContext);
        }

        JwsHeader headers = headersBuilder.build();
        JwtClaimsSet claims = claimsBuilder.build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(headers, claims));
    }

    /**
     * Sets the {@link OAuth2TokenCustomizer} that customizes the
     * {@link JwtEncodingContext#getJwsHeader()} () headers} and/or
     * {@link JwtEncodingContext#getClaims() claims} for the generated {@link Jwt}.
     *
     * @param jwtCustomizer the {@link OAuth2TokenCustomizer} that customizes the headers and/or claims for the generated {@code Jwt}
     */
    public void setJwtCustomizer(OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer) {
        Assert.notNull(jwtCustomizer, "jwtCustomizer cannot be null");
        this.jwtCustomizer = jwtCustomizer;
    }

}
