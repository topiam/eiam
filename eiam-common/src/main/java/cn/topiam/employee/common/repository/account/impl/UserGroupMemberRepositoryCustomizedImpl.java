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
package cn.topiam.employee.common.repository.account.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.account.po.UserPO;
import cn.topiam.employee.common.entity.account.query.UserGroupMemberListQuery;
import cn.topiam.employee.common.repository.account.UserGroupMemberRepositoryCustomized;

import lombok.AllArgsConstructor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * @author TopIAM
 * Created by support@topiam.cn on 2022/2/13 21:27
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
    @Override
    public Page<UserPO> getUserGroupMemberList(UserGroupMemberListQuery query, Pageable pageable) {
        Map<String, Object> args = new HashMap<>();
        //@formatter:off
        String sql = """
                SELECT
                	%s
                FROM
                	UserGroupMemberEntity ugm
                	INNER JOIN UserEntity u ON ugm.userId = u.id
                	INNER JOIN UserGroupEntity ug ON ug.id = ugm.groupId
                	LEFT  JOIN OrganizationMemberEntity om ON u.id = om.userId
                    LEFT  JOIN OrganizationEntity o ON o.id = om.orgId
                """;
        args.put("userGroupMemberId", query.getId());
        StringBuilder whereSql = new StringBuilder("""
                WHERE
                	ugm.groupId = :userGroupMemberId
                	AND ug.id = :userGroupMemberId
                """);
        //用户名
        if (StringUtils.isNoneBlank(query.getFullName())) {
            whereSql.append(" AND u.fullName LIKE :fullName ");
            args.put("fullName", "%" + query.getFullName() + "%");
        }
        //@formatter:on
        String listSql = sql.formatted("""
                NEW cn.topiam.employee.common.entity.account.po.UserPO(u,
                LISTAGG(o.displayPath , ',' ))
                """);
        TypedQuery<UserPO> listQuery = entityManager
            .createQuery(listSql + whereSql + " GROUP BY u.id ", UserPO.class);
        args.forEach(listQuery::setParameter);
        listQuery.setFirstResult((int) pageable.getOffset());
        listQuery.setMaxResults(pageable.getPageSize());
        String countSql = sql.formatted("COUNT(DISTINCT u.id)");
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql + whereSql, Long.class);
        args.forEach(countQuery::setParameter);
        return new PageImpl<>(listQuery.getResultList(), pageable, countQuery.getSingleResult());
    }

    private final EntityManager entityManager;
}
