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
package cn.topiam.employee.common.repository.identitysource.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.identitysource.IdentitySourceEventRecordEntity;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceEventRecordRepositoryCustomized;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/10 23:33
 */
@SuppressWarnings("DuplicatedCode")
@Repository
public class IdentitySourceEventRecordRepositoryCustomizedImpl implements
                                                               IdentitySourceEventRecordRepositoryCustomized {

    @Override
    public void batchSave(List<IdentitySourceEventRecordEntity> list) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO identity_source_event_record (id_, identity_source_id, action_type, object_id, object_name, object_type, status_,event_time,desc_,create_by,create_time,update_by,update_time,remark_,is_deleted) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
            //@formatter:off
                        IdentitySourceEventRecordEntity entity = list.get(i);
                        ps.setLong(1, entity.getId());
                        ps.setLong(2, entity.getIdentitySourceId());
                        ps.setString(3, Objects.isNull(entity.getActionType()) ? null : entity.getActionType().getCode());
                        ps.setString(4, entity.getObjectId());
                        ps.setString(5, entity.getObjectName());
                        ps.setString(6, Objects.isNull(entity.getObjectType()) ? null : entity.getObjectType().getCode());
                        ps.setString(7, Objects.isNull(entity.getStatus()) ? null : entity.getStatus().getCode());
                        ps.setTimestamp(8, Timestamp.valueOf(entity.getEventTime()));
                        ps.setString(9, entity.getDesc());
                        ps.setString(10, entity.getCreateBy());
                        ps.setTimestamp(11, Timestamp.valueOf(entity.getCreateTime()));
                        ps.setString(12, entity.getUpdateBy());
                        ps.setTimestamp(13, Timestamp.valueOf(entity.getUpdateTime()));
                        ps.setString(14, entity.getRemark());
                        ps.setBoolean(15, false);
                        //@formatter:on
                }

                @Override
                public int getBatchSize() {
                    return list.size();
                }
            });
    }

    private final JdbcTemplate jdbcTemplate;

    public IdentitySourceEventRecordRepositoryCustomizedImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
