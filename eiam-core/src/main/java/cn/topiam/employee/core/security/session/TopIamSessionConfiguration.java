/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.security.session;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.session.RedisSessionProperties;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.NonNull;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.core.security.jackson2.CoreJackson2Module;

import lombok.RequiredArgsConstructor;

/**
 * SessionConfig
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/8 21:15
 */
@Configuration
@RequiredArgsConstructor
@AutoConfigureAfter({ SessionAutoConfiguration.class })
@EnableConfigurationProperties({ RedisSessionProperties.class })
public class TopIamSessionConfiguration implements BeanClassLoaderAware {

    private ClassLoader loader;

    @Bean
    @ConditionalOnMissingBean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(objectMapper());
    }

    /**
     * Customized {@link ObjectMapper} to add mix-in for class that doesn't have default
     * constructors
     * @return the {@link ObjectMapper} to use
     */
    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        CoreJackson2Module jackson2Module = new CoreJackson2Module();
        List<Module> modules = SecurityJackson2Modules.getModules(this.loader);
        mapper.registerModules(modules);
        mapper.registerModules(jackson2Module);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    @Override
    public void setBeanClassLoader(@NonNull ClassLoader classLoader) {
        this.loader = classLoader;
    }

    /**
     * 是session为Spring Security提供的
     *
     * 用于在集群环境下控制会话并发的会话注册表实现
     *
     * @return {@link SessionRegistry}
     */
    @Bean
    @ConditionalOnMissingBean(value = TopIamSessionBackedSessionRegistry.class)
    public SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry(FindByIndexNameSessionRepository<? extends Session> sessionRepository,
                                                                                 RedisSessionProperties redisSessionProperties,
                                                                                 StringRedisTemplate stringRedisTemplate) {
        return new TopIamSessionBackedSessionRegistry<>(sessionRepository, redisSessionProperties,
            stringRedisTemplate);
    }

    /**
     * 配置TokenRepository
     *
     * @return {@link PersistentTokenRepository}
     */
    @Bean
    @ConditionalOnMissingBean(value = PersistentTokenRepository.class)
    public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
        JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
        repository.setDataSource(dataSource);
        return repository;
    }

    /**
     * 配置密码加密
     *
     * @return {@link PasswordEncoder}
     */
    @Bean
    @ConditionalOnMissingBean(value = PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
