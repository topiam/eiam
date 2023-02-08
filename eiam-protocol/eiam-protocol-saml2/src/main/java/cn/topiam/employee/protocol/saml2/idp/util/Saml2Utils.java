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
package cn.topiam.employee.protocol.saml2.idp.util;

import java.util.Objects;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.metadata.*;
import org.opensaml.saml.saml2.metadata.impl.*;
import org.opensaml.security.credential.UsageType;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.core.session.SessionRegistry;

import cn.topiam.employee.application.saml2.model.Saml2ProtocolConfig;
import cn.topiam.employee.common.repository.app.AppSaml2ConfigRepository;
import cn.topiam.employee.common.util.SamlKeyStoreProvider;
import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.support.exception.TopIamException;
import static org.opensaml.saml.common.xml.SAMLConstants.SAML2_POST_BINDING_URI;
import static org.opensaml.saml.common.xml.SAMLConstants.SAML2_REDIRECT_BINDING_URI;

import static cn.topiam.employee.common.constants.ProtocolConstants.APP_CODE_VARIABLE;
import static cn.topiam.employee.common.constants.ProtocolConstants.Saml2EndpointConstants;
import static cn.topiam.employee.protocol.cas.util.ProtocolUtils.getBean;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/13 23:20
 */
public class Saml2Utils {

    public static <B extends HttpSecurityBuilder<B>> AppSaml2ConfigRepository getAppSaml2ConfigRepository(B builder) {
        AppSaml2ConfigRepository appRepository = builder
            .getSharedObject(AppSaml2ConfigRepository.class);
        if (appRepository == null) {
            appRepository = getBean(builder, AppSaml2ConfigRepository.class);
            builder.setSharedObject(AppSaml2ConfigRepository.class, appRepository);
        }
        return appRepository;
    }

    public static <B extends HttpSecurityBuilder<B>> SessionRegistry getSessionRegistry(B builder) {
        SessionRegistry sessionRegistry = builder.getSharedObject(SessionRegistry.class);
        if (sessionRegistry == null) {
            sessionRegistry = getBean(builder, SessionRegistry.class);
            builder.setSharedObject(SessionRegistry.class, sessionRegistry);
        }
        return sessionRegistry;
    }

    /**
     * 获取EntityDescriptor
     *
     * @param config {@link Saml2ProtocolConfig}
     * @return {@link EntityDescriptor}
     */
    public static EntityDescriptor getEntityDescriptor(Saml2ProtocolConfig config) {
        //@formatter:off
        try {
            EntityDescriptor entityDescriptor = new EntityDescriptorBuilder().buildObject();
            //EntityId是 metadata 地址
            String idpEntityId = ServerContextHelp.getPortalPublicBaseUrl() + Saml2EndpointConstants.SAML_METADATA_PATH.replace(APP_CODE_VARIABLE,
                    config.getAppCode());
            entityDescriptor.setEntityID(idpEntityId);
            //ID
            entityDescriptor.setID(config.getAppCode());
            //IDP用于SSO的描述符
            IDPSSODescriptor idpssoDescriptor = new IDPSSODescriptorBuilder().buildObject();
            //支持的协议
            idpssoDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
            //AuthnRequest 是否加签
            idpssoDescriptor.setWantAuthnRequestsSigned(false);
            //用于签名断言的 Key 信息生成
            if (!Objects.isNull(config.getIdpSignCert())){
                KeyDescriptor signKeyDescriptor = new KeyDescriptorBuilder().buildObject();
                signKeyDescriptor.setUse(UsageType.SIGNING);
                signKeyDescriptor.setKeyInfo(SamlKeyStoreProvider.getKeyInfo(config.getIdpSignCert()));
                idpssoDescriptor.getKeyDescriptors().add(signKeyDescriptor);
            }
            //用于加密断言的 Key 信息生成
            if (!Objects.isNull(config.getIdpEncryptCert())){
                KeyDescriptor encryptionKeyDescriptor = new KeyDescriptorBuilder().buildObject();
                encryptionKeyDescriptor.setUse(UsageType.ENCRYPTION);
                encryptionKeyDescriptor.setKeyInfo(SamlKeyStoreProvider.getKeyInfo(config.getIdpEncryptCert()));
                idpssoDescriptor.getKeyDescriptors().add(encryptionKeyDescriptor);
            }
            //设置IDP NameIDFormat
            //UNSPECIFIED
            NameIDFormat nameIdFormatUnspecified = new NameIDFormatBuilder().buildObject();
            nameIdFormatUnspecified.setURI(NameIDType.UNSPECIFIED);
            //EMAIL
            NameIDFormat nameIdFormatEmail = new NameIDFormatBuilder().buildObject();
            nameIdFormatEmail.setURI(NameIDType.EMAIL);
            //PERSISTENT
            NameIDFormat nameIdFormatPersistent = new NameIDFormatBuilder().buildObject();
            nameIdFormatPersistent.setURI(NameIDType.PERSISTENT);

            idpssoDescriptor.getNameIDFormats().add(nameIdFormatUnspecified);
            idpssoDescriptor.getNameIDFormats().add(nameIdFormatEmail);
            idpssoDescriptor.getNameIDFormats().add(nameIdFormatPersistent);

            //SSO地址相关

            // POST
            SingleSignOnService singleSignOnServicePost = new SingleSignOnServiceBuilder().buildObject();
            singleSignOnServicePost.setBinding(SAML2_POST_BINDING_URI);
            singleSignOnServicePost.setLocation(ServerContextHelp.getPortalPublicBaseUrl() + Saml2EndpointConstants.SAML_SSO_PATH.replace(APP_CODE_VARIABLE, config.getAppCode()));
            idpssoDescriptor.getSingleSignOnServices().add(singleSignOnServicePost);
            // 重定向
            SingleSignOnService singleSignOnServiceRedirect = new SingleSignOnServiceBuilder().buildObject();
            singleSignOnServiceRedirect.setBinding(SAML2_REDIRECT_BINDING_URI);
            singleSignOnServiceRedirect.setLocation(ServerContextHelp.getPortalPublicBaseUrl() + Saml2EndpointConstants.SAML_SSO_PATH.replace(APP_CODE_VARIABLE, config.getAppCode()));
            idpssoDescriptor.getSingleSignOnServices().add(singleSignOnServiceRedirect);

            //登出地址相关
            SingleLogoutService singleLogoutService = new SingleLogoutServiceBuilder().buildObject();
            singleLogoutService.setBinding(SAML2_REDIRECT_BINDING_URI);
            singleLogoutService.setLocation(ServerContextHelp.getPortalPublicBaseUrl() + Saml2EndpointConstants.SAML_LOGOUT_PATH.replace(APP_CODE_VARIABLE, config.getAppCode()));
            idpssoDescriptor.getSingleLogoutServices().add(singleLogoutService);

            entityDescriptor.getRoleDescriptors().add(idpssoDescriptor);
            return entityDescriptor;
        } catch (Exception e) {
            throw new TopIamException(e.getMessage());
        }
        //@formatter:on
    }
}
