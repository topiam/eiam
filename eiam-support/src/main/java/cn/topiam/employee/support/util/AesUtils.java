/*
 * eiam-support - Employee Identity and Access Management Program
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
package cn.topiam.employee.support.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.util.StringUtils;

import lombok.SneakyThrows;

/**
* AES 对称加密
* @author TopIAM
*/
public class AesUtils {
    private static final String ALGORITHM = "AES";
    private final String        KEY;

    public AesUtils(String key) {
        this.KEY = key;
    }

    /**
     * 生成秘钥
     */
    @SneakyThrows
    public static String generateKey() {
        KeyGenerator keygen;
        keygen = KeyGenerator.getInstance(ALGORITHM);
        // 16 字节 == 128 bit
        keygen.init(128, new SecureRandom());
        SecretKey secretKey = keygen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 生成密钥
     */
    private static SecretKeySpec getSecretKeySpec(String secretKeyStr) {
        return new SecretKeySpec(Base64.getDecoder().decode(secretKeyStr), ALGORITHM);
    }

    /**
     * 加密
     */
    @SneakyThrows
    public String encrypt(String content) {
        if (StringUtils.hasText(content)) {
            return encrypt(content, KEY);
        }
        return null;
    }

    /**
     * 加密
     */
    @SneakyThrows
    public static String encrypt(String content, String secretKey) {
        Key key = getSecretKeySpec(secretKey);
        // 创建密码器
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // 初始化
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder()
            .encodeToString(cipher.doFinal(content.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * 解密
     */
    @SneakyThrows
    public String decrypt(String content) {
        if (StringUtils.hasText(content)) {
            return decrypt(content, KEY);
        }
        return null;
    }

    /**
     * 解密
     */
    @SneakyThrows
    public static String decrypt(String content, String secretKey) {
        Key key = getSecretKeySpec(secretKey);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(content)),
            StandardCharsets.UTF_8);
    }
}
