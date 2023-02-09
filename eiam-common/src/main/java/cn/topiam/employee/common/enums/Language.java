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
 * 语言
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/9 21:04
 */
public enum Language implements BaseEnum {
                                          /**
                                           * 英语
                                           */
                                          EN("en", "英语"),
                                          /**
                                           * 中文
                                           */
                                          ZH("zh", "中文");

    /**
     * code
     */
    @JsonValue
    private final String locale;
    /**
     * desc
     */
    private final String desc;

    Language(String locale, String desc) {
        this.locale = locale;
        this.desc = desc;
    }

    public String getLocale() {
        return locale;
    }

    @Override
    public String getCode() {
        return locale;
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
    public static Language getType(String code) {
        Language[] values = values();
        for (Language status : values) {
            if (String.valueOf(status.getLocale()).equals(code)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.getLocale();
    }

}
