/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.web.converter.EnumConvert;

import lombok.Getter;

/**
 * 事件状态
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/11/6 21:57
 */
@Getter
public enum EventStatus {
                         /**
                          * 成功
                          */
                         SUCCESS("success", "成功"),
                         /**
                          * 失败
                          */
                         FAIL("fail", "失败");

    @JsonValue
    private final String code;
    private final String desc;

    EventStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取类型
     *
     * @param code {@link String}
     * @return {@link EventStatus}
     */
    @EnumConvert
    public static EventStatus getType(String code) {
        EventStatus[] values = values();
        for (EventStatus status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        return null;
    }
}
