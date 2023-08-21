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

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;

import cn.topiam.employee.common.entity.account.OrganizationMemberEntity;
import cn.topiam.employee.common.entity.account.UserGroupMemberEntity;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.repository.account.OrganizationMemberRepository;
import cn.topiam.employee.common.repository.account.UserGroupMemberRepository;
import cn.topiam.employee.common.repository.app.AppRepositoryCustomized;
import cn.topiam.employee.common.repository.app.impl.mapper.AppEntityMapper;

import lombok.AllArgsConstructor;

/**
 * App Repository Customized
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/29 21:27
 */
@Repository
@AllArgsConstructor
public class AppRepositoryCustomizedImpl implements AppRepositoryCustomized {

    /**
     * 获取我的应用列表
     *
     * @param name     {@link  String}
     * @param userId {@link  Long}
     * @param pageable {@link  String}
     * @return {@link List}
     */
    @Override
    public Page<AppEntity> getAppList(Long userId, String name, Pageable pageable) {
        List<Object> paramList = Lists.newArrayList();
        //当前用户加入的用户组Id
        List<Long> groupIdList = userGroupMemberRepository.findByUserId(userId).stream()
            .map(UserGroupMemberEntity::getGroupId).toList();
        //当前用户加入的组织id
        List<String> orgId = organizationMemberRepository.findAllByUserId(userId).stream()
            .map(OrganizationMemberEntity::getOrgId).toList();
        paramList.addAll(groupIdList);
        paramList.addAll(orgId);
        paramList.add(userId);
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("subjectIds", paramList);
        //@formatter:off
        StringBuilder builder = new StringBuilder("""
                SELECT DISTINCT
                	app.*
                FROM
                	app
                	LEFT JOIN app_access_policy app_acce ON app.id_ = app_acce.app_id AND app_acce.is_deleted = '0'
                WHERE
                	app.is_enabled = 1
                	AND app.is_deleted = '0'
                	AND (app_acce.subject_id IN (:subjectIds) OR app.authorization_type = 'all_access')
                """);
        //用户名
        if (StringUtils.isNoneBlank(name)) {
            builder.append(" AND app.name_ like '%").append(name).append("%'");
        }
        //@formatter:on
        String sql = builder.toString();
        List<AppEntity> list = namedParameterJdbcTemplate.query(
            builder.append(" LIMIT ").append(pageable.getPageNumber() * pageable.getPageSize())
                .append(",").append(pageable.getPageSize()).toString(),
            paramMap, new AppEntityMapper());
        //@formatter:off
        String countSql = "SELECT count(*) FROM (" + sql + ") app_account_";
        //@formatter:on
        Integer count = namedParameterJdbcTemplate.queryForObject(countSql, paramMap,
            Integer.class);
        return new PageImpl<>(list, pageable, count);
    }

    /**
     * NamedParameterJdbcTemplate
     */
    private final NamedParameterJdbcTemplate   namedParameterJdbcTemplate;

    /**
     * UserGroupMemberRepository
     */
    private final UserGroupMemberRepository    userGroupMemberRepository;

    /**
     * OrganizationMemberRepository
     */
    private final OrganizationMemberRepository organizationMemberRepository;
}
