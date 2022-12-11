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

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import cn.topiam.employee.support.exception.TopIamException;

/**
 * PhoneUtils
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/11/27 23:11
 */
public class PhoneUtils {

    /**
     * 获取手机号
     *
     * @param phone {@link String}
     * @return {@link String}
     */
    public static String getPhoneAreaCode(String phone) {
        Phonenumber.PhoneNumber phoneNumber = parsePhone(phone);
        return String.valueOf(phoneNumber.getCountryCode());
    }

    /**
     * 获取手机号
     * @param phone {@link String}
     * @return {@link String}
     */
    public static String getPhoneNumber(String phone) {
        Phonenumber.PhoneNumber phoneNumber = parsePhone(phone);
        return String.valueOf(phoneNumber.getNationalNumber());
    }

    /**
     * 获取PhoneNumber
     *
     * @param phone {@link String}
     * @return {@link Phonenumber.PhoneNumber}
     */
    public static Phonenumber.PhoneNumber parsePhone(String phone) {
        try {
            return PhoneNumberUtil.getInstance().parse(phone, "CN");
        } catch (NumberParseException e) {
            throw new TopIamException("解析手机号发生异常", e);
        }
    }
}
