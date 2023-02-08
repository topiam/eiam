/*
 * eiam-audit - Employee Identity and Access Management Program
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
package cn.topiam.employee.audit.event;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.springframework.context.ApplicationListener;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import cn.topiam.employee.audit.entity.*;
import cn.topiam.employee.audit.repository.AuditRepository;
import cn.topiam.employee.core.configuration.EiamSupportProperties;
import static cn.topiam.employee.common.constants.AuditConstants.getAuditIndexPrefix;
import static cn.topiam.employee.support.constant.EiamConstants.DEFAULT_DATE_FORMATTER_PATTERN;

/**
 * 事件监听
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/12 22:49
 */
@Component
public class AuditEventListener implements ApplicationListener<AuditEvent> {

    /**
     * onApplicationEvent
     *
     * @param auditEvent {@link AuditEvent}
     */
    @Override
    public void onApplicationEvent(@NonNull AuditEvent auditEvent) {
        Event event = auditEvent.getEvent();
        Actor actor = auditEvent.getActor();
        List<Target> target = auditEvent.getTarget();
        GeoLocation geoLocation = auditEvent.getGeoLocationModal();
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
            auditRepository.save(entity);
        } catch (Exception ignored) {
        } finally {
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
                audit.setTimestamp(
                    entity.getCreateTime().atZone(ZoneId.systemDefault()).toInstant());
                String auditIndex = getAuditIndexPrefix(eiamSupportProperties.getDemo().isOpen())
                                    + LocalDate.now().format(DateTimeFormatter
                                        .ofPattern(DEFAULT_DATE_FORMATTER_PATTERN));
                elasticsearchOperations.save(audit, IndexCoordinates.of(auditIndex));
            }
        }

    }

    /**
     * EiamSupportProperties
     */
    private final EiamSupportProperties   eiamSupportProperties;

    /**
     * AuditRepository
     */
    private final AuditRepository         auditRepository;

    /**
     * ElasticsearchOperations
     */
    private final ElasticsearchOperations elasticsearchOperations;

    public AuditEventListener(EiamSupportProperties eiamSupportProperties,
                              AuditRepository auditRepository,
                              ElasticsearchOperations elasticsearchOperations) {
        this.eiamSupportProperties = eiamSupportProperties;
        this.auditRepository = auditRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

}
