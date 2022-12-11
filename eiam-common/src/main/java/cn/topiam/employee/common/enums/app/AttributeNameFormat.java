/*
 * eiam-common - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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

import org.opensaml.saml.saml2.core.Attribute;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * 属性 AttributeNameFormat
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/31 22:02
 */
public enum AttributeNameFormat {
                                 /**
                                  * URI for unspecified name format.
                                  */
                                 UNSPECIFIED(Attribute.UNSPECIFIED),
                                 /**
                                  * basic
                                  */
                                 BASIC(Attribute.BASIC),
                                 /**
                                  * URI_REFERENCE
                                  */
                                 URI_REFERENCE(Attribute.URI_REFERENCE);

    @JsonValue
    private final String value;

    AttributeNameFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @EnumConvert
    public static AttributeNameFormat getType(String code) {
        AttributeNameFormat[] values = values();
        for (AttributeNameFormat status : values) {
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
