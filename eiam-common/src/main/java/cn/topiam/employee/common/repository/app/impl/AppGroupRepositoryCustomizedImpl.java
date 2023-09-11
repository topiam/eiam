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

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.account.query.UserGroupMemberListQuery;
import cn.topiam.employee.common.entity.app.po.AppGroupPO;
import cn.topiam.employee.common.entity.app.query.AppGroupQuery;
import cn.topiam.employee.common.repository.app.AppGroupRepositoryCustomized;
import cn.topiam.employee.common.repository.app.impl.mapper.AppGroupPoMapper;

import lombok.AllArgsConstructor;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/9/8 19:20
 */
@Repository
@AllArgsConstructor
public class AppGroupRepositoryCustomizedImpl implements AppGroupRepositoryCustomized {

    /**
     * 获取应用组应用列表
     *
     * @param query    {@link UserGroupMemberListQuery}
     * @param pageable {@link Pageable}
     * @return {@link Page}
     */
    @SuppressWarnings("DuplicatedCode")
    @Override
    public Page<AppGroupPO> getAppGroupList(AppGroupQuery query, Pageable pageable) {
        //@formatter:off
        StringBuilder builder = new StringBuilder("SELECT `group`.id_, `group`.name_, `group`.code_, `group`.type_, `group`.create_time, `group`.remark_, IFNULL( ass.app_count, 0) AS app_count FROM app_group `group` LEFT JOIN(SELECT aga.group_id, COUNT(*) AS `app_count` FROM app_group_association aga INNER JOIN app ON aga.app_id = app.id_ AND app.is_deleted = 0 GROUP BY aga.group_id ) ass ON `group`.id_ = ass.group_id WHERE is_deleted = '0'");
        //分组名称
        if (StringUtils.isNoneBlank(query.getName())) {
            builder.append(" AND `group`.name_ like '%").append(query.getName()).append("%'");
        }
        //分组编码
        if (StringUtils.isNoneBlank(query.getCode())) {
            builder.append(" AND `group`.code_ like '%").append(query.getCode()).append("%'");
        }
        //分组类型
        if (ObjectUtils.isNotEmpty(query.getType())) {
            builder.append(" AND `group`.type_ like '%").append(query.getType().getCode()).append("%'");
        }
        builder.append(" ORDER BY `group`.create_time DESC");
        //@formatter:on
        String sql = builder.toString();
        List<AppGroupPO> list = jdbcTemplate.query(
            builder.append(" LIMIT ").append(pageable.getPageNumber() * pageable.getPageSize())
                .append(",").append(pageable.getPageSize()).toString(),
            new AppGroupPoMapper());
        //@formatter:off
        String countSql = "SELECT count(*) FROM (" + sql + ") app_";
        //@formatter:on
        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class);
        return new PageImpl<>(list, pageable, count);
    }

    private final JdbcTemplate jdbcTemplate;
}
