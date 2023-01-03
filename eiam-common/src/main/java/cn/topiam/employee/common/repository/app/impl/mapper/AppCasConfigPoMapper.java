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
package cn.topiam.employee.common.repository.app.impl.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;

import cn.topiam.employee.common.entity.app.po.AppCasConfigPO;
import cn.topiam.employee.common.enums.app.InitLoginType;

/**
 * AppCasConfigPOPOMapper
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 16:31
 */
public class AppCasConfigPoMapper implements RowMapper<AppCasConfigPO> {

    @Override
    public AppCasConfigPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        AppCasConfigPO configPO = new AppCasConfigPO();
        configPO.setAppId(rs.getLong("id_"));
        configPO.setAppId(rs.getLong("app_id"));
        configPO.setAppCode(rs.getString("code_"));
        configPO.setClientId(rs.getString("client_id"));
        configPO.setClientSecret(rs.getString("client_secret"));
        configPO.setInitLoginType(InitLoginType.getType(rs.getString("init_login_type")));
        configPO.setInitLoginUrl(rs.getString("init_login_url"));
        configPO.setAppTemplate(rs.getString("template_"));
        configPO.setCreateBy(rs.getString("create_by"));
        configPO.setCreateTime(rs.getObject("create_time", LocalDateTime.class));
        configPO.setUpdateBy(rs.getString("update_by"));
        configPO.setCreateTime(rs.getObject("update_time", LocalDateTime.class));
        configPO.setRemark(rs.getString("remark_"));
        configPO.setSpCallbackUrl(rs.getString("sp_callback_url"));
        return configPO;
    }
}
