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
package cn.topiam.employee.openapi.exception.handler;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import cn.topiam.employee.openapi.common.OpenApiResponse;
import cn.topiam.employee.openapi.constants.OpenApiStatus;
import cn.topiam.employee.openapi.exception.OpenApiException;

import lombok.AllArgsConstructor;

import jakarta.validation.ConstraintViolationException;

/**
 * 全局异常处理
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/20 21:55
 */
@AllArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Exception
     *
     * @return {@link OpenApiResponse}
     */
    @ExceptionHandler(value = Exception.class)
    public OpenApiResponse exception(Exception e) {
        return new OpenApiResponse(OpenApiStatus.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
    }

    /**
     * ConstraintViolationException
     *
     * @return {@link ModelAndView}
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public OpenApiResponse constraintViolationException(ConstraintViolationException e) {
        return new OpenApiResponse(OpenApiStatus.INVALID_PARAMETER.getCode(), e.getMessage());
    }

    /**
     * OpenApiException
     *
     * @return {@link OpenApiResponse}
     */
    @ExceptionHandler(value = OpenApiException.class)
    public OpenApiResponse exception(OpenApiException e) {
        return new OpenApiResponse(e.getCode(), e.getMessage());
    }
}
