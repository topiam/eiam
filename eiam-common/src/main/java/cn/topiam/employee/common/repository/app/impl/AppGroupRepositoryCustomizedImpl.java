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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.account.query.UserGroupMemberListQuery;
import cn.topiam.employee.common.entity.app.po.AppGroupPO;
import cn.topiam.employee.common.entity.app.query.AppGroupQuery;
import cn.topiam.employee.common.repository.app.AppGroupRepositoryCustomized;
import cn.topiam.employee.support.repository.aspect.query.QuerySingleResult;

import lombok.AllArgsConstructor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import static cn.topiam.employee.common.enums.app.AuthorizationType.ALL_ACCESS;

/**
 * @author TopIAM
 * Created by support@topiam.cn on 2023/9/8 19:20
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
        Map<String, Object> args = new HashMap<>();
        //@formatter:off
        String hql = """
                SELECT
                	NEW cn.topiam.employee.common.entity.app.po.AppGroupPO(group.id,
                	group.name,
                	group.code,
                	group.type,
                	group.createTime,
                	group.remark,
                	IFNULL(ass.appCount, 0 ) AS appCount)
                FROM
                	AppGroupEntity group
                	LEFT JOIN (
                	SELECT
                	    aga.groupId as groupId,
                		COUNT(app.id) AS appCount
                	FROM
                		AppGroupAssociationEntity aga
                		INNER JOIN AppEntity app ON aga.app.id = app.id
                	GROUP BY
                		aga.groupId
                	) ass ON group.id = ass.groupId
                """;
        //@formatter:on
        String whereSql = " WHERE 1 = 1 ";
        //用户名
        //分组名称
        if (StringUtils.isNoneBlank(query.getName())) {
            whereSql += " AND group.name LIKE :name";
            args.put("name", "%" + query.getName() + "%");
        }
        //分组编码
        if (StringUtils.isNoneBlank(query.getCode())) {
            whereSql += " AND group.code LIKE :code";
            args.put("code", "%" + query.getCode() + "%");
        }
        //分组类型
        if (ObjectUtils.isNotEmpty(query.getType())) {
            whereSql += " AND group.type = :type";
            args.put("type", query.getType());
        }
        //按照创建时间倒序
        TypedQuery<AppGroupPO> listQuery = entityManager
            .createQuery(hql + whereSql + " ORDER BY group.createTime DESC", AppGroupPO.class);
        args.forEach(listQuery::setParameter);
        listQuery.setFirstResult((int) pageable.getOffset());
        listQuery.setMaxResults(pageable.getPageSize());
        String countSql = """
                SELECT
                 	COUNT(DISTINCT group.id)
                 FROM
                 	AppGroupEntity group
                 	LEFT JOIN (
                 	SELECT
                 		aga.groupId AS groupId,
                 		COUNT(app.id) AS appCount
                 	FROM
                 		AppGroupAssociationEntity aga
                 		INNER JOIN AppEntity app ON aga.app.id = app.id
                 	GROUP BY
                 		aga.groupId
                 	) ass ON group.id = ass.groupId
                 """;
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql + whereSql, Long.class);
        args.forEach(countQuery::setParameter);
        return new PageImpl<>(listQuery.getResultList(), pageable, countQuery.getSingleResult());
    }

    /**
     * 查询应用组列表
     *
     * @param subjectIds {@link List}
     * @param query  {@link AppGroupQuery}
     * @return {@link List}
     */
    @Override
    public List<AppGroupPO> getAppGroupList(List<String> subjectIds, AppGroupQuery query) {
        Map<String, Object> args = new HashMap<>();
        //@formatter:off
        String hql = """
                SELECT
                	NEW cn.topiam.employee.common.entity.app.po.AppGroupPO(group.id,
                	group.name,
                	group.code,
                	group.type,
                	group.createTime,
                	group.remark,
                	IFNULL( ass.appCount, 0 ) AS appCount)
                FROM
                	AppGroupEntity group
                	LEFT JOIN (
                	SELECT
                		aga.groupId AS groupId,
                		COUNT( DISTINCT aga.id ) AS appCount
                	FROM
                		AppGroupAssociationEntity aga
                		LEFT JOIN AppEntity app ON aga.app.id = app.id
                		LEFT JOIN AppAccessPolicyEntity app_acce ON app.id = app_acce.appId
                		WHERE (app_acce.subjectId IN (:subjectIds) OR app.authorizationType = :type)
                	GROUP BY
                		aga.groupId
                	) ass ON group.id = ass.groupId
                """;
        //@formatter:on
        String whereSql = " WHERE 1 = 1 ";
        //分组名称
        if (StringUtils.isNoneBlank(query.getName())) {
            whereSql += "AND group.name LIKE :name";
            args.put("name", "%" + query.getName() + "%");
        }
        //分组编码
        if (StringUtils.isNoneBlank(query.getCode())) {
            whereSql += "AND group.code LIKE :code";
            args.put("code", "%" + query.getCode() + "%");
        }
        //分组类型
        if (ObjectUtils.isNotEmpty(query.getType())) {
            whereSql += "AND group.type =:type";
            args.put("type", query);
        }
        TypedQuery<AppGroupPO> listQuery = entityManager.createQuery(hql + whereSql,
            AppGroupPO.class);
        args.put("subjectIds", subjectIds);
        args.put("type", ALL_ACCESS);
        args.forEach(listQuery::setParameter);
        return listQuery.getResultList();
    }

    /**
     * 根据当前用户和分组获取应用数量
     *
     * @param subjectIds  {@link Long}
     * @param groupId {@link String}
     * @return {@link Long}
     */
    @Override
    @QuerySingleResult
    public Long getAppCount(List<String> subjectIds, String groupId) {
        //@formatter:off
        String hql = """
                SELECT
                	COUNT(DISTINCT app.id)
                FROM
                	AppEntity app
                	LEFT JOIN AppAccessPolicyEntity app_acce ON app.id = app_acce.appId
                	LEFT JOIN AppGroupAssociationEntity ass ON app.id = ass.app.id
                WHERE
                	app.enabled = true
                	AND  app_acce.subjectId IN (:subjectIds) OR app.authorizationType = :type
                	AND ass.groupId = :groupId
                """;
        //@formatter:on
        TypedQuery<Long> query = entityManager.createQuery(hql, Long.class);
        query.setParameter("subjectIds", subjectIds);
        query.setParameter("type", ALL_ACCESS.getCode());
        query.setParameter("groupId", groupId);
        return query.getSingleResult();
    }

    /**
     * EntityManager
     */
    private final EntityManager entityManager;
}
