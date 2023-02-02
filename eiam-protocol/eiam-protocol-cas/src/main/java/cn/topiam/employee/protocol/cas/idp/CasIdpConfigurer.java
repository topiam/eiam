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
package cn.topiam.employee.protocol.cas.idp;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.common.repository.app.AppCasConfigRepository;
import cn.topiam.employee.protocol.cas.idp.auth.CentralAuthenticationService;
import cn.topiam.employee.protocol.cas.idp.endpoint.Cas10IdpValidateEndpointFilter;
import cn.topiam.employee.protocol.cas.idp.endpoint.Cas30IdpValidateEndpointFilter;
import cn.topiam.employee.protocol.cas.idp.endpoint.CasIdpSingleSignOnEndpointFilter;
import cn.topiam.employee.protocol.cas.idp.filter.CasAuthorizationServerContextFilter;
import cn.topiam.employee.protocol.cas.idp.util.CasUtils;
import static cn.topiam.employee.protocol.cas.idp.util.CasUtils.*;

/**
 * 认证配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 16:23
 */
public class CasIdpConfigurer<B extends HttpSecurityBuilder<B>>
                             extends AbstractHttpConfigurer<CasIdpConfigurer<B>, B> {

    @Override
    public void configure(B http) {
        AppCasConfigRepository appCasConfigRepository = CasUtils.getAppCasConfigRepository(http);
        ApplicationServiceLoader applicationServiceLoader = getApplicationServiceLoader(http);
        SessionRegistry sessionRegistry = getSessionRegistry(http);
        CentralAuthenticationService centralAuthenticationService = getCentralAuthenticationService(
            http);
        DocumentBuilder documentBuilder = getDocumentBuilder(http);
        //CAS 登陆过滤器
        http.addFilterAfter(new CasIdpSingleSignOnEndpointFilter(applicationServiceLoader,
            centralAuthenticationService), UsernamePasswordAuthenticationFilter.class);

        //cas 1.0 验证过滤器
        http.addFilterBefore(
            new Cas10IdpValidateEndpointFilter(applicationServiceLoader, sessionRegistry,
                centralAuthenticationService, documentBuilder),
            CasIdpSingleSignOnEndpointFilter.class);

        //cas 3.0 & 2.0验证过滤器
        http.addFilterBefore(
            new Cas30IdpValidateEndpointFilter(applicationServiceLoader, sessionRegistry,
                centralAuthenticationService, documentBuilder),
            Cas10IdpValidateEndpointFilter.class);

        //CAS 授权服务器应用上下文过滤器
        http.addFilterBefore(
            new CasAuthorizationServerContextFilter(getEndpointsMatcher(), appCasConfigRepository),
            Cas30IdpValidateEndpointFilter.class);
    }

    public RequestMatcher getEndpointsMatcher() {
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        requestMatchers.add(CasIdpSingleSignOnEndpointFilter.getRequestMatcher());
        requestMatchers.add(Cas30IdpValidateEndpointFilter.getRequestMatcher());
        requestMatchers.add(Cas10IdpValidateEndpointFilter.getRequestMatcher());
        return new OrRequestMatcher(requestMatchers);
    }
}
