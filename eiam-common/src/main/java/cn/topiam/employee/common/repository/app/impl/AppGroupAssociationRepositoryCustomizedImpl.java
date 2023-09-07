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

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.account.query.UserGroupMemberListQuery;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.app.query.AppGroupAssociationListQuery;
import cn.topiam.employee.common.repository.app.AppGroupAssociationRepositoryCustomized;
import cn.topiam.employee.common.repository.app.impl.mapper.AppEntityMapper;

import lombok.AllArgsConstructor;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/9/7 21:27
 */
@Repository
@AllArgsConstructor
public class AppGroupAssociationRepositoryCustomizedImpl implements
                                                         AppGroupAssociationRepositoryCustomized {

    /**
     * 获取应用组应用列表
     *
     * @param query    {@link UserGroupMemberListQuery}
     * @param pageable {@link Pageable}
     * @return {@link Page}
     */
    @SuppressWarnings("DuplicatedCode")
    @Override
    public Page<AppEntity> getAppGroupAssociationList(AppGroupAssociationListQuery query,
                                                      Pageable pageable) {
        //@formatter:off
        StringBuilder builder = new StringBuilder("""
                SELECT
                  app.*
                FROM
                	app app LEFT JOIN app_group_association ass ON app.id_ = ass.app_id AND app.is_deleted = 0 AND ass.is_deleted = 0
                WHERE ass.group_id = '%s'
                """.formatted(query.getId()));
        //应用名称
        if (StringUtils.isNoneBlank(query.getAppName())) {
            builder.append(" AND app.name_ like '%").append(query.getAppName()).append("%'");
        }
        builder.append(" ORDER BY `app`.create_time DESC");
        //@formatter:on
        String sql = builder.toString();
        List<AppEntity> list = jdbcTemplate.query(
            builder.append(" LIMIT ").append(pageable.getPageNumber() * pageable.getPageSize())
                .append(",").append(pageable.getPageSize()).toString(),
            new AppEntityMapper());
        //@formatter:off
        String countSql = "SELECT count(*) FROM (" + sql + ") app_";
        //@formatter:on
        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class);
        return new PageImpl<>(list, pageable, count);
    }

    private final JdbcTemplate jdbcTemplate;
}
