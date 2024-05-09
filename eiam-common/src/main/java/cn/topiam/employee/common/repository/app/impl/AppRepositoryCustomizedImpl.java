/*
 * Copyright (c) 2022-Present. Jinan Yuanchuang Network Technology Co., Ltd.
 * All rights reserved.
 * 项目名称：TOPIAM 企业数字身份管控平台
 * 版权说明：本软件属济南源创网络科技有限公司所有，受著作权法和国际版权条约的保护。在未获得济南源创网络科技有限公司正式授权情况下，任何企业和个人，未经授权擅自复制、修改、分发本程序的全部或任何部分，将要承担一切由此导致的民事或刑事责任。
 */
package cn.topiam.employee.common.repository.app.impl;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.app.po.AppPO;
import cn.topiam.employee.common.entity.app.query.GetAppListQuery;
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
                	LEFT JOIN AppAccessPolicyEntity appAcce ON app.id = appAcce.appId
                	LEFT JOIN AppGroupAssociationEntity ass ON app.id = ass.app.id
                WHERE
                	app.enabled = true
                	AND appAcce.enabled = true
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
     * @param query {@link GetAppListQuery}
     * @param pageable {@link  String}
     * @return {@link List}
     */
    @Override
    public Page<AppEntity> getAppList(List<String> subjectIds, GetAppListQuery query,
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
