/*
 * eiam-protocol-oidc - Employee Identity and Access Management
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
package cn.topiam.eiam.protocol.oidc.configurers;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.eiam.protocol.oidc.endpoint.OidcProviderConfigurationEndpointFilter;
import cn.topiam.employee.protocol.code.configurer.AbstractConfigurer;
import static cn.topiam.employee.common.constant.ProtocolConstants.OidcEndpointConstants.WELL_KNOWN_OPENID_CONFIGURATION;

/**
 * OIDC 服务配置端点配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/27 21:09
 */
public final class OidcProviderConfigurationEndpointConfigurer extends AbstractConfigurer {

    private RequestMatcher requestMatcher;

    /**
     * Restrict for internal use only.
     */
    OidcProviderConfigurationEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    @Override
    public void init(HttpSecurity httpSecurity) {
        this.requestMatcher = new AntPathRequestMatcher(WELL_KNOWN_OPENID_CONFIGURATION,
            HttpMethod.GET.name());
    }

    @Override
    public void configure(HttpSecurity httpSecurity) {
        OidcProviderConfigurationEndpointFilter oidcProviderConfigurationEndpointFilter = new OidcProviderConfigurationEndpointFilter(
            getRequestMatcher());
        httpSecurity.addFilterBefore(postProcess(oidcProviderConfigurationEndpointFilter),
            AbstractPreAuthenticatedProcessingFilter.class);
    }

    @Override
    public RequestMatcher getRequestMatcher() {
        return this.requestMatcher;
    }
}
