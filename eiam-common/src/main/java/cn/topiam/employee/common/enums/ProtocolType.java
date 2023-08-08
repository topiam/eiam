/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.enums.BaseEnum;

/**
 * LDAP协议类型
 *
 * @author TopIAM Created by support@topiam.cn on 2023-03-29 22:30:27
 */
public enum ProtocolType implements BaseEnum {
                                              /**
                                               * LDAP
                                               */
                                              LDAP("ldap://", "LDAP"),
                                              /**
                                               * StartTLS
                                               */
                                              STARTTLS("startTLS", "StartTLS"),
                                              /**
                                               * LDAP
                                               */
                                              LDAPS("ldaps://", "LDAPS");

    /**
     * code
     */
    @JsonValue
    private final String code;
    /**
     * 名称
     */
    private final String name;

    ProtocolType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return null;
    }

    public String getName() {
        return name;
    }

    public static ProtocolType getType(String code) {
        ProtocolType[] values = values();
        for (ProtocolType status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        throw new NullPointerException("未找到该类型");
    }

    @Override
    public String toString() {
        return this.code;
    }
}
