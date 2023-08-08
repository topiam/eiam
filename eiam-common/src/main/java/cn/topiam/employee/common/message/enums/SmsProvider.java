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
package cn.topiam.employee.common.message.enums;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * 短信平台
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/8/19
 */
public enum SmsProvider implements Serializable {

                                                 /**
                                                  * 阿里云
                                                  */
                                                 ALIYUN("aliyun", "阿里云"),
                                                 /**
                                                  * 腾讯云
                                                  */
                                                 TENCENT("tencent", "腾讯云"),
                                                 /**
                                                  * 七牛
                                                  */
                                                 QINIU("qiniu", "七牛");

    /**
     * code
     */
    @JsonValue
    private final String code;
    /**
     * desc
     */
    private final String desc;

    SmsProvider(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    @EnumConvert
    public static SmsProvider getType(String code) {
        SmsProvider[] values = values();
        for (SmsProvider status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        throw new NullPointerException("未找到该平台");
    }

    @Override
    public String toString() {
        return this.code;
    }
}
