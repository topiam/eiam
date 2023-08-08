/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import cn.topiam.employee.support.enums.SecretType;
import cn.topiam.employee.support.exception.enums.ExceptionStatus;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.HttpResponseUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.employee.support.constant.EiamConstants.CAPTCHA_CODE_SESSION;

/**
 * 认证失败
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/2 22:11
 */
public class ConsoleAuthenticationFailureHandler implements AuthenticationFailureHandler {

    /**
     * Called when an authentication attempt fails.
     *
     * @param request   the request during which the authentication attempt occurred.
     * @param response  the response.
     * @param exception the exception which was thrown to reject the authentication
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) {
        ApiRestResult<Object> result = ApiRestResult.builder()
            .status(ExceptionStatus.EX000101.getCode()).message(StringUtils
                .defaultString(exception.getMessage(), ExceptionStatus.EX000101.getMessage()))
            .build();
        request.getSession().removeAttribute(SecretType.LOGIN.getKey());
        request.getSession().removeAttribute(CAPTCHA_CODE_SESSION);
        HttpResponseUtils.flushResponseJson(response, HttpStatus.BAD_REQUEST.value(), result);
    }
}
