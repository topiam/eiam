/*
 * eiam-protocol-jwt - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.jwt.authentication;

import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;

import cn.topiam.employee.application.jwt.model.JwtProtocolConfig;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.protocol.jwt.exception.JwtAuthenticationException;
import static cn.topiam.employee.audit.event.type.EventType.APP_SSO;

/**
 * 认证失败监听
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/7/8 21:25
 */
public class JwtAuthenticationFailureEventListener implements
                                                   ApplicationListener<AbstractAuthenticationFailureEvent> {

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(@NonNull AbstractAuthenticationFailureEvent event) {
        if (event.getException() instanceof JwtAuthenticationException) {
            JwtRequestAuthenticationToken authentication = (JwtRequestAuthenticationToken) event
                .getAuthentication();
            JwtProtocolConfig config = authentication.getConfig();
            auditEventPublish.publish(APP_SSO, authentication, EventStatus.FAIL, Lists.newArrayList(
                Target.builder().id(config.getAppId()).type(TargetType.APPLICATION).build()));
        }
    }

    private final AuditEventPublish auditEventPublish;

    public JwtAuthenticationFailureEventListener(AuditEventPublish auditEventPublish) {
        Assert.notNull(auditEventPublish, "auditEventPublish must not be null ");
        this.auditEventPublish = auditEventPublish;
    }
}
