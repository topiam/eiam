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
package cn.topiam.employee.common.message.enums;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * 邮件安全方式
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/19 23:09
 */
public enum MailSafetyType implements Serializable {
                                                    /**
                                                     * 无
                                                     */
                                                    None("none", "无"),
                                                    /**
                                                     * SSL
                                                     */
                                                    SSL("ssl", "SSL");

    /**
     * code
     */
    @JsonValue
    private final String code;
    /**
     * 描述
     */
    private final String desc;

    /**
     * 构造
     *
     * @param code {@link String}
     * @param desc {@link String}
     */
    MailSafetyType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @EnumConvert
    public static MailSafetyType getType(String code) {
        MailSafetyType[] values = values();
        for (MailSafetyType status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        throw new NullPointerException("未找到该类型");
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return code;
    }
}
