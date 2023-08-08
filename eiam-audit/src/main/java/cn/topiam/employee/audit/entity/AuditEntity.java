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
package cn.topiam.employee.audit.entity;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.support.repository.domain.LogicDeleteEntity;
import cn.topiam.employee.support.security.userdetails.UserType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_WHERE;

/**
 * 审计
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/8/1 21:41
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "audit")
@SQLDelete(sql = "update audit set " + SOFT_DELETE_SET + " where id_ = ?")
@Where(clause = SOFT_DELETE_WHERE)
public class AuditEntity extends LogicDeleteEntity<Long> {

    @Serial
    private static final long serialVersionUID = -3119319193111206582L;

    /**
     * Request Id
     */
    @Column(name = "request_id")
    private String            requestId;

    /**
     * Session Id
     */
    @Column(name = "session_id")
    private String            sessionId;

    /**
     * 操作目标
     */
    @Type(JsonType.class)
    @Column(name = "target_")
    private List<Target>      targets;

    /**
     * UserAgent
     */
    @Type(JsonType.class)
    @Column(name = "user_agent")
    private UserAgent         userAgent;

    /**
     * 地理位置
     */
    @Type(JsonType.class)
    @Column(name = "geo_location")
    private GeoLocation       geoLocation;

    /**
     * 审计事件类型
     */
    @Column(name = "event_type")
    private EventType         eventType;

    /**
     * 参数
     */
    @Column(name = "event_param")
    private String            eventParam;

    /**
     * 事件内容
     */
    @Column(name = "event_content")
    private String            eventContent;

    /**
     * 事件结果
     */
    @Column(name = "event_result")
    private String            eventResult;

    /**
     * 事件时间
     */
    @Column(name = "event_time")
    private LocalDateTime     eventTime;

    /**
     * 事件状态
     */
    @Column(name = "event_status")
    private EventStatus       eventStatus;

    /**
     * 操作者ID
     */
    @Column(name = "actor_id")
    private String            actorId;

    /**
     * 操作人类型
     */
    @Column(name = "actor_type")
    private UserType          actorType;

    /**
     * 身份验证类型
     */
    @Column(name = "actor_auth_type")
    private String            actorAuthType;
}
