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
package cn.topiam.employee.protocol.oidc.authentication.consent;

import java.io.IOException;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import com.google.common.collect.Sets;

import cn.topiam.employee.common.repository.app.AppOidcConfigRepository;
import cn.topiam.employee.core.security.util.SecurityUtils;
import cn.topiam.employee.protocol.oidc.endpoint.AbstractEiamEndpointFilter;

/**
 * 授权同意Endpoint
 *
 * @author SanLi
 * Created by qinggang.zuo@gmail.com / 2689170096@qq.com on  2022/12/17 21:25
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class EiamOAuth2AuthorizationConsentEndpointFilter extends AbstractEiamEndpointFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException,
                                                                      ServletException {
        if (requestMatcher.matches(request)) {
            Authentication principal = SecurityUtils.getSecurityContext().getAuthentication();
            String clientId = request.getParameter(OAuth2ParameterNames.CLIENT_ID);
            String state = request.getParameter(OAuth2ParameterNames.STATE);
            Set<String> requestedScopes = Sets
                .newHashSet(request.getParameter(OAuth2ParameterNames.SCOPE).split(" "));
            //查询应用具有的权限
            Set<String> authorizedScopes = Sets.newHashSet();
            DefaultConsentPage.displayConsent(request, response, clientId, principal,
                requestedScopes, authorizedScopes, state);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private final RequestMatcher requestMatcher;

    public EiamOAuth2AuthorizationConsentEndpointFilter(AppOidcConfigRepository appOidcConfigRepository,
                                                        RequestMatcher requestMatcher) {
        super(appOidcConfigRepository);
        Assert.notNull(appOidcConfigRepository, "appOidcConfigRepository must not be null");
        this.requestMatcher = new AndRequestMatcher(requestMatcher,
            new RequestHeaderRequestMatcher("Location"));
    }

}
