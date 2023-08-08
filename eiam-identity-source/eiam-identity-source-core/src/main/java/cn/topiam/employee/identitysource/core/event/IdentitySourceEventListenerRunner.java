/*
 * eiam-identity-source-core - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.core.event;

import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 身份源配置事件刷新
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/20 21:45
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
@ConditionalOnBean(value = IdentitySourceEventListener.class)
public class IdentitySourceEventListenerRunner implements CommandLineRunner, Ordered,
                                               ApplicationContextAware {

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) {
        RTopic topic = redissonClient.getTopic(IdentitySourceEventUtils.IDENTITY_SOURCE_EVENT);
        //添加监听器
        topic.addListener(IdentitySourceEvent.class, (channel, msg) -> {
            //注册
            if (IdentitySourceEventType.REGISTER.equals(msg.getIdentitySourceEventType())) {
                identitySourceEventListener.register(msg.getId());
                return;
            }
            //卸载
            if (IdentitySourceEventType.DESTROY.equals(msg.getIdentitySourceEventType())) {
                identitySourceEventListener.destroy(msg.getId());
            }
            //同步
            if (IdentitySourceEventType.SYNC.equals(msg.getIdentitySourceEventType())) {
                identitySourceEventListener.sync(msg.getId());
            }
        });
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    /**
     * Set the ApplicationContext that this object runs in.
     * Normally this call will be used to initialize the object.
     * <p>Invoked after population of normal bean properties but before an init callback such
     * as {@link InitializingBean#afterPropertiesSet()}
     * or a custom init-method. Invoked after {@link ResourceLoaderAware#setResourceLoader},
     * {@link ApplicationEventPublisherAware#setApplicationEventPublisher} and
     * {@link MessageSourceAware}, if applicable.
     *
     * @param applicationContext the ApplicationContext object to be used by this object
     * @throws ApplicationContextException in case of context initialization errors
     * @throws BeansException              if thrown by application context methods
     * @see BeanInitializationException
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Getter
    private ApplicationContext                applicationContext;

    private final RedissonClient              redissonClient;

    private final IdentitySourceEventListener identitySourceEventListener;

}
