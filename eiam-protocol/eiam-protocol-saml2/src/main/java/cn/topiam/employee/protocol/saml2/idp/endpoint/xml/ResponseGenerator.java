/*
 * eiam-protocol-saml2 - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.saml2.idp.endpoint.xml;

import java.time.Instant;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.impl.ResponseBuilder;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.SignatureBuilder;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;

import cn.topiam.employee.application.saml2.model.Saml2SsoModel;
import cn.topiam.employee.common.enums.app.AuthnContextClassRefType;
import cn.topiam.employee.common.enums.app.SamlEncryptAssertAlgorithmType;
import cn.topiam.employee.common.enums.app.SamlNameIdFormatType;
import cn.topiam.employee.common.enums.app.SamlSignAssertAlgorithmType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import static cn.topiam.employee.common.util.SamlUtils.generateSecureRandomId;

/**
 * ResponseGenerator
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/1 22:48
 */
@SuppressWarnings("DuplicatedCode")
@RequiredArgsConstructor
@Getter
public class ResponseGenerator {

    /**
     * Issuer
     */
    private final String                                 issuerName;

    /**
     * 断言接收地址
     */
    private final String                                 acsUrl;

    /**
     * Recipient（收件人）
     * Recipient 是指定 TopIAM 可以向其呈现断言的实体或位置的URI。
     * 此属性可以指定应将断言传递到的收件人端点。此属性有助于防止中介机构将断言重定向到其他端点。
     * 默认情况下，TopIAM 发送ACS URL作为收件人值。
     */
    private final String                                 recipient;

    /**
     * 目标受众
     *  指定此SAML断言的目标受众，默认和SP Entity ID相同。
     *  通常是 URL，但在技术上可以格式化为任何数据字符串
     */
    private final String                                 audienceUri;

    /**
     * nameIdValue
     */
    private final String                                 nameIdValue;

    /**
     * nameIdFormatType
     */
    private final SamlNameIdFormatType                   nameIdFormatType;

    /**
     * 是否签名响应
     */
    private final Boolean                                signed;

    /**
     * 响应签名使用的非对称算法
     */
    private final SamlSignAssertAlgorithmType            signAlgorithm;

    /**
     * 是否签名断言
     */
    private final Boolean                                assertSigned;

    /**
     * 断言签名使用的非对称算法
     */
    private final SamlSignAssertAlgorithmType            assertSignAlgorithm;

    /**
     * 是否加密断言
     */
    private final Boolean                                assertEncrypted;

    /**
     * 断言加密使用的非对称算法
     */
    private final SamlEncryptAssertAlgorithmType         assertEncryptAlgorithm;

    /**
     * 签名证书
     */
    private final Credential                             signCredential;

    /**
     * 加密证书
     */
    private final Credential                             encryptionCredential;

    private final AuthnContextClassRefType               authnContextClassRefType;

    /**
     * 断言属性
     */
    private final List<Saml2SsoModel.AttributeStatement> attributeStatements;

    /**
     * 认证请求ID
     */
    @Setter
    @Getter
    private String                                       authnRequestId;

    /**
     * 生成 Response
     *
     * @return {@link Response}
     */
    public Response generateResponse() {
        Response response = new ResponseBuilder().buildObject();
        //SP断言地址
        response.setDestination(getAcsUrl());
        response.setID(generateSecureRandomId());
        //Issuer
        response.setIssuer(new IssuerGenerator(getIssuerName()).generateIssuer());
        response.setIssueInstant(Instant.now());
        //状态
        response.setStatus(new StatusGenerator().generateStatus(StatusCode.SUCCESS));
        //版本
        response.setVersion(SAMLVersion.VERSION_20);
        if (StringUtils.isNotBlank(authnRequestId)) {
            response.setInResponseTo(getAuthnRequestId());
        }
        //构建断言
        AssertionGenerator assertionGenerator = new AssertionGenerator(getIssuerName(),
            getAssertSigned(), getAssertSignAlgorithm(), getSignCredential(), getAssertEncrypted(),
            getAssertEncryptAlgorithm(), getEncryptionCredential(), getRecipient(),
            getAudienceUri(), getNameIdFormatType(), getNameIdValue(),
            getAuthnContextClassRefType(), getAttributeStatements());
        assertionGenerator.setAuthnRequestId(authnRequestId);
        //生成断言
        Assertion assertion = assertionGenerator.generateAssertion();
        response.getAssertions().add(assertion);
        //加密断言
        if (getAssertEncrypted()) {
            EncryptedAssertion encryptedAssertion = assertionGenerator
                .encryptedAssertion(assertion);
            response.getEncryptedAssertions().add(encryptedAssertion);
        }
        //签名响应
        if (getSigned()) {
            Signature signature = new SignatureBuilder().buildObject();
            signature.setSigningCredential(getSignCredential());
            signature.setSignatureAlgorithm(getSignAlgorithm().getValue());
            signature
                .setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
            response.setSignature(signature);
            try {
                XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(response)
                    .marshall(response);
            } catch (MarshallingException e) {
                throw new RuntimeException(e);
            }
            try {
                Signer.signObject(signature);
            } catch (SignatureException e) {
                throw new RuntimeException(e);
            }
        }

        return response;
    }

}
