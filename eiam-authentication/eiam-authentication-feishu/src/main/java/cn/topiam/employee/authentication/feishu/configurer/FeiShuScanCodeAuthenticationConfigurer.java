/*
 * eiam-authentication-feishu - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.feishu.configurer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import cn.topiam.employee.authentication.common.service.UserIdpService;
import cn.topiam.employee.authentication.feishu.filter.FeiShuAuthorizationRequestGetFilter;
import cn.topiam.employee.authentication.feishu.filter.FeiShuLoginAuthenticationFilter;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;

/**
 * 认证配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/19 23:58
 */
public final class FeiShuScanCodeAuthenticationConfigurer extends
                                                          AbstractAuthenticationFilterConfigurer<HttpSecurity, FeiShuScanCodeAuthenticationConfigurer, FeiShuLoginAuthenticationFilter> {

    private final IdentityProviderRepository identityProviderRepository;
    private final UserIdpService             userIdpService;

    public FeiShuScanCodeAuthenticationConfigurer(IdentityProviderRepository identityProviderRepository,
                                                  UserIdpService userIdpService) {
        Assert.notNull(identityProviderRepository, "identityProviderRepository must not be null");
        Assert.notNull(userIdpService, "userIdpService must not be null");
        this.identityProviderRepository = identityProviderRepository;
        this.userIdpService = userIdpService;
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

    @Override
    public void init(HttpSecurity http) throws Exception {
        //微信扫码登录认证
        FeiShuLoginAuthenticationFilter loginAuthenticationFilter = new FeiShuLoginAuthenticationFilter(
            identityProviderRepository, userIdpService);
        this.setAuthenticationFilter(loginAuthenticationFilter);
        //处理URL
        super.loginProcessingUrl(FeiShuLoginAuthenticationFilter.DEFAULT_FILTER_PROCESSES_URI);
        super.init(http);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        //微信扫码请求重定向
        FeiShuAuthorizationRequestGetFilter requestRedirectFilter = new FeiShuAuthorizationRequestGetFilter(
            identityProviderRepository);
        http.addFilterBefore(requestRedirectFilter, OAuth2AuthorizationRequestRedirectFilter.class);
        http.addFilterBefore(this.getAuthenticationFilter(), OAuth2LoginAuthenticationFilter.class);
        super.configure(http);
    }

    public RequestMatcher getRequestMatcher() {
        return new OrRequestMatcher(FeiShuAuthorizationRequestGetFilter.getRequestMatcher(),
            FeiShuLoginAuthenticationFilter.getRequestMatcher());
    }

    public static FeiShuScanCodeAuthenticationConfigurer feiShuScanCode(IdentityProviderRepository identityProviderRepository,
                                                                        UserIdpService userIdpService) {
        return new FeiShuScanCodeAuthenticationConfigurer(identityProviderRepository,
            userIdpService);
    }
}
