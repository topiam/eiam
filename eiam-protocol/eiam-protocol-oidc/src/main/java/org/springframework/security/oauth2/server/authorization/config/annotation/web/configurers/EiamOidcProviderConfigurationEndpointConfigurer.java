/*
 * eiam-protocol-oidc - Employee Identity and Access Management Program
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
package org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers;

import java.util.function.Consumer;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.oidc.OidcProviderConfiguration;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.protocol.oidc.endpoint.EiamOidcProviderConfigurationEndpointFilter;
import cn.topiam.employee.protocol.oidc.util.EiamOAuth2Utils;

/**
 * 提供商端点适配器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/11/9 22:53
 */
public final class EiamOidcProviderConfigurationEndpointConfigurer extends
                                                                   AbstractOAuth2Configurer {
    private RequestMatcher                              requestMatcher;
    private Consumer<OidcProviderConfiguration.Builder> defaultProviderConfigurationCustomizer;

    /**
     * Restrict for internal use only.
     */
    EiamOidcProviderConfigurationEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    void addDefaultProviderConfigurationCustomizer(Consumer<OidcProviderConfiguration.Builder> defaultProviderConfigurationCustomizer) {
        this.defaultProviderConfigurationCustomizer = this.defaultProviderConfigurationCustomizer == null
            ? defaultProviderConfigurationCustomizer
            : this.defaultProviderConfigurationCustomizer
                .andThen(defaultProviderConfigurationCustomizer);
    }

    @Override
    void init(HttpSecurity httpSecurity) {
        this.requestMatcher = new AntPathRequestMatcher(
            ProtocolConstants.OidcEndpointConstants.WELL_KNOWN_OPENID_CONFIGURATION,
            HttpMethod.GET.name());
    }

    @Override
    void configure(HttpSecurity httpSecurity) {
        EiamOidcProviderConfigurationEndpointFilter oidcProviderConfigurationEndpointFilter = new EiamOidcProviderConfigurationEndpointFilter(
            EiamOAuth2Utils.getAppOidcConfigRepository(httpSecurity), this.requestMatcher);
        httpSecurity.addFilterBefore(postProcess(oidcProviderConfigurationEndpointFilter),
            AbstractPreAuthenticatedProcessingFilter.class);
    }

    @Override
    RequestMatcher getRequestMatcher() {
        return this.requestMatcher;
    }

}
