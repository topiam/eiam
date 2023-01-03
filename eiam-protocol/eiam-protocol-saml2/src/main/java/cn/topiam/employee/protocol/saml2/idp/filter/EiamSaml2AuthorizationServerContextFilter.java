/*
 * eiam-protocol-saml2 - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.saml2.idp.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.application.exception.AppNotExistException;
import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.common.entity.app.po.AppSaml2ConfigPO;
import cn.topiam.employee.common.repository.app.AppSaml2ConfigRepository;
import static cn.topiam.employee.common.constants.ProtocolConstants.APP_CODE;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/11/2 21:02
 */
public final class EiamSaml2AuthorizationServerContextFilter extends OncePerRequestFilter {

    private final RequestMatcher           endpointsMatcher;
    private final AppSaml2ConfigRepository appSaml2ConfigRepository;

    private final RequestMatcher           appAuthorizePathRequestMatcher = new AntPathRequestMatcher(
        ProtocolConstants.Saml2EndpointConstants.SAML2_AUTHORIZE_BASE_PATH + "/**");

    public EiamSaml2AuthorizationServerContextFilter(RequestMatcher endpointsMatcher,
                                                     AppSaml2ConfigRepository appSaml2ConfigRepository) {
        Assert.notNull(endpointsMatcher, "endpointsMatcher cannot be null");
        Assert.notNull(appSaml2ConfigRepository, "appSaml2ConfigRepository cannot be null");
        this.endpointsMatcher = endpointsMatcher;
        this.appSaml2ConfigRepository = appSaml2ConfigRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException,
                                                                      IOException {
        //@formatter:off
        try {
            //匹配
            if ( appAuthorizePathRequestMatcher.matches(request) && endpointsMatcher.matches(request)){
                //获取应用编码
                Map<String, String> variables = appAuthorizePathRequestMatcher.matcher(request).getVariables();
                String appCode = variables.get(APP_CODE);
                AppSaml2ConfigPO configPo = appSaml2ConfigRepository.findByAppCode(appCode);
                if (Objects.isNull(configPo)){
                    throw new AppNotExistException();
                }

                //封装上下文内容
                Map<String,Object> config=new HashMap<>(16);
                ApplicationContextHolder.setProviderContext(new ApplicationContext(configPo.getAppId(),configPo.getAppCode(), configPo.getAppTemplate(), configPo.getClientId(), configPo.getClientSecret(), config));
            }
            filterChain.doFilter(request, response);
        } finally {
            ApplicationContextHolder.resetProviderContext();
        }
        //@formatter:on
    }

}
