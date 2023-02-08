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

import com.alibaba.fastjson2.JSON;

import cn.topiam.employee.common.entity.app.AppTsaConfigEntity;
import cn.topiam.employee.common.entity.app.po.AppTsaConfigPO;
import cn.topiam.employee.common.enums.app.AuthorizationType;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/01/14 10:58
 */
@SuppressWarnings("DuplicatedCode")
public class AppTsaConfigPoMapper implements RowMapper<AppTsaConfigPO> {

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
    public AppTsaConfigPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        //@formatter:off
        AppTsaConfigPO appForm = new AppTsaConfigPO();
        appForm.setAppId(rs.getLong("app_id"));
        //应用表相关
        appForm.setAppCode(rs.getString("code_"));
        appForm.setAppTemplate(rs.getString("template_"));
        appForm.setEnabled(rs.getBoolean("is_enabled"));
        appForm.setClientId(rs.getString("client_id"));
        appForm.setClientSecret(rs.getString("client_secret"));
//        appForm.setInitLoginType(InitLoginType.getType(rs.getString("init_login_type")));
        appForm.setInitLoginUrl(rs.getString("init_login_url"));
        appForm.setAuthorizationType(AuthorizationType.getType(rs.getString("authorization_type")));
        //配置相关
        appForm.setLoginPage(rs.getString("login_page"));
        appForm.setAutoLoginSteps(JSON.parseArray(rs.getString("auto_login_steps"))
                .toList(AppTsaConfigEntity.AutoLoginStep.class));
        appForm.setCreateAccountSteps(JSON.parseArray(rs.getString("create_account_stepss"))
                .toList(AppTsaConfigEntity.CreateAccountStep.class));
        //创建修改相关
        appForm.setCreateBy(rs.getString("create_by"));
        appForm.setCreateTime(rs.getObject("create_time", LocalDateTime.class));
        appForm.setUpdateBy(rs.getString("update_by"));
        appForm.setCreateTime(rs.getObject("update_time", LocalDateTime.class));
        appForm.setRemark(rs.getString("remark_"));
        return appForm;
        //@formatter:on
    }

}
