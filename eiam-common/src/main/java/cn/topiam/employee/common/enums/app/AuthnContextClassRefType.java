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

import org.opensaml.saml.saml2.core.AuthnContext;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * Authn 上下文类引用类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/4 23:44
 */
public enum AuthnContextClassRefType {

                                      /**
                                       * URI for unspecified authentication context.
                                       */
                                      UNSPECIFIED_AUTHN_CTX(AuthnContext.UNSPECIFIED_AUTHN_CTX),
                                      /**
                                       * URI for Password authentication context.
                                       */
                                      PASSWORD_AUTHN_CTX(AuthnContext.PASSWORD_AUTHN_CTX),

                                      /**
                                       * URI for Password Protected Transport authentication context.
                                       */
                                      PPT_AUTHN_CTX(AuthnContext.PPT_AUTHN_CTX);

    @JsonValue
    private final String value;

    AuthnContextClassRefType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @EnumConvert
    public static AuthnContextClassRefType getType(String code) {
        AuthnContextClassRefType[] values = values();
        for (AuthnContextClassRefType status : values) {
            if (String.valueOf(status.getValue()).equals(code)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
