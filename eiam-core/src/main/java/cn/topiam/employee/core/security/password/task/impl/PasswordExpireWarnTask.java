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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.client.elc.Queries;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.scheduling.annotation.Scheduled;

import com.google.common.collect.Maps;

import cn.topiam.employee.common.entity.account.UserElasticSearchEntity;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.common.enums.SmsType;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.core.message.mail.MailMsgEventPublish;
import cn.topiam.employee.core.message.sms.SmsMsgEventPublish;
import cn.topiam.employee.core.security.password.task.PasswordExpireTask;
import cn.topiam.employee.support.autoconfiguration.SupportProperties;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.trace.Trace;

import lombok.RequiredArgsConstructor;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import static cn.topiam.employee.core.message.MsgVariable.EXPIRE_DAYS;
import static cn.topiam.employee.core.setting.constant.PasswordPolicySettingConstants.*;

/**
 * 密码过期前提醒任务
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/17 22:22
 */
@RequiredArgsConstructor
public class PasswordExpireWarnTask implements PasswordExpireTask {
    private final Logger logger = LoggerFactory.getLogger(PasswordExpireWarnTask.class);

    /**
     * 提醒 每天凌晨扫描，发送邮件及短信提醒
     */
    @Lock(throwException = false)
    @Trace
    @Scheduled(cron = "0 0 0 * * ?")
    @Override
    public void execute() {
        long start = System.currentTimeMillis();
        logger.info("密码过期前提醒任务开始");
        //密码有效期天数
        int expireDays = getExpireDays();
        //密码过期前提醒天数
        int warnExpireDays = getWarnExpireDays();
        //1、根据提醒时间，分页查询即将要过期的密码
        // 查询已启用用户信息
        Query query = Queries.termQueryAsQuery("status", UserStatus.ENABLE.getCode());
        List<UserElasticSearchEntity> list = userRepository.getAllUserElasticSearchEntity(
            IndexCoordinates.of(supportProperties.getUser().getIndexPrefix()), query);
        logger.info("待提醒用户为:{}个", list.size());
        int reminderNumber = 0;
        //2、发送通知提醒
        for (UserElasticSearchEntity entity : list) {
            // 获取到密码期日期
            LocalDate expiredDate = entity.getLastUpdatePasswordTime().atOffset(ZoneOffset.MAX)
                .plusDays(expireDays).toLocalDate();
            LocalDate warnDate = expiredDate.minusDays(warnExpireDays);
            //如果当前日期等于密码到期日期前几天的日期
            if (LocalDate.now().equals(warnDate)) {
                reminderNumber++;
                long expiredDays = Duration.between(LocalDate.now().atTime(LocalTime.MIN),
                    expiredDate.atTime(LocalTime.MIN)).toDays();
                //如果邮箱不为空，发送邮件通知
                if (StringUtils.isNotBlank(entity.getEmail())) {
                    logger.info("开始提醒用户:{}", entity.getUsername());
                    HashMap<String, Object> map = Maps.newHashMap();
                    //查询两个时间的间距
                    map.put(EXPIRE_DAYS, expiredDays);
                    mailMsgEventPublish.publish(MailType.PASSWORD_SOON_EXPIRED_REMIND,
                        entity.getEmail(), map);
                    logger.info("结束提醒用户:{}", entity.getUsername());
                }
                // 短信通知
                if (StringUtils.isNotBlank(entity.getPhone())) {
                    logger.info("开始提醒用户:{}", entity.getUsername());
                    LinkedHashMap<String, String> map = Maps.newLinkedHashMap();
                    // 查询两个时间的间距
                    map.put(EXPIRE_DAYS, String.valueOf(expiredDays));
                    smsMsgEventPublish.publish(SmsType.PASSWORD_SOON_EXPIRED_REMIND,
                        entity.getPhone(), map);
                    logger.info("结束提醒用户:{}", entity.getUsername());
                }
            }
        }
        logger.info("密码过期前提醒任务结束, 提醒用户数量[{}], 耗时:[{}]s", reminderNumber,
            (System.currentTimeMillis() - start) / 1000);
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

    /**
     * SupportProperties
     */
    private final SupportProperties   supportProperties;
}
