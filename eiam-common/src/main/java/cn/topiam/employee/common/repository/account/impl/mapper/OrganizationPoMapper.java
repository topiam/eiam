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
package cn.topiam.employee.common.repository.account.impl.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import cn.topiam.employee.common.entity.account.po.OrganizationPO;
import cn.topiam.employee.common.enums.DataOrigin;
import cn.topiam.employee.common.enums.account.OrganizationType;

import lombok.extern.slf4j.Slf4j;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/5/20 22:25
 */
@Slf4j
public class OrganizationPoMapper implements RowMapper<OrganizationPO> {
    /**
     * Implementations must implement this method to map each row of data
     * in the ResultSet. This method should not call {@code next()} on
     * the ResultSet; it is only supposed to map values of the current row.
     *
     * @param rs     the ResultSet to map (pre-initialized for the current row)
     * @param rowNum the number of the current row
     * @return the result object for the current row (may be {@code null})
     * @throws SQLException if an SQLException is encountered getting
     *                      column values (that is, there's no need to catch SQLException)
     */
    @Override
    public OrganizationPO mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        //@formatter:off
        OrganizationPO organization = new OrganizationPO();
        organization.setId(rs.getString("id_"));
        organization.setCode(rs.getString("code_"));
        organization.setDisplayPath(rs.getString("display_path"));
        organization.setName(rs.getString("name_"));
        organization.setPath(rs.getString("path_"));
        organization.setOrder(rs.getString("order_"));
        organization.setLeaf(rs.getBoolean("is_leaf"));
        organization.setEnabled(rs.getBoolean("is_enabled"));
        organization.setParentId(rs.getString("parent_id"));
        organization.setDataOrigin(DataOrigin.getType(rs.getString("data_origin")));
        organization.setExternalId(rs.getString("external_id"));
        organization.setIdentitySourceId(rs.getLong("identity_source_id"));
        organization.setType(OrganizationType.getType(rs.getString("type_")));
        organization.setRemark(rs.getString("remark_"));
        return organization;
        //@formatter:on
    }
}
