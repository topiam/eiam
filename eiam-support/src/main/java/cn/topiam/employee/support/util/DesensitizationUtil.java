/*
 * eiam-support - Employee Identity and Access Management Program
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
package cn.topiam.employee.support.util;

/**
 * 脱敏工具类
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/4 23:38
 */
public class DesensitizationUtil {

    /**
     * 只显示第一个汉字，其他隐藏为2个星号<例子：李**>
     *
     * @param fullName {@link String}
     * @param  index 1 为第index位开始脱敏
     * @return {@link String}
     */
    public static String left(String fullName, int index) {
        if (org.apache.commons.lang3.StringUtils.isBlank(fullName)) {
            return "";
        }
        String name = org.apache.commons.lang3.StringUtils.left(fullName, index);
        return org.apache.commons.lang3.StringUtils.rightPad(name,
            org.apache.commons.lang3.StringUtils.length(fullName), "*");
    }

    /**
     * 110****58，前面保留3位明文，后面保留2位明文
     *
     * @param name {@link String}
     * @param index 3
     * @param end 2
     * @return {@link String}
     */
    public static String around(String name, int index, int end) {
        if (org.apache.commons.lang3.StringUtils.isBlank(name)) {
            return "";
        }
        return org.apache.commons.lang3.StringUtils.left(name, index).concat(
            org.apache.commons.lang3.StringUtils.removeStart(org.apache.commons.lang3.StringUtils
                .leftPad(org.apache.commons.lang3.StringUtils.right(name, end),
                    org.apache.commons.lang3.StringUtils.length(name), "*"),
                "***"));
    }

    /**
     * 后四位，其他隐藏<例子：****1234>
     *
     * @param num {@link String}
     * @return {@link String}
     */
    public static String right(String num, int end) {
        if (org.apache.commons.lang3.StringUtils.isBlank(num)) {
            return "";
        }
        return org.apache.commons.lang3.StringUtils.leftPad(
            org.apache.commons.lang3.StringUtils.right(num, end),
            org.apache.commons.lang3.StringUtils.length(num), "*");
    }

    /***
     * 手机号码前三后四脱敏
     *
     * @param mobile  {@link String}
     * @return  {@link String}
     */
    public static String phoneEncrypt(String mobile) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(mobile) || (mobile.length() != 11)) {
            return mobile;
        }
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * [电子邮箱] 邮箱前缀隐藏，用星号代替，@及后面的地址显示 <例子:******@163.com>
     *
     * @param email {@link String}
     * @return {@link String}
     */
    public static String emailEncrypt(String email) {
        if (org.apache.commons.lang3.StringUtils.isBlank(email)) {
            return "";
        }
        return email.replaceAll("(^\\w)[^@]*(@.*$)", "$1****$2");
    }

    /**
     * 身份证前三后四脱敏
     * @param id {@link String}
     * @return {@link String}
     */
    public static String idEncrypt(String id) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(id) || (id.length() < 8)) {
            return id;
        }
        return id.replaceAll("(?<=\\w{3})\\w(?=\\w{4})", "*");
    }

    /**
     * 护照前2后3位脱敏，护照一般为8或9位
     * @param id {@link String}
     * @return {@link String}
     */
    public static String idPassport(String id) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(id) || (id.length() < 8)) {
            return id;
        }
        return id.substring(0, 2) + new String(new char[id.length() - 5]).replace("\0", "*")
               + id.substring(id.length() - 3);
    }

    /**
     * 证件后几位脱敏
     * @param id {@link String}
     * @param sensitiveSize {@link String}
     * @return {@link String}
     */
    public static String idPassport(String id, int sensitiveSize) {
        if (org.apache.commons.lang3.StringUtils.isBlank(id)) {
            return "";
        }
        int length = org.apache.commons.lang3.StringUtils.length(id);
        return org.apache.commons.lang3.StringUtils.rightPad(
            org.apache.commons.lang3.StringUtils.left(id, length - sensitiveSize), length, "*");
    }
}