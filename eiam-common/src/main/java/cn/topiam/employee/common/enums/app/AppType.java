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
import cn.topiam.employee.common.enums.OrganizationType;
import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * 应用类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/22 20:21
 */
public enum AppType implements BaseEnum {

                                         /**
                                          * 定制应用
                                          */
                                         CUSTOM_MADE("custom_made", "定制应用"),
                                         /**
                                          * 标准应用
                                          */
                                         STANDARD("standard", "标准应用"),
                                         /**
                                          * 自研
                                          */
                                         SELF_DEVELOPED("self_developed", "自研应用"),
                                         /**
                                           * TSA
                                           */
                                         TSA("tsa", "TSA"),;

    /**
     * code
     */
    @JsonValue
    private final String code;
    /**
     * desc
     */
    private final String desc;

    AppType(String code, String desc) {
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
     * 获取来源
     *
     * @param code {@link String}
     * @return {@link OrganizationType}
     */
    @EnumConvert
    public static AppType getType(String code) {
        AppType[] values = values();
        for (AppType source : values) {
            if (String.valueOf(source.getCode()).equals(code)) {
                return source;
            }
        }
        return null;
    }

    public static AppType getName(String name) {
        AppType[] values = values();
        for (AppType source : values) {
            if (source.name().equals(name)) {
                return source;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
