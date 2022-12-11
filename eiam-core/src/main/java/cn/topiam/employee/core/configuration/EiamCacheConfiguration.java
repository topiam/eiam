/*
 * eiam-core - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.core.configuration;

import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.support.cache.TopIamKeyGenerator;
import static cn.topiam.employee.support.constant.EiamConstants.COLON;

/**
 * 缓存配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/18 23:03
 */
@Configuration
@EnableCaching
public class EiamCacheConfiguration {

    /**
     * 自定义KEY生成
     *
     * @return {@link String}
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return new TopIamKeyGenerator();
    }

    /**
     * Redis 缓存管理器构建器定制器
     * 这里只自定义序列化方式，或其他spring cache配置文件显示配置中没有的配置。
     *
     * @return {@link  RedisCacheManagerBuilderCustomizer}
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> {
            builder.cacheDefaults(createConfiguration(cacheProperties));
            List<String> cacheNames = cacheProperties.getCacheNames();
            if (!cacheNames.isEmpty()) {
                builder.initialCacheNames(new LinkedHashSet<>(cacheNames));
            }
            if (cacheProperties.getRedis().isEnableStatistics()) {
                builder.enableStatistics();
            }
        };
    }

    /**
     * 创建配置
     *
     * @param cacheProperties {@link  CacheProperties}
     * @return {@link  RedisCacheConfiguration}
     */
    private RedisCacheConfiguration createConfiguration(CacheProperties cacheProperties) {
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        ObjectMapper objectMapper = jacksonObjectMapper.copy();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(
            objectMapper);
        config = config.serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(serializer));
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.computePrefixWith(
                cacheName -> redisProperties.getKeyPrefix() + COLON + cacheName + COLON);
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }

    private final CacheProperties cacheProperties;

    private final ObjectMapper    jacksonObjectMapper;

    public EiamCacheConfiguration(CacheProperties cacheProperties,
                                  ObjectMapper jacksonObjectMapper) {
        this.cacheProperties = cacheProperties;
        this.jacksonObjectMapper = jacksonObjectMapper;
    }
}
