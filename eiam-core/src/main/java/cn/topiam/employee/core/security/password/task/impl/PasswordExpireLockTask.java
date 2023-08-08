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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.scheduling.annotation.Scheduled;

import com.google.common.collect.Lists;

import cn.topiam.employee.common.entity.account.UserElasticSearchEntity;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.core.mq.UserMessagePublisher;
import cn.topiam.employee.core.mq.UserMessageTag;
import cn.topiam.employee.core.security.password.task.PasswordExpireTask;
import cn.topiam.employee.support.autoconfiguration.SupportProperties;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.trace.Trace;

import lombok.RequiredArgsConstructor;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
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
    @Lock(throwException = false)
    @Trace
    @Scheduled(cron = "0 0 0 * * ?")
    @Override
    public void execute() {
        long start = System.currentTimeMillis();
        logger.info("密码过期锁定用户任务开始");
        int expireDays = getExpireDays();
        // 查询非密码过期锁定和过期锁定用户信息
        Query query = QueryBuilders.terms(builder -> {
            builder.terms(new TermsQueryField.Builder().value(
                Lists.newArrayList(FieldValue.of(UserStatus.PASSWORD_EXPIRED_LOCKED.getCode()),
                    FieldValue.of(UserStatus.EXPIRED_LOCKED.getCode())))
                .build());
            builder.field("status");
            return builder;
        });
        List<UserElasticSearchEntity> userElasticSearchList = userRepository
            .getAllUserElasticSearchEntity(
                IndexCoordinates.of(supportProperties.getUser().getIndexPrefix()), query);
        List<String> lockedUserIdList = new ArrayList<>();
        Iterator<UserElasticSearchEntity> iterator = userElasticSearchList.iterator();
        logger.info("密码过期待锁定用户数量为:{}个", userElasticSearchList.size());
        while (!userElasticSearchList.isEmpty()) {
            UserElasticSearchEntity entity = iterator.next();
            //获取到期日期
            LocalDateTime expiredDate = entity.getLastUpdatePasswordTime().atOffset(ZoneOffset.MAX)
                .plusDays(expireDays).toLocalDateTime();
            if (LocalDateTime.now().isBefore(expiredDate)) {
                lockedUserIdList.add(entity.getId());
                userRepository.updateUserStatus(Long.valueOf(entity.getId()),
                    UserStatus.PASSWORD_EXPIRED_LOCKED);
                logger.info("锁定密码过期用户：{}", entity.getUsername());
                iterator.remove();
            }
            iterator = userElasticSearchList.iterator();
        }
        // 推送es用户消息
        userMessagePublisher.sendUserChangeMessage(UserMessageTag.SAVE,
            String.join(",", lockedUserIdList));
        logger.info("密码过期锁定用户任务结束: 冻结用户数量[{}], 耗时:[{}]s", lockedUserIdList.size(),
            (System.currentTimeMillis() - start) / 1000);
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
    private final SettingRepository    settingRepository;

    /**
     * UserRepository
     */
    private final UserRepository       userRepository;

    /**
     * SupportProperties
     */
    private final SupportProperties    supportProperties;

    /**
     * UserMessagePublisher
     */
    private final UserMessagePublisher userMessagePublisher;

}
