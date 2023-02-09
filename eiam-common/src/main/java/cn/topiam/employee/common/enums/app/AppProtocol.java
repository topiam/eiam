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
import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * 应用模板
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/11/29 22:27
 */
public enum AppProtocol implements BaseEnum {
                                             /**
                                              * OIDC
                                              */
                                             OIDC("oidc", "OIDC"),

                                             /**
                                              * SAML
                                              */
                                             SAML2("saml2", "SAML 2.0"),

                                             /**
                                              * CAS
                                              */
                                             CAS("cas", "CAS"),

                                             /**
                                              * JWT
                                              */
                                             JWT("jwt", "JWT"),

                                             /**
                                              * FORM表单
                                              */
                                             FORM("form", "表单代填"),

                                             /**
                                              * TSA
                                              */
                                             TSA("tsa", "TSA");

    @JsonValue
    private final String code;
    /**
     * name
     */
    private final String desc;

    AppProtocol(String code, String desc) {
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

    @EnumConvert
    public static AppProtocol getType(String code) {
        AppProtocol[] values = values();
        for (AppProtocol source : values) {
            if (String.valueOf(source.getCode()).equals(code)) {
                return source;
            }
        }
        return null;
    }
}
