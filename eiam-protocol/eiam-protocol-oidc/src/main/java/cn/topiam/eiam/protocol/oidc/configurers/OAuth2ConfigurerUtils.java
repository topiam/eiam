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
package cn.topiam.eiam.protocol.oidc.configurers;

import java.util.Objects;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.*;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import cn.topiam.employee.common.repository.app.AppCertRepository;
import cn.topiam.employee.common.repository.app.AppOidcConfigRepository;
import static cn.topiam.employee.support.security.util.HttpSecurityConfigUtils.getOptionalBean;

/**
 * Utility methods for the OAuth 2.0 Configurers.
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/27 21:38
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public final class OAuth2ConfigurerUtils {

    private OAuth2ConfigurerUtils() {
    }

    static RegisteredClientRepository getRegisteredClientRepository(HttpSecurity httpSecurity) {
        RegisteredClientRepository registeredClientRepository = httpSecurity
            .getSharedObject(RegisteredClientRepository.class);
        if (registeredClientRepository == null) {
            registeredClientRepository = getBean(httpSecurity, RegisteredClientRepository.class);
            httpSecurity.setSharedObject(RegisteredClientRepository.class,
                registeredClientRepository);
        }
        return registeredClientRepository;
    }

    public static OAuth2AuthorizationService getAuthorizationService(HttpSecurity httpSecurity) {
        OAuth2AuthorizationService authorizationService = httpSecurity
            .getSharedObject(OAuth2AuthorizationService.class);
        if (authorizationService == null) {
            authorizationService = getOptionalBean(httpSecurity, OAuth2AuthorizationService.class);
            if (authorizationService == null) {
                authorizationService = new InMemoryOAuth2AuthorizationService();
            }
            httpSecurity.setSharedObject(OAuth2AuthorizationService.class, authorizationService);
        }
        return authorizationService;
    }

    static OAuth2AuthorizationConsentService getAuthorizationConsentService(HttpSecurity httpSecurity) {
        OAuth2AuthorizationConsentService authorizationConsentService = httpSecurity
            .getSharedObject(OAuth2AuthorizationConsentService.class);
        if (authorizationConsentService == null) {
            authorizationConsentService = getOptionalBean(httpSecurity,
                OAuth2AuthorizationConsentService.class);
            if (authorizationConsentService == null) {
                authorizationConsentService = new InMemoryOAuth2AuthorizationConsentService();
            }
            httpSecurity.setSharedObject(OAuth2AuthorizationConsentService.class,
                authorizationConsentService);
        }
        return authorizationConsentService;
    }

    @SuppressWarnings("unchecked")
    static OAuth2TokenGenerator<? extends OAuth2Token> getTokenGenerator(HttpSecurity httpSecurity) {
        OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator = httpSecurity
            .getSharedObject(OAuth2TokenGenerator.class);
        if (tokenGenerator == null) {
            tokenGenerator = getOptionalBean(httpSecurity, OAuth2TokenGenerator.class);
            if (tokenGenerator == null) {
                JwtGenerator jwtGenerator = getJwtGenerator(httpSecurity);
                OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
                OAuth2TokenCustomizer<OAuth2TokenClaimsContext> accessTokenCustomizer = getAccessTokenCustomizer(
                    httpSecurity);
                if (accessTokenCustomizer != null) {
                    accessTokenGenerator.setAccessTokenCustomizer(accessTokenCustomizer);
                }
                OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
                if (jwtGenerator != null) {
                    tokenGenerator = new DelegatingOAuth2TokenGenerator(jwtGenerator,
                        accessTokenGenerator, refreshTokenGenerator);
                } else {
                    tokenGenerator = new DelegatingOAuth2TokenGenerator(accessTokenGenerator,
                        refreshTokenGenerator);
                }
            }
            httpSecurity.setSharedObject(OAuth2TokenGenerator.class, tokenGenerator);
        }
        return tokenGenerator;
    }

    private static JwtGenerator getJwtGenerator(HttpSecurity httpSecurity) {
        JwtGenerator jwtGenerator = httpSecurity.getSharedObject(JwtGenerator.class);
        if (jwtGenerator == null) {
            JwtEncoder jwtEncoder = getJwtEncoder(httpSecurity);
            if (jwtEncoder != null) {
                jwtGenerator = new JwtGenerator(jwtEncoder);
                OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer = getJwtCustomizer(
                    httpSecurity);
                if (jwtCustomizer != null) {
                    jwtGenerator.setJwtCustomizer(jwtCustomizer);
                }
                httpSecurity.setSharedObject(JwtGenerator.class, jwtGenerator);
            }
        }
        return jwtGenerator;
    }

    private static JwtEncoder getJwtEncoder(HttpSecurity httpSecurity) {
        JwtEncoder jwtEncoder = httpSecurity.getSharedObject(JwtEncoder.class);
        if (jwtEncoder == null) {
            jwtEncoder = getOptionalBean(httpSecurity, JwtEncoder.class);
            if (jwtEncoder == null) {
                JWKSource<SecurityContext> jwkSource = getJwkSource(httpSecurity);
                if (jwkSource != null) {
                    jwtEncoder = new NimbusJwtEncoder(jwkSource);
                }
            }
            if (jwtEncoder != null) {
                httpSecurity.setSharedObject(JwtEncoder.class, jwtEncoder);
            }
        }
        return jwtEncoder;
    }

    @SuppressWarnings("unchecked")
    static JWKSource<SecurityContext> getJwkSource(HttpSecurity httpSecurity) {
        JWKSource<SecurityContext> jwkSource = httpSecurity.getSharedObject(JWKSource.class);
        if (jwkSource == null) {
            ResolvableType type = ResolvableType.forClassWithGenerics(JWKSource.class,
                SecurityContext.class);
            jwkSource = getOptionalBean(httpSecurity, type);
            if (Objects.isNull(jwkSource)) {
                jwkSource = new ClientJwkSource();
            }
            httpSecurity.setSharedObject(JWKSource.class, jwkSource);
        }
        return jwkSource;
    }

    private static OAuth2TokenCustomizer<JwtEncodingContext> getJwtCustomizer(HttpSecurity httpSecurity) {
        ResolvableType type = ResolvableType.forClassWithGenerics(OAuth2TokenCustomizer.class,
            JwtEncodingContext.class);
        return getOptionalBean(httpSecurity, type);
    }

    private static OAuth2TokenCustomizer<OAuth2TokenClaimsContext> getAccessTokenCustomizer(HttpSecurity httpSecurity) {
        ResolvableType type = ResolvableType.forClassWithGenerics(OAuth2TokenCustomizer.class,
            OAuth2TokenClaimsContext.class);
        return getOptionalBean(httpSecurity, type);
    }

    static AuthorizationServerSettings getAuthorizationServerSettings(HttpSecurity httpSecurity) {
        AuthorizationServerSettings authorizationServerSettings = httpSecurity
            .getSharedObject(AuthorizationServerSettings.class);
        if (authorizationServerSettings == null) {
            authorizationServerSettings = getBean(httpSecurity, AuthorizationServerSettings.class);
            httpSecurity.setSharedObject(AuthorizationServerSettings.class,
                authorizationServerSettings);
        }
        return authorizationServerSettings;
    }

    static <T> T getBean(HttpSecurity httpSecurity, Class<T> type) {
        return httpSecurity.getSharedObject(ApplicationContext.class).getBean(type);
    }

    @SuppressWarnings("unchecked")
    static <T> T getBean(HttpSecurity httpSecurity, ResolvableType type) {
        ApplicationContext context = httpSecurity.getSharedObject(ApplicationContext.class);
        String[] names = context.getBeanNamesForType(type);
        if (names.length == 1) {
            return (T) context.getBean(names[0]);
        }
        if (names.length > 1) {
            throw new NoUniqueBeanDefinitionException(type, names);
        }
        throw new NoSuchBeanDefinitionException(type);
    }

    public static AppOidcConfigRepository getAppOidcConfigRepository(HttpSecurity httpSecurity) {
        AppOidcConfigRepository appRepository = httpSecurity
            .getSharedObject(AppOidcConfigRepository.class);
        if (appRepository == null) {
            appRepository = getBean(httpSecurity, AppOidcConfigRepository.class);
            httpSecurity.setSharedObject(AppOidcConfigRepository.class, appRepository);
        }
        return appRepository;
    }

    public static AppCertRepository getAppCertRepository(HttpSecurity httpSecurity) {
        AppCertRepository appRepository = httpSecurity.getSharedObject(AppCertRepository.class);
        if (appRepository == null) {
            appRepository = getBean(httpSecurity, AppCertRepository.class);
            httpSecurity.setSharedObject(AppCertRepository.class, appRepository);
        }
        return appRepository;
    }

}
