/*
 * eiam-protocol-cas - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.cas.idp.endpoint;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.topiam.employee.application.ApplicationService;
import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.application.cas.CasApplicationService;
import cn.topiam.employee.application.cas.model.CasSsoModel;
import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.core.context.ServerContextHelp;

import lombok.RequiredArgsConstructor;
import static cn.topiam.employee.common.constants.AuthorizeConstants.FE_LOGIN;
import static cn.topiam.employee.protocol.cas.idp.constant.ProtocolConstants.SERVICE;

/**
 * @author yunzhang
 */
@RequiredArgsConstructor
public class CasIdpSingleSignOutEndpointFilter extends OncePerRequestFilter
                                               implements OrderedFilter {

    private static final RequestMatcher    REQUEST_MATCHER = new AntPathRequestMatcher(
        ProtocolConstants.CasEndpointConstants.CAS_LOGOUT_PATH);

    private final ApplicationServiceLoader applicationServiceLoader;

    private final SessionRegistry          sessionRegistry;

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (REQUEST_MATCHER.matches(request) && HttpMethod.GET.matches(request.getMethod())) {

            //获取应用配置
            ApplicationContext applicationContext = ApplicationContextHolder
                .getApplicationContext();
            ApplicationService applicationService = applicationServiceLoader
                .getApplicationService(applicationContext.getAppTemplate());
            CasSsoModel casSsoModel = ((CasApplicationService) applicationService)
                .getSsoModel(applicationContext.getAppId());

            //登出用户
            HttpSession session = request.getSession(false);
            if (session != null) {
                sessionRegistry.removeSessionInformation(session.getId());
            }

            String service = request.getParameter(SERVICE);
            //all service urls be filtered via the service management tool
            if (service != null && service.equals(casSsoModel.getClientServiceUrl())) {
                response.sendRedirect(service);
            } else {
                //跳转登录
                response.sendRedirect(ServerContextHelp.getPortalPublicBaseUrl() + FE_LOGIN);
            }
        }
    }

    public static RequestMatcher getRequestMatcher() {
        return REQUEST_MATCHER;
    }
}
