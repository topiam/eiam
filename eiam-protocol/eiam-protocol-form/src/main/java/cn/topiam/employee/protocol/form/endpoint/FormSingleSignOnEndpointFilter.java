/*
 * eiam-protocol-form - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.form.endpoint;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.common.constants.ProtocolConstants;

import freemarker.template.Configuration;

/**
 * Saml 接受SP发起登录端点
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/7 22:46
 */
@SuppressWarnings("DuplicatedCode")
public class FormSingleSignOnEndpointFilter extends AbstractFormEndpointFilter {
    private static final RequestMatcher REQUEST_MATCHER = new AntPathRequestMatcher(
        ProtocolConstants.FormEndpointConstants.FORM_SSO_PATH);

    public FormSingleSignOnEndpointFilter(ApplicationServiceLoader applicationServiceLoader,
                                          Configuration cfg) {
        super(applicationServiceLoader, cfg);
    }

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request      {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @param filterChain  {@link FilterChain}
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {
        doFilter(REQUEST_MATCHER, request, response, filterChain);
    }

    public static RequestMatcher getRequestMatcher() {
        return REQUEST_MATCHER;
    }
}
