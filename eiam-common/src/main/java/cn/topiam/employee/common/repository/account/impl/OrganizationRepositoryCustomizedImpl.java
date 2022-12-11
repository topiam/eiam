/*
 * eiam-common - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.account.OrganizationEntity;
import cn.topiam.employee.common.repository.account.OrganizationRepositoryCustomized;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/9/24 23:12
 */
@Repository
public class OrganizationRepositoryCustomizedImpl implements OrganizationRepositoryCustomized {

    /**
     * 批量保存
     *
     * @param list {@link List}
     */
    @Override
    public void batchSave(List<OrganizationEntity> list) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO organization (id_, code_, name_, parent_id, is_leaf, external_id, data_origin, type_, is_enabled, order_, path_, display_path, identity_source_id,create_by,create_time,update_by,update_time,remark_) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                    OrganizationEntity entity = list.get(i);
                    ps.setString(1, entity.getId());
                    ps.setString(2, entity.getCode());
                    ps.setString(3, entity.getName());
                    ps.setString(4, entity.getParentId());
                    ps.setBoolean(5, entity.getLeaf());
                    ps.setString(6, entity.getExternalId());
                    ps.setString(7, entity.getDataOrigin().getCode());
                    ps.setString(8, entity.getType().getCode());
                    ps.setBoolean(9, entity.getEnabled());
                    ps.setObject(10, !Objects.isNull(entity.getOrder()) ? entity.getOrder() : null);
                    ps.setString(11, entity.getPath());
                    ps.setString(12, entity.getDisplayPath());
                    ps.setLong(13, entity.getIdentitySourceId());
                    ps.setString(14, entity.getCreateBy());
                    ps.setTimestamp(15, Timestamp.valueOf(entity.getCreateTime()));
                    ps.setString(16, entity.getUpdateBy());
                    ps.setTimestamp(17, Timestamp.valueOf(entity.getUpdateTime()));
                    ps.setString(18, entity.getRemark());
                }

                @Override
                public int getBatchSize() {
                    return list.size();
                }
            });
    }

    /**
     * 批量更新
     *
     * @param list {@link List}
     */
    @Override
    public void batchUpdate(List<OrganizationEntity> list) {
        jdbcTemplate.batchUpdate(
            "UPDATE  organization SET code_=?, name_=?, parent_id=?, is_leaf=?, external_id=?, data_origin=?, type_=? ,is_enabled=?,order_=?, path_=?,display_path=?,identity_source_id=?,create_by=?,create_time=?,update_by=?,update_time=?,remark_=? WHERE id_=?",
            new BatchPreparedStatementSetter() {

                @Override
                public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                    OrganizationEntity entity = list.get(i);
                    ps.setString(1, entity.getCode());
                    ps.setString(2, entity.getName());
                    ps.setString(3, entity.getParentId());
                    ps.setBoolean(4, entity.getLeaf());
                    ps.setString(5, entity.getExternalId());
                    ps.setString(6, entity.getDataOrigin().getCode());
                    ps.setString(7, entity.getType().getCode());
                    ps.setBoolean(8, entity.getEnabled());
                    ps.setObject(9, Objects.isNull(entity.getOrder()) ? null : entity.getOrder());
                    ps.setString(10, entity.getPath());
                    ps.setString(11, entity.getDisplayPath());
                    ps.setLong(12, entity.getIdentitySourceId());
                    ps.setString(13, entity.getCreateBy());
                    ps.setTimestamp(14, Timestamp.valueOf(entity.getCreateTime()));
                    ps.setString(15, entity.getUpdateBy());
                    ps.setTimestamp(16, Timestamp.valueOf(entity.getUpdateTime()));
                    ps.setString(17, entity.getRemark());
                    ps.setString(18, entity.getId());
                }

                @Override
                public int getBatchSize() {
                    return list.size();
                }
            });
    }

    private final JdbcTemplate jdbcTemplate;

    public OrganizationRepositoryCustomizedImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
