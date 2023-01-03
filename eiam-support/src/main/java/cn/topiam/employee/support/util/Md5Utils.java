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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.extern.slf4j.Slf4j;

/**
 * Md5加密类
 * @author TopIAM
 * Created by support@topiam.cn on  2019/5/26
 */
@Slf4j
public class Md5Utils {

    /**
     * 16位加密
     *
     * @param plainText plainText
     * @return String
     */
    public static String md516(String plainText) {
        return md516(plainText, StandardCharsets.UTF_8.name());
    }

    /**
     * 16位加密
     *
     * @param plainText plainText
     * @return String
     */
    public static String md516(String plainText, String charSet) {
        String result = null;
        try {
            byte[] ptBytes = plainText.getBytes(charSet);
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(ptBytes);
            byte[] b = md.digest();
            int i;
            StringBuilder buf = new StringBuilder("");
            for (byte value : b) {
                i = value;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            // result = buf.toString(); //md5 32bit
            // result = buf.toString().substring(8, 24))); //md5 16bit
            result = buf.toString().substring(8, 24);
        } catch (Exception e) {
            log.error("{}", (Object) e.getStackTrace());
        }
        return result;
    }

    /**
     * 32位加密
     *
     * @param plainText plainText
     * @return String
     */
    public static String md532(String plainText) {
        return md532(plainText, StandardCharsets.UTF_8.name());
    }

    /**
     * 32位加密
     *
     * @param plainText plainText
     * @return String
     */
    public static String md532(String plainText, String charSet) {
        String result = null;
        try {
            byte[] ptBytes = plainText.getBytes(charSet);
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(ptBytes);
            byte[] b = md.digest();
            int i;
            StringBuilder buf = new StringBuilder("");
            for (byte value : b) {
                i = value;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            // result = buf.toString(); //md5 32bit
            // result = buf.toString().substring(8, 24))); //md5 16bit
            result = buf.toString();
        } catch (Exception e) {
            log.error("{}", (Object) e.getStackTrace());
        }
        return result;
    }

    public static String getMd5Code(String strObj) {
        String resultString;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = StringUtils.byteToHexString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return null;
        }
        return resultString;
    }

    public static String encode(String text) {

        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                int number = b & 0xff;
                String hex = Integer.toHexString(number);
                if (hex.length() == 1) {
                    sb.append("0").append(hex);
                } else {
                    sb.append(hex);
                }
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            log.error("{}", (Object) e.getStackTrace());
        }

        return "";
    }
}
