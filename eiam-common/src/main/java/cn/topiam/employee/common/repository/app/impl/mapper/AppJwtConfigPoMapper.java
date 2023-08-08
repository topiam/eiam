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

import cn.topiam.employee.common.entity.app.po.AppJwtConfigPO;
import cn.topiam.employee.common.enums.app.AuthorizationType;
import cn.topiam.employee.common.enums.app.InitLoginType;
import cn.topiam.employee.common.enums.app.JwtBindingType;
import cn.topiam.employee.common.enums.app.JwtIdTokenSubjectType;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/02/12 22:58
 */
@SuppressWarnings("DuplicatedCode")
public class AppJwtConfigPoMapper implements RowMapper<AppJwtConfigPO> {

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
    public AppJwtConfigPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        //@formatter:off
        AppJwtConfigPO configPo = new AppJwtConfigPO();
        configPo.setAppId(rs.getLong("app_id"));
        //应用表相关
        configPo.setAppCode(rs.getString("code_"));
        configPo.setAppTemplate(rs.getString("template_"));
        configPo.setEnabled(rs.getBoolean("is_enabled"));
        configPo.setClientId(rs.getString("client_id"));
        configPo.setClientSecret(rs.getString("client_secret"));
        configPo.setInitLoginType(InitLoginType.getType(rs.getString("init_login_type")));
        configPo.setInitLoginUrl(rs.getString("init_login_url"));
        configPo.setAuthorizationType(AuthorizationType.getType(rs.getString("authorization_type")));
        //配置相关
        configPo.setRedirectUrl(rs.getString("redirect_url"));
        configPo.setTargetLinkUrl(rs.getString("target_link_url"));
        configPo.setBindingType(JwtBindingType.getType(rs.getString("binding_type")));
        configPo.setIdTokenTimeToLive(rs.getInt("id_token_time_to_live"));
        configPo.setIdTokenSubjectType(JwtIdTokenSubjectType.getType(rs.getString("id_token_subject_type")));
        //创建修改相关
        configPo.setCreateBy(rs.getString("create_by"));
        configPo.setCreateTime(rs.getObject("create_time", LocalDateTime.class));
        configPo.setUpdateBy(rs.getString("update_by"));
        configPo.setCreateTime(rs.getObject("update_time", LocalDateTime.class));
        configPo.setRemark(rs.getString("remark_"));
        return configPo;
        //@formatter:on
    }

}
