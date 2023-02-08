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

import java.io.Serial;
import java.util.List;

import org.springframework.context.ApplicationEvent;

import cn.topiam.employee.audit.entity.*;

import lombok.Getter;

/**
 * 审计事件
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/8/1 21:56
 */
@Getter
public class AuditEvent extends ApplicationEvent {
    @Serial
    private static final long  serialVersionUID = 3425943796938543659L;

    private final String       requestId;
    private final String       sessionId;
    private final Actor        actor;
    private final Event        event;
    private final List<Target> target;
    private final UserAgent    userAgent;
    private final GeoLocation  geoLocationModal;

    public AuditEvent(String requestId, String sessionId, Actor actor, Event event,
                      UserAgent userAgent, GeoLocation geoLocation, List<Target> targets) {
        super(requestId);
        this.requestId = requestId;
        this.sessionId = sessionId;
        this.actor = actor;
        this.event = event;
        this.target = targets;
        this.userAgent = userAgent;
        this.geoLocationModal = geoLocation;
    }
}
