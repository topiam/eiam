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
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.app.query.AppQuery;
import cn.topiam.employee.common.entity.app.query.GetAppListQuery;
import cn.topiam.employee.common.repository.account.OrganizationMemberRepository;
import cn.topiam.employee.common.repository.account.UserGroupMemberRepository;
import cn.topiam.employee.common.repository.app.AppRepositoryCustomized;
import cn.topiam.employee.common.repository.app.impl.mapper.AppEntityMapper;

import lombok.AllArgsConstructor;
import static cn.topiam.employee.common.enums.app.AuthorizationType.ALL_ACCESS;

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
     * @param userId   {@link  Long}
     * @param query    {@link GetAppListQuery}
     * @param pageable {@link  Pageable}
     * @return {@link List}
     */
    @Override
    public Page<AppEntity> getAppList(Long userId, GetAppListQuery query, Pageable pageable) {
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("subjectIds", getSubjectIds(userId));
        //@formatter:off
        StringBuilder builder = new StringBuilder("SELECT DISTINCT app.* FROM app LEFT JOIN app_access_policy app_acce ON app.id_ = app_acce.app_id AND app_acce.is_deleted = '0' LEFT JOIN app_group_association ass ON app.id_ = ass.app_id AND ass.is_deleted = '0' WHERE app.is_enabled = 1 AND app.is_deleted = '0' AND (app_acce.subject_id IN (:subjectIds) OR app.authorization_type = '"+ALL_ACCESS.getCode()+"')");
        //用户名
        if (StringUtils.isNoneBlank(query.getName())) {
            builder.append(" AND app.name_ like '%").append(query.getName()).append("%'");
        }
        //分组id
        if (Objects.nonNull(query.getGroupId())) {
            builder.append(" AND ass.group_id = ").append(query.getGroupId());
        }
        //@formatter:on
        String sql = builder.toString();
        List<AppEntity> list = namedParameterJdbcTemplate.query(
            builder.append(" LIMIT ").append(pageable.getPageNumber() * pageable.getPageSize())
                .append(",").append(pageable.getPageSize()).toString(),
            paramMap, new AppEntityMapper());
        //@formatter:off
        String countSql = "SELECT count(*) FROM (" + sql + ") app_";
        //@formatter:on
        Integer count = namedParameterJdbcTemplate.queryForObject(countSql, paramMap,
            Integer.class);
        return new PageImpl<>(list, pageable, Objects.requireNonNull(count).longValue());
    }

    /**
     * 获取应用列表
     *
     * @param appQuery {@link  AppQuery}
     * @param pageable {@link  Pageable}
     * @return {@link List}
     */
    @Override
    public Page<AppEntity> getAppList(AppQuery appQuery, Pageable pageable) {
        //@formatter:off
        StringBuilder builder = new StringBuilder("SELECT DISTINCT app.* FROM app LEFT JOIN app_group_association `group` ON app.id_ = `group`.app_id WHERE app.is_deleted =0");

        //应用名称
        if (StringUtils.isNoneBlank(appQuery.getName())) {
            builder.append(" AND app.name_ like '%").append(appQuery.getName()).append("%'");
        }
        //协议类型
        if (Objects.nonNull(appQuery.getProtocol())) {
            builder.append(" AND app.protocol_ = ").append(appQuery.getProtocol().getCode());
        }
        //应用组ID
        if(Objects.nonNull(appQuery.getGroupId())){
            builder.append(" AND group.group_id = ").append(appQuery.getGroupId());
        }

        //@formatter:on
        String sql = builder.toString();
        List<AppEntity> list = jdbcTemplate.query(
            builder.append(" LIMIT ").append(pageable.getPageNumber() * pageable.getPageSize())
                .append(",").append(pageable.getPageSize()).toString(),
            new AppEntityMapper());
        //@formatter:off
        String countSql = "SELECT count(*) FROM (" + sql + ") app_account_";
        //@formatter:on
        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class);
        return new PageImpl<>(list, pageable, Objects.requireNonNull(count).longValue());
    }

    /**
     * 获取用户应用数量
     *
     * @param userId {@link Long}
     * @return {@link Long}
     */
    @Override
    public Long getAppCount(Long userId) {
        //@formatter:on
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("subjectIds", getSubjectIds(userId));
        StringBuilder builder = new StringBuilder(
            "SELECT COUNT(DISTINCT app.id_) FROM app LEFT JOIN app_access_policy app_acce ON app.id_ = app_acce.app_id AND app_acce.is_deleted = '0' WHERE app.is_enabled = 1 AND app.is_deleted = '0' AND (app_acce.subject_id IN (:subjectIds) OR app.authorization_type = '"
                                                  + ALL_ACCESS.getCode() + "')");
        return namedParameterJdbcTemplate.queryForObject(builder.toString(), paramMap, Long.class);
        //@formatter:off
    }


    /**
     * 根据用户ID获取访问策略主体ID
     *
     * @param userId {@link Long}
     * @return {@link List}
     */
    private List<Object> getSubjectIds(Long userId){
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
