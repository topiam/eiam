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

import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.enums.app.AppProtocol;
import cn.topiam.employee.common.enums.app.AppType;
import cn.topiam.employee.common.enums.app.AuthorizationType;
import cn.topiam.employee.common.enums.app.InitLoginType;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/13 22:25
 */
public class AppEntityMapper implements RowMapper<AppEntity> {

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
    public AppEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AppEntity appEntity = new AppEntity();
        appEntity.setId(rs.getLong("id_"));
        appEntity.setCode(rs.getString("code_"));
        appEntity.setName(rs.getString("name_"));
        appEntity.setClientId(rs.getString("client_id"));
        appEntity.setClientSecret(rs.getString("client_secret"));
        appEntity.setTemplate(rs.getString("template_"));
        appEntity.setProtocol(AppProtocol.getType(rs.getString("protocol_")));
        appEntity.setType(AppType.getType(rs.getString("type_")));
        appEntity.setIcon(rs.getString("icon_"));
        appEntity.setInitLoginType(InitLoginType.getType(rs.getString("init_login_type")));
        appEntity.setInitLoginUrl(rs.getString("init_login_url"));
        appEntity
            .setAuthorizationType(AuthorizationType.getType(rs.getString("authorization_type")));
        appEntity.setEnabled(rs.getBoolean("is_enabled"));
        appEntity.setRemark(rs.getString("remark_"));
        return appEntity;
    }
}
