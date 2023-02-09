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

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * 用户性别
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/7/17 19:28
 */
public enum UserGender implements BaseEnum {
                                            /**
                                             * 男
                                             */
                                            MALE("1", "男"),
                                            /**
                                             * 女
                                             */
                                            FEMALE("0", "女"),
                                            /**
                                             * 未知
                                             */
                                            UNKNOWN("-1", "未知");

    @JsonValue
    private final String code;
    private final String desc;

    UserGender(String code, String desc) {
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
     * 获取认证平台
     *
     * @param code {@link String}
     * @return {@link UserGender}
     */
    @EnumConvert
    public static UserGender getType(String code) {
        UserGender[] values = values();
        for (UserGender status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        throw new NullPointerException("未获取到对应性别");
    }
}
