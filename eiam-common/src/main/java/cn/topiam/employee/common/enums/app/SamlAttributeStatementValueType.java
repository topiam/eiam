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
import static org.apache.commons.text.StringSubstitutor.DEFAULT_VAR_END;
import static org.apache.commons.text.StringSubstitutor.DEFAULT_VAR_START;

/**
 * Saml 2 AttributeStatementValue 类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/22 23:26
 */
public enum SamlAttributeStatementValueType implements BaseEnum {
                                                                 /**
                                                                  * 手机号
                                                                  */
                                                                 PHONE("user.phone",
                                                                       DEFAULT_VAR_START
                                                                                     + "user.phone"
                                                                                     + DEFAULT_VAR_END),
                                                                 /**
                                                                  * 用户名
                                                                  */
                                                                 USERNAME("user.username",
                                                                          DEFAULT_VAR_START + "user.username"
                                                                                           + DEFAULT_VAR_END),
                                                                 /**
                                                                  * 昵称
                                                                  */
                                                                 NICK_NAME("user.nickName",
                                                                           DEFAULT_VAR_START + "user.nickName"
                                                                                            + DEFAULT_VAR_END),
                                                                 /**
                                                                  * 邮箱
                                                                  */
                                                                 EMAIL("user.email",
                                                                       DEFAULT_VAR_START
                                                                                     + "user.email"
                                                                                     + DEFAULT_VAR_END),
                                                                 /**
                                                                  * 应用用户名
                                                                  */
                                                                 APP_USERNAME("app_user.username",
                                                                              DEFAULT_VAR_START + "app_user.username"
                                                                                                   + DEFAULT_VAR_END);

    @JsonValue
    private final String code;
    private final String expression;

    SamlAttributeStatementValueType(String code, String expression) {
        this.code = code;
        this.expression = expression;
    }

    @EnumConvert
    public static SamlAttributeStatementValueType getType(String code) {
        SamlAttributeStatementValueType[] values = values();
        for (SamlAttributeStatementValueType status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return null;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return code;
    }
}
