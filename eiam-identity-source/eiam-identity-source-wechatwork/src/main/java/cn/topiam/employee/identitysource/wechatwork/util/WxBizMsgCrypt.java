/*
 * eiam-identity-source-wechatwork - Employee Identity and Access Management
 * Copyright © 2022-Present Jinan Yuanchuang Network Technology Co., Ltd. (support@topiam.cn)
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
package cn.topiam.employee.identitysource.wechatwork.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 针对org.apache.commons.codec.binary.Base64，
 * 需要导入架包commons-codec-1.9（或commons-codec-1.8等其他版本）
 * 官方下载地址：http://commons.apache.org/proper/commons-codec/download_codec.cgi
 *
 * 提供接收和推送给企业微信消息的加解密接口(UTF8编码的字符串).
 * <ol>
 * 	<li>第三方回复加密消息给企业微信</li>
 * 	<li>第三方收到企业微信发送的消息，验证消息的安全性，并对消息进行解密。</li>
 * </ol>
 * 说明：异常java.security.InvalidKeyException:illegal Key Size的解决方案
 * <ol>
 * 	<li>在官方网站下载JCE无限制权限策略文件（JDK7的下载地址：
 *      http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html</li>
 * 	<li>下载后解压，可以看到local_policy.jar和US_export_policy.jar以及readme.txt</li>
 * 	<li>如果安装了JRE，将两个jar文件放到%JRE_HOME%\lib\security目录下覆盖原来的文件</li>
 * 	<li>如果安装了JDK，将两个jar文件放到%JDK_HOME%\jre\lib\security目录下覆盖原来文件</li>
 * </ol>
 *
 * @author TopIAM
 */
public class WxBizMsgCrypt {
    static Charset CHARSET = StandardCharsets.UTF_8;
    Base64         base64  = new Base64();
    byte[]         aesKey;
    String         token;
    String         receiveId;

    /**
     * 构造函数
     * @param token 企业微信后台，开发者设置的token
     * @param encodingAesKey 企业微信后台，开发者设置的EncodingAESKey
     * @param receiveId, 不同场景含义不同，详见文档
     *
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public WxBizMsgCrypt(String token, String encodingAesKey,
                         String receiveId) throws AesException {
        if (encodingAesKey.length() != 43) {
            throw new AesException(AesException.ILLEGAL_AES_KEY);
        }

        this.token = token;
        this.receiveId = receiveId;
        aesKey = Base64.decodeBase64(encodingAesKey + "=");
    }

    /**
     * 生成4个字节的网络字节序
     * @param sourceNumber {@link  Integer}
     * @return {@link Byte}
     */
    byte[] getNetworkBytesOrder(int sourceNumber) {
        byte[] orderBytes = new byte[4];
        orderBytes[3] = (byte) (sourceNumber & 0xFF);
        orderBytes[2] = (byte) (sourceNumber >> 8 & 0xFF);
        orderBytes[1] = (byte) (sourceNumber >> 16 & 0xFF);
        orderBytes[0] = (byte) (sourceNumber >> 24 & 0xFF);
        return orderBytes;
    }

    /**
     * 还原4个字节的网络字节序
     * @param orderBytes {@link Byte}
     * @return {@link Integer}
     */
    int recoverNetworkBytesOrder(byte[] orderBytes) {
        int sourceNumber = 0;
        for (int i = 0; i < 4; i++) {
            sourceNumber <<= 8;
            sourceNumber |= orderBytes[i] & 0xff;
        }
        return sourceNumber;
    }

    /**
     * 随机生成16位字符串
     * @return {@link  String}
     */
    String getRandomStr() {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 对明文进行加密.
     *
     * @param text 需要加密的明文
     * @return 加密后base64编码的字符串
     * @throws AesException aes加密失败
     */
    String encrypt(String randomStr, String text) throws AesException {
        ByteGroup byteCollector = new ByteGroup();
        byte[] randomStrBytes = randomStr.getBytes(CHARSET);
        byte[] textBytes = text.getBytes(CHARSET);
        byte[] networkBytesOrder = getNetworkBytesOrder(textBytes.length);
        byte[] receiveIdBytes = receiveId.getBytes(CHARSET);

        // randomStr + networkBytesOrder + text + receiveid
        byteCollector.addBytes(randomStrBytes);
        byteCollector.addBytes(networkBytesOrder);
        byteCollector.addBytes(textBytes);
        byteCollector.addBytes(receiveIdBytes);

        // ... + pad: 使用自定义的填充方式对明文进行补位填充
        byte[] padBytes = Pkcs7Encoder.encode(byteCollector.size());
        byteCollector.addBytes(padBytes);

        // 获得最终的字节流, 未加密
        byte[] unencrypted = byteCollector.toBytes();

        try {
            // 设置加密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

            // 加密
            byte[] encrypted = cipher.doFinal(unencrypted);

            // 使用BASE64对加密后的字符串进行编码
            return base64.encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.ENCRYPT_AES_ERROR);
        }
    }

