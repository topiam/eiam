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
package cn.topiam.employee.common.enums.account;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.enums.BaseEnum;
import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * 用户范围运算符
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023-05-05
 */
public enum OperatorType implements BaseEnum {
                                              /**
                                               * 是以下其中之一
                                               */
                                              IN("in", "是以下其中之一"),
                                              /**
                                               * 包含
                                               */
                                              LIKE("like", "包含"),
                                              /**
                                               * 开始于
                                               */
                                              GT("gt", "开始于"),
                                              /**
                                               * 结束于
                                               */
                                              LT("lt", "结束于"),
                                              /**
                                               * 相等
                                               */
                                              EQ("eq", "相等");

    /**
     * code
     */
    @JsonValue
    private final String code;
    /**
     * desc
     */
    private final String desc;

    OperatorType(String code, String desc) {
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
     * 获取类型
     *
     * @param code {@link String}
     * @return {@link OperatorType}
     */
    @EnumConvert
    public static OperatorType getType(String code) {
        OperatorType[] values = values();
        for (OperatorType status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.code;
    }

}
