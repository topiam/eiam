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
package cn.topiam.employee.audit.event;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.audit.entity.*;
import cn.topiam.employee.audit.repository.AuditRepository;

/**
 * 事件监听
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/12 22:49
 */
@Component
@Async
public class AuditEventListener implements ApplicationListener<AuditEvent> {

    private final Logger logger = LoggerFactory.getLogger(AuditEventListener.class);

    /**
     * onApplicationEvent
     *
     * @param auditEvent {@link AuditEvent}
     */
    @Override
    public void onApplicationEvent(@NonNull AuditEvent auditEvent) {
        Event event = auditEvent.getEvent();
        Actor actor = auditEvent.getActor();
        List<Target> target = auditEvent.getTargets();
        GeoLocation geoLocation = auditEvent.getGeoLocation();
        UserAgent userAgent = auditEvent.getUserAgent();
        //保存数据库
        AuditEntity entity = new AuditEntity();
        try {
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
            entity.setActorAuthType(actor.getAuthType());
            auditRepository.save(entity);
        } catch (Exception e) {
            logger.error("Audit record saving failed: {}", JSONObject.toJSONString(entity), e);
        }

    }

    /**
     * AuditRepository
     */
    private final AuditRepository auditRepository;

    public AuditEventListener(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

}
