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
 * MFA 模式
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/19 23:04
 */
public enum MfaMode implements BaseEnum {
                                         /**
                                          * 常开模式
                                          */
                                         ALWAYS("always", "常开模式"),
                                         /**
                                          * 关闭
                                          */
                                         NONE("none", "关闭"),
                                         /**
                                          * 智能模式
                                          */
                                         SMART("smart", "智能模式");

    /**
     * code
     */
    @JsonValue
    private final String code;
    /**
     * desc
     */
    private final String desc;

    MfaMode(String code, String desc) {
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
    public static MfaMode getType(String code) {
        MfaMode[] values = values();
        for (MfaMode status : values) {
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
