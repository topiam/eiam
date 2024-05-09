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

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.account.query.UserListQuery;
import cn.topiam.employee.common.entity.app.po.AppAccessPolicyPO;
import cn.topiam.employee.common.entity.app.query.AppAccessPolicyQuery;
import cn.topiam.employee.common.repository.app.AppAccessPolicyRepositoryCustomized;
import cn.topiam.employee.support.exception.TopIamException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * AppPolicy Repository Customized
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/12/29 21:27
 */
@Repository
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
        Map<String, Object> args = new HashMap<>();
        //@formatter:off
        String hql = """
                SELECT
                	NEW cn.topiam.employee.common.entity.app.po.AppAccessPolicyPO(a.id,
                	a.appId,
                	a.subjectId,
                	a.subjectType,
                	a.enabled,
                	a.createTime,
                	subject.name,
                	app.name,
                	app.icon,
                	app.type,
                	app.template,
                	app.protocol)
                FROM
                	AppAccessPolicyEntity a
                	INNER JOIN AppEntity app ON a.appId = app.id
                INNER JOIN
                (
                SELECT
                    id as id,
                	name as name
                FROM
                	UserGroupEntity UNION ALL
                SELECT
                	id as id,
                	name as name
                FROM
                	OrganizationEntity UNION ALL
                SELECT
                	id as id,
                	username AS name
                FROM
                	UserEntity
                	) subject ON a.subjectId = subject.id
                WHERE 1=1
                """;
        //@formatter:on
        String whereSql = "";
        if (ObjectUtils.isNotEmpty(query.getSubjectType())) {
            whereSql += " AND subjectType = :subjectType";
            args.put("subjectType", query.getSubjectType());
        }
        //主体名称
        if (StringUtils.isNotEmpty(query.getSubjectName())) {
            whereSql += " AND subject.name LIKE :subjectName";
            args.put("subjectName", "%" + query.getSubjectName() + "%");
        }

        //主体ID
        if (StringUtils.isNotBlank(query.getSubjectId())) {
            whereSql += " AND a.subjectId = :subjectId";
            args.put("subjectId", query.getSubjectId());
        } else {
            if (StringUtils.isEmpty(query.getAppId())) {
                throw new TopIamException("主体ID不能为空");
            }
        }

        //应用ID
        if (StringUtils.isNotBlank(query.getAppId())) {
            whereSql += " AND a.appId = :appId";
            args.put("appId", query.getAppId());
        }

        //应用名称
        if (StringUtils.isNotBlank(query.getAppName())) {
            whereSql += " AND app.name LIKE :appName";
            args.put("appName", "%" + query.getAppName() + "%");
        }

        //按照创建时间倒序
        TypedQuery<AppAccessPolicyPO> listQuery = entityManager.createQuery(hql + whereSql,
            AppAccessPolicyPO.class);
        args.forEach(listQuery::setParameter);
        listQuery.setFirstResult((int) pageable.getOffset());
        listQuery.setMaxResults(pageable.getPageSize());
        String countSql = """
                SELECT
                	COUNT(DISTINCT a.id)
                FROM
                	AppAccessPolicyEntity a
                	INNER JOIN AppEntity app ON a.appId = app.id
                INNER JOIN
                (
                SELECT
                	id as id,
                	name as name
                FROM
                	UserGroupEntity UNION ALL
                SELECT
                	id as id,
                	name as name
                FROM
                	OrganizationEntity UNION ALL
                SELECT
                	id as id,
                	username AS name
                FROM
                	UserEntity
                	) subject ON a.subjectId = subject.id
                WHERE 1=1
                """;
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql + whereSql, Long.class);
        args.forEach(countQuery::setParameter);
        return new PageImpl<>(listQuery.getResultList(), pageable, countQuery.getSingleResult());
    }

    /**
     * EntityManager
     */
    private final EntityManager entityManager;

    public AppAccessPolicyRepositoryCustomizedImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
