/*
 * eiam-protocol-core - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.code;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;

import cn.topiam.employee.core.context.ContextService;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.security.savedredirect.HttpSessionRedirectCache;
import cn.topiam.employee.support.security.savedredirect.RedirectCache;
import cn.topiam.employee.support.security.web.AbstractAuthenticationEntryPoint;
import cn.topiam.employee.support.util.HttpResponseUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import static cn.topiam.employee.support.context.ServletContextService.isHtmlRequest;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/7/5 21:24
 */
public class UnauthorizedAuthenticationEntryPoint extends AbstractAuthenticationEntryPoint {
    private final Logger        logger        = LoggerFactory.getLogger(this.getClass());
    private final RedirectCache redirectCache = new HttpSessionRedirectCache();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        //记录
        redirectCache.saveRedirect(request, response, RedirectCache.RedirectType.REQUEST);
        //判断请求
        boolean isHtmlRequest = isHtmlRequest(request);
        // HTML
        if (isHtmlRequest) {
            //跳转前端SESSION过期路由
            response.sendRedirect(ContextService.getPortalLoginUrl());
            return;
        }
        // JSON
        ApiRestResult<Object> result = ApiRestResult.builder()
            .status(String.valueOf(UNAUTHORIZED.value()))
            .message(Objects.toString(authException.getMessage(), UNAUTHORIZED.getReasonPhrase()))
            .build();
        HttpResponseUtils.flushResponseJson(response, UNAUTHORIZED.value(), result);
    }
}
