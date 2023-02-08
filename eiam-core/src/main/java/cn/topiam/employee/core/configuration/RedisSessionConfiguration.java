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
package cn.topiam.employee.core.configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.*;
import org.springframework.session.config.SessionRepositoryCustomizer;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.config.ConfigureNotifyKeyspaceEventsAction;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;
import org.springframework.util.ClassUtils;

@Configuration
public class RedisSessionConfiguration implements BeanClassLoaderAware {

    private Integer                                                          maxInactiveIntervalInSeconds = MapSession.DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS;

    private String                                                           redisNamespace               = RedisIndexedSessionRepository.DEFAULT_NAMESPACE;

    private FlushMode                                                        flushMode                    = FlushMode.ON_SAVE;

    private SaveMode                                                         saveMode                     = SaveMode.ON_SET_ATTRIBUTE;

    private RedisConnectionFactory                                           redisConnectionFactory;

    private IndexResolver<Session>                                           indexResolver;

    private RedisSerializer<Object>                                          defaultRedisSerializer;

    private ApplicationEventPublisher                                        applicationEventPublisher;

    private List<SessionRepositoryCustomizer<RedisIndexedSessionRepository>> sessionRepositoryCustomizers;

    private ConfigureRedisAction                                             configureRedisAction         = new ConfigureNotifyKeyspaceEventsAction();

    private ClassLoader                                                      classLoader;

    private Executor                                                         redisTaskExecutor;

    private Executor                                                         redisSubscriptionExecutor;

    @Autowired(required = false)
    @Qualifier("springSessionRedisSubscriptionExecutor")
    public void setRedisSubscriptionExecutor(Executor redisSubscriptionExecutor) {
        this.redisSubscriptionExecutor = redisSubscriptionExecutor;
    }

    /**
     * Sets the action to perform for configuring Redis.
     * @param configureRedisAction the configureRedis to set. The default is
     * {@link ConfigureNotifyKeyspaceEventsAction}.
     */
    @Autowired(required = false)
    public void setConfigureRedisAction(ConfigureRedisAction configureRedisAction) {
        this.configureRedisAction = configureRedisAction;
    }

    @Autowired(required = false)
    @Qualifier("springSessionDefaultRedisSerializer")
    public void setDefaultRedisSerializer(RedisSerializer<Object> defaultRedisSerializer) {
        this.defaultRedisSerializer = defaultRedisSerializer;
    }

    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Autowired
    public void setRedisConnectionFactory(@SpringSessionRedisConnectionFactory ObjectProvider<RedisConnectionFactory> springSessionRedisConnectionFactory,
                                          ObjectProvider<RedisConnectionFactory> redisConnectionFactory) {
        RedisConnectionFactory redisConnectionFactoryToUse = springSessionRedisConnectionFactory
            .getIfAvailable();
        if (redisConnectionFactoryToUse == null) {
            redisConnectionFactoryToUse = redisConnectionFactory.getObject();
        }
        this.redisConnectionFactory = redisConnectionFactoryToUse;
    }

    @Autowired(required = false)
    public void setIndexResolver(IndexResolver<Session> indexResolver) {
        this.indexResolver = indexResolver;
    }

    @Autowired(required = false)
    public void setSessionRepositoryCustomizer(ObjectProvider<SessionRepositoryCustomizer<RedisIndexedSessionRepository>> sessionRepositoryCustomizers) {
        this.sessionRepositoryCustomizers = sessionRepositoryCustomizers.orderedStream()
            .collect(Collectors.toList());
    }

    @Autowired(required = false)
    @Qualifier("springSessionRedisTaskExecutor")
    public void setRedisTaskExecutor(Executor redisTaskExecutor) {
        this.redisTaskExecutor = redisTaskExecutor;
    }

