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
package cn.topiam.employee.common.repository.app.impl.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.common.entity.app.po.AppOidcConfigPO;
import cn.topiam.employee.common.enums.app.AuthorizationType;
import cn.topiam.employee.common.enums.app.InitLoginType;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/13 22:25
 */
@SuppressWarnings({ "DuplicatedCode", "unchecked" })
public class AppOidcConfigPoMapper implements RowMapper<AppOidcConfigPO> {

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
    public AppOidcConfigPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        //@formatter:off
        AppOidcConfigPO appAccount = new AppOidcConfigPO();
        appAccount.setAppId(rs.getLong("id_"));
        appAccount.setAppId(rs.getLong("app_id"));
        //应用表相关
        appAccount.setAppCode(rs.getString("code_"));
        appAccount.setAppTemplate(rs.getString("template_"));
        appAccount.setEnabled(rs.getBoolean("is_enabled"));
        appAccount.setClientId(rs.getString("client_id"));
        appAccount.setClientSecret(rs.getString("client_secret"));
        appAccount.setInitLoginType(InitLoginType.getType(rs.getString("init_login_type")));
        appAccount.setInitLoginUrl(rs.getString("init_login_url"));
        appAccount.setAuthorizationType(AuthorizationType.getType(rs.getString("authorization_type")));
        //配置相关
        appAccount.setClientAuthMethods(JSONObject.parseObject(rs.getString("client_auth_methods"), Set.class));
        appAccount.setAuthGrantTypes(JSONObject.parseObject(rs.getString("auth_grant_types"), Set.class));
        appAccount.setResponseTypes(JSONObject.parseObject(rs.getString("response_types"), Set.class));
        appAccount.setRedirectUris(JSONObject.parseObject(rs.getString("redirect_uris"), Set.class));
        appAccount.setGrantScopes(JSONObject.parseObject(rs.getString("grant_scopes"), Set.class));
        appAccount.setRequireAuthConsent(rs.getBoolean("require_auth_consent"));
        appAccount.setRequireProofKey(rs.getBoolean("require_proof_key"));
        appAccount.setTokenEndpointAuthSigningAlgorithm(
            rs.getString("token_endpoint_auth_signing_algorithm"));
        appAccount.setRefreshTokenTimeToLive(rs.getInt("refresh_token_time_to_live"));
        appAccount.setAccessTokenFormat(rs.getString("access_token_format"));
        appAccount.setAccessTokenTimeToLive(rs.getInt("access_token_time_to_live"));
        appAccount.setIdTokenTimeToLive(rs.getInt("id_token_time_to_live"));
        appAccount.setIdTokenSignatureAlgorithm(rs.getString("id_token_signature_algorithm"));
        appAccount.setReuseRefreshToken(rs.getBoolean("reuse_refresh_token"));
        //创建修改相关
        appAccount.setCreateBy(rs.getString("create_by"));
        appAccount.setCreateTime(rs.getObject("create_time", LocalDateTime.class));
        appAccount.setUpdateBy(rs.getString("update_by"));
        appAccount.setCreateTime(rs.getObject("update_time", LocalDateTime.class));
        appAccount.setRemark(rs.getString("remark_"));
        return appAccount;
        //@formatter:on
    }
}
