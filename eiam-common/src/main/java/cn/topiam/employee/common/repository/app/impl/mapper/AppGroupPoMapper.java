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
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;

import cn.topiam.employee.common.entity.app.po.AppGroupPO;
import cn.topiam.employee.common.enums.app.AppGroupType;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/13 22:25
 */
public class AppGroupPoMapper implements RowMapper<AppGroupPO> {

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
    public AppGroupPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        AppGroupPO appGroup = new AppGroupPO();
        appGroup.setId(rs.getLong("id_"));
        appGroup.setName(rs.getString("name_"));
        appGroup.setCode(rs.getString("code_"));
        appGroup.setType(AppGroupType.getType(rs.getString("type_")));
        appGroup.setRemark(rs.getString("remark_"));
        appGroup.setAppCount(rs.getInt("app_count"));
        appGroup.setCreateTime(rs.getObject("create_time", LocalDateTime.class));
        return appGroup;
    }
}
