/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.mq;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.RabbitConverterFuture;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.audit.event.AuditEvent;
import cn.topiam.employee.support.trace.TraceUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.PostConstruct;
import static cn.topiam.employee.support.trace.TraceAspect.TRACE_ID;

/**
 * MQ消息发送
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/5/30 23:12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditMessagePublisher {

    private final AsyncRabbitTemplate asyncRabbitTemplate;

    private final AmqpAdmin           amqpAdmin;

    public final static String        AUDIT_TOPIC = "audit";

    @PostConstruct
    public void init() {
        TopicExchange topicExchange = new TopicExchange(AUDIT_TOPIC);
        Queue queue = new Queue(AUDIT_TOPIC, true);
        amqpAdmin.declareExchange(topicExchange);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(topicExchange).with(AUDIT_TOPIC));
    }

    /**
     * 发送异步审计消息
     *
     * @param data {@link String} 审计内容
     */
    public void sendAuditChangeMessage(AuditEvent data) {
        String traceId = TraceUtils.get();
        log.info("发送审计消息, 审计内容:[{}]", JSONObject.toJSONString(data));
        RabbitConverterFuture<Object> future = asyncRabbitTemplate.convertSendAndReceive(
            AUDIT_TOPIC, AUDIT_TOPIC,
            MessageBuilder.withPayload(data).setHeader(TRACE_ID, traceId).build());
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("发送审计消息成功，处理结果为:[{}]", result);
            } else {
                log.error("发送审计消息异常，审计内容:[{}]", JSONObject.toJSONString(data), ex);
            }
        });
    }
}
