/*
 * eiam-portal - Employee Identity and Access Management
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
package cn.topiam.employee.portal.authentication;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.util.ObjectUtils;

import cn.topiam.employee.audit.entity.Actor;
import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.audit.repository.AuditRepository;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.core.help.SettingHelp;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.util.PhoneNumberUtils;
import static cn.topiam.employee.core.help.SettingHelp.getLoginFailureDuration;
import static cn.topiam.employee.core.security.util.SecurityUtils.getFailureMessage;
import static cn.topiam.employee.support.security.userdetails.UserType.USER;
import static cn.topiam.employee.support.security.util.SecurityUtils.getPrincipal;

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
        logger.error("认证失败", event.getException());
        String principal = getPrincipal(event);
        if (StringUtils.isNotBlank(principal)) {
            UserEntity user = getUserRepository().findByUsername(principal);
            if (ObjectUtils.isEmpty(user)) {
                // 手机号
                if (PhoneNumberUtils.isPhoneValidate(principal)) {
                    user = getUserRepository().findByPhone(PhoneNumberUtils.getPhoneNumber(principal));
                }
                if (ObjectUtils.isEmpty(user)) {
                    // 邮箱
                    user = getUserRepository().findByEmail(principal);
                }
            }
            if (Objects.isNull(user)) {
                logger.error("账户不存在:[{}]", principal);
                return;
            }
            Actor actor = Actor.builder().id(user.getId().toString()).type(USER).build();
            publish.publish(EventType.LOGIN_PORTAL, content + "：" + user.getUsername(), actor, EventStatus.FAIL);
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
    /**
     * 更新登录失败计数
     *
     * @param user {@link UserEntity}
     */
    private void updateLoginFailCount(UserEntity user) {
        Integer count = SettingHelp.getLoginFailureCount();
        UserRepository userRepository = getUserRepository();
        //统计用户登录失败次数
        RAtomicLong rAtomicLong = getRedissonClient().getAtomicLong(String.valueOf(user.getId()));
        //如果存在该key
        if (rAtomicLong.isExists()) {
            long loginFailCount = rAtomicLong.incrementAndGet();
            if (loginFailCount > count) {
                user.setStatus(UserStatus.LOCKED);
                user.setLockExpiredTime(LocalDateTime.now());
                userRepository.save(user);
                rAtomicLong.deleteAsync();
            }
        }
        //如果不存在该key，并且用户未被锁定
        if (!rAtomicLong.isExists() && !user.getStatus().equals(UserStatus.LOCKED)) {
            rAtomicLong.incrementAndGet();
            Instant expireTime = Instant.now().plus(getLoginFailureDuration(), ChronoUnit.MINUTES);
            rAtomicLong.expire(expireTime);
        }
    }

    private UserRepository getUserRepository() {
        return ApplicationContextHelp.getBean(UserRepository.class);
    }

    private RedissonClient getRedissonClient() {
        return ApplicationContextHelp.getBean(RedissonClient.class);
    }

}
