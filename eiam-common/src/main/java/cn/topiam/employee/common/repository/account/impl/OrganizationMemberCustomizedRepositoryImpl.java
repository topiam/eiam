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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.account.OrganizationMemberEntity;
import cn.topiam.employee.common.repository.account.OrganizationMemberCustomizedRepository;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/2 02:54
 */
@Repository
public class OrganizationMemberCustomizedRepositoryImpl implements
                                                        OrganizationMemberCustomizedRepository {

    @Override
    public void batchSave(List<OrganizationMemberEntity> list) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO organization_member (id_, org_id, user_id,create_by,create_time,update_by,update_time,remark_,is_deleted) VALUES (?,?,?,?,?,?,?,?,?)",
            new BatchPreparedStatementSetter() {

                @Override
                public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                    OrganizationMemberEntity entity = list.get(i);
                    ps.setLong(1, entity.getId());
                    ps.setString(2, entity.getOrgId());
                    ps.setLong(3, entity.getUserId());
                    ps.setString(4, entity.getCreateBy());
                    ps.setTimestamp(5, Timestamp.valueOf(entity.getCreateTime()));
                    ps.setString(6, entity.getUpdateBy());
                    ps.setTimestamp(7, Timestamp.valueOf(entity.getUpdateTime()));
                    ps.setString(8, entity.getRemark());
                    ps.setBoolean(9, false);
                }

                @Override
                public int getBatchSize() {
                    return list.size();
                }
            });
    }

    private final JdbcTemplate jdbcTemplate;

    public OrganizationMemberCustomizedRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
