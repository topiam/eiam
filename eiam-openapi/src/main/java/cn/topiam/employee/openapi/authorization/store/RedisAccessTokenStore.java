/*
 * eiam-openapi - Employee Identity and Access Management
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
package cn.topiam.employee.openapi.authorization.store;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import cn.topiam.employee.openapi.authorization.AccessToken;
import static cn.topiam.employee.support.constant.EiamConstants.COLON;

/**
 * RedisTokenStore
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/5/8 21:54
 */
public class RedisAccessTokenStore implements AccessTokenStore {

    /**
     * save
     *
     * @param accessToken {@link AccessToken}
     */
    @Override
    public void save(AccessToken accessToken) {
        //缓存 index
        redisTemplate.opsForValue().set(getIndexKey(accessToken.getClientId()),
            accessToken.getValue(), accessToken.getExpiresIn(), TimeUnit.SECONDS);
        //缓存 access_token
        redisTemplate.opsForValue().set(getAccessTokenKey(accessToken.getValue()), accessToken,
            accessToken.getExpiresIn(), TimeUnit.SECONDS);
    }

    /**
     * 根据token删除
     *
     * @param token {@link String}
     */
    @Override
    public void deleteByToken(String token) {
        AccessToken accessToken = (AccessToken) redisTemplate.opsForValue()
            .get(getAccessTokenKey(token));
        if (!Objects.isNull(accessToken)) {
            //删除index
            redisTemplate.delete(getIndexKey(accessToken.getClientId()));
            //删除access_token
            redisTemplate.delete(getAccessTokenKey(token));
        }
    }

    /**
     * 根据token查询
     *
     * @param token {@link String}
     * @return {@link AccessToken}
     */
    @Override
    public AccessToken findByToken(String token) {
        return (AccessToken) redisTemplate.opsForValue().get(getAccessTokenKey(token));
    }

    /**
     * 根据 clientId 查询
     *
     * @param clientId {@link String} 客户端ID
     * @return {@link List}
     */
    @Override
    public AccessToken findByClientId(String clientId) {
        String index = (String) redisTemplate.opsForValue().get(getIndexKey(clientId));
        if (StringUtils.isNotEmpty(index)) {
            return (AccessToken) redisTemplate.opsForValue().get(getAccessTokenKey(index));
        }
        return null;
    }

    /**
     * RedisTemplate
     */
    private final RedisTemplate<Object, Object> redisTemplate;

    public static final String                  OPENAPI_TOKEN_KEY_PREFIX = "openapi" + COLON
                                                                           + "access_token" + COLON;
    public static final String                  INDEX_KEY_PREFIX         = "openapi" + COLON
                                                                           + "index" + COLON;

    private static String getAccessTokenKey(String key) {
        return OPENAPI_TOKEN_KEY_PREFIX + key;
    }

    private static String getIndexKey(String key) {
        return INDEX_KEY_PREFIX + key;
    }

    public RedisAccessTokenStore(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
