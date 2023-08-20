/*
 * eiam-authentication-alipay - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.alipay.filter;

import java.io.IOException;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.authentication.common.filter.AbstractIdpAuthenticationProcessingFilter;
import cn.topiam.employee.authentication.common.service.UserIdpService;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.core.help.ServerHelp;
import cn.topiam.employee.support.util.HttpUrlUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.employee.authentication.common.IdentityProviderType.ALIPAY_OAUTH;
import static cn.topiam.employee.authentication.common.constant.AuthenticationConstants.PROVIDER_CODE;

/**
 * 支付宝 登录过滤器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/8/19 17:58
 */
@SuppressWarnings("DuplicatedCode")
public class AlipayLoginAuthenticationFilter extends AbstractIdpAuthenticationProcessingFilter {
    public final static String                DEFAULT_FILTER_PROCESSES_URI = ALIPAY_OAUTH
        .getLoginPathPrefix() + "/" + "{" + PROVIDER_CODE + "}";
    public static final AntPathRequestMatcher REQUEST_MATCHER              = new AntPathRequestMatcher(
        DEFAULT_FILTER_PROCESSES_URI, HttpMethod.GET.name());

    /**
     * Creates a new instance
     *
     * @param defaultFilterProcessesUrl  the {@link String}
     * @param userIdpService             {@link  UserIdpService}
     * @param identityProviderRepository {@link IdentityProviderRepository}
     */
    protected AlipayLoginAuthenticationFilter(String defaultFilterProcessesUrl,
                                              UserIdpService userIdpService,
                                              IdentityProviderRepository identityProviderRepository) {
        super(defaultFilterProcessesUrl, userIdpService, identityProviderRepository);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException,
                                                                              IOException,
                                                                              ServletException {
        if (!REQUEST_MATCHER.matches(request)) {
            throw new AuthenticationServiceException(
                "Authentication method not supported: " + request.getMethod());
        }
        return null;
    }

    public static String getLoginUrl(String providerId) {
        String url = ServerHelp.getPortalPublicBaseUrl() + ALIPAY_OAUTH.getLoginPathPrefix() + "/"
                     + providerId;
        return HttpUrlUtils.format(url);
    }

    public static RequestMatcher getRequestMatcher() {
        return REQUEST_MATCHER;
    }
}
