/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.security.mfa.provider;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.topiam.employee.support.util.QrCodeUtils;

/**
 * Totp 身份验证器，java服务端实现
 *
 * @author TopIAM
 */
public class TotpAuthenticator {

    public static final String HMAC_SHA_1       = "HmacSHA1";
    public static final int    BYTE_SIZE        = 8;
    public static final int    OFFSET_SIZE      = 4;
    public static final int    MAX_WINDOWS_SIZE = 17;
    private final Logger       logger           = LoggerFactory.getLogger(TotpAuthenticator.class);

    /**
     * 生成的key长度( Generate secret key length)
     */
    public static final int    SECRET_SIZE      = 20;

    /**
     * 可偏移时间, 默认3, 最大17
     */
    int                        windowSize       = 3;

    /**
     * 设置窗口大小。这是一个整数值，表示我们允许的 30 秒窗口数。窗口越大，我们对时钟偏差的容忍度就越高。
     *
     * @param size 窗口大小, 必须 >=1且<=17。其他值被忽略
     */
    public void setWindowSize(int size) {
        if (size >= 1 && size <= MAX_WINDOWS_SIZE) {
            windowSize = size;
        }
    }

    /**
     * 生成随机密钥。这必须由服务器保存并与用户帐户相关联，以验证 Google Authenticator 显示的代码。用户必须在他们的设备上注册这个秘钥。
     * 生成一个随机秘钥
     * @return secret key
     */
    public static String generateSecretKey() {
        SecureRandom sr = new SecureRandom();
        byte[] buffer = sr.generateSeed(SECRET_SIZE);
        Base32 codec = new Base32();
        byte[] bEncodedKey = codec.encode(buffer);
        return new String(bEncodedKey);
    }

    /**
     * 生成一个google身份验证器，识别的字符串，只需要把该方法返回值生成二维码扫描就可以了。
     * @param user 账号
     * @param secret 密钥
     * @return QR code value
     */
    public static String getQrBarcode(String user, String secret, String issuer) {
        String format = "otpauth://totp/%s?secret=%s&issuer=%s";
        return QrCodeUtils.createQrCode(String.format(format, user, secret, issuer), 180, 180);
    }

    /**
     * 验证code是否合法
     *
     * @param secret 用户secret.
     * @param code 用户设备上显示的代码
     * @param timeMilliseconds 以毫秒为单位的时间
     * @return pass
     */
    public boolean checkCode(String secret, long code, long timeMilliseconds) {
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secret);
        // 将unix毫秒时间转换为30秒“窗口”
        // 这是根据 TOTP 规范（有关详细信息，请参阅 RFC）
        long time = (timeMilliseconds / 1000L) / 30L;
        // windowSize窗口偏移量
        for (int i = -windowSize; i <= windowSize; ++i) {
            long hash;
            try {
                hash = verifyCode(decodedKey, time + i);
            } catch (Exception e) {
                logger.error("验证code异常: {}", e.getMessage(), e);
                return false;
            }
            if (hash == code) {
                return true;
            }
        }
        // 验证码无效
        return false;
    }

    /**
     * 获取当前time正确code
     *
     * @param key 授权key
     * @param time 时间
     * @return 当前time正确code
     * @throws NoSuchAlgorithmException 算法异常
     * @throws InvalidKeyException 秘钥异常
     */
    private static int verifyCode(byte[] key, long time) throws NoSuchAlgorithmException,
                                                         InvalidKeyException {
        byte[] data = new byte[BYTE_SIZE];
        long value = time;
        // 无符号右移BYTE_SIZE位
        for (int i = BYTE_SIZE; i-- > 0; value >>>= BYTE_SIZE) {
            data[i] = (byte) value;
        }
        SecretKeySpec signKey = new SecretKeySpec(key, HMAC_SHA_1);
        Mac mac = Mac.getInstance(HMAC_SHA_1);
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        // 与运算，两者都为1时才得1，否则就得0
        int offset = hash[20 - 1] & 0xF;
        long truncatedHash = 0;
        for (int i = 0; i < OFFSET_SIZE; ++i) {
            truncatedHash <<= BYTE_SIZE;
            // 处理有符号字节：只保留第一个字节。
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return (int) truncatedHash;
    }
}
