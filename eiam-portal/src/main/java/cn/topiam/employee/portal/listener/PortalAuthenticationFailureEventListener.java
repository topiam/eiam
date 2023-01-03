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

import java.time.LocalDateTime;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.util.ObjectUtils;

import cn.topiam.employee.audit.entity.Actor;
import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.enums.EventType;
import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.audit.repository.AuditRepository;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.common.enums.UserType;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.core.context.SettingContextHelp;
import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import static cn.topiam.employee.core.context.SettingContextHelp.getLoginFailureDuration;
import static cn.topiam.employee.core.security.util.SecurityUtils.getFailureMessage;

/**
 * 认证失败
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/3 22:42
 */
public class PortalAuthenticationFailureEventListener implements
                                                      ApplicationListener<AbstractAuthenticationFailureEvent> {

    private final Logger logger = LoggerFactory
        .getLogger(PortalAuthenticationFailureEventListener.class);

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(@NonNull AbstractAuthenticationFailureEvent event) {
        //@formatter:off
        AuditEventPublish publish = ApplicationContextHelp.getBean(AuditEventPublish.class);
        String content = getFailureMessage(event);
        logger.error("认证失败 [{}]",content);
        String principal=null;
        if (event.getAuthentication().getPrincipal() instanceof String){
            principal = (String) event.getAuthentication().getPrincipal();
        }
        if (event.getAuthentication().getPrincipal() instanceof UserDetails || event.getAuthentication().getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails){
            principal = ((UserDetails) event.getAuthentication().getPrincipal()).getUsername();
        }
        if (StringUtils.isNotBlank(principal)){
            UserEntity user = getUserRepository().findByUsername(principal);
            if (ObjectUtils.isEmpty(user)) {
                // 手机号
                user = getUserRepository().findByPhone(principal);
                if (ObjectUtils.isEmpty(user)) {
                    // 邮箱
                    user = getUserRepository().findByEmail(principal);
                }
            }
            if (Objects.isNull(user)) {
                Actor actor = Actor.builder().type(UserType.USER).build();
                publish.publish(EventType.LOGIN_PORTAL,"账户不存在："+principal, actor, EventStatus.FAIL);
                return;
            }
            Actor actor = Actor.builder().id(user.getId().toString()).type(UserType.USER).build();
            publish.publish(EventType.LOGIN_PORTAL,content+"："+user.getUsername(), actor,EventStatus.FAIL);
            //更新登录失败计数
            updateLoginFailCount(user);
        }
        //@formatter:on
    }

    /**
     * 更新登录失败计数
     *
     * @param user {@link UserEntity}
     */
    private void updateLoginFailCount(UserEntity user) {
        //当前时间
        LocalDateTime nowTime = LocalDateTime.now();
        //根据当前时间减去登录失败持续时间
        LocalDateTime beforeTime = nowTime.minusMinutes(getLoginFailureDuration());
        Integer count = SettingContextHelp.getLoginFailureCount();
        UserRepository userRepository = getUserRepository();
        //统计用户登录失败次数
        Integer loginFailCount = getAuditRepository().countLoginFailByUserId(beforeTime, nowTime,
            user.getId());
        if (loginFailCount.equals(count)) {
            user.setStatus(UserStatus.LOCKED);
            userRepository.save(user);
        }
    }

    private UserRepository getUserRepository() {
        return ApplicationContextHelp.getBean(UserRepository.class);
    }

    private AuditRepository getAuditRepository() {
        return ApplicationContextHelp.getBean(AuditRepository.class);
    }

}
