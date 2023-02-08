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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import com.google.common.collect.Lists;

import cn.topiam.employee.common.entity.app.po.AppPermissionPolicyPO;
import cn.topiam.employee.common.entity.app.query.AppPolicyQuery;
import cn.topiam.employee.common.repository.app.AppPermissionPolicyRepositoryCustomized;
import cn.topiam.employee.common.repository.app.impl.mapper.AppPermissionPolicyPoMapper;

import lombok.RequiredArgsConstructor;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/4 22:46
 */
@Repository
@RequiredArgsConstructor
public class AppPermissionPolicyRepositoryCustomizedImpl implements
                                                         AppPermissionPolicyRepositoryCustomized {

    private String leftJoin(String table, String condition) {
        return " LEFT JOIN " + table + " ON " + condition + " AND " + table + ".is_deleted = '0' ";
    }

    @Override
    public Page<AppPermissionPolicyPO> findPage(AppPolicyQuery query, Pageable pageable) {
        //查询条件
        //@formatter:off
        // 所属应用
        StringBuilder where = new StringBuilder("WHERE policy.is_deleted = '0' AND policy.app_id = '").append(query.getAppId()).append("' ");
        // 主体类型
        where.append(" AND policy.subject_type = '").append(query.getSubjectType().getCode()).append("' ");
        // 客体类型
        where.append(" AND policy.object_type = '").append(query.getObjectType().getCode()).append("' ");
        // 主体id
        if (!ObjectUtils.isEmpty(query.getSubjectId())) {
            where.append("policy.subject_id = '").append(query.getSubjectId()).append("' ");
        }
        // 客体id
        if (!ObjectUtils.isEmpty(query.getObjectId())) {
            where.append("policy.object_id = '").append(query.getObjectId()).append("' ");
        }
        // 授权效果
        if (!ObjectUtils.isEmpty(query.getEffect())) {
            where.append("policy.effect = '").append(query.getEffect().getCode()).append("' ");
        }

        List<String> fields = Lists.newArrayList("policy.subject_id", "policy.object_id", "policy.subject_type", "policy.object_type", "policy.id", "policy.effect");
        String subjectJoin;
        String objectJoin = null;
        switch (query.getSubjectType()) {
            case USER -> {
                subjectJoin = leftJoin("app_account account", "policy.subject_id = account.id");
                fields.add("account.account as subject_name");
            }
            case USER_GROUP -> {
                subjectJoin = leftJoin("user_group group", "policy.subject_id = group.id");
                fields.add("group.name as subject_name");
            }
            case ORGANIZATION -> {
                subjectJoin = leftJoin("organization org", "policy.subject_id = org.id");
                fields.add("org.name as subject_name");
            }
            case ROLE -> {
                subjectJoin = leftJoin("app_permission_role role", "policy.subject_id = role.id");
                fields.add("role.name as subject_name");
            }
            default -> throw new RuntimeException("暂不支持");
        }
        switch (query.getObjectType()) {
            case PERMISSION -> {
                objectJoin = leftJoin("app_permission_action action", "policy.subject_id = action.id");
                fields.add("action.name as object_name");
            }
            case ROLE -> {
                objectJoin = leftJoin("app_permission_role role2", "policy.subject_id = role2.id");
                fields.add("role2.name as object_name");
            }
            case RESOURCE -> {
                objectJoin = leftJoin("app_permission_resource resource", "policy.subject_id = resource.id");
                fields.add("resource.name as object_name");
            }
        }
        StringBuilder selectSql = new StringBuilder("SELECT ").append(String.join(", ", fields))
                .append(" FROM app_permission_policy policy ").append(subjectJoin).append(objectJoin);

        // @formatter:off
        List<AppPermissionPolicyPO> list = jdbcTemplate
                .query(
                        selectSql.append(" LIMIT ").append(pageable.getPageNumber() * pageable.getPageSize())
                                .append(",").append(pageable.getPageSize()).toString(),
                        new AppPermissionPolicyPoMapper());
        //@formatter:off
        String countSql = "SELECT count(*) FROM (" + selectSql + ") app_policy_";
        //@formatter:on
        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class);
        return new PageImpl<>(list, pageable, count);
    }

    /**
     * JdbcTemplate
     */
    private final JdbcTemplate jdbcTemplate;
}
