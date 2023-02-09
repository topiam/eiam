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

/**
 * 权限类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/10 22:51
 */
public enum PermissionActionType implements BaseEnum {
                                                      /**
                                                       * 菜单
                                                       */
                                                      MENU("MENU", "菜单"),
                                                      /**
                                                       * 按钮
                                                       */
                                                      BUTTON("BUTTON", "按钮"),
                                                      /**
                                                       * API
                                                       */
                                                      API("API", "API"),
                                                      /**
                                                       * 数据
                                                       */
                                                      DATA("DATA", "数据"),
                                                      /**
                                                       * 其他
                                                       */
                                                      OTHER("OTHER", "其他");

    /**
     * code
     */
    @JsonValue
    private final String code;
    /**
     * desc
     */
    private final String desc;

    PermissionActionType(String code, String desc) {
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

    public static PermissionActionType getType(String code) {
        PermissionActionType[] values = values();
        for (PermissionActionType status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        return null;
    }
}
