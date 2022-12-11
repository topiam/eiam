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
package cn.topiam.employee.common.exception.handler;

import javax.validation.ConstraintViolationException;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import cn.topiam.employee.support.exception.TopIamException;

import lombok.AllArgsConstructor;

/**
 * 全局异常处理
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/20 20:55
 */
@AllArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Exception
     *
     * @return {@link ModelAndView}
     */
    @ExceptionHandler(value = Exception.class)
    public ModelAndView exception(WebRequest request, Exception e) {
        request.setAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE,
            HttpStatus.INTERNAL_SERVER_ERROR.value(), WebRequest.SCOPE_REQUEST);
        setExceptionAttribute(request, e);
        return new ModelAndView(serverProperties.getError().getPath());
    }

    /**
     * TopIamException
     *
     * @return {@link ModelAndView}
     */
    @ExceptionHandler(value = TopIamException.class)
    public ModelAndView topIamException(WebRequest request, TopIamException e) {
        request.setAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE, e.getHttpStatus().value(),
            WebRequest.SCOPE_REQUEST);
        return new ModelAndView(serverProperties.getError().getPath());
    }

    /**
     * BindException
     *
     * HandlerExceptionResolver 默认处理为400状态码，这里设置为500，并转发到异常处理页面。
     *
     * @return {@link ModelAndView}
     */
    @ExceptionHandler(value = BindException.class)
    public ModelAndView bindException(WebRequest request) {
        request.setAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE, HttpStatus.BAD_REQUEST.value(),
            WebRequest.SCOPE_REQUEST);
        return new ModelAndView(serverProperties.getError().getPath());
    }

    /**
     * ConstraintViolationException
     *
     * HandlerExceptionResolver 默认处理为400状态码，这里设置为500，并转发到异常处理页面。
     *
     * @return {@link ModelAndView}
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ModelAndView constraintViolationException(WebRequest request) {
        request.setAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE, HttpStatus.BAD_REQUEST.value(),
            WebRequest.SCOPE_REQUEST);
        return new ModelAndView(serverProperties.getError().getPath());
    }

    private void setExceptionAttribute(WebRequest request, Exception exception) {
        request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, exception,
            WebRequest.SCOPE_REQUEST);
        request.setAttribute(DefaultErrorAttributes.class.getName() + ".ERROR", exception,
            WebRequest.SCOPE_REQUEST);
        request.setAttribute(WebUtils.ERROR_EXCEPTION_TYPE_ATTRIBUTE, exception.getClass(),
            WebRequest.SCOPE_REQUEST);
    }

    /**
     * ServerProperties
     */
    private final ServerProperties serverProperties;
}
