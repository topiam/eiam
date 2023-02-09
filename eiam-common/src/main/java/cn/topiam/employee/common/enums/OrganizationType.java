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
 * 组织机构类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/9 21:04
 */
public enum OrganizationType implements BaseEnum {
                                                  /**
                                                   * 集团
                                                   */
                                                  GROUP("group", "集团"),
                                                  /**
                                                   * 公司
                                                   */
                                                  COMPANY("company", "公司"),
                                                  /**
                                                   * 部门
                                                   */
                                                  DEPARTMENT("department", "部门"),
                                                  /**
                                                   * 单位
                                                   */
                                                  UNIT("unit", "单位");

    /**
     * code
     */
    @JsonValue
    private final String code;
    /**
     * desc
     */
    private final String desc;

    OrganizationType(String code, String desc) {
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
     * @return {@link OrganizationType}
     */
    @EnumConvert
    public static OrganizationType getType(String code) {
        OrganizationType[] values = values();
        for (OrganizationType status : values) {
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
