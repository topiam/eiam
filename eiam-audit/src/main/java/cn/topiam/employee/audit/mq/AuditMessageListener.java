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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;

import cn.topiam.employee.audit.entity.*;
import cn.topiam.employee.audit.event.AuditEvent;
import cn.topiam.employee.audit.repository.AuditElasticSearchRepository;
import cn.topiam.employee.audit.repository.AuditRepository;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.trace.TraceUtils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.audit.mq.AuditMessagePublisher.AUDIT_TOPIC;
import static cn.topiam.employee.support.trace.TraceAspect.TRACE_ID;

/**
 * 审计消息监听器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/5/30 23:12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditMessageListener {

    /**
     * 接收用户消息
     *
     * @param message {@link Message}
     * @param channel {@link Channel}
     */
    @SneakyThrows
    @RabbitListener(queues = AUDIT_TOPIC, ackMode = "MANUAL")
    @RabbitHandler()
    public String onMessage(Message message, Channel channel, @Payload AuditEvent auditEvent,
                            @Headers Map<String, Object> headers) throws TopIamException {
        try {
            //设置TraceId
            TraceUtils.put(String.valueOf(headers.get(TRACE_ID)));
            log.info("接收审计事件入参: [{}]", message);
            Event event = auditEvent.getEvent();
            Actor actor = auditEvent.getActor();
            List<Target> target = auditEvent.getTargets();
            GeoLocation geoLocation = auditEvent.getGeoLocation();
            UserAgent userAgent = auditEvent.getUserAgent();
            //保存数据库
            AuditEntity entity = new AuditEntity();
            Optional<AuditEntity> optional = auditRepository
                .findByRequestId(auditEvent.getRequestId());
            if (optional.isEmpty()) {
                entity.setRequestId(auditEvent.getRequestId());
                entity.setSessionId(auditEvent.getSessionId());
                //事件
                entity.setEventType(event.getType());
                entity.setEventContent(event.getContent());
                entity.setEventParam(event.getParam());
                entity.setEventStatus(event.getStatus());
                entity.setEventResult(event.getResult());
                entity.setEventTime(event.getTime());
                //操作目标
                entity.setTargets(target);
                entity.setGeoLocation(geoLocation);
                entity.setUserAgent(userAgent);
                entity.setActorId(actor.getId());
                entity.setActorType(actor.getType());
                auditRepository.save(entity);
            } else {
                entity = optional.get();
            }

            if (!Objects.isNull(entity.getId())) {
                //保存 Elasticsearch
                AuditElasticSearchEntity audit = AuditElasticSearchEntity.builder().build();
                audit.setRequestId(auditEvent.getRequestId());
                audit.setSessionId(auditEvent.getSessionId());
                audit.setId(entity.getId().toString());
                audit.setEvent(event);
                audit.setTargets(target);
                audit.setGeoLocation(geoLocation);
                audit.setUserAgent(userAgent);
                audit.setActor(actor);
                auditElasticSearchRepository.save(audit);
            }
            log.info("处理审计事件成功:[{}]", message.getMessageProperties().getDeliveryTag());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return "处理审计事件成功";
        } catch (Exception e) {
            log.error("处理审计事件出现异常: MessageProperties: [{}], 审计内容:[{}]",
                message.getMessageProperties(), JSONObject.toJSONString(auditEvent), e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            return "处理审计事件失败";
        }
    }

    /**
     * AuditRepository
     */
    private final AuditRepository              auditRepository;

    /**
     * AuditElasticSearchRepository
     */
    private final AuditElasticSearchRepository auditElasticSearchRepository;
}
