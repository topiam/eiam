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
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.app.po.AppPO;
import cn.topiam.employee.common.entity.app.query.GetAppListQueryParam;
import cn.topiam.employee.common.repository.app.AppRepositoryCustomized;
import cn.topiam.employee.support.repository.aspect.query.QuerySingleResult;

import lombok.AllArgsConstructor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import static cn.topiam.employee.common.enums.app.AuthorizationType.ALL_ACCESS;

/**
 * App Repository Customized
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/12/29 21:27
 */
@Repository
@AllArgsConstructor
public class AppRepositoryCustomizedImpl implements AppRepositoryCustomized {

    /**
     * 根据主体ID获取应用列表
     *
     * @param subjectIds {@link  String}
     * @return {@link List}
     */
    @Override
    public List<AppPO> getAppList(List<String> subjectIds) {
        Map<String, Object> args = new HashMap<>();
        //@formatter:off
        String hql = """
                SELECT
                	NEW cn.topiam.employee.common.entity.app.po.AppPO(app.id,app.name,app.code,app.clientId,app.clientSecret,
                    app.template,app.protocol,app.type,app.icon,app.initLoginUrl,app.authorizationType,app.enabled,LISTAGG(DISTINCT ass.groupId,","))
                FROM
                	AppEntity app
                	LEFT JOIN AppAccessPolicyEntity appAcce ON app.id = appAcce.appId AND appAcce.enabled = true
                	LEFT JOIN AppGroupAssociationEntity ass ON app.id = ass.app.id
                WHERE
                	app.enabled = true
                	AND (appAcce.subjectId IN (:subjectIds) OR app.authorizationType = :type)
                GROUP BY app.id
                """;
        //@formatter:on
        TypedQuery<AppPO> listQuery = entityManager.createQuery(hql, AppPO.class);
        args.put("subjectIds", subjectIds);
        args.put("type", ALL_ACCESS);
        args.forEach(listQuery::setParameter);
        return listQuery.getResultList();
    }

    /**
     * 根据主体ID，查询参数、分页条件获取应用列表
     *
     * @param subjectIds {@link  List}
     * @param query {@link GetAppListQueryParam}
     * @param pageable {@link  String}
     * @return {@link List}
     */
    @Override
    public Page<AppEntity> getAppList(List<String> subjectIds, GetAppListQueryParam query,
                                      Pageable pageable) {
        Map<String, Object> args = new HashMap<>();
        //@formatter:off
        String hql = """
                SELECT DISTINCT
                	app
                FROM
                	AppEntity app
                	LEFT JOIN AppAccessPolicyEntity appAcce ON app.id = appAcce.appId
                	LEFT JOIN AppGroupAssociationEntity ass ON app.id = ass.app.id
                WHERE
                	app.enabled = true
                	AND (appAcce.subjectId IN (:subjectIds) AND appAcce.enabled = true OR app.authorizationType = :type)
                """;
        //@formatter:on
        String whereSql = "";
        //用户名
        if (org.apache.commons.lang3.StringUtils.isNoneBlank(query.getName())) {
            whereSql += " AND app.name LIKE :name ";
            args.put("name", "%" + query.getName() + "%");
        }

        //分组id
        if (Objects.nonNull(query.getGroupId())) {
            whereSql += " AND ass.groupId = :groupId";
            args.put("groupId", query.getGroupId());
        }

        TypedQuery<AppEntity> listQuery = entityManager.createQuery(hql + whereSql,
            AppEntity.class);
        args.put("subjectIds", subjectIds);
        args.put("type", ALL_ACCESS);
        args.forEach(listQuery::setParameter);
        listQuery.setFirstResult((int) pageable.getOffset());
        listQuery.setMaxResults(pageable.getPageSize());
        String countSql = """
                SELECT
                	COUNT(DISTINCT app.id)
                FROM
                	AppEntity app
                	LEFT JOIN AppAccessPolicyEntity appAcce ON app.id = appAcce.appId
                	LEFT JOIN AppGroupAssociationEntity ass ON app.id = ass.app.id
                WHERE
                	app.enabled = true
                	AND (appAcce.subjectId IN (:subjectIds) AND appAcce.enabled = true OR app.authorizationType = :type)
                """;
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql + whereSql, Long.class);
        args.forEach(countQuery::setParameter);
        return new PageImpl<>(listQuery.getResultList(), pageable, countQuery.getSingleResult());
    }

    /**
     * 获取用户应用数量
     *
     * @param subjectIds {@link List}
     * @return {@link Long}
     */
    @Override
    @QuerySingleResult
    public Long getAppCount(List<String> subjectIds) {
        String hql = """
                SELECT
                    COUNT(DISTINCT app.id)
                FROM
                    AppEntity app
                LEFT JOIN
                    AppAccessPolicyEntity aap ON app.id = aap.appId
                WHERE
                    app.enabled = true
                    AND (aap.subjectId IN (:subjectIds) AND aap.enabled = true OR app.authorizationType =:authorizationType)
                """;
        TypedQuery<Long> query = entityManager.createQuery(hql, Long.class);
        query.setParameter("subjectIds", subjectIds);
        query.setParameter("authorizationType", ALL_ACCESS);
        return query.getSingleResult();
    }

    /**
     * EntityManager
     */
    private final EntityManager entityManager;
}
