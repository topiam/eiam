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

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TopIAM
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2018/10/25 20:23
 */
public class StringUtils {
    /**
     * 默认分隔符
     */
    public static final String SPLIT_DEFAULT = ",";

    /**
     * byte数组 转 16进制 字符串
     *
     * @param b b
     * @return String
     */
    public static String byteToHexString(byte[] b) {
        StringBuilder result = new StringBuilder();
        for (byte value : b) {
            String hex = Integer.toHexString(value & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            result.append(hex.toUpperCase());
        }
        return result.toString();
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 16进制 转 byte数组
     *
     * @param hexString hexString
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || "".equals(hexString)) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * 统计字符串字符数（非连续）
     *
     * @param string {@link  String}
     * @return {@link  Map}
     */
    public static Map<Character, Long> statisticsCharCount(String string) {
        return Stream.of(string).flatMap((Function<String, Stream<Character>>) s -> {
            final char[] chars = s.toCharArray();
            final ArrayList<Character> list = new ArrayList<>();
            for (char aChar : chars) {
                list.add(aChar);
            }
            return list.stream();
        }).collect(Collectors.groupingBy(Character::charValue, Collectors.counting()));
    }
}
