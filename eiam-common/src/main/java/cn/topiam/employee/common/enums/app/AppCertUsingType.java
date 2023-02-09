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
 * 证书使用类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/31 22:28
 */
public enum AppCertUsingType implements BaseEnum {
                                                  /**
                                                   * OIDC JWK
                                                   */
                                                  OIDC_JWK("oidc_jwk", "OIDC JWK"),
                                                  /**
                                                   * SAML签名
                                                   */
                                                  SAML_SIGN("saml_sign", "SAML 签名"),
                                                  /**
                                                   * SAML 加密
                                                   */
                                                  SAML_ENCRYPT("saml_encrypt", "SAML 加密");

    @JsonValue
    private final String code;
    /**
     * name
     */
    private final String desc;

    AppCertUsingType(String code, String desc) {
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
    public static AppCertUsingType getType(String code) {
        AppCertUsingType[] values = values();
        for (AppCertUsingType type : values) {
            if (String.valueOf(type.getCode()).equals(code)) {
                return type;
            }
        }
        return null;
    }
}
