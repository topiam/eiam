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
package cn.topiam.employee.protocol.form;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.protocol.form.endpoint.FormInitSingleSignOnEndpointFilter;
import cn.topiam.employee.protocol.form.endpoint.FormSingleSignOnEndpointFilter;
import cn.topiam.employee.support.context.ApplicationContextHelp;

import freemarker.template.Configuration;
import static cn.topiam.employee.protocol.cas.util.ProtocolUtils.getApplicationServiceLoader;

/**
 * 认证配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/10 22:58
 */
public final class FormProtocolConfigurer<B extends HttpSecurityBuilder<B>> extends
                                         AbstractHttpConfigurer<FormProtocolConfigurer<B>, B> {

    @Override
    public void configure(B http) {
        ApplicationServiceLoader applicationServiceLoader = getApplicationServiceLoader(http);
        Configuration configuration = ApplicationContextHelp.getBean(Configuration.class);
        //Form 单点登录地址
        http.addFilterAfter(
            new FormSingleSignOnEndpointFilter(applicationServiceLoader, configuration),
            UsernamePasswordAuthenticationFilter.class);
        //发起Form表单登录过滤器
        http.addFilterAfter(
            new FormInitSingleSignOnEndpointFilter(applicationServiceLoader, configuration),
            FormSingleSignOnEndpointFilter.class);
    }

    public RequestMatcher getEndpointsMatcher() {
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        //Form 门户端发起登录
        requestMatchers.add(FormSingleSignOnEndpointFilter.getRequestMatcher());
        //Form 服务端发起登录
        requestMatchers.add(FormInitSingleSignOnEndpointFilter.getRequestMatcher());
        return new OrRequestMatcher(requestMatchers);
    }

}
