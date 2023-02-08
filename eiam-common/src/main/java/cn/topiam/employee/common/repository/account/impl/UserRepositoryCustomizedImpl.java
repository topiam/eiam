/*
 * eiam-common - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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
import java.util.List;
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
import cn.topiam.employee.common.repository.account.impl.mapper.UserEntityMapper;
import cn.topiam.employee.common.repository.account.impl.mapper.UserPoMapper;

import lombok.AllArgsConstructor;
import static cn.topiam.employee.common.constants.AccountConstants.USER_CACHE_NAME;

/**
 * User Repository Customized
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/29 20:27
 */
@Repository
@CacheConfig(cacheNames = { USER_CACHE_NAME })
@AllArgsConstructor
public class UserRepositoryCustomizedImpl implements UserRepositoryCustomized {

    /**
     * 获取用户列表
     *
     * @param pageable {@link  Pageable}
     * @param query    {@link  UserListQuery}
     * @return {@link Page}
     */
    @SuppressWarnings("DuplicatedCode")
    @Override
    public Page<UserPO> getUserList(UserListQuery query, Pageable pageable) {
        //@formatter:off
        StringBuilder builder = new StringBuilder("SELECT `user`.id_, `user`.username_,`user`.password_, `user`.email_, `user`.phone_,`user`.phone_area_code, `user`.full_name ,`user`.nick_name, `user`.avatar_ , `user`.status_, `user`.data_origin, `user`.email_verified, `user`.phone_verified, `user`.shared_secret, `user`.totp_bind , `user`.auth_total, `user`.last_auth_ip, `user`.last_auth_time, `user`.expand_, `user`.external_id , `user`.expire_date,`user`.create_by, `user`.create_time, `user`.update_by , `user`.update_time, `user`.remark_, group_concat(organization_.display_path) AS org_display_path FROM `user` INNER JOIN `organization_member` ON (`user`.id_ = organization_member.user_id) INNER JOIN `organization` organization_ ON (organization_.id_ = organization_member.org_id) WHERE `user`.is_deleted = 0");
        //组织条件
        if (StringUtils.isNoneBlank(query.getOrganizationId())) {
            //包含子节点
            if (Boolean.TRUE.equals(query.getInclSubOrganization())) {
                builder.append(" AND FIND_IN_SET('").append(query.getOrganizationId()).append("', REPLACE(organization_.path_, '/', ','))> 0");
            }
            else {
                builder.append(" AND organization_.id_ = '").append(query.getOrganizationId()).append("'");
            }
        }
        //用户名条件
        if (StringUtils.isNoneBlank(query.getUsername())) {
            builder.append(" AND `user`.username_ LIKE '%").append(query.getUsername()).append("%'");
        }
        //姓名
        if (StringUtils.isNoneBlank(query.getFullName())) {
            builder.append(" AND `user`.full_name LIKE '%").append(query.getFullName()).append("%'");
        }
        //手机号条件
        if (StringUtils.isNoneBlank(query.getPhone())) {
            builder.append(" AND `user`.phone_ = '").append(query.getPhone()).append("'");
        }
        //邮箱地址条件
        if (StringUtils.isNoneBlank(query.getEmail())) {
            builder.append(" AND `user`.email_ LIKE '%").append(query.getEmail()).append("%'");
        }
        //状态条件
        if (!Objects.isNull(query.getStatus())) {
            builder.append(" AND `user`.status_ = '").append(query.getStatus().getCode()).append("'");
        }
        //数据来源
        if (!Objects.isNull(query.getDataOrigin())) {
            builder.append(" AND `user`.data_origin = '").append(query.getDataOrigin().getCode()).append("'");
        }
        builder.append(" GROUP BY `user`.id_");
        //@formatter:on
        String sql = builder.toString();
        List<UserPO> list = jdbcTemplate.query(
            builder.append(" LIMIT ").append(pageable.getPageNumber() * pageable.getPageSize())
                .append(",").append(pageable.getPageSize()).toString(),
            new UserPoMapper());
        //@formatter:off
        String countSql = "SELECT count(*) FROM (" + sql + ") user_";
        //@formatter:on
        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class);
        return new PageImpl<>(list, pageable, count);
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
        StringBuilder builder = new StringBuilder(
            """
                    SELECT
                                    	`user`.id_,
                                    	`user`.username_,
                                    	`user`.password_,
                                    	`user`.email_,
                                    	`user`.phone_,
                                    	`user`.phone_area_code,
                                    	`user`.full_name,
                                    	`user`.nick_name,
                                    	`user`.avatar_,
                                    	`user`.status_,
                                    	`user`.data_origin,
                                    	`user`.email_verified,
                                    	`user`.phone_verified,
                                    	`user`.shared_secret,
                                    	`user`.totp_bind,
                                    	`user`.auth_total,
                                    	`user`.last_auth_ip,
                                    	`user`.last_auth_time,
                                    	`user`.expand_,
                                    	`user`.external_id,
                                    	`user`.expire_date,
                                    	`user`.create_by,
                                    	`user`.create_time,
                                    	`user`.update_by,
                                    	`user`.update_time,
                                    	`user`.remark_,
                                    	group_concat( organization_.display_path ) AS org_display_path
                                    FROM `user`
                                    LEFT JOIN `organization_member` ON ( `user`.id_ = organization_member.user_id AND organization_member.is_deleted = '0' )
                                    LEFT JOIN `organization` organization_ ON ( organization_.id_ = organization_member.org_id AND organization_.is_deleted = '0' )
                                    WHERE
                                        user.is_deleted = 0 AND
                                    	user.id_ NOT IN (
                                    	SELECT
                                    		u.id_
                                    	FROM
                                    		user u
                                    		INNER JOIN user_group_member ugm ON ugm.user_id = u.id_
                                    		INNER JOIN user_group ug ON ug.id_ = ugm.group_id
                                    	WHERE
                                    	u.is_deleted = '0'
                                    	AND ug.id_ = '%s' AND ugm.group_id = '%s')
                    """.formatted(query.getId(), query.getId()));
        if (StringUtils.isNoneBlank(query.getKeyword())) {
            builder.append(" AND  user.username_ LIKE '%").append(query.getKeyword()).append("%'");
            builder.append(" OR  user.full_name LIKE '%").append(query.getKeyword()).append("%'");
            builder.append(" OR  user.phone_ = '").append(query.getKeyword()).append("'");
            builder.append(" OR  user.email_ = '").append(query.getKeyword()).append("'");
        }
        builder.append(" GROUP BY `user`.id_");
        //@formatter:on
        String sql = builder.toString();
        List<UserPO> list = jdbcTemplate.query(
            builder.append(" LIMIT ").append(pageable.getPageNumber() * pageable.getPageSize())
                .append(",").append(pageable.getPageSize()).toString(),
            new UserPoMapper());
        //@formatter:off
        String countSql = "SELECT COUNT(*) FROM(" + sql + ") user_";
        //@formatter:on
        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class);
        return new PageImpl<>(list, pageable, count);
    }

    /**
     * 根据组织ID查询用户列表
     *
     * @param organizationId {@link String}
     * @return {@link List}
     */
    @Override
    public List<UserEntity> findAllByOrgId(String organizationId) {
        return jdbcTemplate.query(
            "SELECT `user`.id_, `user`.username_, user.password_,`user`.email_, `user`.phone_,user.phone_area_code,  `user`.full_name ,`user`.nick_name ,`user`.avatar_ , `user`.status_, `user`.data_origin, `user`.email_verified, `user`.shared_secret, `user`.totp_bind , `user`.auth_total, `user`.last_auth_ip, `user`.last_auth_time, `user`.expand_, `user`.external_id , `user`.expire_date,`user`.identity_source_id, `user`.create_by, `user`.create_time, `user`.update_by, `user`.update_time , `user`.remark_ FROM `user` INNER JOIN `organization_member` ON `user`.id_ = organization_member.user_id INNER JOIN `organization` organization_ ON organization_.id_ = organization_member.org_id WHERE 1 = 1 AND organization_.id_ = '"
                                  + organizationId + "'" + "GROUP BY `user`.id_",
            new UserEntityMapper());
    }

    /**
     * 根据组织ID、数据来源查询用户列表
     *
     * @param organizationId {@link String}
     * @param identitySourceId {@link Long}
     * @return {@link List}
     */
    @Override
    public List<UserEntity> findAllByOrgIdAndIdentitySourceId(String organizationId,
                                                              Long identitySourceId) {
        return jdbcTemplate.query(
            "SELECT `user`.id_, `user`.username_, user.password_,`user`.email_, `user`.phone_,user.phone_area_code,  `user`.full_name ,`user`.nick_name ,`user`.avatar_ , `user`.status_, `user`.data_origin, `user`.email_verified, `user`.shared_secret, `user`.totp_bind , `user`.auth_total, `user`.last_auth_ip, `user`.last_auth_time, `user`.expand_, `user`.external_id , `user`.expire_date,`user`.identity_source_id , `user`.create_by, `user`.create_time, `user`.update_by, `user`.update_time , `user`.remark_ FROM `user` INNER JOIN `organization_member` ON `user`.id_ = organization_member.user_id INNER JOIN `organization` organization_ ON organization_.id_ = organization_member.org_id WHERE 1 = 1 AND organization_.id_ = '"
                                  + organizationId + "' AND user.identity_source_id = '"
                                  + identitySourceId + "' GROUP BY `user`.id_",
            new UserEntityMapper());
    }

    /**
     * 按组织外部 ID 和数据来源查找用户列表
     *
     * @param externalId {@link String}
     * @param identitySourceId {@link Long}
     * @return {@link List}
     */
    @Override
    public List<UserPO> findAllByOrgExternalIdAndIdentitySourceId(String externalId,
                                                                  Long identitySourceId) {
        return jdbcTemplate.query(
            "SELECT `user`.id_, `user`.username_, user.password_,`user`.email_, `user`.phone_,user.phone_area_code,  `user`.full_name ,`user`.nick_name ,`user`.avatar_ , `user`.status_, `user`.data_origin, `user`.email_verified,`user`.phone_verified, `user`.shared_secret, `user`.totp_bind , `user`.auth_total, `user`.last_auth_ip, `user`.last_auth_time, `user`.expand_, `user`.external_id , `user`.expire_date, `user`.create_by, `user`.create_time, `user`.update_by, `user`.update_time , `user`.remark_ FROM `user` INNER JOIN `organization_member` ON `user`.id_ = organization_member.user_id INNER JOIN `organization` organization_ ON organization_.id_ = organization_member.org_id WHERE 1 = 1 AND organization_.id_ = '"
                                  + externalId + "' AND user.identity_source_id = '"
                                  + identitySourceId + "' GROUP BY `user`.id_",
            new UserPoMapper());
    }

    @Override
    public List<UserEntity> findAllByOrgIdNotExistAndIdentitySourceId(Long identitySourceId) {
        return jdbcTemplate.query(
            "SELECT * FROM(SELECT `user`.id_, `user`.username_, user.password_, `user`.email_, `user`.phone_, `user`.phone_area_code, `user`.full_name, `user`.nick_name, `user`.avatar_, `user`.status_, `user`.data_origin, user.identity_source_id,`user`.email_verified, `user`.shared_secret, `user`.totp_bind, `user`.auth_total, `user`.last_auth_ip, `user`.last_auth_time, `user`.expand_, `user`.external_id, `user`.expire_date, `user`.create_by, `user`.create_time, `user`.update_by, `user`.update_time, `user`.remark_ FROM `user` LEFT JOIN `organization_member` ON `user`.id_ = organization_member.user_id LEFT JOIN `organization` organization_ ON organization_.id_ = organization_member.org_id WHERE `organization_member`.user_id IS NULL) user WHERE user.identity_source_id = '"
                                  + identitySourceId + "'" + "GROUP BY `user`.id_",
            new UserEntityMapper());
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
            "INSERT INTO user (id_, username_, password_, email_, phone_, phone_area_code, full_name,nick_name, avatar_, external_id, expire_date, status_, email_verified, phone_verified,shared_secret, auth_total,last_auth_ip,last_auth_time,expand_,data_origin,identity_source_id,totp_bind,last_update_password_time ,create_by,create_time,update_by,update_time,remark_) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ",
            new BatchPreparedStatementSetter() {

                @Override
                public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                    UserEntity entity = list.get(i);
                    ps.setLong(1, entity.getId());
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
                    ps.setString(15, entity.getSharedSecret());
                    ps.setLong(16, Objects.isNull(entity.getAuthTotal())?0L:entity.getAuthTotal());
                    ps.setString(17, entity.getLastAuthIp());
                    ps.setTimestamp(18, !Objects.isNull(entity.getLastAuthTime()) ? Timestamp.valueOf(entity.getLastAuthTime()) : null);
                    ps.setString(19, entity.getExpand());
                    ps.setString(20, entity.getDataOrigin().getCode());
                    ps.setObject(21, entity.getIdentitySourceId());
                    ps.setBoolean(22, !Objects.isNull(entity.getTotpBind()) && entity.getTotpBind());
                    ps.setTimestamp(23, !Objects.isNull(entity.getLastUpdatePasswordTime())?Timestamp.valueOf(entity.getLastUpdatePasswordTime()):null);
                    ps.setString(24, entity.getCreateBy());
                    ps.setTimestamp(25, Timestamp.valueOf(entity.getCreateTime()));
                    ps.setString(26, entity.getUpdateBy());
                    ps.setTimestamp(27, Timestamp.valueOf(entity.getUpdateTime()));
                    ps.setString(28, entity.getRemark());
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
            "UPDATE user SET  username_=?, password_=?, email_=?, phone_=?, phone_area_code=?,full_name=?,nick_name=?, avatar_=?, external_id=?, expire_date=?, status_=?, email_verified=?, phone_verified=?,shared_secret=?, auth_total=?,last_auth_ip=?,last_auth_time=?,expand_=?,data_origin=?,identity_source_id=?,totp_bind=?,last_update_password_time =?,create_by=?,create_time=?,update_by=?,update_time=?,remark_=? WHERE  id_=?",
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
                        ps.setString(14, entity.getSharedSecret());
                        ps.setLong(15, entity.getAuthTotal());
                        ps.setString(16, entity.getLastAuthIp());
                        ps.setTimestamp(17, !Objects.isNull(entity.getLastAuthTime()) ? Timestamp.valueOf(entity.getLastAuthTime()) : null);
                        ps.setString(18, entity.getExpand());
                        ps.setString(19, entity.getDataOrigin().getCode());
                        ps.setObject(20, entity.getIdentitySourceId());
                        ps.setBoolean(21, !Objects.isNull(entity.getTotpBind()) && entity.getTotpBind());
                        ps.setTimestamp(22, !Objects.isNull(entity.getLastUpdatePasswordTime())?Timestamp.valueOf(entity.getLastUpdatePasswordTime()):null);
                        ps.setString(23, entity.getCreateBy());
                        ps.setTimestamp(24, Timestamp.valueOf(entity.getCreateTime()));
                        ps.setString(25, entity.getUpdateBy());
                        ps.setTimestamp(26, Timestamp.valueOf(entity.getUpdateTime()));
                        ps.setString(27, entity.getRemark());
                        ps.setLong(28, entity.getId());
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
    private final JdbcTemplate jdbcTemplate;
}
