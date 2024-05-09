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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.account.query.UserGroupMemberListQuery;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.app.query.AppGroupAssociationListQuery;
import cn.topiam.employee.common.repository.app.AppGroupAssociationRepositoryCustomized;

import lombok.AllArgsConstructor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * @author TopIAM
 * Created by support@topiam.cn on 2023/9/7 21:27
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
        Map<String, Object> args = new HashMap<>();
        //@formatter:off
        String hql = """
                SELECT DISTINCT
                	 app
                FROM
                	AppEntity app
                	LEFT JOIN AppGroupAssociationEntity ass ON app.id = ass.app.id
                WHERE
                	ass.groupId = :groupId
                """;
        //@formatter:on
        String whereSql = "";
        //用户名
        if (StringUtils.isNoneBlank(query.getAppName())) {
            whereSql += " AND app.name LIKE :name ";
            args.put("name", "%" + query.getAppName() + "%");
        }
        //按照创建时间倒序
        TypedQuery<AppEntity> listQuery = entityManager
            .createQuery(hql + whereSql + " ORDER BY app.createTime DESC", AppEntity.class);
        args.put("groupId", query.getId());
        args.forEach(listQuery::setParameter);
        listQuery.setFirstResult((int) pageable.getOffset());
        listQuery.setMaxResults(pageable.getPageSize());
        String countSql = """
                SELECT
                	COUNT(DISTINCT app.id)
                FROM
                	AppEntity app
                	LEFT JOIN AppGroupAssociationEntity ass ON app.id = ass.app.id
                WHERE
                	ass.groupId = :groupId
                """;
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql + whereSql, Long.class);
        args.forEach(countQuery::setParameter);
        return new PageImpl<>(listQuery.getResultList(), pageable, countQuery.getSingleResult());
    }

    /**
     * EntityManager
     */
    private final EntityManager entityManager;
}
