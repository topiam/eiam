/*
 * eiam-common - Employee Identity and Access Management Program
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
package cn.topiam.employee.common.repository.app.impl;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.account.query.UserListQuery;
import cn.topiam.employee.common.entity.app.po.AppAccessPolicyPO;
import cn.topiam.employee.common.entity.app.query.AppAccessPolicyQuery;
import cn.topiam.employee.common.repository.app.AppAccessPolicyRepositoryCustomized;
import cn.topiam.employee.common.repository.app.impl.mapper.AppAccessPolicyPoMapper;

import lombok.AllArgsConstructor;

/**
 * AppPolicy Repository Customized
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/29 20:27
 */
@Repository
@AllArgsConstructor
public class AppAccessPolicyRepositoryCustomizedImpl implements
                                                     AppAccessPolicyRepositoryCustomized {

    /**
     * 获取应用授权策略列表
     *
     * @param pageable {@link  Pageable}
     * @param query    {@link  UserListQuery}
     * @return {@link Page}
     */
    @Override
    public Page<AppAccessPolicyPO> getAppPolicyList(AppAccessPolicyQuery query, Pageable pageable) {
        //@formatter:off
        StringBuilder builder = new StringBuilder("""
                SELECT
                	a.id_,
                	a.app_id,
                	a.subject_id,
                	a.subject_type,
                	a.create_time,
                	subject.name_,
                	app.name_ AS app_name,
                	app.type_ AS app_type,
                	app.template_ AS app_template,
                	app.protocol_ AS app_protocol
                FROM
                	app_access_policy a
                	LEFT JOIN app ON a.app_id = app.id_ AND app.is_deleted = '0'
                LEFT JOIN
                """);
        builder.append("""
                ( SELECT
                	id_,
                	name_,
                	is_deleted
                FROM
                	user_group UNION ALL
                SELECT
                	id_,
                	name_,
                	is_deleted
                FROM
                	organization UNION ALL
                SELECT
                	id_,
                	username_ AS name_,
                	is_deleted
                FROM
                	`user`
                	) `subject` ON a.subject_id = `subject`.id_ AND `subject`.is_deleted = '0'
                WHERE
                	a.is_deleted = '0'
                """);
        if (ObjectUtils.isNotEmpty(query.getSubjectType())) {
            builder.append(" AND a.subject_type = '").append(query.getSubjectType().getCode()).append("'");
        }
        //主体名称
        if (StringUtils.isNotEmpty(query.getSubjectName())) {
            builder.append(" AND subject.name_ like '%").append(query.getSubjectName()).append("%'");
        }
        //主体ID
        if (StringUtils.isNotBlank(query.getSubjectId())) {
            builder.append(" AND a.subject_id = '").append(query.getSubjectId()).append("'");
        }
        //应用ID
        if (StringUtils.isNotEmpty(query.getAppId())) {
            builder.append(" AND a.app_id = '").append(query.getAppId()).append("'");
        }
        //应用名称
        if (StringUtils.isNotBlank(query.getAppName())) {
            builder.append(" AND app.name_ like '%").append(query.getAppName()).append("'");
        }
        //@formatter:on
        String sql = builder.toString();
        List<AppAccessPolicyPO> list = jdbcTemplate.query(
            builder.append(" LIMIT ").append(pageable.getPageNumber() * pageable.getPageSize())
                .append(",").append(pageable.getPageSize()).toString(),
            new AppAccessPolicyPoMapper());
        //@formatter:off
        String countSql = "SELECT count(*) FROM (" + sql + ") app_access_policy_";
        //@formatter:on
        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class);
        return new PageImpl<>(list, pageable, count);
    }

    /**
     * JdbcTemplate
     */
    private final JdbcTemplate jdbcTemplate;
}
