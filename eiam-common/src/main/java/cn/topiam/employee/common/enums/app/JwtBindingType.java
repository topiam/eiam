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
package cn.topiam.employee.common.enums.app;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.enums.BaseEnum;
import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * Jwt 跳转方式
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/02/15
 */
public enum JwtBindingType implements BaseEnum {
                                                /**
                                                 * redirect
                                                 */
                                                REDIRECT("get", HttpMethod.GET, "重定向跳转"),
                                                /**
                                                 * post
                                                 */
                                                POST("post", HttpMethod.POST, "页面请求跳转");

    /**
     * code
     */
    @JsonValue
    private final String     code;

    /**
     * http method
     */
    private final HttpMethod httpMethod;
    /**
     * desc
     */
    private final String     desc;

    JwtBindingType(String code, HttpMethod httpMethod, String desc) {
        this.code = code;
        this.httpMethod = httpMethod;
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

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    /**
     * 获取类型
     *
     * @param code {@link String}
     * @return {@link InitLoginType}
     */
    @EnumConvert
    public static JwtBindingType getType(String code) {
        JwtBindingType[] values = values();
        for (JwtBindingType status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
