/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.repository.app.impl;

import java.util.List;
import java.util.Objects;

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
import cn.topiam.employee.common.enums.app.AuthorizationType;
import cn.topiam.employee.common.repository.app.AppAccessPolicyRepositoryCustomized;
import cn.topiam.employee.common.repository.app.impl.mapper.AppAccessPolicyPoMapper;
import cn.topiam.employee.support.exception.TopIamException;

import lombok.AllArgsConstructor;

/**
 * AppPolicy Repository Customized
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/29 21:27
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
                	INNER JOIN app ON a.app_id = app.id_ AND app.is_deleted = '0'
                INNER JOIN
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
        } else {
            if (StringUtils.isEmpty(query.getAppId())) {
                throw new TopIamException("主体ID不能为空");
            }
        }

        if(StringUtils.isNotBlank(query.getAppId())){
            //应用ID
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
     * 用户是否允许访问应用
     *
     * @param appId  {@link Long}
     * @param userId {@link Long}
     * @return {@link Boolean}
     */
    @Override
    public Boolean hasAllowAccess(Long appId, Long userId) {
        //@formatter:off
        String builder = """
                SELECT
                	count(*) as count
                FROM
                    app LEFT JOIN
                	app_access_policy aap ON app.id_ = aap.app_id AND aap.is_deleted = '0'
                	WHERE app.id_ = '%s' AND app.is_deleted = '0' AND (app.authorization_type = '%s'
                	 OR (aap.subject_id IN (
                        SELECT
                            id_
                        FROM
                            user_group_member
                        WHERE user_id = '%3$s'
                         UNION ALL
                        SELECT
                        	id_
                        FROM
                        	organization_member
                        WHERE user_id = '%3$s')
                      OR aap.subject_id = '%3$s'))
                """.formatted(appId, AuthorizationType.ALL_ACCESS.getCode(), userId);
        //@formatter:on
        Integer count = jdbcTemplate.queryForObject(builder, Integer.class);
        return !Objects.isNull(count) && count > 0;
    }

    /**
     * JdbcTemplate
     */
    private final JdbcTemplate jdbcTemplate;
}
