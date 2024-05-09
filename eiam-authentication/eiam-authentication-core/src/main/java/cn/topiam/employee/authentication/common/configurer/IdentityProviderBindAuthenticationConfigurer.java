/*
 * eiam-authentication-core - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.common.configurer;

import java.util.Objects;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import cn.topiam.employee.authentication.common.IdentityProviderAuthenticationService;
import cn.topiam.employee.authentication.common.authentication.IdentityProviderAuthenticationProvider;
import cn.topiam.employee.authentication.common.filter.IdentityProviderBindUserAuthenticationFilter;
import static cn.topiam.employee.authentication.common.filter.IdentityProviderBindUserAuthenticationFilter.DEFAULT_FILTER_PROCESSES_URI;
import static cn.topiam.employee.support.security.util.HttpSecurityConfigUtils.getUserDetailsService;
import static cn.topiam.employee.support.security.util.HttpSecurityFilterOrderRegistrationUtils.putFilterBefore;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn  on  2023/8/6 23:43
 */
public class IdentityProviderBindAuthenticationConfigurer extends
                                                          AbstractAuthenticationFilterConfigurer<HttpSecurity, IdentityProviderBindAuthenticationConfigurer, IdentityProviderBindUserAuthenticationFilter> {

    private UserDetailsService                          userDetailsService;

    private final IdentityProviderAuthenticationService identityProviderAuthenticationService;

    private final PasswordEncoder                       passwordEncoder;

    /**
     * Creates a new instance with minimal defaults
     */
    public IdentityProviderBindAuthenticationConfigurer(UserDetailsService userDetailsService,
                                                        IdentityProviderAuthenticationService identityProviderAuthenticationService,
                                                        PasswordEncoder passwordEncoder) {
        super(new IdentityProviderBindUserAuthenticationFilter(), DEFAULT_FILTER_PROCESSES_URI);
        Assert.notNull(userDetailsService, "userDetailsService must not be null");
        Assert.notNull(identityProviderAuthenticationService, "userIdpService must not be null");
        Assert.notNull(identityProviderAuthenticationService, "passwordEncoder must not be null");
        this.identityProviderAuthenticationService = identityProviderAuthenticationService;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Creates a new instance with minimal defaults
     */
    public IdentityProviderBindAuthenticationConfigurer(IdentityProviderAuthenticationService identityProviderAuthenticationService,
                                                        PasswordEncoder passwordEncoder) {
        super(new IdentityProviderBindUserAuthenticationFilter(), DEFAULT_FILTER_PROCESSES_URI);
        Assert.notNull(identityProviderAuthenticationService, "userIdpService must not be null");
        Assert.notNull(identityProviderAuthenticationService, "passwordEncoder must not be null");
        this.identityProviderAuthenticationService = identityProviderAuthenticationService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void init(HttpSecurity http) throws Exception {
        super.init(http);
        putFilterBefore(http, getAuthenticationFilter(), OAuth2LoginAuthenticationFilter.class);
        //认证提供商
        if (Objects.isNull(userDetailsService)) {
            userDetailsService = getUserDetailsService(http);
        }
        http.authenticationProvider(new IdentityProviderAuthenticationProvider(userDetailsService,
            identityProviderAuthenticationService, passwordEncoder));
    }

    /**
     * Create the {@link RequestMatcher} given a loginProcessingUrl
     *
     * @param loginProcessingUrl creates the {@link RequestMatcher} based upon the
     *                           loginProcessingUrl
     * @return the {@link RequestMatcher} to use based upon the loginProcessingUrl
     */
    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl);
    }

    public static IdentityProviderBindAuthenticationConfigurer idpBind(UserDetailsService userDetailsService,
                                                                       IdentityProviderAuthenticationService identityProviderAuthenticationService,
                                                                       PasswordEncoder passwordEncoder) {
        return new IdentityProviderBindAuthenticationConfigurer(userDetailsService,
            identityProviderAuthenticationService, passwordEncoder);
    }

    public static IdentityProviderBindAuthenticationConfigurer idpBind(IdentityProviderAuthenticationService identityProviderAuthenticationService,
                                                                       PasswordEncoder passwordEncoder) {
        return new IdentityProviderBindAuthenticationConfigurer(
            identityProviderAuthenticationService, passwordEncoder);
    }
}
