/*
 * eiam-portal - Employee Identity and Access Management Program
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
package cn.topiam.employee.portal.listener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Lists;

import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.authentication.common.util.AuthenticationUtils;
import cn.topiam.employee.common.geo.GeoLocationService;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.context.ServletContextHelp;
import cn.topiam.employee.support.util.IpUtils;

import lombok.AllArgsConstructor;
import static cn.topiam.employee.audit.enums.EventType.LOGIN_PORTAL;

/**
 * 监听登录成功事件
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/9/3
 */
@AllArgsConstructor
public class PortalAuthenticationSuccessEventListener implements
                                                      ApplicationListener<AuthenticationSuccessEvent> {

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(@NonNull AuthenticationSuccessEvent event) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes();
        AuditEventPublish auditEventPublish = ApplicationContextHelp
            .getBean(AuditEventPublish.class);
        UserRepository userRepository = ApplicationContextHelp.getBean(UserRepository.class);
        Authentication authentication = event.getAuthentication();
        Object principal = event.getAuthentication().getPrincipal();
        //@formatter:off
        if (principal instanceof UserDetails) {
            //认证类型
            ((UserDetails) principal).setAuthType(AuthenticationUtils.geAuthType(authentication));
            //区域
            ((UserDetails) principal).setGeoLocation(geoLocationService.getGeoLocation(IpUtils.getIpAddr(ServletContextHelp.getRequest())));
            //登录时间
            ((UserDetails) principal).setLoginTime(getDateTimeOfTimestamp(event.getTimestamp()));
            //认证次数+1
            userRepository.updateAuthSucceedInfo(((UserDetails) principal).getId()
                    ,((UserDetails) principal).getGeoLocation().getIp(),((UserDetails) principal).getLoginTime());
            // 审计事件
            List<Target> targets= Lists.newArrayList(Target.builder().type(TargetType.PORTAL).build());
            auditEventPublish.publish(LOGIN_PORTAL, event.getAuthentication(), EventStatus.SUCCESS,targets);
            //@formatter:on
        }
    }

    private static LocalDateTime getDateTimeOfTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    private final GeoLocationService geoLocationService;
}
