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
package cn.topiam.employee.common.repository.account.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.account.po.UserPO;
import cn.topiam.employee.common.entity.account.query.UserGroupMemberListQuery;
import cn.topiam.employee.common.repository.account.UserGroupMemberRepositoryCustomized;
import cn.topiam.employee.common.repository.account.impl.mapper.UserPoMapper;

import lombok.AllArgsConstructor;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/13 21:27
 */
@Repository
@AllArgsConstructor
public class UserGroupMemberRepositoryCustomizedImpl implements
                                                     UserGroupMemberRepositoryCustomized {
    /**
     * 获取用户组成员列表
     *
     * @param query    {@link UserGroupMemberListQuery}
     * @param pageable {@link Pageable}
     * @return {@link Page}
     */
    @SuppressWarnings("DuplicatedCode")
    @Override
    public Page<UserPO> getUserGroupMemberList(UserGroupMemberListQuery query, Pageable pageable) {
        //@formatter:off
        StringBuilder builder = new StringBuilder("""
                SELECT
                	`u`.id_,
                	`u`.username_,
                	`u`.password_,
                	`u`.email_,
                	`u`.phone_,
                	`u`.phone_area_code,
                	`u`.full_name,
                	`u`.nick_name,
                	`u`.avatar_,
                	`u`.status_,
                	`u`.data_origin,
                	`u`.email_verified,
                	`u`.phone_verified,
                	`u`.shared_secret,
                	`u`.totp_bind,
                	`u`.auth_total,
                	`u`.last_auth_ip,
                	`u`.last_auth_time,
                	`u`.expand_,
                	`u`.external_id,
                	`u`.expire_date,
                	`u`.create_by,
                	`u`.create_time,
                	`u`.update_by,
                	`u`.update_time,
                	`u`.remark_,
                	group_concat( organization_.display_path ) AS org_display_path
                FROM
                	user_group_member ugm
                	INNER JOIN user u ON ugm.user_id = u.id_ AND u.is_deleted = '0'
                	INNER JOIN user_group ug ON ug.id_ = ugm.group_id AND ug.is_deleted = '0'
                	LEFT JOIN organization_member ON ( u.id_ = organization_member.user_id AND organization_member.is_deleted = '0')
                    LEFT JOIN organization organization_ ON ( organization_.id_ = organization_member.org_id AND organization_.is_deleted = '0')
                WHERE
                    ugm.is_deleted = '0'
                	AND ugm.group_id = '%s'
                	AND ug.id_ = '%s'
                """.formatted(query.getId(), query.getId()));
        //用户名
        if (StringUtils.isNoneBlank(query.getUsername())) {
            builder.append(" AND username_='%").append(query.getUsername()).append("%'");
        }
        builder.append("GROUP BY `u`.id_");
        //@formatter:on
        String sql = builder.toString();
        List<UserPO> list = jdbcTemplate.query(
            builder.append(" LIMIT ").append(pageable.getPageNumber() * pageable.getPageSize())
                .append(",").append(pageable.getPageSize()).toString(),
            new UserPoMapper());
        //@formatter:off
        String countSql = "SELECT count(*) FROM (" + sql + ") user_";
        //@formatter:on
        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class);
        return new PageImpl<>(list, pageable, count);
    }

    private final JdbcTemplate jdbcTemplate;
}
