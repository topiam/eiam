/*
 * eiam-openapi - Employee Identity and Access Management
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
package cn.topiam.employee.openapi.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import cn.topiam.employee.openapi.constants.OpenApiStatus;
import cn.topiam.employee.support.trace.TraceUtils;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenApiResponse
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/25 03:40
 */
@Data
public class OpenApiResponse<T> {

    /**
     * code
     */
    @JsonProperty(value = "code")
    @Schema(description = "编码")
    private final String code;

    /**
     * msg
     */
    @JsonProperty(value = "msg")
    @Schema(description = "编码说明")
    private final String msg;

    /**
     * result
     */
    @JsonProperty(value = "result")
    @Schema(description = "返回结果")
    private T            result;

    /**
     * requestId
     */
    @JsonProperty(value = "requestId")
    @Schema(description = "请求 ID")
    private final String requestId;

    public OpenApiResponse(OpenApiStatus status) {
        this.code = status.getCode();
        this.msg = status.getDesc();
        this.requestId = TraceUtils.get();
    }

    public OpenApiResponse(String code, String message) {
        this.code = code;
        this.msg = message;
        this.requestId = TraceUtils.get();
    }

    public OpenApiResponse(OpenApiStatus status, T result) {
        this.code = status.getCode();
        this.msg = status.getDesc();
        this.result = result;
        this.requestId = TraceUtils.get();
    }

    public static OpenApiResponse<Void> success() {
        return new OpenApiResponse<>(OpenApiStatus.SUCCESS);
    }

    public static <T> OpenApiResponse<T> success(T result) {
        return new OpenApiResponse<>(OpenApiStatus.SUCCESS, result);
    }
}
