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
package cn.topiam.employee.common.repository.app.impl.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import cn.topiam.employee.common.entity.app.po.AppPermissionPolicyPO;
import cn.topiam.employee.common.enums.app.AppPolicyEffect;
import cn.topiam.employee.common.enums.app.AppPolicyObjectType;
import cn.topiam.employee.common.enums.app.AppPolicySubjectType;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/13 23:25
 */
public class AppPermissionPolicyPoMapper implements RowMapper<AppPermissionPolicyPO> {

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
    @SuppressWarnings("DuplicatedCode")
    @Override
    public AppPermissionPolicyPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        AppPermissionPolicyPO appPermissionPolicyPo = new AppPermissionPolicyPO();
        appPermissionPolicyPo.setId(rs.getLong("id_"));
        appPermissionPolicyPo.setEffect(AppPolicyEffect.getType(rs.getString("effect")));
        appPermissionPolicyPo.setSubjectId(rs.getString("subject_id"));
        appPermissionPolicyPo
            .setSubjectType(AppPolicySubjectType.getType(rs.getString("subject_type")));
        appPermissionPolicyPo.setSubjectName(rs.getString("subject_name"));
        appPermissionPolicyPo.setObjectId(rs.getLong("object_id"));
        appPermissionPolicyPo
            .setObjectType(AppPolicyObjectType.getType(rs.getString("object_type")));
        appPermissionPolicyPo.setObjectName(rs.getString("object_name"));
        return appPermissionPolicyPo;
    }
}
