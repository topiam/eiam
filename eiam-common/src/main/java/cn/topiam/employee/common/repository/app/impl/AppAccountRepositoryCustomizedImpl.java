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

import cn.topiam.employee.common.entity.account.query.UserListQuery;
import cn.topiam.employee.common.entity.app.po.AppAccountPO;
import cn.topiam.employee.common.entity.app.query.AppAccountQuery;
import cn.topiam.employee.common.repository.app.AppAccountRepositoryCustomized;

import lombok.AllArgsConstructor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * AppAccount Repository Customized
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/12/29 21:27
 */
@Repository
@AllArgsConstructor
public class AppAccountRepositoryCustomizedImpl implements AppAccountRepositoryCustomized {

    /**
     * 获取应用账户列表
     *
     * @param pageable {@link  Pageable}
     * @param query    {@link  UserListQuery}
     * @return {@link Page}
     */
    @Override
    public Page<AppAccountPO> getAppAccountList(AppAccountQuery query, Pageable pageable) {
        Map<String, Object> args = new HashMap<>();
        //@formatter:off
        String hql = """
                SELECT
                	NEW cn.topiam.employee.common.entity.app.po.AppAccountPO(a.id,
                	a.appId,
                	a.userId,
                	a.account,
                	a.createTime,
                	a.defaulted,
                	u.username,
                	p.name,
                	p.type,
                	p.template,
                	p.protocol)
                FROM
                	AppAccountEntity a
                	LEFT JOIN UserEntity u ON a.userId = u.id
                	LEFT JOIN AppEntity p ON a.appId = p.id
                	WHERE 1=1
                """;
        //@formatter:on
        String whereSql = "";
        //用户名
        if (StringUtils.isNoneBlank(query.getUsername())) {
            whereSql += " AND u.username LIKE :username";
            args.put("username", "%" + query.getUsername() + "%");
        }
        //用户ID
        if (StringUtils.isNoneBlank(query.getUserId())) {
            whereSql += " AND u.id = :userId";
            args.put("userId", query.getUserId());
        }
        //账户名称
        if (StringUtils.isNoneBlank(query.getAccount())) {
            whereSql += " AND a.account LIKE :account";
            args.put("account", "%" + query.getAccount() + "%");
        }

        //应用id
        if (StringUtils.isNoneBlank(query.getAppId())) {
            whereSql += " AND a.appId = :appId";
            args.put("appId", query.getAppId());
        }

        //应用名称
        if (StringUtils.isNotBlank(query.getAppName())) {
            whereSql += " AND p.name LIKE :appName";
            args.put("appName", "%" + query.getAppName() + "%");
        }
        //按照创建时间倒序
        TypedQuery<AppAccountPO> listQuery = entityManager.createQuery(hql + whereSql,
            AppAccountPO.class);
        args.forEach(listQuery::setParameter);
        listQuery.setFirstResult((int) pageable.getOffset());
        listQuery.setMaxResults(pageable.getPageSize());
        String countSql = """
                SELECT
                	COUNT(DISTINCT a.id)
                FROM
                	AppAccountEntity a
                	LEFT JOIN UserEntity u ON a.userId = u.id
                	LEFT JOIN AppEntity p ON a.appId = p.id
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
}
