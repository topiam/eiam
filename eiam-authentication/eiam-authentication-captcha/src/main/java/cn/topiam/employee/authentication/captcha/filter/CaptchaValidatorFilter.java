/*
 * eiam-authentication-captcha - Employee Identity and Access Management Program
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
package cn.topiam.employee.authentication.captcha.filter;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.authentication.captcha.CaptchaValidator;
import cn.topiam.employee.common.constants.AuthorizeConstants;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.trace.TraceUtils;
import static cn.topiam.employee.common.constants.AuthorizeConstants.FORM_LOGIN;
import static cn.topiam.employee.support.constant.EiamConstants.CAPTCHA_CODE_SESSION;
import static cn.topiam.employee.support.exception.enums.ExceptionStatus.EX000102;
import static cn.topiam.employee.support.util.HttpResponseUtils.flushResponse;

/**
 * 验证码过滤器
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/10/23 22:34
 */
public class CaptchaValidatorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException,
                                                                      IOException {
        if (requiresAuthentication(request)) {
            TraceUtils.put(UUID.randomUUID().toString());
            boolean validate = captchaValidator.validate(request, response);
            if (!validate) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                flushResponse(response, JSONObject.toJSONString(ApiRestResult.builder()
                    .status(EX000102.getCode()).message(EX000102.getMessage()).build()));
                return;
            }
            filterChain.doFilter(request, response);
            TraceUtils.remove();
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 校验验证码
     *
     * @param captcha {@link String}
     * @return boolean
     */
    public boolean validate(String captcha) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        String value = String.valueOf(request.getSession().getAttribute(CAPTCHA_CODE_SESSION));
        return StringUtils.equals(value, captcha);
    }

    /**
     * 需要认证
     *
     * @param request {@link HttpServletRequest}
     * @return {@link Boolean}
     */
    protected boolean requiresAuthentication(HttpServletRequest request) {
        OrRequestMatcher requestMatcher = new OrRequestMatcher(
            //登录
            new AntPathRequestMatcher(FORM_LOGIN, HttpMethod.POST.name()),
            //发送OTP
            new AntPathRequestMatcher(AuthorizeConstants.LOGIN_OTP_SEND, HttpMethod.POST.name()));
        return requestMatcher.matches(request);
    }

    /**
     * CaptchaValidator
     */
    private final CaptchaValidator captchaValidator;

    /**
     *
     * @param captchaValidator {@link CaptchaValidator}
     */
    public CaptchaValidatorFilter(CaptchaValidator captchaValidator) {
        this.captchaValidator = captchaValidator;
    }

}
