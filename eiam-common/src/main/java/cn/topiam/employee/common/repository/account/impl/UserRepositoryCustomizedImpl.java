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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.po.UserPO;
import cn.topiam.employee.common.entity.account.query.UserListNotInGroupQuery;
import cn.topiam.employee.common.entity.account.query.UserListQuery;
import cn.topiam.employee.common.repository.account.UserRepositoryCustomized;

import lombok.AllArgsConstructor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import static cn.topiam.employee.common.constant.AccountConstants.USER_CACHE_NAME;

/**
 * User Repository Customized
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/12/29 21:27
 */
@Repository
@AllArgsConstructor
@CacheConfig(cacheNames = { USER_CACHE_NAME })
public class UserRepositoryCustomizedImpl implements UserRepositoryCustomized {

    /**
     * 获取用户列表
     *
     * @param pageable {@link  Pageable}
     * @param query    {@link  UserListQuery}
     * @return {@link Page}
     */
    @Override
    public Page<UserPO> getUserList(UserListQuery query, Pageable pageable) {
        //@formatter:off
        String listSql = getUserListHql("""
            NEW cn.topiam.employee.common.entity.account.po.UserPO(
                user,LISTAGG(organization_.displayPath,','))
        """);
        String countSql = getUserListHql(" COUNT(DISTINCT user.id) ");
        StringBuilder whereSql = new StringBuilder();
        Map<String, Object> args = new HashMap<>();
        //组织条件
        if (StringUtils.isNotBlank(query.getOrganizationId())) {
            if (Boolean.TRUE.equals(query.getInclSubOrganization())) {
                //包含子节点
                whereSql.append(" AND LOCATE(:orgId, organization_.path) > 0");
                args.put("orgId", query.getOrganizationId());
            }
            else {
                whereSql.append(" AND organization_.id = :orgId");
                args.put("orgId", query.getOrganizationId());
            }
        }
        //用户名条件
        if (StringUtils.isNoneBlank(query.getUsername())) {
            whereSql.append(" AND user.username LIKE :username");
            args.put("username", "%" + query.getUsername() + "%");
        }
        //姓名
        if (StringUtils.isNoneBlank(query.getFullName())) {
            whereSql.append(" AND user.fullName LIKE :fullName");
            args.put("fullName", "%" + query.getFullName() + "%");
        }
        //手机号条件
        if (StringUtils.isNoneBlank(query.getPhone())) {
            whereSql.append(" AND user.phone = :phone");
            args.put("phone", query.getPhone());
        }
        //邮箱地址条件
        if (StringUtils.isNoneBlank(query.getEmail())) {
            whereSql.append(" AND user.email LIKE :email");
            args.put("email", "%" + query.getEmail() + "%");
        }
        //状态条件
        if (Objects.nonNull(query.getStatus())) {
            whereSql.append(" AND user.status = :status");
            args.put("status", query.getStatus());
        }
        //数据来源
        if (Objects.nonNull(query.getDataOrigin())) {
            whereSql.append(" AND user.dataOrigin = :dataOrigin");
            args.put("dataOrigin", query.getDataOrigin());
        }
        TypedQuery<UserPO> listQuery = buildUserListQuery(listSql + whereSql + " GROUP BY user.id ", args, pageable);
        TypedQuery<Long> countQuery = buildCountQuery(countSql + whereSql, args);
        return new PageImpl<>(listQuery.getResultList(), pageable, countQuery.getSingleResult());
    }

