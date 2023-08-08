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
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.RabbitConverterFuture;
import org.springframework.messaging.support.MessageBuilder;

import cn.topiam.employee.support.constant.EiamConstants;
import cn.topiam.employee.support.trace.TraceUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.support.trace.TraceAspect.TRACE_ID;

/**
 * 消息发送
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/15 23:12
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractMessagePublisher {

    /**
     * 用户消息
     */
    public final static String        USER        = "user";

    /**
     * 用户保存
     */
    public final static String        USER_SAVE   = USER + EiamConstants.POINT + "save";

    /**
     * 用户删除
     */
    public final static String        USER_DELETE = USER + EiamConstants.POINT + "delete";

    /**
     * 短信/邮件消息
     */
    public final static String        NOTICE      = "notice";

    /**
     * 短信
     */
    public final static String        NOTICE_SMS  = NOTICE + EiamConstants.POINT + "sms";

    /**
     * 邮件
     */
    public final static String        NOTICE_MAIL = NOTICE + EiamConstants.POINT + "mail";

    /**
     * AsyncRabbitTemplate
     */
    private final AsyncRabbitTemplate asyncRabbitTemplate;

    /**
     * AmqpAdmin
     */
    protected final AmqpAdmin         amqpAdmin;

    /**
     * 发送异步RocketMQ消息
     *
     * @param exchange   {@link String}
     * @param routingKey {@link String}
     * @param message    {@link String}
     */
    public RabbitConverterFuture<Object> sendMessage(String exchange, String routingKey,
                                                     String message) {
        // 获取traceId，放入消息keys属性中
        String traceId = TraceUtils.get();
        RabbitConverterFuture<Object> future = asyncRabbitTemplate.convertSendAndReceive(exchange,
            routingKey, MessageBuilder.withPayload(message).setHeader(TRACE_ID, traceId).build());
        return future;
    }
}
