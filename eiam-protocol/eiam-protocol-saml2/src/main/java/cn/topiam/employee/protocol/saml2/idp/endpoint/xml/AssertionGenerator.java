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
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.impl.AssertionBuilder;
import org.opensaml.saml.saml2.core.impl.NameIDBuilder;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.SignatureBuilder;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

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
 * 断言生成器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/1 22:53
 */
@SuppressWarnings("DuplicatedCode")
@RequiredArgsConstructor
@Getter
public class AssertionGenerator {
    private static final Logger                          logger = LoggerFactory
        .getLogger(AssertionGenerator.class);

    /**
     * IssuerName
     */
    private final String                                 issuerName;

    /**
     * 是否签名
     */
    private final Boolean                                signed;

    /**
     * 签名算法
     */
    private final SamlSignAssertAlgorithmType            signAlgorithm;

    /**
     * 签名证书
     */
    private final Credential                             signCredential;

    /**
     * 是否加密断言
     */
    private final Boolean                                assertEncrypted;

    /**
     * 断言加密使用的非对称算法
     */
    private final SamlEncryptAssertAlgorithmType         assertEncryptAlgorithm;

    /**
     * 加密证书
     */
    private final Credential                             encryptionCredential;

    /**
     * 接受者
     */
    private final String                                 recipient;

    /**
     * Audience URI
     */
    private final String                                 audienceUri;

    /**
     * nameIdFormatType
     */
    private final SamlNameIdFormatType                   nameIdFormatType;

    /**
     * nameIdValue
     */
    private final String                                 nameIdValue;

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

    public Assertion generateAssertion() {
        Assertion assertion = new AssertionBuilder().buildObject();
        assertion.setIssuer(new IssuerGenerator(getIssuerName()).generateIssuer());
        assertion.setIssueInstant(Instant.now());
        assertion.setID(generateSecureRandomId());
        //主题
        Subject subject = new SubjectGenerator().generateSubject();
        //NameID
        NameID nameId = new NameIDBuilder().buildObject();
        nameId.setFormat(getNameIdFormatType().getValue());
        nameId.setValue(getNameIdValue());
        subject.setNameID(nameId);

        Instant notOnOrAfter = Instant.now().plus(5, ChronoUnit.SECONDS);
        //主题确认
        SubjectConfirmationGenerator confirmationGenerator = new SubjectConfirmationGenerator(
            getRecipient(), notOnOrAfter);
        confirmationGenerator.setAuthnRequestId(authnRequestId);
        subject.getSubjectConfirmations().add(confirmationGenerator.generateSubjectConfirmations());

        assertion.setSubject(subject);

        assertion
            .setConditions(new ConditionsGenerator(getAudienceUri(), notOnOrAfter, notOnOrAfter)
                .generateConditions());
        //属性生成器
        if (!CollectionUtils.isEmpty(attributeStatements)) {
            assertion.getAttributeStatements().add(
                new AttributeStatementGenerator(attributeStatements).generateAttributeStatements());
        }

        assertion.getAuthnStatements()
            .add(new AuthnStatementGenerator(authnContextClassRefType).generateAuthnStatements());

        //签名断言
        if (getSigned()) {
            Signature signature = new SignatureBuilder().buildObject();
            signature.setSigningCredential(getSignCredential());
            signature.setSignatureAlgorithm(getSignAlgorithm().getValue());
            signature
                .setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

            assertion.setSignature(signature);

            try {
                XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(assertion)
                    .marshall(assertion);
            } catch (MarshallingException e) {
                throw new RuntimeException(e);
            }

            try {
                Signer.signObject(signature);
            } catch (SignatureException e) {
                throw new RuntimeException(e);
            }
        }
        return assertion;
    }

    /**
     * 加密断言
     * @param assertion {@link  Assertion}
     * @return {@link EncryptedAssertion}
     */
    public EncryptedAssertion encryptedAssertion(Assertion assertion) {

        return new EncryptedAssertionGenerator(assertion, getAssertEncryptAlgorithm(),
            getEncryptionCredential()).generateEncryptAssertion();
    }
}
