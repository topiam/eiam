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

import java.util.Objects;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.binding.security.impl.SAMLProtocolMessageXMLSignatureSecurityHandler;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.security.impl.SAMLSignatureProfileValidator;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureValidator;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;

import cn.topiam.employee.support.exception.TopIamException;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

/**
 * SAML 验证
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/1 22:44
 */
public class Saml2ValidatorSuite {
    /**
     * 验证签名
     *
     * @param signature       {@link Signature}
     * @param credentialResolver {@link CredentialResolver}
     * @throws Exception Exception
     */
    public static void verifySignatureUsingSignatureValidator(Signature signature,
                                                              CredentialResolver credentialResolver,
                                                              String entityId) throws Exception {
        // 设置标准以获得相关证书
        CriteriaSet criteriaSet = new CriteriaSet();
        criteriaSet.add(new UsageCriterion(UsageType.SIGNING));
        criteriaSet.add(new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME));
        criteriaSet.add(new ProtocolCriterion(SAMLConstants.SAML20P_NS));
        criteriaSet.add(new EntityIdCriterion(entityId));
        // 解析凭证
        Credential credential = credentialResolver.resolveSingle(criteriaSet);

        // 验证签名格式
        SAMLSignatureProfileValidator profileValidator = new SAMLSignatureProfileValidator();
        profileValidator.validate(Objects.requireNonNull(signature));

        // 验证签名
        SignatureValidator.validate(signature, credential);
    }

    /**
     * 使用消息处理程序验证签名
     *
     * @param context {@link MessageContext}
     * @throws Exception Exception
     */
    public static void verifySignatureUsingMessageHandler(MessageContext context,
                                                          CredentialResolver credentialResolver,
                                                          String entityId) throws Exception {
        SecurityParametersContext secParamsContext = context
            .getSubcontext(SecurityParametersContext.class, true);
        Objects.requireNonNull(secParamsContext).setSignatureValidationParameters(
            buildSignatureValidationParameters(credentialResolver));

        SAMLPeerEntityContext peerEntityContext = context.getSubcontext(SAMLPeerEntityContext.class,
            true);
        peerEntityContext.setEntityId(entityId);
        peerEntityContext.setRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);

        SAMLProtocolContext protocolContext = context.getSubcontext(SAMLProtocolContext.class,
            true);
        protocolContext.setProtocol(SAMLConstants.SAML20P_NS);

        SAMLProtocolMessageXMLSignatureSecurityHandler signatureSecurityHandler = new SAMLProtocolMessageXMLSignatureSecurityHandler();
        signatureSecurityHandler.invoke(context);

        if (!peerEntityContext.isAuthenticated()) {
            throw new TopIamException("消息未签名");
        }
    }

    private static SignatureValidationParameters buildSignatureValidationParameters(CredentialResolver credentialResolver) throws Exception {
        SignatureValidationParameters validationParameters = new SignatureValidationParameters();
        validationParameters.setSignatureTrustEngine(buildTrustEngine(credentialResolver));
        return validationParameters;
    }

    private static ExplicitKeySignatureTrustEngine buildTrustEngine(CredentialResolver credentialResolver) {
        final KeyInfoCredentialResolver keyInfoResolver = DefaultSecurityConfigurationBootstrap
            .buildBasicInlineKeyInfoCredentialResolver();

        return new ExplicitKeySignatureTrustEngine(credentialResolver, keyInfoResolver);

    }

}
