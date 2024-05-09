/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.audit.entity.AuditEntity;
import cn.topiam.employee.audit.event.type.EventType;

/**
 * 行为审计repository
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2021/9/11 22:32
 */
@Repository
public interface AuditRepository extends JpaRepository<AuditEntity, String>,
                                 AuditCustomizedRepository, JpaSpecificationExecutor<AuditEntity> {

    @Query(value = "SELECT COUNT(*) FROM AuditEntity WHERE eventType = :type AND eventTime BETWEEN :startTime AND :endTime")
    Long countByTypeAndTime(@Param("type") EventType type,
                            @Param("startTime") LocalDateTime startTime,
                            @Param("endTime") LocalDateTime endTime);
}