    @Bean
    public RedisIndexedSessionRepository sessionRepository() {
        RedisTemplate<Object, Object> redisTemplate = createRedisTemplate();
        CustomRedisSessionRepository sessionRepository = new CustomRedisSessionRepository(
            redisTemplate);
        sessionRepository.setApplicationEventPublisher(this.applicationEventPublisher);
        if (this.indexResolver != null) {
            sessionRepository.setIndexResolver(this.indexResolver);
        }
        if (this.defaultRedisSerializer != null) {
            sessionRepository.setDefaultSerializer(this.defaultRedisSerializer);
        }
        sessionRepository.setDefaultMaxInactiveInterval(this.maxInactiveIntervalInSeconds);
        // TODO
        //        if (StringUtils.hasText(this.redisNamespace)) {
        sessionRepository.setRedisKeyNamespace("topiam:session");
        //        }
        sessionRepository.setFlushMode(this.flushMode);
        sessionRepository.setSaveMode(this.saveMode);
        int database = resolveDatabase();
        sessionRepository.setDatabase(database);
        this.sessionRepositoryCustomizers
            .forEach((sessionRepositoryCustomizer) -> sessionRepositoryCustomizer
                .customize(sessionRepository));
        return sessionRepository;
    }

    @Bean
    public RedisMessageListenerContainer springSessionRedisMessageListenerContainer(RedisIndexedSessionRepository sessionRepository) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(this.redisConnectionFactory);
        if (this.redisTaskExecutor != null) {
            container.setTaskExecutor(this.redisTaskExecutor);
        }
        if (this.redisSubscriptionExecutor != null) {
            container.setSubscriptionExecutor(this.redisSubscriptionExecutor);
        }
        container.addMessageListener(sessionRepository,
            Arrays.asList(new ChannelTopic(sessionRepository.getSessionDeletedChannel()),
                new ChannelTopic(sessionRepository.getSessionExpiredChannel())));
        container.addMessageListener(sessionRepository, Collections.singletonList(
            new PatternTopic(sessionRepository.getSessionCreatedChannelPrefix() + "*")));
        return container;
    }

    @Bean
    public InitializingBean enableRedisKeyspaceNotificationsInitializer() {
        return new EnableRedisKeyspaceNotificationsInitializer(this.redisConnectionFactory,
            this.configureRedisAction);
    }

    static class EnableRedisKeyspaceNotificationsInitializer implements InitializingBean {

        private final RedisConnectionFactory connectionFactory;

        private ConfigureRedisAction         configure;

        EnableRedisKeyspaceNotificationsInitializer(RedisConnectionFactory connectionFactory,
                                                    ConfigureRedisAction configure) {
            this.connectionFactory = connectionFactory;
            this.configure = configure;
        }

        @Override
        public void afterPropertiesSet() {
            if (this.configure == ConfigureRedisAction.NO_OP) {
                return;
            }
            RedisConnection connection = this.connectionFactory.getConnection();
            try {
                this.configure.configure(connection);
            } finally {
                try {
                    connection.close();
                } catch (Exception ex) {
                    LogFactory.getLog(getClass()).error("Error closing RedisConnection", ex);
                }
            }
        }

    }

    private RedisTemplate<Object, Object> createRedisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        if (this.defaultRedisSerializer != null) {
            redisTemplate.setDefaultSerializer(this.defaultRedisSerializer);
        }
        redisTemplate.setConnectionFactory(this.redisConnectionFactory);
        redisTemplate.setBeanClassLoader(this.classLoader);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private int resolveDatabase() {
        if (ClassUtils.isPresent("io.lettuce.core.RedisClient", null)
            && this.redisConnectionFactory instanceof LettuceConnectionFactory) {
            return ((LettuceConnectionFactory) this.redisConnectionFactory).getDatabase();
        }
        if (ClassUtils.isPresent("redis.clients.jedis.Jedis", null)
            && this.redisConnectionFactory instanceof JedisConnectionFactory) {
            return ((JedisConnectionFactory) this.redisConnectionFactory).getDatabase();
        }
        return RedisIndexedSessionRepository.DEFAULT_DATABASE;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
