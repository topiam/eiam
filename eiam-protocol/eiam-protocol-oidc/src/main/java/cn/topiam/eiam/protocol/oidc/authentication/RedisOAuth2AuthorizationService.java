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

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.http.converter.json.SpringHandlerInstantiator;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.eiam.protocol.oidc.jackson.OAuth2AuthorizationModule;
import cn.topiam.employee.support.jackjson.SupportJackson2Module;

import lombok.Setter;
import static cn.topiam.eiam.protocol.oidc.constant.OidcProtocolConstants.ID_TOKEN;
import static cn.topiam.eiam.protocol.oidc.constant.OidcProtocolConstants.OIDC_PROTOCOL_CACHE_PREFIX;

/**
 * RedisOAuth2AuthorizationService
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/31 21:41
 */
@SuppressWarnings({ "AlibabaServiceOrDaoClassShouldEndWithImpl",
                    "AlibabaClassNamingShouldBeCamel" })
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private static final String                   ID_TO_AUTHORIZATION      = "id_to_authorization:";

    private static final String                   STATE_TO_AUTHORIZATION   = "state_to_authorization:";

    private static final String                   CODE_TO_AUTHORIZATION    = "code_to_authorization:";

    private static final String                   ACCESS_TO_AUTHORIZATION  = "access_to_authorization:";

    private static final String                   REFRESH_TO_AUTHORIZATION = "refresh_to_authorization:";

    private static final String                   ID_TO_CORRELATIONS       = "id_to_correlations:";

    private static final String                   UID_TO_AUTHORIZATIONS    = "uid_to_authorizations:";

    private static final String                   CID_TO_AUTHORIZATIONS    = "cid_to_authorizations:";

    private static final MessageDigest            DIGEST;

    private final RedisOperations<String, String> redisOperations;

    private final RegisteredClientRepository      clientRepository;

    @Setter
    private ObjectMapper                          objectMapper             = new ObjectMapper();

    @Setter
    private String                                prefix                   = OIDC_PROTOCOL_CACHE_PREFIX;

    static {
        try {
            DIGEST = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public RedisOAuth2AuthorizationService(RedisOperations<String, String> redisOperations,
                                           RegisteredClientRepository clientRepository,
                                           AutowireCapableBeanFactory beanFactory) {
        Assert.notNull(redisOperations, "redisOperations mut not be null");
        this.redisOperations = redisOperations;
        this.clientRepository = clientRepository;

        ClassLoader classLoader = this.getClass().getClassLoader();
        objectMapper.registerModules(SupportJackson2Module.getModules(classLoader));
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        objectMapper.registerModule(new OAuth2AuthorizationModule());
        objectMapper.setHandlerInstantiator(new SpringHandlerInstantiator(beanFactory));
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        final String clientId = authorization.getRegisteredClientId();
        RegisteredClient registeredClient = clientRepository.findById(clientId);
        Assert.notNull(registeredClient, "Registered client must not be null");

        Duration codeTtl = registeredClient.getTokenSettings().getAuthorizationCodeTimeToLive();
        Duration accessTokenTtl = registeredClient.getTokenSettings().getAccessTokenTimeToLive();
        Duration refreshTokenTtl = registeredClient.getTokenSettings().getRefreshTokenTimeToLive();
        // idToken过期时间
        // TODO 等 spring-authorization-server 授权服务器支持后更改
        Duration idTokenTtl = Duration.of(30, ChronoUnit.MINUTES);
        Duration max = authorization.getRefreshToken() == null ? accessTokenTtl
            : Collections.max(Arrays.asList(accessTokenTtl, refreshTokenTtl, idTokenTtl));

        final String authorizationId = authorization.getId();
        final String idToAuthorizationKey = getIdToAuthorizationKey(authorizationId);
        final String cidToAuthorizationsKey = getCidToAuthorizations(clientId);

        redisOperations.opsForValue().set(idToAuthorizationKey, write(authorization),
            max.getSeconds(), TimeUnit.SECONDS);

        redisOperations.opsForSet().add(cidToAuthorizationsKey, authorizationId);
        redisOperations.expire(cidToAuthorizationsKey, max);

        Set<String> correlationValues = new HashSet<>();
        //state
        Optional.ofNullable(authorization.getAttribute(OAuth2ParameterNames.STATE))
            .ifPresent(token -> {
                final String stateToAuthorizationKey = getStateToAuthorization((String) token);
                redisOperations.opsForValue().set(stateToAuthorizationKey, authorizationId,
                    codeTtl.getSeconds(), TimeUnit.SECONDS);
                correlationValues.add(stateToAuthorizationKey);
            });
        //授权码
        Optional.ofNullable(authorization.getToken(OAuth2AuthorizationCode.class))
            .ifPresent(token -> {
                final String codeToAuthorizationKey = getCodeToAuthorization(
                    token.getToken().getTokenValue());
                redisOperations.opsForValue().set(codeToAuthorizationKey, authorizationId,
                    codeTtl.getSeconds(), TimeUnit.SECONDS);
                correlationValues.add(codeToAuthorizationKey);
            });
        //access_token
        Optional.ofNullable(authorization.getAccessToken()).ifPresent(token -> {
            final String accessToAuthorization = getAccessTokenToAuthorization(
                token.getToken().getTokenValue());
            redisOperations.opsForValue().set(accessToAuthorization, authorizationId,
                accessTokenTtl.getSeconds(), TimeUnit.SECONDS);
            correlationValues.add(accessToAuthorization);
        });
        //refresh_token
        Optional.ofNullable(authorization.getRefreshToken()).ifPresent(token -> {
            final String refreshToAuthorization = getRefreshTokenToAuthorization(
                token.getToken().getTokenValue());
            redisOperations.opsForValue().set(refreshToAuthorization, authorizationId,
                refreshTokenTtl.getSeconds(), TimeUnit.SECONDS);
            correlationValues.add(refreshToAuthorization);
        });
        //id_token
        Optional
            .ofNullable(authorization
                .getToken(org.springframework.security.oauth2.core.oidc.OidcIdToken.class))
            .ifPresent(token -> {
                final String idTokenToAuthorization = getIdTokenToAuthorization(
                    token.getToken().getTokenValue());
                redisOperations.opsForValue().set(idTokenToAuthorization, authorizationId,
                    idTokenTtl.getSeconds(), TimeUnit.SECONDS);
                correlationValues.add(idTokenToAuthorization);
            });
        if (!CollectionUtils.isEmpty(correlationValues)) {
            redisOperations.opsForSet().add(getIdToCorrelations(authorizationId),
                correlationValues.toArray(String[]::new));
            redisOperations.expire(getIdToCorrelations(authorizationId), max);
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        List<String> keysToRemove = new ArrayList<>();
        keysToRemove.add(getIdToAuthorizationKey(authorization.getId()));
        keysToRemove.add(getIdToCorrelations(authorization.getId()));
        Optional
            .ofNullable(
                redisOperations.opsForSet().members(getIdToCorrelations(authorization.getId())))
            .ifPresent(keysToRemove::addAll);
        redisOperations.delete(keysToRemove);

        final String clientId = authorization.getRegisteredClientId();
        redisOperations.opsForSet().remove(getCidToAuthorizations(clientId), authorization.getId());
    }

    @Override
    public OAuth2Authorization findById(String id) {
        return Optional.ofNullable(redisOperations.opsForValue().get(getIdToAuthorizationKey(id)))
            .map(this::parse).orElse(null);
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
        if (tokenType == null) {
            return Optional
                .ofNullable(redisOperations.opsForValue().get(getStateToAuthorization(token)))
                .or(() -> Optional
                    .ofNullable(redisOperations.opsForValue().get(getCodeToAuthorization(token))))
                .or(() -> Optional.ofNullable(
                    redisOperations.opsForValue().get(getAccessTokenToAuthorization(token))))
                .or(() -> Optional.ofNullable(
                    redisOperations.opsForValue().get(getRefreshTokenToAuthorization(token))))
                .map(this::findById).orElse(null);
        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            return Optional
                .ofNullable(redisOperations.opsForValue().get(getStateToAuthorization(token)))
                .map(this::findById).orElse(null);
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            return Optional
                .ofNullable(redisOperations.opsForValue().get(getCodeToAuthorization(token)))
                .map(this::findById).orElse(null);
        } else if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
            return Optional
                .ofNullable(redisOperations.opsForValue().get(getAccessTokenToAuthorization(token)))
                .map(this::findById).orElse(null);
        } else if (OAuth2TokenType.REFRESH_TOKEN.equals(tokenType)) {
            return Optional
                .ofNullable(
                    redisOperations.opsForValue().get(getRefreshTokenToAuthorization(token)))
                .map(this::findById).orElse(null);
        } else if (ID_TOKEN.equals(tokenType)) {
            return Optional
                .ofNullable(redisOperations.opsForValue().get(getIdTokenToAuthorization(token)))
                .map(this::findById).orElse(null);
        }
        return null;
    }

    private String getIdToAuthorizationKey(String authorizationId) {
        return prefix + ID_TO_AUTHORIZATION + authorizationId;
    }

    private String getStateToAuthorization(String state) {
        return prefix + STATE_TO_AUTHORIZATION + generateKey(state);
    }

    private String getCodeToAuthorization(String code) {
        return prefix + CODE_TO_AUTHORIZATION + generateKey(code);
    }

    private String getAccessTokenToAuthorization(String accessToken) {
        return prefix + ACCESS_TO_AUTHORIZATION + generateKey(accessToken);
    }

    private String getRefreshTokenToAuthorization(String refreshToken) {
        return prefix + REFRESH_TO_AUTHORIZATION + generateKey(refreshToken);
    }

    private String getIdTokenToAuthorization(String idToken) {
        return prefix + UID_TO_AUTHORIZATIONS + generateKey(idToken);
    }

    private String getIdToCorrelations(String authorizationId) {
        return prefix + ID_TO_CORRELATIONS + authorizationId;
    }

    public String getCidToAuthorizations(String clientId) {
        return prefix + CID_TO_AUTHORIZATIONS + clientId;
    }

    protected static String generateKey(String rawKey) {
        byte[] bytes = DIGEST.digest(rawKey.getBytes(StandardCharsets.UTF_8));
        return String.format("%032x", new BigInteger(1, bytes));
    }

    private String write(Object data) {
        try {
            return this.objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private OAuth2Authorization parse(String data) {
        try {
            return this.objectMapper.readValue(data, new TypeReference<>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

}
