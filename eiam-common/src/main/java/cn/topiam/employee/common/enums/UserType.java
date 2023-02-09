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
 * 用户类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/22 23:35
 */
public enum UserType implements BaseEnum {
                                          /**
                                           * 用户
                                           */
                                          USER("user", "用户"),
                                          /**
                                           * 管理员
                                           */
                                          ADMIN("admin", "管理员"),
                                          /**
                                           * 开发人员
                                           */
                                          DEVELOPER("developer", "开发人员"),
                                          /**
                                           * 未知
                                           */
                                          UNKNOWN("unknown", "未知");

    @JsonValue
    private String code;

    private String desc;

    UserType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取审计类型
     *
     * @param code {@link String}
     * @return {@link UserType}
     */
    @EnumConvert
    public static UserType getType(String code) {
        UserType[] values = values();
        for (UserType status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        throw new NullPointerException("未获取到类型");
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
