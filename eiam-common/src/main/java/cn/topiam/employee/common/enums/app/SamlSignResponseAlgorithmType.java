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

import cn.topiam.employee.support.web.converter.EnumConvert;
import static org.opensaml.xmlsec.signature.support.SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256;

/**
 * Saml Response 使用的非对称算法
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/22 23:37
 */
public enum SamlSignResponseAlgorithmType {

                                           /**
                                            * Signature - Required RSAwithSHA256.
                                            */
                                           RSA_SHA256(ALGO_ID_SIGNATURE_RSA_SHA256);

    @JsonValue
    private final String value;

    SamlSignResponseAlgorithmType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @EnumConvert
    public static SamlSignResponseAlgorithmType getType(String code) {
        SamlSignResponseAlgorithmType[] values = values();
        for (SamlSignResponseAlgorithmType status : values) {
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
