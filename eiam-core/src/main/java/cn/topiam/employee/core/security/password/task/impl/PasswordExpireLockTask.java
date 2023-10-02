/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.security.password.task.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.core.security.password.task.PasswordExpireTask;
import cn.topiam.employee.support.trace.Trace;

import lombok.RequiredArgsConstructor;
import static cn.topiam.employee.core.setting.constant.PasswordPolicySettingConstants.PASSWORD_POLICY_DEFAULT_SETTINGS;
import static cn.topiam.employee.core.setting.constant.PasswordPolicySettingConstants.PASSWORD_POLICY_VALID_DAYS;

/**
 * 密码过期锁定任务
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/17 22:22
 */
@RequiredArgsConstructor
public class PasswordExpireLockTask implements PasswordExpireTask {

    private final Logger logger = LoggerFactory.getLogger(PasswordExpireLockTask.class);

    /**
     * 锁定密码过期用户
     */
    @Trace
    @Scheduled(cron = "0 0 0 * * ?")
    @Override
    public void execute() {
        logger.info("密码过期锁定用户任务开始");
        int expireDays = getExpireDays();
        //1、根据提醒时间，分页查询即将要过期的密码
        List<UserEntity> list = userRepository.findPasswordExpireUser(expireDays);
        Iterator<UserEntity> iterator = list.iterator();
        logger.info("密码过期待锁定用户数量为:{}个", list.size());
        while (!list.isEmpty()) {
            UserEntity entity = iterator.next();
            //获取到期日期
            LocalDateTime expiredDate = entity.getLastUpdatePasswordTime().atOffset(ZoneOffset.MAX)
                .plusDays(expireDays).toLocalDateTime();
            if (LocalDateTime.now().isBefore(expiredDate)) {
                entity.setStatus(UserStatus.PASSWORD_EXPIRED_LOCKED);
                userRepository.save(entity);
                logger.info("锁定密码过期用户:{}", entity.getUsername());
                iterator.remove();
            }
            iterator = list.iterator();
        }
        logger.info("密码过期锁定用户任务结束");
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
    private final SettingRepository settingRepository;

    /**
     * UserRepository
     */
    private final UserRepository    userRepository;

}
