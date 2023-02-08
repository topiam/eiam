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
package cn.topiam.employee.protocol.saml2.idp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.common.repository.app.AppSaml2ConfigRepository;
import cn.topiam.employee.protocol.saml2.idp.endpoint.Saml2IdpMetadataEndpointFilter;
import cn.topiam.employee.protocol.saml2.idp.endpoint.Saml2IdpSingleSignOnEndpointFilter;
import cn.topiam.employee.protocol.saml2.idp.endpoint.Saml2IdpSingleSignOutEndpointFilter;
import cn.topiam.employee.protocol.saml2.idp.endpoint.Saml2InitSingleSignOnEndpointFilter;
import cn.topiam.employee.protocol.saml2.idp.filter.EiamSaml2AuthorizationServerContextFilter;
import static cn.topiam.employee.protocol.cas.util.ProtocolUtils.getApplicationServiceLoader;
import static cn.topiam.employee.protocol.saml2.idp.util.Saml2Utils.getAppSaml2ConfigRepository;
import static cn.topiam.employee.protocol.saml2.idp.util.Saml2Utils.getSessionRegistry;

/**
 * 认证配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/10 22:58
 */
public final class Saml2IdpConfigurer<B extends HttpSecurityBuilder<B>>
                                     extends AbstractHttpConfigurer<Saml2IdpConfigurer<B>, B> {

    @Override
    public void configure(B http) {
        AppSaml2ConfigRepository appSaml2ConfigRepository = getAppSaml2ConfigRepository(http);
        SessionRegistry sessionRegistry = getSessionRegistry(http);
        ApplicationServiceLoader applicationServiceLoader = getApplicationServiceLoader(http);
        http.addFilterAfter(new Saml2IdpSingleSignOnEndpointFilter(applicationServiceLoader),
            UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(new Saml2InitSingleSignOnEndpointFilter(applicationServiceLoader),
            Saml2IdpSingleSignOnEndpointFilter.class);
        //登出过滤器
        http.addFilterAfter(
            new Saml2IdpSingleSignOutEndpointFilter(applicationServiceLoader, sessionRegistry),
            Saml2InitSingleSignOnEndpointFilter.class);
        //元数据过滤器
        http.addFilterBefore(new Saml2IdpMetadataEndpointFilter(applicationServiceLoader),
            Saml2IdpSingleSignOnEndpointFilter.class);
        //SAML2 授权服务器应用上下文过滤器
        http.addFilterBefore(new EiamSaml2AuthorizationServerContextFilter(getEndpointsMatcher(),
            appSaml2ConfigRepository), Saml2IdpMetadataEndpointFilter.class);
    }

    public RequestMatcher getEndpointsMatcher() {
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        //SAML 元数据
        requestMatchers.add(Saml2IdpMetadataEndpointFilter.getRequestMatcher());
        //SAML SP发起登录
        requestMatchers.add(Saml2IdpSingleSignOnEndpointFilter.getRequestMatcher());
        //SAML IDP 发起登录
        requestMatchers.add(Saml2InitSingleSignOnEndpointFilter.getRequestMatcher());
        //SAML SP 登出
        requestMatchers.add(Saml2IdpSingleSignOutEndpointFilter.getRequestMatcher());
        return new OrRequestMatcher(requestMatchers);
    }
}
