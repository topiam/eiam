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
package cn.topiam.employee.common.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/6 22:11
 */
public enum UserIdType implements BaseEnum {
                                            /**
                                             * 身份证号
                                             */
                                            IDENTITY_CARD("identity_card", "身份证号");

    /**
     * code
     */
    @JsonValue
    @JSONField
    private final String code;
    /**
     * desc
     */
    private final String desc;

    UserIdType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    /**
     * 获取来源
     *
     * @param code {@link String}
     * @return {@link OrganizationType}
     */
    @EnumConvert
    public static UserIdType getType(String code) {
        UserIdType[] values = values();
        for (UserIdType source : values) {
            if (String.valueOf(source.getCode()).equals(code)) {
                return source;
            }
        }
        return null;
    }

    public static UserIdType getName(String name) {
        UserIdType[] values = values();
        for (UserIdType source : values) {
            if (source.name().equals(name)) {
                return source;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
