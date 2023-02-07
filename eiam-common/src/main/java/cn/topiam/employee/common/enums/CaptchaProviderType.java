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
 * 验证码提供类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/7 20:01
 */
public enum CaptchaProviderType implements BaseEnum {

                                                     /**
                                                      * ALIYUN
                                                      */
                                                     ALIYUN("aliyun", "阿里云"),
                                                     /**
                                                      *TENCENT
                                                      */
                                                     TENCENT("tencent", "腾讯"),
                                                     /**
                                                      *GEE_TEST
                                                      */
                                                     GEE_TEST("geetest", "极验"),
                                                     /**
                                                      *HCAPTCHA
                                                      */
                                                     HCAPTCHA("hcaptcha", "Hcaptcha"),
                                                     /**
                                                      *RECAPTCHA
                                                      */
                                                     RECAPTCHA("recaptcha", "reCAPTCHA");

    @JsonValue
    private final String code;
    private final String desc;

    /**
     * 获取提供商
     *
     * @param code {@link String}
     * @return {@link CaptchaProviderType}
     */
    @EnumConvert
    public static CaptchaProviderType getType(String code) {
        CaptchaProviderType[] values = values();
        for (CaptchaProviderType status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        throw new NullPointerException("未获取到对应提供商");
    }

    CaptchaProviderType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return null;
    }
}
