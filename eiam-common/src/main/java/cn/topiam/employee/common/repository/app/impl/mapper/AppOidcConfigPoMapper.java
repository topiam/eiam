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
        AppOidcConfigPO appOidc = new AppOidcConfigPO();
        appOidc.setAppId(rs.getLong("id_"));
        appOidc.setAppId(rs.getLong("app_id"));
        //应用表相关
        appOidc.setAppCode(rs.getString("code_"));
        appOidc.setAppTemplate(rs.getString("template_"));
        appOidc.setEnabled(rs.getBoolean("is_enabled"));
        appOidc.setClientId(rs.getString("client_id"));
        appOidc.setClientSecret(rs.getString("client_secret"));
        appOidc.setInitLoginType(InitLoginType.getType(rs.getString("init_login_type")));
        appOidc.setInitLoginUrl(rs.getString("init_login_url"));
        appOidc.setAuthorizationType(AuthorizationType.getType(rs.getString("authorization_type")));
        //配置相关
        appOidc.setClientAuthMethods(JSONObject.parseObject(rs.getString("client_auth_methods"), Set.class));
        appOidc.setAuthGrantTypes(JSONObject.parseObject(rs.getString("auth_grant_types"), Set.class));
        appOidc.setResponseTypes(JSONObject.parseObject(rs.getString("response_types"), Set.class));
        appOidc.setRedirectUris(JSONObject.parseObject(rs.getString("redirect_uris"), Set.class));
        appOidc.setGrantScopes(JSONObject.parseObject(rs.getString("grant_scopes"), Set.class));
        appOidc.setRequireAuthConsent(rs.getBoolean("require_auth_consent"));
        appOidc.setRequireProofKey(rs.getBoolean("require_proof_key"));
        appOidc.setTokenEndpointAuthSigningAlgorithm(
            rs.getString("token_endpoint_auth_signing_algorithm"));
        appOidc.setRefreshTokenTimeToLive(rs.getInt("refresh_token_time_to_live"));
        appOidc.setAccessTokenFormat(rs.getString("access_token_format"));
        appOidc.setAccessTokenTimeToLive(rs.getInt("access_token_time_to_live"));
        appOidc.setIdTokenTimeToLive(rs.getInt("id_token_time_to_live"));
        appOidc.setIdTokenSignatureAlgorithm(rs.getString("id_token_signature_algorithm"));
        appOidc.setReuseRefreshToken(rs.getBoolean("reuse_refresh_token"));
        //创建修改相关
        appOidc.setCreateBy(rs.getString("create_by"));
        appOidc.setCreateTime(rs.getObject("create_time", LocalDateTime.class));
        appOidc.setUpdateBy(rs.getString("update_by"));
        appOidc.setCreateTime(rs.getObject("update_time", LocalDateTime.class));
        appOidc.setRemark(rs.getString("remark_"));
        return appOidc;
        //@formatter:on
    }
}
