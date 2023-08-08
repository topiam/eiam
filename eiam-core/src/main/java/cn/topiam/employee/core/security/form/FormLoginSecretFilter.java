/*
 * eiam-core - Employee Identity and Access Management
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
package cn.topiam.employee.core.security.form;

import java.io.IOException;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.support.enums.SecretType;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.AesUtils;
import cn.topiam.employee.support.web.servlet.ParameterRequestWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY;

import static cn.topiam.employee.common.constant.AuthorizeConstants.FORM_LOGIN;
import static cn.topiam.employee.support.exception.enums.ExceptionStatus.EX900005;
import static cn.topiam.employee.support.util.HttpResponseUtils.flushResponse;

/**
 * 秘钥过滤器
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/10/23 21:34
 */
public class FormLoginSecretFilter extends OncePerRequestFilter {
    /**
     * 匹配路径
     */
    private final RequestMatcher requiresAuthenticationRequestMatcher = new AntPathRequestMatcher(
        FORM_LOGIN, HttpMethod.POST.name());

    /**
     * 需要认证
     *
     * @param request {@link HttpServletRequest}
     * @return {@link Boolean}
     */
    protected boolean requiresAuthentication(HttpServletRequest request) {
        return requiresAuthenticationRequestMatcher.matches(request);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException,
                                                                      IOException {
        if (requiresAuthentication(request)) {
            // 获取加密密码
            String password = request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);
            //拿到秘钥，解密
            try {
                String secret = (String) request.getSession()
                    .getAttribute(SecretType.LOGIN.getKey());
                password = AesUtils.decrypt(password, secret);
            } catch (Exception exception) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                flushResponse(response, JSONObject.toJSONString(ApiRestResult.builder()
                    .status(EX900005.getCode()).message(EX900005.getMessage()).build()));
                return;
            }
            ParameterRequestWrapper wrapper = new ParameterRequestWrapper(request);
            wrapper.addParameter(SPRING_SECURITY_FORM_PASSWORD_KEY, password);
            filterChain.doFilter(wrapper, response);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
