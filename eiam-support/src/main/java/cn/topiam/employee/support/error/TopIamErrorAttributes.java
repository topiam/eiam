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
package cn.topiam.employee.support.error;

import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.WebRequest;

import com.alibaba.fastjson2.JSON;

import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.trace.TraceUtils;
import static cn.topiam.employee.support.exception.TopIamException.BIND_EXCEPTION;
import static cn.topiam.employee.support.exception.TopIamException.CONSTRAINT_VIOLATION_EXCEPTION;
import static cn.topiam.employee.support.exception.enums.ExceptionStatus.EX900009;

/**
 * TopIAM
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/29 00:06
 */
public class TopIamErrorAttributes extends DefaultErrorAttributes {
    public static final String ERRORS     = "errors";
    public static final String ERROR      = "error";
    public static final String EXCEPTION  = "exception";
    public static final String REQUEST_ID = "requestId";
    public static final String MESSAGE    = "message";
    public static final String PATH       = "path";
    public static final String STATUS     = "status";
    public static final String TRACE      = "trace";
    public static final String SUCCESS    = "success";

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest request,
                                                  ErrorAttributeOptions options) {
        Throwable throwable = getError(request);
        Map<String, Object> attributes = super.getErrorAttributes(request, options);
        //业务异常
        if (throwable instanceof TopIamException) {
            attributes.put(MESSAGE, throwable.getMessage());
            attributes.put(STATUS, ((TopIamException) throwable).getErrorCode());
        }
        //绑定异常
        if (throwable instanceof BindException) {
            BindingResult bindingResult = ((BindException) throwable).getBindingResult();
            StringBuilder buffer = new StringBuilder();
            //解析原错误信息，封装后返回，此处返回非法的字段名称，原始值，错误信息
            for (FieldError error : bindingResult.getFieldErrors()) {
                buffer.append(error.getDefaultMessage()).append(",");
            }
            attributes.put(MESSAGE, buffer.substring(0, buffer.length() - 1));
            attributes.put(STATUS, BIND_EXCEPTION);
        }
        //约束违反异常
        if (throwable instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) throwable)
                .getConstraintViolations();
            StringBuffer errorMsg = new StringBuffer();
            constraintViolations.forEach(ex -> errorMsg.append(ex.getMessage()).append(","));
            attributes.put(MESSAGE, errorMsg.substring(0, errorMsg.length() - 1));
            attributes.put(STATUS, CONSTRAINT_VIOLATION_EXCEPTION);
        }
        //数据访问异常
        if (throwable instanceof DataAccessException) {
            attributes.put(MESSAGE, EX900009.getMessage());
            attributes.put(STATUS, EX900009.getCode());
        }
        //是否具有 errors
        if (attributes.containsKey(ERRORS)) {
            attributes.put(ERRORS, JSON.toJSONString((attributes.get(ERRORS))));
        }
        //请求ID
        attributes.put(REQUEST_ID, TraceUtils.get());
        attributes.put(SUCCESS, false);
        return attributes;
    }

}
