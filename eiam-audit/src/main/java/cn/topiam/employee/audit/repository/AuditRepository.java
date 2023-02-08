/*
 * eiam-audit - Employee Identity and Access Management Program
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
package cn.topiam.employee.audit.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.audit.entity.AuditEntity;
import cn.topiam.employee.support.repository.LogicDeleteRepository;

/**
 * 行为审计repository
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/11 22:32
 */
@Repository
public interface AuditRepository extends LogicDeleteRepository<AuditEntity, Long>,
                                 QuerydslPredicateExecutor<AuditEntity> {

    /**
     * 统计指定时间范围内用户登录失败次数
     *
     * @param startTime {@link LocalDateTime}
     * @param endTime {@link LocalDateTime}
     * @param userId {@link Long}
     * @return {@link Integer}
     */
    @Query(value = "SELECT count(*) FROM `audit` WHERE event_time BETWEEN :startTime AND :endTime AND actor_id  = :userId AND event_type = 'eiam:event:account:user_login_fail'", nativeQuery = true)
    Integer countLoginFailByUserId(@Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime,
                                   @Param("userId") Long userId);
}
