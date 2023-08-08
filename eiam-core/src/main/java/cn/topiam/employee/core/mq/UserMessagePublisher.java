/*
 * eiam-core - Employee Identity and Access Management
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
package cn.topiam.employee.core.mq;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.RabbitConverterFuture;
import org.springframework.stereotype.Component;

import cn.topiam.employee.support.constant.EiamConstants;

import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.PostConstruct;

/**
 * 用户MQ消息发送
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/5/30 23:12
 */
@Slf4j
@Component
public class UserMessagePublisher extends AbstractMessagePublisher {

    @PostConstruct
    public void init() {
        TopicExchange topicExchange = new TopicExchange(USER);
        Queue saveQueue = new Queue(USER_SAVE, true);
        Queue deleteQueue = new Queue(USER_DELETE, true);
        amqpAdmin.declareExchange(topicExchange);
        amqpAdmin.declareQueue(saveQueue);
        amqpAdmin.declareQueue(deleteQueue);
        amqpAdmin.declareBinding(BindingBuilder.bind(saveQueue).to(topicExchange).with(USER_SAVE));
        amqpAdmin
            .declareBinding(BindingBuilder.bind(deleteQueue).to(topicExchange).with(USER_DELETE));
    }

    /**
     * 构造函数
     *
     * @param asyncRabbitTemplate {@link AsyncRabbitTemplate}
     * @param amqpAdmin           {@link AmqpAdmin}
     */
    public UserMessagePublisher(AsyncRabbitTemplate asyncRabbitTemplate, AmqpAdmin amqpAdmin) {
        super(asyncRabbitTemplate, amqpAdmin);
    }

    /**
     * 发送异步用户消息
     *
     * @param tag     {@link UserMessageTag} SAVE/DELETE
     * @param userIds {@link String} 逗号分隔的用户id
     */
    public void sendUserChangeMessage(UserMessageTag tag, String userIds) {
        log.info("发送[{}]用户消息, 用户ID:[{}]", tag.name(), userIds);
        String routingKey = USER + EiamConstants.POINT + tag.name().toLowerCase();
        RabbitConverterFuture<Object> future = sendMessage(USER, routingKey, userIds);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("发送[{}]用户消息成功, 用户ID:[{}]，处理结果为:[{}]", tag.name(), userIds, result);
            } else {
                log.info("发送[{}]用户消息异常，用户ID:[{}]，失败原因:[{}] ", tag.name(), userIds, ex);
            }
        });
    }
}
