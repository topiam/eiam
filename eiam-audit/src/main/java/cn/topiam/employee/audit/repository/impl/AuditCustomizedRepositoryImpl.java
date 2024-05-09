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
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
 * Created by support@topiam.cn on 2022/10/2 02:54
 */
@Repository
@RequiredArgsConstructor
public class AuditCustomizedRepositoryImpl implements AuditCustomizedRepository {

    /**
     * NamedParameterJdbcTemplate
     */
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<AuditStatisticsResult> authnHotProvider(List<EventType> types,
                                                        LocalDateTime startTime,
                                                        LocalDateTime endTime) {
        String sql = """
                        SELECT
                            actor_auth_type AS key_,
                            COUNT(*) AS count_
                        FROM
                            eiam_audit
                        WHERE
                            event_type IN (:types)
                            AND event_time BETWEEN :startTime
                            AND :endTime
                        GROUP BY
                            actor_auth_type
                        ORDER BY count_
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("types",
            types.stream().map(EventType::getCode).collect(Collectors.toList()));
        params.addValue("startTime", startTime);
        params.addValue("endTime", endTime);
        return namedParameterJdbcTemplate.query(sql, params, new AuditStatisticsResultMapper());
    }

    @Override
    public List<AuthnQuantityResult> authnQuantity(List<EventType> types, LocalDateTime startTime,
                                                   LocalDateTime endTime, String dateFormat) {
        String sql = """
                        SELECT
                            DATE_FORMAT( event_time, :dateFormat ) AS name_,
                            COUNT(*) AS count_,
                            event_status AS status_
                         FROM
                            eiam_audit
                         WHERE
                            event_type IN (:types)
                            AND event_time BETWEEN :startTime
                            AND :endTime
                         GROUP BY
                            DATE_FORMAT( event_time, :dateFormat ),
                            event_status
                        ORDER BY name_
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("types",
            types.stream().map(EventType::getCode).collect(Collectors.toList()));
        params.addValue("startTime", startTime);
        params.addValue("endTime", endTime);
        params.addValue("dateFormat", dateFormat);
        return namedParameterJdbcTemplate.query(sql, params, new AuthnQuantityResultMapper());
    }

    @Override
    public List<AuditStatisticsResult> appVisitRank(EventType type, LocalDateTime startTime,
                                                    LocalDateTime endTime) {
        String sql = """
                        SELECT
                            JSON_EXTRACT( target_, '$[0].id' ) AS key_,
                            COUNT(*) AS count_
                         FROM
                            eiam_audit
                         WHERE
                            event_type = :type
                            AND event_time BETWEEN :startTime
                            AND :endTime
                         GROUP BY
                            JSON_EXTRACT(
                                target_,
                            '$[0].id'
                            )
                        ORDER BY count_
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("type", type.getCode());
        params.addValue("startTime", startTime);
        params.addValue("endTime", endTime);
        return namedParameterJdbcTemplate.query(sql, params, new AuditStatisticsResultMapper());
    }

    @Override
    public List<AuditStatisticsResult> authnZone(List<EventType> types, LocalDateTime startTime,
                                                 LocalDateTime endTime) {
        String sql = """
                        SELECT
                            JSON_EXTRACT( geo_location, '$.provinceCode' ) AS key_,
                            COUNT(*) AS count_
                         FROM
                            eiam_audit
                         WHERE
                            event_type IN (:types)
                            AND event_time BETWEEN :startTime
                            AND :endTime
                         GROUP BY
                         	JSON_EXTRACT(
                         	geo_location,
                         	'$.provinceCode'
                         	)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("types",
            types.stream().map(EventType::getCode).collect(Collectors.toList()));
        params.addValue("startTime", startTime);
        params.addValue("endTime", endTime);
        return namedParameterJdbcTemplate.query(sql, params, new AuditStatisticsResultMapper());
    }
}
