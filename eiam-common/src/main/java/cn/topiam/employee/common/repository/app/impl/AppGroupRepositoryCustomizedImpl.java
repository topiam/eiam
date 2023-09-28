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

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;

import cn.topiam.employee.common.entity.account.OrganizationMemberEntity;
import cn.topiam.employee.common.entity.account.UserGroupMemberEntity;
import cn.topiam.employee.common.entity.account.query.UserGroupMemberListQuery;
import cn.topiam.employee.common.entity.app.po.AppGroupPO;
import cn.topiam.employee.common.entity.app.query.AppGroupQuery;
import cn.topiam.employee.common.repository.account.OrganizationMemberRepository;
import cn.topiam.employee.common.repository.account.UserGroupMemberRepository;
import cn.topiam.employee.common.repository.app.AppGroupRepositoryCustomized;
import cn.topiam.employee.common.repository.app.impl.mapper.AppGroupPoMapper;

import lombok.AllArgsConstructor;
import static cn.topiam.employee.common.enums.app.AuthorizationType.ALL_ACCESS;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/9/8 19:20
 */
@Repository
@AllArgsConstructor
public class AppGroupRepositoryCustomizedImpl implements AppGroupRepositoryCustomized {

    private StringBuilder getBaseAppGroupListSql(AppGroupQuery query) {
        //@formatter:off
        StringBuilder builder = new StringBuilder("SELECT `group`.id_, `group`.name_, `group`.code_, `group`.type_, `group`.create_time, `group`.remark_, IFNULL( ass.app_count, 0) AS app_count FROM app_group `group` LEFT JOIN(SELECT aga.group_id, COUNT(*) AS `app_count` FROM app_group_association aga INNER JOIN app ON aga.app_id = app.id_ AND app.is_deleted = 0 GROUP BY aga.group_id ) ass ON `group`.id_ = ass.group_id WHERE is_deleted = '0'");
        //分组名称
        if (StringUtils.isNoneBlank(query.getName())) {
            builder.append(" AND `group`.name_ like '%").append(query.getName()).append("%'");
        }
        //分组编码
        if (StringUtils.isNoneBlank(query.getCode())) {
            builder.append(" AND `group`.code_ like '%").append(query.getCode()).append("%'");
        }
        //分组类型
        if (ObjectUtils.isNotEmpty(query.getType())) {
            builder.append(" AND `group`.type_ like '%").append(query.getType().getCode()).append("%'");
        }
        builder.append(" ORDER BY `group`.create_time DESC");
        //@formatter:on
        return builder;
    }

