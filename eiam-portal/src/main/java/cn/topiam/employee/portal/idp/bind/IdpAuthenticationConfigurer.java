/*
 * eiam-portal - Employee Identity and Access Management Program
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
package cn.topiam.employee.portal.idp.bind;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.authentication.common.service.UserIdpService;
import cn.topiam.employee.common.repository.account.UserIdpRepository;
import cn.topiam.employee.portal.handler.PortalAuthenticationFailureHandler;
import cn.topiam.employee.portal.handler.PortalAuthenticationSuccessHandler;

/**
 * IDP Authentication Configurer
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/10 22:58
 */
public final class IdpAuthenticationConfigurer<H extends HttpSecurityBuilder<H>> extends
                                              AbstractAuthenticationFilterConfigurer<H, IdpAuthenticationConfigurer<H>, IdpBindUserAuthenticationFilter> {

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
    public void init(H http) throws Exception {
        //设置登录成功失败处理器
        super.successHandler(new PortalAuthenticationSuccessHandler());
        super.failureHandler(new PortalAuthenticationFailureHandler());
        //MFA认证
        IdpBindUserAuthenticationFilter loginAuthenticationFilter = new IdpBindUserAuthenticationFilter(
            userIdpService, userIdpRepository, passwordEncoder, auditEventPublish);
        this.setAuthenticationFilter(loginAuthenticationFilter);
        //处理URL
        super.loginProcessingUrl(IdpBindUserAuthenticationFilter.DEFAULT_FILTER_PROCESSES_URI);
        super.init(http);
    }

    @Override
    public void configure(H http) throws Exception {
        http.addFilterBefore(this.getAuthenticationFilter(), OAuth2LoginAuthenticationFilter.class);
        super.configure(http);
    }

    private final UserIdpService    userIdpService;
    private final UserIdpRepository userIdpRepository;
    private final PasswordEncoder   passwordEncoder;
    private final AuditEventPublish auditEventPublish;

    public IdpAuthenticationConfigurer(UserIdpService userIdpService,
                                       UserIdpRepository userIdpRepository,
                                       PasswordEncoder passwordEncoder,
                                       AuditEventPublish auditEventPublish) {
        this.userIdpService = userIdpService;
        this.userIdpRepository = userIdpRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditEventPublish = auditEventPublish;
    }
}
