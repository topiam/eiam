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
package cn.topiam.employee.common.entity.app;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

import cn.topiam.employee.common.enums.app.*;
import cn.topiam.employee.support.repository.domain.LogicDeleteEntity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_WHERE;

/**
 * APP SAML 配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/22 22:31
 */
@Getter
@Setter
@ToString
@Entity
@Accessors(chain = true)
@Table(name = "app_saml2_config")
@SQLDelete(sql = "update app_saml2_config set " + SOFT_DELETE_SET + " where id_ = ?")
@SQLDeleteAll(sql = "update app_saml2_config set " + SOFT_DELETE_SET + " where id_ = ?")
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Where(clause = SOFT_DELETE_WHERE)
public class AppSaml2ConfigEntity extends LogicDeleteEntity<Long> {
    /**
     * APP ID
     */
    @Column(name = "app_id")
    private Long                           appId;

    /**
     * SP 元数据
     */
    @Column(name = "sp_metadata")
    private String                         spMetadata;

    /**
     * SpEntityId
     */
    @Column(name = "sp_entity_id")
    private String                         spEntityId;

    /**
     * SP 单点登录 ACS URL
     */
    @Column(name = "sp_acs_url")
    private String                         spAcsUrl;

    /**
     * SP 单点登出 URL
     */
    @Column(name = "sp_slo_url")
    private String                         spSloUrl;

    /**
     * 是否对 SAML Request 签名进行验证 ，用来对SAML Request签名进行验证，对应SP元数据文件中“AuthnRequestsSigned”值
     */
    @Column(name = "sp_requests_signed")
    private Boolean                        spRequestsSigned;

    /**
     * SP公钥证书，用来验证SAML request的签名，对应SP元数据文件中 use='signing' 证书内容
     */
    @Column(name = "sp_sign_cert")
    private String                         spSignCert;

    /**
     * Recipient（收件人）
     *
     * Recipient 是指定 TopIAM 可以向其呈现断言的实体或位置的URI。
     * 此属性可以指定应将断言传递到的收件人端点。此属性有助于防止中介机构将断言重定向到其他端点。
     * 默认情况下，TopIAM 发送ACS URL作为收件人值。
     */
    @Column(name = "recipient_")
    private String                         recipient;

    /**
     * 目标受众
     *
     *  指定此SAML断言的目标受众，默认和SP Entity ID相同。
     *  通常是 URL，但在技术上可以格式化为任何数据字符串
     */
    @Column(name = "audience_")
    private String                         audience;

    /**
     * 单点登录 ACS BINDING
     */
    @Column(name = "acs_binding")
    private String                         acsBinding;

    /**
     * 单点登出 SLS BINDING
     */
    @Column(name = "sls_binding")
    private String                         slsBinding;

    /**
     * SAML Response 中指定账户标识 NameID 字段格式。一般无需修改。
     */
    @Column(name = "nameid_format")
    private SamlNameIdFormatType           nameIdFormat;

    /**
     * NameId
     */
    @Column(name = "nameid_value_type")
    private SamlNameIdValueType            nameIdValueType;

    /**
     * 签名断言
     */
    @Column(name = "assert_signed")
    private Boolean                        assertSigned;

    /**
     * 断言签名使用的非对称算法
     */
    @Column(name = "assert_sign_algorithm")
    private SamlSignAssertAlgorithmType    assertSignAlgorithm;

    /**
     * 加密断言
     */
    @Column(name = "assert_encrypted")
    private Boolean                        assertEncrypted;

    /**
     * 断言加密使用的非对称算法
     */
    @Column(name = "assert_encrypt_algorithm")
    private SamlEncryptAssertAlgorithmType assertEncryptAlgorithm;

    /**
     * 响应是否加密
     */
    @Column(name = "response_signed")
    private Boolean                        responseSigned;

    /**
     * 响应签名使用的非对称算法
     */
    @Column(name = "response_sign_algorithm")
    private SamlSignAssertAlgorithmType    responseSignAlgorithm;

    /**
     * SAML 身份认证上下文
     */
    @Column(name = "authn_context_classref")
    private AuthnContextClassRefType       authnContextClassRef;

    /**
     * IDP 发起 SSO 登录成功后，应用应自动跳转的地址。在 SAML Response 中会在 RelayState 参数中传递，应用读取后实现跳转。
     */
    @Column(name = "relay_state")
    private String                         relayState;

    /**
     * 在 SAML Response 中，可以将额外用户字段（例如邮箱、显示名等）返回给应用解析。
     */
    @Type(type = "json")
    @Column(name = "attribute_statements")
    private List<AttributeStatement>       attributeStatements;

    /**
     * 模版配置
     */
    @Type(type = "json")
    @Column(name = "additional_config")
    private Map<String, Object>            additionalConfig;

    @Data
    public static class AttributeStatement implements Serializable {

        @Serial
        private static final long   serialVersionUID = -7186912435615904210L;

        /**
         * key
         */
        private String              key;

        /**
         * 名称
         */
        private String              name;

        /**
         * nameFormat
         */
        private AttributeNameFormat nameFormat;

        /**
         * value
         */
        @JsonAlias({ "value", "valueExpression" })
        private String              valueExpression;

        public AttributeStatement() {
        }

        public AttributeStatement(String name, String valueExpression) {
            this.name = name;
            this.valueExpression = valueExpression;
            this.nameFormat = AttributeNameFormat.UNSPECIFIED;
        }

        public AttributeStatement(String name, AttributeNameFormat nameFormat,
                                  String valueExpression) {
            this.name = name;
            this.valueExpression = valueExpression;
            this.nameFormat = nameFormat;
        }
    }
}
