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
package cn.topiam.employee.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.*;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.impl.KeyStoreCredentialResolver;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.signature.KeyInfo;

import cn.topiam.employee.support.exception.TopIamException;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import static org.springframework.util.StringUtils.hasText;

import static cn.topiam.employee.common.util.SamlUtils.getParserPool;

/**
 * 用于解析证书与私钥的工具类
 *
 * @author TopIAM
 */
public class SamlKeyStoreProvider {

    private static final char[] DEFAULT_KS_PASSWD = UUID.randomUUID().toString().toCharArray();

    /**
     * 获取 KeyInfo
     *
     * @param credential {@link  Credential}
     * @return {@link KeyInfo}
     */
    public static KeyInfo getKeyInfo(Credential credential) {
        try {
            NamedKeyInfoGeneratorManager manager = DefaultSecurityConfigurationBootstrap
                .buildBasicKeyInfoGeneratorManager();
            KeyInfoGenerator generator = Objects
                .requireNonNull(manager.getDefaultManager().getFactory(credential)).newInstance();
            return generator.generate(credential);
        } catch (Exception e) {
            throw new TopIamException(e.getMessage());
        }
    }

    public static KeyStore getKeyStore(String entityId, String privateKey, String certificateString,
                                       String password) {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(null, DEFAULT_KS_PASSWD);

            byte[] certBytes = X509Utilities.getDer(certificateString);
            Certificate certificate = X509Utilities.getCertificate(certBytes);
            ks.setCertificateEntry(entityId, certificate);

            if (hasText(privateKey)) {
                PrivateKey pkey = X509Utilities.readPrivateKey(privateKey, password);
                ks.setKeyEntry(entityId, pkey, password.toCharArray(),
                    new Certificate[] { certificate });
            }

            return ks;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException
                | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static KeyStoreCredentialResolver getCredentialsResolver(String entityId, String key,
                                                                     String cer, String password) {
        KeyStore ks = getKeyStore(entityId, key, cer, password);
        Map<String, String> passwords = hasText(key) ? Collections.singletonMap(entityId, password)
            : Collections.emptyMap();
        return new KeyStoreCredentialResolver(ks, passwords);
    }

    public static Credential getCredential(String entityId, String key, String cer,
                                           String password) {
        try {
            KeyStoreCredentialResolver resolver = getCredentialsResolver(entityId, key, cer,
                password);
            CriteriaSet cs = new CriteriaSet();
            EntityIdCriterion criteria = new EntityIdCriterion(entityId);
            cs.add(criteria);
            return resolver.resolveSingle(cs);
        } catch (ResolverException e) {
            throw new RuntimeException("Can't obtain private key", e);
        }
    }

    /**
     * 获取 KeyStoreCredentialResolver
     *
     * @param entityId   {@link String}
     * @param credential {@link String}
     * @return {@link KeyStoreCredentialResolver}
     */
    public static KeyStoreCredentialResolver getKeyStoreCredentialResolver(String entityId,
                                                                           String credential) {

        KeyStore keyStore = getKeyStore(entityId, credential);
        return new KeyStoreCredentialResolver(keyStore, new HashMap<>(16));
    }

    public static KeyStore getKeyStore(String entityId, String cert) {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(null, DEFAULT_KS_PASSWD);

            byte[] certBytes = X509Utilities.getDer(cert);
            Certificate certificate = X509Utilities.getCertificate(certBytes);
            ks.setCertificateEntry(entityId, certificate);
            return ks;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException
                | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取 EntityDescriptor
     *
     * @param inputStream {@link InputStream}
     * @return {@link EntityDescriptor}
     */
    public static List<EntityDescriptor> getEntityDescriptors(InputStream inputStream) {
        List<EntityDescriptor> list = new ArrayList<>();
        try {
            InputStreamMetadataResolver idpMetaDataProvider = new InputStreamMetadataResolver(
                inputStream);
            idpMetaDataProvider.setRequireValidMetadata(true);
            idpMetaDataProvider.setId(idpMetaDataProvider.getClass().getCanonicalName());
            idpMetaDataProvider.setParserPool(getParserPool());
            idpMetaDataProvider.initialize();
            for (EntityDescriptor idpEntityDescriptor : idpMetaDataProvider) {
                list.add(idpEntityDescriptor);
            }
        } catch (Exception e) {
            throw new TopIamException(e.getMessage(), e);
        }
        return list;
    }

}
