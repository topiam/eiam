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

import cn.topiam.employee.common.message.enums.MessageType;
import cn.topiam.employee.support.constant.EiamConstants;

import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.PostConstruct;

/**
 * MQ消息发送
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/15 23:12
 */
@Slf4j
@Component
public class NoticeMessagePublisher extends AbstractMessagePublisher {

    @PostConstruct
    public void init() {
        TopicExchange topicExchange = new TopicExchange(NOTICE);
        Queue smsQueue = new Queue(NOTICE_SMS, true);
        Queue mailQueue = new Queue(NOTICE_MAIL, true);
        amqpAdmin.declareExchange(topicExchange);
        amqpAdmin.declareQueue(smsQueue);
        amqpAdmin.declareQueue(mailQueue);
        amqpAdmin.declareBinding(BindingBuilder.bind(smsQueue).to(topicExchange).with(NOTICE_SMS));
        amqpAdmin
            .declareBinding(BindingBuilder.bind(mailQueue).to(topicExchange).with(NOTICE_MAIL));
    }

    /**
     * 构造函数
     *
     * @param asyncRabbitTemplate {@link AsyncRabbitTemplate}
     * @param amqpAdmin           {@link AmqpAdmin}
     */
    public NoticeMessagePublisher(AsyncRabbitTemplate asyncRabbitTemplate, AmqpAdmin amqpAdmin) {
        super(asyncRabbitTemplate, amqpAdmin);
    }

    /**
     * 发送异步短信邮件通知消息
     *
     * @param type    {@link MessageType}
     * @param message {@link String}
     */
    public void sendNotice(MessageType type, String message) {
        log.info("发送[{}]通知消息, message:[{}]", type.getDesc(), message);
        String routingKey = NOTICE + EiamConstants.POINT + type.getCode().toLowerCase();
        RabbitConverterFuture<Object> future = sendMessage(NOTICE, routingKey, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("发送[{}]通知消息成功, message:[{}]，处理结果为:[{}]", type.getDesc(), message, result);
            } else {
                log.info("发送[{}]通知消息异常，message:[{}],失败原因:[{}], ", type.getDesc(), message,
                    ex.getMessage(), ex);
            }
        });
    }
}
