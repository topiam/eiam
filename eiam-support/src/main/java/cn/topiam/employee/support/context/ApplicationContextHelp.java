/*
 * eiam-support - Employee Identity and Access Management Program
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
package cn.topiam.employee.support.context;

import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;

import cn.topiam.employee.support.configrefresh.ConfigRefreshListenerRunner;

/**
 * 获取Spring上下文对象
 *
 * @author TopIAM
 * Created  by support@topiam.cn on  2018/2/23
 */
public final class ApplicationContextHelp {
    /**
     * 上下文对象实例
     */
    private static ApplicationContext applicationContext;

    /**
     * 构造函数
     * @param applicationContext {@link ApplicationContext}
     * @throws BeansException  BeansException
     */
    public static void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        ApplicationContextHelp.applicationContext = applicationContext;
    }

    /**
     * 获取applicationContext
     * @return {@link ApplicationContext}
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 通过name获取 Bean.
     * @param name {@link String} name
     * @return {@link Object}
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     * @param clazz {@link Class}
     * @param <T> type
     * @return {@link Object}
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     * @param name {@link String}
     * @param clazz {@link Class}
     * @param <T> type
     * @return T
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    /**
     * refresh
     *
     * 利用redis订阅分发，完成集群参数刷新加载
     */
    @Deprecated
    private static void refresh() {
        RedissonClient redisson = ApplicationContextHelp.getBean(RedissonClient.class);
        RTopic topic = redisson.getTopic(ConfigRefreshListenerRunner.CONFIG_REFRESH);
        topic.publish(ConfigRefreshListenerRunner.REFRESH_ALL);
    }

    /**
     * refresh
     * @param beanName {@link  String}
     * 利用redis订阅分发，完成集群参数刷新加载
     */
    public static void refresh(String beanName) {
        RedissonClient redisson = ApplicationContextHelp.getBean(RedissonClient.class);
        RTopic topic = redisson.getTopic(ConfigRefreshListenerRunner.CONFIG_REFRESH);
        topic.publish(beanName);
    }
}