    /**
     * 获取用户组不存在成员列表
     *
     * @param query    {@link UserListNotInGroupQuery}
     * @param pageable {@link Pageable}
     * @return {@link Page}
     */
    @Override
    public Page<UserPO> getUserListNotInGroupId(UserListNotInGroupQuery query, Pageable pageable) {
        //@formatter:off
        String listSql = getUserListHql("""
            NEW cn.topiam.employee.common.entity.account.po.UserPO(
                user,LISTAGG(organization_.displayPath,','))
        """);
        String countSql = getUserListHql(" COUNT(DISTINCT user.id) ");
        Map<String, Object> args = new HashMap<>();
        StringBuilder whereSql = new StringBuilder("""
                WHERE NOT EXISTS (
                     SELECT 1
                     FROM UserGroupMemberEntity ugm
                     WHERE ugm.userId = user.id
                     AND ugm.groupId = :userGroupId
                 )
                """);
        args.put("userGroupId", query.getId());
        if (StringUtils.isNoneBlank(query.getKeyword())) {
            whereSql.append(" AND ( user.username LIKE :keyWord");
            whereSql.append(" OR  user.fullName LIKE :keyWord");
            whereSql.append(" OR  user.phone LIKE :keyWord");
            whereSql.append(" OR  user.email LIKE :keyWord )");
            args.put("keyWord", "%" + query.getKeyword() + "%");
        }
        TypedQuery<UserPO> listQuery = buildUserListQuery(listSql + whereSql + " GROUP BY user.id ", args, pageable);
        TypedQuery<Long> countQuery = buildCountQuery(countSql + whereSql, args);
        return new PageImpl<>(listQuery.getResultList(), pageable, countQuery.getSingleResult());
    }

    @NotNull
    private static String getUserListHql(String columns) {
        return """
                SELECT
                    %s
                FROM
                    UserEntity user
                    INNER JOIN OrganizationMemberEntity om ON user.id = om.userId
                    INNER JOIN OrganizationEntity organization_ ON organization_.id = om.orgId
                """.formatted(columns);
    }

    private TypedQuery<UserPO> buildUserListQuery(String sql, Map<String, Object> args, Pageable pageable) {
        TypedQuery<UserPO> query = entityManager.createQuery(sql, UserPO.class);
        args.forEach(query::setParameter);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        return query;
    }

    private TypedQuery<Long> buildCountQuery(String countSql, Map<String, Object> args) {
        TypedQuery<Long> query = entityManager.createQuery(countSql, Long.class);
        args.forEach(query::setParameter);
        return query;
    }

    @Override
    public List<UserEntity> findAllByOrgIdNotExistAndIdentitySourceId(String identitySourceId) {
        return entityManager.createQuery("""
                    SELECT
                        user
                    FROM
                        UserEntity user
                        LEFT JOIN OrganizationMemberEntity organization_member ON user.id = organization_member.userId
                    WHERE
                        organization_member.userId IS NULL AND user.identitySourceId = :identitySourceId
                    """,
            UserEntity.class).setParameter("identitySourceId", identitySourceId).getResultList();
    }

