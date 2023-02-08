/*
 * eiam-application-saml2 - Employee Identity and Access Management Program
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
package cn.topiam.employee.application.saml2.pojo;

import java.util.List;
import java.util.Map;

import cn.topiam.employee.common.entity.app.AppSaml2ConfigEntity;
import cn.topiam.employee.common.enums.app.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Saml2 配置返回
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/31 22:46
 */
@Data
@Schema(description = "Saml2 配置返回结果")
public class AppSaml2StandardConfigGetResult {
    /**
     * SSO 发起方
     */
    @Parameter(description = "SSO 发起方")
    private InitLoginType                                 initLoginType;

    /**
     * SSO 登录链接
     */
    @Parameter(description = "SSO 登录链接")
    private String                                        initLoginUrl;

    /**
     * 授权范围
     */
    @Parameter(description = "SSO 授权范围")
    private AuthorizationType                             authorizationType;
    /**
     * SpEntityId
     */
    @Parameter(description = "SP Entity ID")
    private String                                        spEntityId;

    /**
     * SP 单点登录 ACS URL
     */
    @Parameter(description = "SP 单点登录 ACS URL")
    private String                                        spAcsUrl;

    /**
     * 允许使用SAML断言的资源，默认和SP Entity ID相同。
     */
    @Parameter(description = "Audience")
    private String                                        spAudience;

    /**
     * 是否对 SAML Request 签名进行验证 ，用来对SAML Request签名进行验证，对应SP元数据文件中“AuthnRequestsSigned”值
     */
    @Parameter(description = "是否对 SAML Request 签名进行验证")
    private Boolean                                       spRequestsSigned;

    /**
     * SP公钥证书，用来验证SAML request的签名，对应SP元数据文件中 use='signing' 证书内容
     */
    @Parameter(description = "SP公钥证书，用来验证SAML request的签名request的签名")
    private String                                        spSignCert;

    /**
     * 单点登录 ACS BINDING
     */
    @Parameter(name = "单点登录 ACS BINDING")
    private String                                        acsBinding;

    /**
     * NameId 值类型
     */
    @Parameter(description = "NameIdType")
    private SamlNameIdValueType                           nameIdValueType;

    /**
     * SAML Response 中指定账户标识 NameID 字段格式。一般无需修改。
     */
    @Parameter(description = "NameIdFormat")
    private SamlNameIdFormatType                          nameIdFormat;

    /**
     * 是否对断言签名
     */
    @Parameter(description = "是否对断言签名")
    private Boolean                                       assertSigned;

    /**
     * 断言签名使用的非对称算法
     */
    @Parameter(description = "断言签名使用的非对称算法")
    private SamlSignAssertAlgorithmType                   assertSignAlgorithm;

    /**
     * 是否对断言加密
     */
    @Parameter(description = "是否对断言加密")
    private Boolean                                       assertEncrypted;

    /**
     * 加密使用的非对称算法
     */
    @Parameter(description = "加密使用的非对称算法")
    private SamlEncryptAssertAlgorithmType                assertEncryptAlgorithm;

    /**
     * 响应是否加密
     */
    @Parameter(description = "响应是否加密")
    private Boolean                                       responseSigned;

    /**
     * 响应签名使用的非对称算法
     */
    @Parameter(description = "响应签名使用的非对称算法")
    private SamlSignAssertAlgorithmType                   responseSignAlgorithm;

    /**
     * SAML 身份认证上下文
     */
    @Parameter(description = "AuthnContextClassRef")
    private AuthnContextClassRefType                      authnContextClassRef;

    /**
     * IDP 发起 SSO 登录成功后，应用应自动跳转的地址。在 SAML Response 中会在 RelayState 参数中传递，应用读取后实现跳转。
     */
    @Parameter(description = "RelayState")
    private String                                        relayState;

    /**
     * 在 SAML Response 中，可以将额外用户字段（例如邮箱、显示名等）返回给应用解析。
     */
    @Parameter(description = "AttributeStatements")
    private List<AppSaml2ConfigEntity.AttributeStatement> attributeStatements;

    /**
     * 协议端点域
     */
    @Parameter(description = "协议端点域")
    private AppSaml2ProtocolEndpoint                      protocolEndpoint;

    /**
     * 模版配置
     */
    private Map<String, Object>                           additionalConfig;

}