    /**
     * 对密文进行解密.
     *
     * @param text 需要解密的密文
     * @return 解密得到的明文
     * @throws AesException aes解密失败
     */
    String decrypt(String text) throws AesException {
        byte[] original;
        try {
            // 设置解密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);

            // 使用BASE64对密文进行解码
            byte[] encrypted = Base64.decodeBase64(text);

            // 解密
            original = cipher.doFinal(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.DECRYPT_AES_ERROR);
        }

        String xmlContent, fromReceiveId;
        try {
            // 去除补位字符
            byte[] bytes = Pkcs7Encoder.decode(original);

            // 分离16位随机字符串,网络字节序和receiveid
            byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);

            int xmlLength = recoverNetworkBytesOrder(networkOrder);

            xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET);
            fromReceiveId = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length),
                CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.ILLEGAL_BUFFER);
        }

        // receiveid不相同的情况
        if (!fromReceiveId.equals(receiveId)) {
            throw new AesException(AesException.VALIDATE_CORP_ID_ERROR);
        }
        return xmlContent;

    }

    /**
     * 将企业微信回复用户的消息加密打包.
     * <ol>
     * 	<li>对要发送的消息进行AES-CBC加密</li>
     * 	<li>生成安全签名</li>
     * 	<li>将消息密文和安全签名打包成xml格式</li>
     * </ol>
     *
     * @param replyMsg 企业微信待回复用户的消息，xml格式的字符串
     * @param timeStamp 时间戳，可以自己生成，也可以用URL参数的timestamp
     * @param nonce 随机串，可以自己生成，也可以用URL参数的nonce
     *
     * @return 加密后的可以直接回复用户的密文，包括msg_signature, timestamp, nonce, encrypt的xml格式的字符串
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String encryptMsg(String replyMsg, String timeStamp, String nonce) throws AesException {
        // 加密
        String encrypt = encrypt(getRandomStr(), replyMsg);

        // 生成安全签名
        if (Objects.equals(timeStamp, "")) {
            timeStamp = Long.toString(System.currentTimeMillis());
        }

        String signature = Sha1.getSha1(token, timeStamp, nonce, encrypt);

        // 生成发送的xml
        return XmlParse.generate(encrypt, signature, timeStamp, nonce);
    }

    /**
     * 检验消息的真实性，并且获取解密后的明文.
     * <ol>
     * 	<li>利用收到的密文生成安全签名，进行签名验证</li>
     * 	<li>若验证通过，则提取xml中的加密消息</li>
     * 	<li>对消息进行解密</li>
     * </ol>
     *
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timeStamp 时间戳，对应URL参数的timestamp
     * @param nonce 随机串，对应URL参数的nonce
     * @param encrypt 密文，对应POST请求的数据
     *
     * @return 解密后的原文
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String decryptMsg(String msgSignature, String timeStamp, String nonce,
                             String encrypt) throws AesException {

        //        // 密钥，公众账号的app secret
        //        // 提取密文
        //        Object[] encrypt = XmlParse.extract(postData);

        // 验证安全签名
        String signature = Sha1.getSha1(token, timeStamp, nonce, encrypt);

        // 和URL中的签名比较是否相等
        if (!signature.equals(msgSignature)) {
            throw new AesException(AesException.VALIDATE_SIGNATURE_ERROR);
        }

        // 解密
        return decrypt(encrypt);
    }

    /**
     * 验证URL
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timeStamp 时间戳，对应URL参数的timestamp
     * @param nonce 随机串，对应URL参数的nonce
     * @param echoStr 随机串，对应URL参数的echostr
     *
     * @return 解密之后的echostr
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String verifyUrl(String msgSignature, String timeStamp, String nonce,
                            String echoStr) throws AesException {
        String signature = Sha1.getSha1(token, timeStamp, nonce, echoStr);

        if (!signature.equals(msgSignature)) {
            throw new AesException(AesException.VALIDATE_SIGNATURE_ERROR);
        }

        return decrypt(echoStr);
    }

}
