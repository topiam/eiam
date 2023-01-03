/*
 * eiam-protocol-cas - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.cas.idp.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.application.exception.AppNotExistException;
import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.common.entity.app.po.AppCasConfigPO;
import cn.topiam.employee.common.repository.app.AppCasConfigRepository;
import static cn.topiam.employee.common.constants.ProtocolConstants.APP_CODE;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 22:37
 */
public class CasAuthorizationServerContextFilter extends OncePerRequestFilter {

    private final RequestMatcher         endpointsMatcher;
    private final AppCasConfigRepository appCasConfigRepository;

    private final RequestMatcher         appAuthorizePathRequestMatcher = new AntPathRequestMatcher(
        ProtocolConstants.CasEndpointConstants.CAS_AUTHORIZE_BASE_PATH + "/**");

    public CasAuthorizationServerContextFilter(RequestMatcher endpointsMatcher,
                                               AppCasConfigRepository appCasConfigRepository) {
        Assert.notNull(endpointsMatcher, "endpointsMatcher cannot be null");
        Assert.notNull(appCasConfigRepository, "appCasConfigRepository cannot be null");
        this.endpointsMatcher = endpointsMatcher;
        this.appCasConfigRepository = appCasConfigRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            //匹配
            if (appAuthorizePathRequestMatcher.matches(request)
                && endpointsMatcher.matches(request)) {
                //获取应用编码
                Map<String, String> variables = appAuthorizePathRequestMatcher.matcher(request)
                    .getVariables();
                String appCode = variables.get(APP_CODE);
                AppCasConfigPO configPo = appCasConfigRepository.findByAppCode(appCode);
                if (Objects.isNull(configPo)) {
                    throw new AppNotExistException();
                }

                //封装上下文内容
                Map<String, Object> config = new HashMap<>(16);
                ApplicationContextHolder.setProviderContext(new ApplicationContext(
                    configPo.getAppId(), configPo.getAppCode(), configPo.getAppTemplate(),
                    configPo.getClientId(), configPo.getClientSecret(), config));
            }
            filterChain.doFilter(request, response);
        } finally {
            ApplicationContextHolder.resetProviderContext();
        }
    }
}
