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

import java.util.function.Function;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.oidc.web.OidcUserInfoEndpointFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.common.constants.ProtocolConstants;

/**
 * Configurer for OpenID Connect 1.0 UserInfo Endpoint.
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/26 19:24
 */
public final class EiamOidcUserInfoEndpointConfigurer extends AbstractOAuth2Configurer {
    private RequestMatcher                                            requestMatcher;
    private Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper;

    /**
     * Restrict for internal use only.
     */
    EiamOidcUserInfoEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    /**
     * Sets the {@link Function} used to extract claims from {@link OidcUserInfoAuthenticationContext}
     * to an instance of {@link OidcUserInfo} for the UserInfo response.
     *
     * <p>
     * The {@link OidcUserInfoAuthenticationContext} gives the mapper access to the {@link OidcUserInfoAuthenticationToken},
     * as well as, the following context attributes:
     * <ul>
     * <li>{@link OidcUserInfoAuthenticationContext#getAccessToken()} containing the bearer token used to make the request.</li>
     * <li>{@link OidcUserInfoAuthenticationContext#getAuthorization()} containing the {@link OidcIdToken} and
     * {@link OAuth2AccessToken} associated with the bearer token used to make the request.</li>
     * </ul>
     *
     * @param userInfoMapper the {@link Function} used to extract claims from {@link OidcUserInfoAuthenticationContext} to an instance of {@link OidcUserInfo}
     * @return the {@link EiamOidcUserInfoEndpointConfigurer} for further configuration
     */
    public EiamOidcUserInfoEndpointConfigurer userInfoMapper(Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
        return this;
    }

    @Override
    void init(HttpSecurity httpSecurity) {
        String userInfoEndpointUri = ProtocolConstants.OidcEndpointConstants.OIDC_USER_INFO_ENDPOINT;
        this.requestMatcher = new OrRequestMatcher(
            new AntPathRequestMatcher(userInfoEndpointUri, HttpMethod.GET.name()),
            new AntPathRequestMatcher(userInfoEndpointUri, HttpMethod.POST.name()));

        OidcUserInfoAuthenticationProvider oidcUserInfoAuthenticationProvider = new OidcUserInfoAuthenticationProvider(
            OAuth2ConfigurerUtils.getAuthorizationService(httpSecurity));
        if (this.userInfoMapper != null) {
            oidcUserInfoAuthenticationProvider.setUserInfoMapper(this.userInfoMapper);
        }
        httpSecurity.authenticationProvider(postProcess(oidcUserInfoAuthenticationProvider));
    }

    @Override
    void configure(HttpSecurity builder) {
        AuthenticationManager authenticationManager = builder
            .getSharedObject(AuthenticationManager.class);

        OidcUserInfoEndpointFilter oidcUserInfoEndpointFilter = new OidcUserInfoEndpointFilter(
            authenticationManager, ProtocolConstants.OidcEndpointConstants.OIDC_USER_INFO_ENDPOINT);
        builder.addFilterAfter(postProcess(oidcUserInfoEndpointFilter),
            FilterSecurityInterceptor.class);
    }

    @Override
    RequestMatcher getRequestMatcher() {
        return this.requestMatcher;
    }

}
