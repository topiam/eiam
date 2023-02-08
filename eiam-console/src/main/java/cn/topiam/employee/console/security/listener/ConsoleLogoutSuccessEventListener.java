/*
 * eiam-console - Employee Identity and Access Management Program
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
package cn.topiam.employee.console.security.listener;

import java.util.List;

import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.event.LogoutSuccessEvent;

import com.google.common.collect.Lists;

import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import static cn.topiam.employee.audit.enums.EventType.LOGOUT_CONSOLE;

/**
 * 退出成功
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/3 22:42
 */
public class ConsoleLogoutSuccessEventListener implements ApplicationListener<LogoutSuccessEvent> {

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(@NonNull LogoutSuccessEvent event) {
        AuditEventPublish auditEventPublish = ApplicationContextHelp
            .getBean(AuditEventPublish.class);
        // 审计事件
        //@formatter:off
        List<Target> targets= Lists.newArrayList(Target.builder().type(TargetType.CONSOLE).build());
        auditEventPublish.publish(LOGOUT_CONSOLE, event.getAuthentication(), EventStatus.SUCCESS,targets);
        //@formatter:on
    }
}
