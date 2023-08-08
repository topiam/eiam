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

import cn.topiam.employee.support.context.ApplicationContextHelp;

/**
 * 身份源配置事件工具
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/19 22:42
 */
public class IdentitySourceEventUtils {
    public static final String IDENTITY_SOURCE_EVENT = "eiam-identity-source-event";

    /**
     * refresh
     * <p>
     * 利用redis订阅分发，完成集群参数刷新加载
     */
    public static void destroy(String id) {
        RedissonClient redisson = ApplicationContextHelp.getBean(RedissonClient.class);
        RTopic topic = redisson.getTopic(IDENTITY_SOURCE_EVENT);
        topic.publish(new IdentitySourceEvent(id, IdentitySourceEventType.DESTROY));
    }

    /**
     * refresh
     * <p>
     * 利用redis订阅分发，完成集群参数刷新加载
     */
    public static void register(String id) {
        RedissonClient redisson = ApplicationContextHelp.getBean(RedissonClient.class);
        RTopic topic = redisson.getTopic(IDENTITY_SOURCE_EVENT);
        topic.publish(new IdentitySourceEvent(id, IdentitySourceEventType.REGISTER));
    }

    /**
     * sync
     * <p>
     * 利用redis订阅分发，发送同步任务
     */
    public static void sync(String id) {
        RedissonClient redisson = ApplicationContextHelp.getBean(RedissonClient.class);
        RTopic topic = redisson.getTopic(IDENTITY_SOURCE_EVENT);
        topic.publish(new IdentitySourceEvent(id, IdentitySourceEventType.SYNC));
    }

}