    /**
     * 获取应用组应用列表（分页）
     *
     * @param query    {@link UserGroupMemberListQuery}
     * @param pageable {@link Pageable}
     * @return {@link Page}
     */
    @Override
    public Page<AppGroupPO> getAppGroupList(AppGroupQuery query, Pageable pageable) {
        StringBuilder builder = getBaseAppGroupListSql(query);
        String sql = getBaseAppGroupListSql(query).toString();
        List<AppGroupPO> list = jdbcTemplate.query(
            builder.append(" LIMIT ").append(pageable.getPageNumber() * pageable.getPageSize())
                .append(",").append(pageable.getPageSize()).toString(),
            new AppGroupPoMapper());
        //@formatter:off
        String countSql = "SELECT count(*) FROM (" + sql + ") app_";
        //@formatter:on
        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class);
        return new PageImpl<>(list, pageable, Objects.requireNonNull(count).longValue());
    }

    /**
     * 查询应用组列表（不分页）
     *
     * @return {@link List}
     */
    @Override
    public List<AppGroupPO> getAppGroupList(AppGroupQuery query) {
        return jdbcTemplate.query(getBaseAppGroupListSql(query).toString(), new AppGroupPoMapper());
    }

    /**
     * 查询应用组列表
     *
     * @param userId {@link Long}
     * @param query  {@link AppGroupQuery}
     * @return {@link List}
     */
    @Override
    public List<AppGroupPO> getAppGroupList(Long userId, AppGroupQuery query) {
        //@formatter:on
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("subjectIds", getAccessPolicysubjectIdsByUserId(userId));
        //@formatter:off
        StringBuilder builder = new StringBuilder("SELECT `group`.id_, `group`.name_, `group`.code_, `group`.type_, `group`.create_time, `group`.remark_, IFNULL( ass.app_count, 0) AS app_count FROM app_group `group` LEFT JOIN(SELECT aga.group_id, COUNT(DISTINCT aga.id_) AS `app_count` FROM app_group_association aga LEFT JOIN app ON aga.app_id = app.id_ AND app.is_deleted = 0 LEFT JOIN app_access_policy app_acce  ON app.id_ = app_acce.app_id and app_acce.is_deleted = 0 WHERE aga.is_deleted = 0 and (app_acce.subject_id IN (:subjectIds) OR app.authorization_type = '"+ALL_ACCESS.getCode()+ "') GROUP BY aga.group_id ) ass ON `group`.id_ = ass.group_id WHERE is_deleted = '0'");
        //分组名称
        if (StringUtils.isNoneBlank(query.getName())) {
            builder.append(" AND `group`.name_ like '%").append(query.getName()).append("%'");
        }
        //分组编码
        if (StringUtils.isNoneBlank(query.getCode())) {
            builder.append(" AND `group`.code_ like '%").append(query.getCode()).append("%'");
        }
        //分组类型
        if (ObjectUtils.isNotEmpty(query.getType())) {
            builder.append(" AND `group`.type_ like '%").append(query.getType().getCode()).append("%'");
        }
        builder.append(" ORDER BY `group`.create_time DESC");
        return namedParameterJdbcTemplate.query(builder.toString(),paramMap, new AppGroupPoMapper());
        //@formatter:off
    }

    /**
     * 根据当前用户和分组获取应用数量
     *
     * @param groupId {@link Long}
     * @param userId  {@link Long}
     * @return {@link Long}
     */
    @Override
    public Long getAppCount(String groupId, Long userId) {
        //@formatter:on
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("subjectIds", getAccessPolicysubjectIdsByUserId(userId));
        //@formatter:off
        StringBuilder builder = new StringBuilder("SELECT COUNT(DISTINCT app.id_) FROM app LEFT JOIN app_access_policy app_acce ON app.id_ = app_acce.app_id AND app_acce.is_deleted = '0' LEFT JOIN app_group_association ass ON app.id_ = ass.app_id AND ass.is_deleted = '0' WHERE app.is_enabled = 1 AND app.is_deleted = '0' AND (app_acce.subject_id IN (:subjectIds) OR app.authorization_type = '"+ALL_ACCESS.getCode()+"')");
        builder.append(" AND ass.group_id = ").append(groupId);
        return namedParameterJdbcTemplate.queryForObject(builder.toString(), paramMap,
                Long.class);
        //@formatter:off
    }


    /**
     * 根据用户ID获取访问策略主体ID
     *
     * @param userId {@link Long}
     * @return {@link List}
     */
    private List<Object> getAccessPolicysubjectIdsByUserId(Long userId){
        //@formatter:on
        List<Object> list = Lists.newArrayList();
        //当前用户加入的用户组Id
        List<Long> groupIdList = userGroupMemberRepository.findByUserId(userId).stream()
            .map(UserGroupMemberEntity::getGroupId).toList();
        //当前用户加入的组织id
        List<String> orgId = organizationMemberRepository.findAllByUserId(userId).stream()
            .map(OrganizationMemberEntity::getOrgId).toList();
        list.addAll(groupIdList);
        list.addAll(orgId);
        list.add(userId);
        return list;
        //@formatter:off
    }


    /**
     * JdbcTemplate
     */
    private final JdbcTemplate jdbcTemplate;


    /**
     * NamedParameterJdbcTemplate
     */
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * UserGroupMemberRepository
     */
    private final UserGroupMemberRepository userGroupMemberRepository;

    /**
     * OrganizationMemberRepository
     */
    private final OrganizationMemberRepository organizationMemberRepository;
}
