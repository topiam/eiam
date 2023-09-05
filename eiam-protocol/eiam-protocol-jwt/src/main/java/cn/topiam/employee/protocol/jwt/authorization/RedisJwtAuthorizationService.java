/*
 * eiam-protocol-jwt - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.jwt.authorization;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.http.converter.json.SpringHandlerInstantiator;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.protocol.jwt.authentication.JwtAuthenticationToken;
import cn.topiam.employee.protocol.jwt.jackson.JwtAuthorizationModule;
import cn.topiam.employee.support.jackjson.SupportJackson2Module;

import lombok.Setter;
import static cn.topiam.employee.protocol.jwt.constant.JwtProtocolConstants.JWT_PROTOCOL_CACHE_PREFIX;

/**
 * redis
 *
 * @author TopIAM
 *
 * Created by support@topiam.cn / 2689170096@qq.com on  2023/9/1 12:51
 */
public class RedisJwtAuthorizationService implements JwtAuthorizationService {
    private static final String CID_TO_AUTHORIZATIONS = "cid_to_authorizations:";
    private static final String UID_TO_AUTHORIZATIONS = "uid_to_authorizations:";
    private static final String ID_TO_AUTHORIZATION   = "id_to_authorization:";
    private static final String ID_TO_CORRELATIONS    = "id_to_correlations:";

    public RedisJwtAuthorizationService(RedisOperations<String, String> redisOperations,
                                        AutowireCapableBeanFactory beanFactory) {
        Assert.notNull(redisOperations, "redisOperations mut not be null");
        this.redisOperations = redisOperations;
        ClassLoader classLoader = this.getClass().getClassLoader();
        objectMapper.registerModules(SupportJackson2Module.getModules(classLoader));
        objectMapper.registerModule(new JwtAuthorizationModule());
        objectMapper.setHandlerInstantiator(new SpringHandlerInstantiator(beanFactory));
    }

    /**
     * save
     *
     * @param token {@link JwtAuthenticationToken}
     */
    @Override
    public void save(JwtAuthenticationToken token) {
        String authorizationId = token.getId();
        String uidToAuthorizationsKey = getIdTokenToAuthorization(authorizationId);
        String idToAuthorizationKey = getIdToAuthorizationKey(authorizationId);
        String idToCorrelationsKey = getIdToCorrelations(authorizationId);
        String cidToAuthorizationsKey = getCidToAuthorizations(token.getConfig().getClientId());
        //过期时间
        Duration timeToLive = Duration.of(token.getConfig().getIdTokenTimeToLive(),
            ChronoUnit.SECONDS);
        Set<String> correlationValues = new HashSet<>();
        //add client authorizations
        correlationValues.add(cidToAuthorizationsKey);
        redisOperations.opsForSet().add(cidToAuthorizationsKey, authorizationId);
        redisOperations.expire(cidToAuthorizationsKey, timeToLive);
        //save authorization
        correlationValues.add(idToAuthorizationKey);
        redisOperations.opsForValue().set(idToAuthorizationKey, write(token));
        redisOperations.expire(idToAuthorizationKey, timeToLive);
        //save id_token
        correlationValues.add(uidToAuthorizationsKey);
        redisOperations.opsForValue().set(uidToAuthorizationsKey,
            token.getIdToken().getTokenValue());
        redisOperations.expire(uidToAuthorizationsKey, timeToLive);
        //save correlations
        correlationValues.add(idToCorrelationsKey);
        redisOperations.opsForSet().add(idToCorrelationsKey,
            correlationValues.toArray(String[]::new));
        redisOperations.expire(idToCorrelationsKey, timeToLive);
    }

    @Override
    public void remove(JwtAuthenticationToken authorization) {
        String authorizationId = authorization.getId();
        String uidToAuthorizationsKey = getIdTokenToAuthorization(authorizationId);
        String idToAuthorizationKey = getIdToAuthorizationKey(authorizationId);
        String idToCorrelationsKey = getIdToCorrelations(authorizationId);
        String cidToAuthorizationsKey = getCidToAuthorizations(
            authorization.getConfig().getClientId());
        redisOperations.delete(idToAuthorizationKey);
        redisOperations.delete(idToCorrelationsKey);
        redisOperations.delete(uidToAuthorizationsKey);
        redisOperations.opsForSet().remove(cidToAuthorizationsKey, authorization.getId());
    }

    @Override
    public JwtAuthenticationToken findById(String id) {
        return Optional.ofNullable(redisOperations.opsForValue().get(getIdToAuthorizationKey(id)))
            .map(this::parse).orElse(null);
    }

    @Override
    public JwtAuthenticationToken findByToken(String token) {
        return Optional
            .ofNullable(redisOperations.opsForValue().get(getIdTokenToAuthorization(token)))
            .map(this::parse).orElse(null);
    }

    private String getIdToCorrelations(String authorizationId) {
        return prefix + ID_TO_CORRELATIONS + authorizationId;
    }

    private String write(Object data) {
        try {
            return this.objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private JwtAuthenticationToken parse(String data) {
        try {
            return this.objectMapper.readValue(data, new TypeReference<>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    public String getCidToAuthorizations(String clientId) {
        return prefix + CID_TO_AUTHORIZATIONS + clientId;
    }

    private String getIdToAuthorizationKey(String authorizationId) {
        return prefix + ID_TO_AUTHORIZATION + authorizationId;
    }

    private String getIdTokenToAuthorization(String idToken) {
        return prefix + UID_TO_AUTHORIZATIONS + generateKey(idToken);
    }

    protected static String generateKey(String rawKey) {
        byte[] bytes = DIGEST.digest(rawKey.getBytes(StandardCharsets.UTF_8));
        return String.format("%032x", new BigInteger(1, bytes));
    }

    private final RedisOperations<String, String> redisOperations;

    @Setter
    private String                                prefix       = JWT_PROTOCOL_CACHE_PREFIX;

    @Setter
    private ObjectMapper                          objectMapper = new ObjectMapper();

    private static final MessageDigest            DIGEST;

    static {
        try {
            DIGEST = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
