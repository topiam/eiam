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
import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * 认证方式
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/31 22:45
 */
public enum AuthenticationType implements BaseEnum {
                                                    /**
                                                     * from表单
                                                     */
                                                    FORM("form", "FORM表单"),
                                                    /**
                                                     * 短信验证码
                                                     */
                                                    SMS("sms", "短信验证码"),
                                                    /**
                                                     * 社交认证
                                                     */
                                                    SOCIAL("social", "社交认证");

    @JsonValue
    private String code;

    private String desc;

    AuthenticationType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取认证平台
     *
     * @param code {@link String}
     * @return {@link AuthenticationType}
     */
    @EnumConvert
    public static AuthenticationType getType(String code) {
        AuthenticationType[] values = values();
        for (AuthenticationType status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        throw new NullPointerException("未获取到对应平台");
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
