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
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.common.entity.app.AppSaml2ConfigEntity;
import cn.topiam.employee.common.entity.app.po.AppSaml2ConfigPO;
import cn.topiam.employee.common.enums.app.*;
import cn.topiam.employee.support.exception.TopIamException;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/13 22:25
 */
@SuppressWarnings("DuplicatedCode")
public class AppSaml2ConfigPoMapper implements RowMapper<AppSaml2ConfigPO> {

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
    public AppSaml2ConfigPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        AppSaml2ConfigPO appAccount = new AppSaml2ConfigPO();
        appAccount.setAppId(rs.getLong("id_"));
        appAccount.setAppId(rs.getLong("app_id"));
        appAccount.setAppCode(rs.getString("code_"));
        appAccount.setClientId(rs.getString("client_id"));
        appAccount.setClientSecret(rs.getString("client_secret"));
        appAccount.setSpMetadata(rs.getString("sp_metadata"));
        appAccount.setSpEntityId(rs.getString("sp_entity_id"));
        appAccount.setSpAcsUrl(rs.getString("sp_acs_url"));
        appAccount.setRecipient(rs.getString("recipient_"));
        appAccount.setAudience(rs.getString("audience_"));
        appAccount.setSpSloUrl(rs.getString("sp_slo_url"));
        appAccount.setSpRequestsSigned(rs.getBoolean("sp_requests_signed"));
        appAccount.setSpSignCert(rs.getString("sp_sign_cert"));
        appAccount.setAcsBinding(rs.getString("acs_binding"));
        appAccount.setSlsBinding(rs.getString("sls_binding"));
        appAccount.setNameIdFormat(SamlNameIdFormatType.getType(rs.getString("nameid_format")));
        appAccount
            .setNameIdValueType(SamlNameIdValueType.getType(rs.getString("nameid_value_type")));
        //加密断言
        appAccount.setAssertSigned(rs.getBoolean("assert_signed"));
        appAccount.setAssertSignAlgorithm(
            SamlSignAssertAlgorithmType.getType(rs.getString("assert_sign_algorithm")));
        //签名断言
        appAccount.setAssertEncrypted(rs.getBoolean("assert_encrypted"));
        appAccount.setAssertEncryptAlgorithm(
            SamlEncryptAssertAlgorithmType.getType(rs.getString("assert_encrypt_algorithm")));
        //响应签名
        appAccount.setResponseSigned(rs.getBoolean("response_signed"));
        appAccount.setResponseSignAlgorithm(
            SamlSignAssertAlgorithmType.getType(rs.getString("response_sign_algorithm")));

        appAccount.setAuthnContextClassRef(
            AuthnContextClassRefType.getType(rs.getString("authn_context_classref")));
        appAccount.setRelayState(rs.getString("relay_state"));
        appAccount.setInitLoginType(InitLoginType.getType(rs.getString("init_login_type")));
        appAccount.setInitLoginUrl(rs.getString("init_login_url"));
        appAccount.setAppTemplate(rs.getString("template_"));
        appAccount
            .setAuthorizationType(AuthorizationType.getType(rs.getString("authorization_type")));
        try {
            //属性语句
            String attributeStatements = rs.getString("attribute_statements");
            if (StringUtils.isNotBlank(attributeStatements)) {
                JavaType attributeStatementType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class,
                        AppSaml2ConfigEntity.AttributeStatement.class);
                List<AppSaml2ConfigEntity.AttributeStatement> attributeStatement = objectMapper
                    .readValue(rs.getString("attribute_statements"), attributeStatementType);
                appAccount.setAttributeStatements(attributeStatement);
            }

            //额外参数
            String additionalConfig = rs.getString("additional_config");
            if (StringUtils.isNotBlank(additionalConfig)) {
                appAccount.setAdditionalConfig(objectMapper
                    .readValue(rs.getString("additional_config"), new TypeReference<>() {
                    }));
            }
        } catch (Exception e) {
            throw new TopIamException(e.getMessage());
        }
        appAccount.setCreateBy(rs.getString("create_by"));
        appAccount.setCreateTime(rs.getObject("create_time", LocalDateTime.class));
        appAccount.setUpdateBy(rs.getString("update_by"));
        appAccount.setCreateTime(rs.getObject("update_time", LocalDateTime.class));
        appAccount.setRemark(rs.getString("remark_"));
        return appAccount;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
}
