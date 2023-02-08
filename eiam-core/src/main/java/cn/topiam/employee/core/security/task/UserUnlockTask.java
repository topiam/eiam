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
package cn.topiam.employee.core.security.task;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

import cn.topiam.employee.common.entity.account.QUserEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.support.trace.Trace;

import lombok.RequiredArgsConstructor;
import static cn.topiam.employee.core.context.SettingContextHelp.getAutoUnlockTime;

/**
 * 用户锁定自动解锁任务
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/17 21:22
 */
@RequiredArgsConstructor
public class UserUnlockTask {

    private final Logger logger = LoggerFactory.getLogger(UserUnlockTask.class);

    /**
     * 每分钟扫描已锁定的用户解锁
     */
    @Trace
    @Scheduled(cron = "0 */1 * * * ?")
    public void execute() {
        logger.info("用户自动解锁任务开始");
        QUserEntity qUserEntity = QUserEntity.userEntity;
        Predicate predicate = ExpressionUtils.and(qUserEntity.isNotNull(),
            qUserEntity.isDeleted.eq(Boolean.FALSE));
        //查询条件
        //@formatter:off
        predicate = ExpressionUtils.and(predicate, qUserEntity.status.eq(UserStatus.LOCKED));
        List<UserEntity> list = (List<UserEntity>) userRepository.findAll(predicate);
        logger.info("待解锁用户数量: [{}] ",list.size());
        for (UserEntity entity:list) {
            logger.info("开始解锁用户:{}",entity.getUsername());
            LocalDateTime updateTime = entity.getUpdateTime();
            Integer unlockTime = getAutoUnlockTime();
            if (updateTime.plusMinutes(unlockTime).isBefore(LocalDateTime.now())){
                entity.setStatus(UserStatus.ENABLE);
                userRepository.save(entity);
                logger.info("成功解锁用户:{}",entity.getUsername());
            }
        }
        logger.info("用户自动解锁任务结束");
    }


    /**
     * UserRepository
     *
     */
    private final UserRepository userRepository;
}
