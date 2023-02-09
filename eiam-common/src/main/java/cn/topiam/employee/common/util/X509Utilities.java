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
package cn.topiam.employee.common.util;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

/**
 * 读取证书和私钥的工具类
 *
 * @author TopIAM
 */
public class X509Utilities {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String BEGIN_CERT     = "-----BEGIN CERTIFICATE-----" + LINE_SEPARATOR;
    public static final String END_CERT       = "-----END CERTIFICATE-----";
    public static final String BEGIN_KEY      = "-----BEGIN RSA PRIVATE KEY-----" + LINE_SEPARATOR;
    public static final String END_KEY        = "-----END RSA PRIVATE KEY-----";

    public static byte[] getDer(String combinedKeyAndCertPem, String begin, String end) {
        String[] tokens = combinedKeyAndCertPem.split(begin);
        tokens = tokens[0].split(end);
        return getDer(tokens[0]);
    }

    public static byte[] getDer(String pem) {
        String data = keyCleanup(pem);

        return DatatypeConverter.parseBase64Binary(data);
    }

    public static String keyCleanup(String pem) {
        return pem.replace(BEGIN_CERT, "").replace(END_CERT, "").replace(BEGIN_KEY, "")
            .replace(END_KEY, "").replace("\n", "").trim();
    }

    public static X509Certificate getCertificate(byte[] der) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(der));
    }

    public static RSAPrivateKey getPrivateKey(byte[] der,
                                              String algorithm) throws InvalidKeySpecException,
                                                                NoSuchAlgorithmException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
        KeyFactory factory = KeyFactory.getInstance(algorithm);
        return (RSAPrivateKey) factory.generatePrivate(spec);
    }

    public static PrivateKey readPrivateKey(String pem, String passphrase) throws IOException {

        PEMParser parser = new PEMParser(new CharArrayReader(pem.toCharArray()));
        Object obj = parser.readObject();
        parser.close();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        KeyPair kp;
        if (obj == null) {
            throw new NullPointerException("Unable to decode PEM key:" + pem);
        } else if (obj instanceof PEMEncryptedKeyPair) {
            // Encrypted key - we will use provided password
            PEMEncryptedKeyPair ckp = (PEMEncryptedKeyPair) obj;
            PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder()
                .build(passphrase.toCharArray());
            kp = converter.getKeyPair(ckp.decryptKeyPair(decProv));
        } else if (obj instanceof PrivateKeyInfo) {
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) obj;
            return converter.getPrivateKey(privateKeyInfo);
        } else {
            // Unencrypted key - no password needed
            PEMKeyPair ukp = (PEMKeyPair) obj;
            kp = converter.getKeyPair(ukp);
        }
        return kp.getPrivate();
    }

    public static PublicKey readPublicKey(String pem, String passphrase) throws IOException {

        PEMParser parser = new PEMParser(new CharArrayReader(pem.toCharArray()));
        Object obj = parser.readObject();
        parser.close();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        KeyPair kp;
        if (obj == null) {
            throw new NullPointerException("Unable to decode PEM key:" + pem);
        } else if (obj instanceof PEMEncryptedKeyPair) {
            // Encrypted key - we will use provided password
            PEMEncryptedKeyPair ckp = (PEMEncryptedKeyPair) obj;
            PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder()
                .build(passphrase.toCharArray());
            kp = converter.getKeyPair(ckp.decryptKeyPair(decProv));
        } else if (obj instanceof SubjectPublicKeyInfo) {
            SubjectPublicKeyInfo privateKeyInfo = (SubjectPublicKeyInfo) obj;
            return converter.getPublicKey(privateKeyInfo);
        } else {
            // Unencrypted key - no password needed
            PEMKeyPair ukp = (PEMKeyPair) obj;
            kp = converter.getKeyPair(ukp);
        }
        return kp.getPublic();
    }

}