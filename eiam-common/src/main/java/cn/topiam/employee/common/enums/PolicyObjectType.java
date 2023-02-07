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
 * 权限策略客体类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/4 19:05
 */
public enum PolicyObjectType implements BaseEnum {
                                                  /**
                                                   * 角色
                                                   */
                                                  ROLE("ROLE", "角色"),
                                                  /**
                                                   * 权限
                                                   */
                                                  PERMISSION("PERMISSION", "权限"),
                                                  /**
                                                   * 资源
                                                   */
                                                  RESOURCE("RESOURCE", "资源");

    /**
     * code
     */
    @JsonValue
    private final String code;
    /**
     * desc
     */
    private final String desc;

    PolicyObjectType(String code, String desc) {
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
     * @return {@link PolicyObjectType}
     */
    @EnumConvert
    public static PolicyObjectType getType(String code) {
        PolicyObjectType[] values = values();
        for (PolicyObjectType status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        return null;
    }
}
