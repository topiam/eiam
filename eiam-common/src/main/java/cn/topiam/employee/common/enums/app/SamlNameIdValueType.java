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
package cn.topiam.employee.common.enums.app;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.common.enums.BaseEnum;
import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * NameID
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/22 23:49
 */
public enum SamlNameIdValueType implements BaseEnum {
                                                     /**
                                                      * 用户名
                                                      */
                                                     USER_USERNAME("user.username"),
                                                     /**
                                                      * 姓名
                                                      */
                                                     USER_FULL_NAME("user.fullName"),
                                                     /**
                                                      * 昵称
                                                      */
                                                     USER_NICK_NAME("user.nickName"),
                                                     /**
                                                      * 邮箱
                                                      */
                                                     USER_EMAIL("user.email"),
                                                     /**
                                                      * 应用账户
                                                      */
                                                     APP_USERNAME("app_user.username");

    @JsonValue
    private final String code;

    SamlNameIdValueType(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return null;
    }

    @EnumConvert
    public static SamlNameIdValueType getType(String code) {
        SamlNameIdValueType[] values = values();
        for (SamlNameIdValueType status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code;
    }
}
