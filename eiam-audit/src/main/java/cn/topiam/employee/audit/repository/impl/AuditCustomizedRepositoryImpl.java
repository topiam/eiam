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
package cn.topiam.employee.audit.repository.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.audit.repository.AuditCustomizedRepository;
import cn.topiam.employee.audit.repository.impl.mapper.AuditStatisticsResultMapper;
import cn.topiam.employee.audit.repository.impl.mapper.AuthnQuantityResultMapper;
import cn.topiam.employee.audit.repository.result.AuditStatisticsResult;
import cn.topiam.employee.audit.repository.result.AuthnQuantityResult;

import lombok.RequiredArgsConstructor;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/2 02:54
 */
@Repository
@RequiredArgsConstructor
public class AuditCustomizedRepositoryImpl implements AuditCustomizedRepository {

    /**
     * JdbcTemplate
     */
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<AuditStatisticsResult> authnHotProvider(EventType type, LocalDateTime startTime,
                                                        LocalDateTime endTime) {
        String sql = """
                        SELECT
                            actor_auth_type AS key_,
                            COUNT(*) AS count_
                        FROM
                            audit
                        WHERE
                            event_type = ?
                            AND event_time BETWEEN ?
                            AND ?
                        GROUP BY
                            actor_auth_type
                """;
        return jdbcTemplate.query(sql, new AuditStatisticsResultMapper(), type.getCode(), startTime,
            endTime);
    }

    @Override
    public List<AuthnQuantityResult> authnQuantity(EventType type, LocalDateTime startTime,
                                                   LocalDateTime endTime, String dateFormat) {
        String sql = """
                        SELECT
                            DATE_FORMAT( event_time, ? ) AS name_,
                            COUNT(*) AS count_,
                            event_status AS status_
                         FROM
                            audit
                         WHERE
                            event_type = ?
                            AND event_time BETWEEN ?
                            AND ?
                         GROUP BY
                            DATE_FORMAT( event_time, ? ),
                            event_status
                """;

        return jdbcTemplate.query(sql, new AuthnQuantityResultMapper(), dateFormat, type.getCode(),
            startTime, endTime, dateFormat);
    }

    @Override
    public List<AuditStatisticsResult> appVisitRank(EventType type, LocalDateTime startTime,
                                                    LocalDateTime endTime) {
        String sql = """
                        SELECT
                            JSON_EXTRACT( target_, '$[0].id' ) AS key_,
                            COUNT(*) AS count_
                         FROM
                            audit
                         WHERE
                            event_type = ?
                            AND event_time BETWEEN ?
                            AND ?
                         GROUP BY
                            JSON_EXTRACT(
                                target_,
                            '$[0].id'
                            )
                """;
        return jdbcTemplate.query(sql, new AuditStatisticsResultMapper(), type.getCode(), startTime,
            endTime);
    }

    @Override
    public List<AuditStatisticsResult> authnZone(EventType type, LocalDateTime startTime,
                                                 LocalDateTime endTime) {
        String sql = """
                        SELECT
                            JSON_EXTRACT( target_, '$.provinceCode' ) AS key_,
                            COUNT(*) AS count_
                         FROM
                            audit
                         WHERE
                            event_type = ?
                            AND event_time BETWEEN ?
                            AND ?
                         GROUP BY
                         	JSON_EXTRACT(
                         		target_,
                         	'$.provinceCode'
                         	)
                """;
        return jdbcTemplate.query(sql, new AuditStatisticsResultMapper(), type.getCode(), startTime,
            endTime);
    }
}
