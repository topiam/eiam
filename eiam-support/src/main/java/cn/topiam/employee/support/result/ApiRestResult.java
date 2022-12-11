/*
 * eiam-support - Employee Identity and Access Management Program
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
package cn.topiam.employee.support.result;

import java.io.Serial;
import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import cn.topiam.employee.support.exception.enums.ExceptionStatus;
import cn.topiam.employee.support.trace.TraceUtils;

import io.swagger.v3.oas.annotations.media.Schema;
import static cn.topiam.employee.support.exception.TopIamException.DEFAULT_EXCEPTION;

/**
 * API REST
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2019/5/17 23:44
 */
@Schema(description = "API REST")
public class ApiRestResult<T> implements Serializable {
    /**
     * 成功
     */
    public static final String SUCCESS          = "200";
    @Serial
    private static final long  serialVersionUID = -5396280450442040415L;
    /**
     * 状态
     */
    @Schema(description = "状态")
    private String             status;
    /**
     * 内容消息
     */
    @Schema(description = "消息")
    private String             message;
    /**
     * 返回内容
     */
    @Schema(description = "返回内容")
    private T                  result;
    /**
     * success
     */
    @Schema(description = "是否成功")
    private Boolean            success;

    /**
     * 请求ID
     */
    @Schema(description = "请求ID")
    private String             requestId;

    public ApiRestResult() {
    }

    /**
     * ApiRestResult
     *
     * @param status  {@link String}
     * @param message {@link String}
     * @param result  {@link String}
     */
    private ApiRestResult(String status, String message, T result) {
        this.status = status;
        this.message = message;
        this.result = result;
        this.success = status.equals(SUCCESS);
        this.requestId = TraceUtils.get();
    }

    /**
     * @param result result
     * @return 结果
     */
    public ApiRestResult<T> result(T result) {
        this.result = result;
        return this;
    }

    /**
     * getResult
     *
     * @return 结果
     */
    public T getResult() {
        return result;
    }

    /**
     * getStatus
     *
     * @return 状态
     */
    public String getStatus() {
        if (StringUtils.isBlank(status)) {
            return SUCCESS;
        }
        return status;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * status
     *
     * @param status {@link String } status
     * @return {@link ApiRestResult}
     */
    public ApiRestResult<T> status(String status) {
        this.status = status;
        return this;
    }

    /**
     * getMessage
     *
     * @return {@link String} message
     */
    public String getMessage() {
        if (StringUtils.isBlank(message)) {
            return "操作成功";
        }
        return message;
    }

    /**
     * message
     *
     * @param msg msg
     * @return {@link ApiRestResult}
     */
    public ApiRestResult<T> message(String msg) {
        this.message = msg;
        return this;
    }

    /**
     * message
     *
     * @param success success
     * @return {@link ApiRestResult}
     */
    public ApiRestResult<T> success(Boolean success) {
        this.success = success;
        return this;
    }

    /**
     * message
     *
     * @param data data
     * @return {@link ApiRestResult}
     */
    public static <T> ApiRestResult<T> ok(T data) {
        return new ApiRestResult<>(SUCCESS, "操作成功", data);
    }

    /**
     * 成功 data null
     *
     * @param <T> type
     * @return {@link ApiRestResult}
     */
    public static <T> ApiRestResult<T> ok() {
        return new ApiRestResult<>(SUCCESS, "操作成功", null);
    }

    /**
     * 默认服务器内部错误
     *
     * @param <T> type
     * @return {@link ApiRestResult}
     */
    public static <T> ApiRestResult<T> err() {
        return new ApiRestResult<>(ExceptionStatus.EX900001.getCode(),
            ExceptionStatus.EX900001.getMessage(), null);
    }

    /**
     * 默认服务器内部错误
     *
     * @param <T>  type
     * @param data data
     * @return {@link ApiRestResult}
     */
    public static <T> ApiRestResult<T> err(T data) {
        return new ApiRestResult<>(ExceptionStatus.EX900001.getCode(),
            ExceptionStatus.EX900001.getMessage(), data);
    }

    /**
     * 自定义 错误码和错误信息
     *
     * @param msg  {@link String }
     * @param code {@link String } 错误码
     * @return {@link ApiRestResult}
     */
    public static <T> ApiRestResult<T> err(String msg, String code) {
        return new ApiRestResult<>(code, msg, null);
    }

    /**
     * toString
     *
     * @return {@link String}
     */
    @Override
    public String toString() {
        return "ApiRestResult{" + "result=" + result + ", status='" + status + '\'' + ", message='"
               + message + '\'' + "} " + super.toString();
    }

    /**
     * build
     *
     * @return {@link ApiRestResult}
     */
    public ApiRestResult<T> build() {
        return new ApiRestResult<>(this.status, this.message, this.result);
    }

    public static <T> RestResultBuilder<T> builder() {
        return new RestResultBuilder<>();
    }

    public static class RestResultBuilder<T> {
        private String status;
        private String message;
        private T      result;

        RestResultBuilder() {
        }

        public RestResultBuilder<T> status(String status) {
            this.status = status;
            return this;
        }

        public RestResultBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public RestResultBuilder<T> result(T result) {
            this.result = result;
            return this;
        }

        public ApiRestResult<T> build() {
            if (ObjectUtils.isEmpty(status)) {
                status = SUCCESS;
            }
            if (!StringUtils.equals(status, SUCCESS) && ObjectUtils.isEmpty(message)) {
                message = DEFAULT_EXCEPTION;
            }
            if (this.result instanceof Boolean) {
                if (this.result.equals(Boolean.FALSE)) {
                    message = DEFAULT_EXCEPTION;
                }
            }
            return new ApiRestResult<>(this.status, this.message, this.result);
        }

        @Override
        public String toString() {
            return "RestResultBuilder{" + "status='" + status + '\'' + ", message='" + message
                   + '\'' + ", result=" + result + '}';
        }
    }
}
