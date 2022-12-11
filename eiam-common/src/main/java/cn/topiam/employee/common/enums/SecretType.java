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
package cn.topiam.employee.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.web.converter.EnumConvert;

import lombok.Getter;
import static cn.topiam.employee.support.constant.EiamConstants.TOPIAM_ENCRYPT_SECRET;
import static cn.topiam.employee.support.constant.EiamConstants.TOPIAM_LOGIN_SECRET;

/**
 * 秘钥类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/9 23:15
 */
public enum SecretType implements BaseEnum {
                                            /**
                                             * 登录
                                             */
                                            LOGIN("login", TOPIAM_LOGIN_SECRET, "登录"),
                                            /**
                                             * 加密
                                             */
                                            ENCRYPT("encrypt", TOPIAM_ENCRYPT_SECRET, "加密");

    @Getter
    @JsonValue
    private final String code;
    @Getter
    private final String key;

    private final String desc;

    SecretType(String code, String key, String desc) {
        this.code = code;
        this.key = key;
        this.desc = desc;
    }

    /**
     * 获取来源
     *
     * @param code {@link String}
     * @return {@link OrganizationType}
     */
    @EnumConvert
    public static SecretType getType(String code) {
        SecretType[] values = values();
        for (SecretType source : values) {
            if (String.valueOf(source.getCode()).equals(code)) {
                return source;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
