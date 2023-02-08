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
package cn.topiam.employee.application.saml2.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import org.opensaml.security.credential.Credential;

import cn.topiam.employee.common.enums.app.*;

import lombok.Builder;
import lombok.Data;

/**
 * Saml2 标准模型
 *
 * 只要应用为SAML2协议，经过适配器处理后，输出统一Modal模型，供协议使用
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/20 00:50
 */
@Data
@Builder
public class Saml2SsoModel implements Serializable {

    /**
     * APP ID
     */
    private String                         appId;

    /**
     * APP Code
     */
    private String                         appCode;

    /**
     * SpEntityId
     */
    private String                         spEntityId;

    /**
     * SP 单点登录 ACS URL
     */
    private String                         spAcsUrl;

    /**
     * SP 单点登出 URL
     */
    private String                         spSlsUrl;

    /**
     * 是否对 SAML Request 签名进行验证 ，用来对SAML Request签名进行验证，对应SP元数据文件中“AuthnRequestsSigned”值
     */
    private Boolean                        spRequestsSigned;

    /**
     * SP公钥证书，用来验证SAML request的签名，对应SP元数据文件中 use='signing' 证书内容
     */
    private String                         spSignCert;

    /**
     * 单点登录 ACS BINDING
     */
    private String                         acsBinding;

    /**
     * 单点登出 SLS BINDING
     */
    private String                         slsBinding;

    /**
     * 接受者
     */
    private String                         recipient;

    /**
     * 允许使用SAML断言的资源，默认和SP Entity ID相同。
     */
    private String                         audience;

    /**
     * SAML Response 中指定账户标识 NameID 字段格式。一般无需修改。
     */
    private SamlNameIdFormatType           nameIdFormat;

    /**
     * NameId
     */
    private String                         nameIdValue;

    /**
     * 签名断言
     */
    private Boolean                        assertSigned;

    /**
     * 断言签名使用的非对称算法
     */
    private SamlSignAssertAlgorithmType    assertSignAlgorithm;

    /**
     * 加密断言
     */
    private Boolean                        assertEncrypted;

    /**
     * 断言加密使用的非对称算法
     */
    private SamlEncryptAssertAlgorithmType assertEncryptAlgorithm;

    /**
     * 响应是否加密
     */
    private Boolean                        responseSigned;

    /**
     * 响应签名使用的非对称算法
     */
    private SamlSignAssertAlgorithmType    responseSignAlgorithm;

    /**
     * SAML 身份认证上下文
     */
    private AuthnContextClassRefType       authnContextClassRef;
    /**
     * IDP 发起 SSO 登录成功后，应用应自动跳转的地址。在 SAML Response 中会在 RelayState 参数中传递，应用读取后实现跳转。
     */
    private String                         relayState;

    /**
     * 在 SAML Response 中，可以将额外用户字段（例如邮箱、显示名等）返回给应用解析。
     */
    private List<AttributeStatement>       attributeStatements;

    /**
     * IDP 签名证书
     */
    private Credential                     idpSignCert;
    /**
     * IDP 加密证书
     */
    private Credential                     idpEncryptCert;

    @Data
    public static class AttributeStatement implements Serializable {

        @Serial
        private static final long   serialVersionUID = -7186912435615904210L;
        /**
         * key
         */
        private String              key;
        /**
         * nameFormat
         */
        private AttributeNameFormat nameFormat;
        /**
         * value
         */
        private String              value;
    }
}
