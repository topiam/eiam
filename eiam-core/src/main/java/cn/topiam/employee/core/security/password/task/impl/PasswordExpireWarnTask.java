/*
 * eiam-core - Employee Identity and Access Management
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
package cn.topiam.employee.core.security.password.task.impl;

import java.time.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Scheduled;

import com.google.common.collect.Maps;

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.common.enums.SmsType;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.core.message.mail.MailMsgEventPublish;
import cn.topiam.employee.core.message.sms.SmsMsgEventPublish;
import cn.topiam.employee.core.security.password.task.PasswordExpireTask;
import cn.topiam.employee.support.trace.Trace;

import lombok.RequiredArgsConstructor;
import static cn.topiam.employee.core.message.MsgVariable.EXPIRE_DAYS;
import static cn.topiam.employee.core.setting.PasswordPolicySettingConstants.*;

/**
 * 密码过期前提醒任务
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/4/17 22:22
 */
@RequiredArgsConstructor
public class PasswordExpireWarnTask implements PasswordExpireTask {
    private final Logger logger = LoggerFactory.getLogger(PasswordExpireWarnTask.class);

    /**
     * 提醒 每天凌晨扫描，发送邮件及短信提醒
     */
    @Trace
    @Scheduled(cron = "0 0 0 * * ?")
    @Override
    public void execute() {
        logger.info("密码过期前提醒任务开始");
        int expireDays = getExpireDays();
        int expireWarnDays = getWarnExpireDays();
        //1、根据提醒时间，分页查询即将要过期的密码
        List<UserEntity> list = userRepository
            .findAll(Example.of(new UserEntity().setStatus(UserStatus.ENABLED)));
        //2、发送通知提醒
        for (UserEntity user : list) {
            // 获取到期日期
            LocalDate expiredDate = user.getLastUpdatePasswordTime().atOffset(ZoneOffset.MAX)
                .plusDays(expireDays).toLocalDate();
            // 到了提醒时间
            if (user.getLastUpdatePasswordTime().plusDays(expireWarnDays)
                .isBefore(LocalDateTime.now())) {
                //如果邮箱不为空，发送邮件通知
                if (StringUtils.isNotBlank(user.getEmail())) {
                    logger.info("邮件通知密码过期前提醒用户:{}", user.getUsername());
                    HashMap<String, Object> map = Maps.newHashMap();
                    //查询两个时间的间距
                    map.put(EXPIRE_DAYS, Duration.between(LocalDate.now(), expiredDate).toDays());
                    mailMsgEventPublish.publish(MailType.PASSWORD_SOON_EXPIRED_REMIND,
                        user.getEmail(), map);
                }
                // 短信通知
                if (StringUtils.isNotBlank(user.getPhone())) {
                    logger.info("短信通知密码过期前提醒用户:{}", user.getUsername());
                    LinkedHashMap<String, String> map = Maps.newLinkedHashMap();
                    // 查询两个时间的间距
                    map.put(EXPIRE_DAYS,
                        String.valueOf(Duration.between(LocalDate.now(), expiredDate).toDays()));
                    smsMsgEventPublish.publish(SmsType.PASSWORD_SOON_EXPIRED_REMIND,
                        user.getPhone(), map);
                }
            }

        }
        logger.info("密码过期前提醒任务结束");
    }

    /**
     * 获取密码过期前警告日
     *
     * @return {@link  Integer}
     */
    private Integer getWarnExpireDays() {
        SettingEntity beforeExpireDaysSetting = settingRepository
            .findByName(PASSWORD_POLICY_VALID_WARN_BEFORE_DAYS);
        String beforeExpireDays = Objects.isNull(beforeExpireDaysSetting)
            ? PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_VALID_WARN_BEFORE_DAYS)
            : beforeExpireDaysSetting.getValue();
        return Integer.valueOf(beforeExpireDays);
    }

    /**
     * 获取密码过期日
     *
     * @return {@link  Integer}
     */
    private Integer getExpireDays() {
        SettingEntity expireDaysSetting = settingRepository.findByName(PASSWORD_POLICY_VALID_DAYS);
        String expireDays = Objects.isNull(expireDaysSetting)
            ? PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_VALID_DAYS)
            : expireDaysSetting.getValue();
        return Integer.parseInt(expireDays);
    }

    /**
     * 设置
     */
    private final SettingRepository   settingRepository;

    /**
     * 用户Repository
     */
    private final UserRepository      userRepository;

    /**
     * 邮件消息发布
     */
    private final MailMsgEventPublish mailMsgEventPublish;

    /**
     * 短信消息发送
     */
    private final SmsMsgEventPublish  smsMsgEventPublish;
}