    /**
     * 批量新增或更新
     *
     * @param list {@link List}
     */
    @Override
    @CacheEvict(allEntries = true)
    public void batchSave(List<UserEntity> list) {
        //@formatter:off
        jdbcTemplate.batchUpdate(
            "INSERT INTO eiam_user (id_, username_, password_, email_, phone_, phone_area_code, full_name,nick_name, avatar_, external_id, expire_date, status_, email_verified, phone_verified, auth_total,last_auth_ip,last_auth_time,expand_,data_origin,identity_source_id,last_update_password_time ,create_by,create_time,update_by,update_time,remark_,is_deleted) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ",
            new BatchPreparedStatementSetter() {

                @Override
                public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                    UserEntity entity = list.get(i);
                    ps.setString(1, entity.getId());
                    ps.setString(2, entity.getUsername());
                    ps.setString(3, entity.getPassword());
                    ps.setObject(4, StringUtils.isBlank(entity.getEmail())?null:entity.getEmail());
                    ps.setObject(5, StringUtils.isBlank(entity.getPhone())?null:entity.getPhone());
                    ps.setString(6, entity.getPhoneAreaCode());
                    ps.setString(7, entity.getFullName());
                    ps.setString(8, entity.getNickName());
                    ps.setString(9, entity.getAvatar());
                    ps.setString(10, entity.getExternalId());
                    ps.setDate(11, Date.valueOf(entity.getExpireDate()));
                    ps.setString(12, entity.getStatus().getCode());
                    ps.setBoolean(13, !Objects.isNull(entity.getEmailVerified()) && entity.getEmailVerified());
                    ps.setBoolean(14, !Objects.isNull(entity.getPhoneVerified()) && entity.getPhoneVerified());
                    ps.setLong(15, Objects.isNull(entity.getAuthTotal())?0L:entity.getAuthTotal());
                    ps.setString(16, entity.getLastAuthIp());
                    ps.setTimestamp(17, !Objects.isNull(entity.getLastAuthTime()) ? Timestamp.valueOf(entity.getLastAuthTime()) : null);
                    ps.setString(18, entity.getExpand());
                    ps.setString(19, entity.getDataOrigin());
                    ps.setObject(20, entity.getIdentitySourceId());
                    ps.setTimestamp(21, !Objects.isNull(entity.getLastUpdatePasswordTime())?Timestamp.valueOf(entity.getLastUpdatePasswordTime()):null);
                    ps.setString(22, entity.getCreateBy());
                    ps.setTimestamp(23, Timestamp.valueOf(entity.getCreateTime()));
                    ps.setString(24, entity.getUpdateBy());
                    ps.setTimestamp(25, Timestamp.valueOf(entity.getUpdateTime()));
                    ps.setString(26, entity.getRemark());
                    ps.setBoolean(27,false);
                }

                @Override
                public int getBatchSize() {
                    return list.size();
                }
            });
        //@formatter:on
    }

    @Override
    @CacheEvict(allEntries = true)
    public void batchUpdate(List<UserEntity> list) {
        //@formatter:off
        jdbcTemplate.batchUpdate(
            "UPDATE eiam_user SET username_=?, password_=?, email_=?, phone_=?, phone_area_code=?,full_name=?,nick_name=?, avatar_=?, external_id=?, expire_date=?, status_=?, email_verified=?, phone_verified=?, auth_total=?,last_auth_ip=?,last_auth_time=?,expand_=?,data_origin=?,identity_source_id=?,last_update_password_time =?,create_by=?,create_time=?,update_by=?,update_time=?,remark_=? WHERE  id_=?",
            new BatchPreparedStatementSetter() {

                @Override
                public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                        UserEntity entity = list.get(i);
                        ps.setString(1, entity.getUsername());
                        ps.setString(2, entity.getPassword());
                        ps.setObject(3, StringUtils.isBlank(entity.getEmail())?null:entity.getEmail());
                        ps.setObject(4, StringUtils.isBlank(entity.getPhone())?null:entity.getPhone());
                        ps.setString(5, entity.getPhoneAreaCode());
                        ps.setString(6, entity.getFullName());
                        ps.setString(7, entity.getNickName());
                        ps.setString(8, entity.getAvatar());
                        ps.setString(9, entity.getExternalId());
                        ps.setDate(10, Date.valueOf(entity.getExpireDate()));
                        ps.setString(11, entity.getStatus().getCode());
                        ps.setBoolean(12, !Objects.isNull(entity.getEmailVerified()) && entity.getEmailVerified());
                        ps.setBoolean(13, !Objects.isNull(entity.getPhoneVerified()) && entity.getPhoneVerified());
                        ps.setLong(14, entity.getAuthTotal());
                        ps.setString(15, entity.getLastAuthIp());
                        ps.setTimestamp(16, !Objects.isNull(entity.getLastAuthTime()) ? Timestamp.valueOf(entity.getLastAuthTime()) : null);
                        ps.setString(17, entity.getExpand());
                        ps.setString(18, entity.getDataOrigin());
                        ps.setObject(19, entity.getIdentitySourceId());
                        ps.setTimestamp(20, !Objects.isNull(entity.getLastUpdatePasswordTime())?Timestamp.valueOf(entity.getLastUpdatePasswordTime()):null);
                        ps.setString(21, entity.getCreateBy());
                        ps.setTimestamp(22, Timestamp.valueOf(entity.getCreateTime()));
                        ps.setString(23, entity.getUpdateBy());
                        ps.setTimestamp(24, Timestamp.valueOf(entity.getUpdateTime()));
                        ps.setString(25, entity.getRemark());
                        ps.setString(26, entity.getId());
                }

                @Override
                public int getBatchSize() {
                    return list.size();
                }
            });
        //@formatter:on
    }

    /**
     * JdbcTemplate
     */
    private final JdbcTemplate  jdbcTemplate;

    /**
     * EntityManager
     */
    private final EntityManager entityManager;
}
