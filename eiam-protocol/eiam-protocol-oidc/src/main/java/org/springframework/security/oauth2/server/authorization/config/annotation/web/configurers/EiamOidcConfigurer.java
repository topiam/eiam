/*
 * eiam-protocol-oidc - Employee Identity and Access Management Program
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
package org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Configurer for OpenID Connect 1.0 support.
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/26 19:33
 */
public final class EiamOidcConfigurer extends AbstractOAuth2Configurer {
    private final Map<Class<? extends AbstractOAuth2Configurer>, AbstractOAuth2Configurer> configurers = new LinkedHashMap<>();
    private RequestMatcher                                                                 requestMatcher;

    /**
     * Restrict for internal use only.
     */
    EiamOidcConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
        addConfigurer(EiamOidcProviderConfigurationEndpointConfigurer.class,
            new EiamOidcProviderConfigurationEndpointConfigurer(objectPostProcessor));
        addConfigurer(EiamOidcUserInfoEndpointConfigurer.class,
            new EiamOidcUserInfoEndpointConfigurer(objectPostProcessor));
    }

    @Override
    void init(HttpSecurity httpSecurity) {
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        this.configurers.values().forEach(configurer -> {
            configurer.init(httpSecurity);
            requestMatchers.add(configurer.getRequestMatcher());
        });
        this.requestMatcher = new OrRequestMatcher(requestMatchers);
    }

    @Override
    void configure(HttpSecurity httpSecurity) {
        this.configurers.values().forEach(configurer -> configurer.configure(httpSecurity));
    }

    @Override
    RequestMatcher getRequestMatcher() {
        return this.requestMatcher;
    }

    @SuppressWarnings("unchecked")
    <T> T getConfigurer(Class<T> type) {
        return (T) this.configurers.get(type);
    }

    private <T extends AbstractOAuth2Configurer> void addConfigurer(Class<T> configurerType,
                                                                    T configurer) {
        this.configurers.put(configurerType, configurer);
    }
}
