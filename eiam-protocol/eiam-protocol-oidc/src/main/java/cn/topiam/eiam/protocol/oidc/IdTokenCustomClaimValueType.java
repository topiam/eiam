/*
 * eiam-protocol-oidc - Employee Identity and Access Management
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
package cn.topiam.eiam.protocol.oidc;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.enums.BaseEnum;
import cn.topiam.employee.support.web.converter.EnumConvert;
import static org.apache.commons.text.StringSubstitutor.DEFAULT_VAR_END;
import static org.apache.commons.text.StringSubstitutor.DEFAULT_VAR_START;

/**
 * IdTokenCustomClaimValueType
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/5/22 23:26
 */
public enum IdTokenCustomClaimValueType implements BaseEnum {
                                                             /**
                                                              * 手机号
                                                              */
                                                             PHONE("user.phone",
                                                                   DEFAULT_VAR_START + "user.phone"
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
                                                                   DEFAULT_VAR_START + "user.email"
                                                                                 + DEFAULT_VAR_END),

                                                             /**
                                                              * 员工工号
                                                              */
                                                             USER_EMPLOYEE_NUMBER("user.employee_number",
                                                                                  DEFAULT_VAR_START + "user.employee_number"
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

    IdTokenCustomClaimValueType(String code, String expression) {
        this.code = code;
        this.expression = expression;
    }

    @EnumConvert
    public static IdTokenCustomClaimValueType getType(String code) {
        IdTokenCustomClaimValueType[] values = values();
        for (IdTokenCustomClaimValueType status : values) {
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
