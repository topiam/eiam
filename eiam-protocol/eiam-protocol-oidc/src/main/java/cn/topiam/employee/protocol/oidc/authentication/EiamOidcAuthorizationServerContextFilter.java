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
package cn.topiam.employee.protocol.oidc.authentication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContext;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.application.exception.AppNotExistException;
import cn.topiam.employee.common.entity.app.po.AppOidcConfigPO;
import cn.topiam.employee.common.repository.app.AppOidcConfigRepository;
import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.support.util.HttpUrlUtils;
import static cn.topiam.employee.common.constants.ProtocolConstants.APP_CODE;
import static cn.topiam.employee.common.constants.ProtocolConstants.OidcEndpointConstants.*;

/**
 * A {@code Filter} that associates the {@link AuthorizationServerContext} to the {@link AuthorizationServerContextHolder}.
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/27 00:13
 */
public final class EiamOidcAuthorizationServerContextFilter extends OncePerRequestFilter {

    private final RequestMatcher          endpointsMatcher;
    private final AppOidcConfigRepository appOidcConfigRepository;

    private final RequestMatcher          appAuthorizePathRequestMatcher = new AntPathRequestMatcher(
        OIDC_AUTHORIZE_BASE_PATH + "/**");

    public EiamOidcAuthorizationServerContextFilter(RequestMatcher endpointsMatcher,
                                                    AppOidcConfigRepository appOidcConfigRepository) {
        Assert.notNull(endpointsMatcher, "endpointsMatcher cannot be null");
        Assert.notNull(appOidcConfigRepository, "appOidcConfigRepository cannot be null");
        this.endpointsMatcher = endpointsMatcher;
        this.appOidcConfigRepository = appOidcConfigRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException,
                                                                      IOException {
        //@formatter:off
        try {
            //匹配
            if ( appAuthorizePathRequestMatcher.matches(request) && endpointsMatcher.matches(request)){
                //获取应用编码
                Map<String, String> variables = appAuthorizePathRequestMatcher.matcher(request).getVariables();
                String appCode = variables.get(APP_CODE);
                AppOidcConfigPO configPo = appOidcConfigRepository.findByAppCode(appCode);
                if (Objects.isNull(configPo)){
                    throw new AppNotExistException();
                }
                //封装 ProviderSettings
                StringSubstitutor sub = new StringSubstitutor(variables, "{", "}");
                AuthorizationServerSettings providerSettings = AuthorizationServerSettings.builder()
                        .issuer(sub.replace(HttpUrlUtils.format(ServerContextHelp.getPortalPublicBaseUrl() + OIDC_AUTHORIZE_PATH)))
                        .authorizationEndpoint(asUrl(ServerContextHelp.getPortalPublicBaseUrl(), sub.replace(AUTHORIZATION_ENDPOINT)))
                        .tokenEndpoint(asUrl(ServerContextHelp.getPortalPublicBaseUrl(), sub.replace(TOKEN_ENDPOINT)))
                        .jwkSetEndpoint(asUrl(ServerContextHelp.getPortalPublicBaseUrl(), sub.replace(JWK_SET_ENDPOINT)))
                        .oidcClientRegistrationEndpoint(asUrl(ServerContextHelp.getPortalPublicBaseUrl(), sub.replace(OIDC_CLIENT_REGISTRATION_ENDPOINT)))
                        .tokenIntrospectionEndpoint(asUrl(ServerContextHelp.getPortalPublicBaseUrl(), sub.replace(TOKEN_INTROSPECTION_ENDPOINT)))
                        .tokenRevocationEndpoint(asUrl(ServerContextHelp.getPortalPublicBaseUrl(), sub.replace(TOKEN_REVOCATION_ENDPOINT)))
                        .oidcUserInfoEndpoint(asUrl(ServerContextHelp.getPortalPublicBaseUrl(), sub.replace(OIDC_USER_INFO_ENDPOINT)))
                        .build();
                AuthorizationServerContext providerContext = new AuthorizationServerContext() {
                    @Override
                    public String getIssuer() {
                        return providerSettings.getIssuer();
                    }
                    @Override
                    public AuthorizationServerSettings getAuthorizationServerSettings() {
                        return providerSettings;
                    }
                };
                AuthorizationServerContextHolder.setContext(providerContext);
                //封装上下文内容
                Map<String,Object> config=new HashMap<>(16);

                ApplicationContextHolder.setProviderContext(new ApplicationContext(configPo.getAppId(),configPo.getAppCode(), configPo.getAppTemplate(), configPo.getClientId(), configPo.getClientSecret(), config));
            }
            filterChain.doFilter(request, response);
        } finally {
            AuthorizationServerContextHolder.resetContext();
            ApplicationContextHolder.resetProviderContext();
        }
        //@formatter:on
    }

    private static String asUrl(String issuer, String endpoint) {
        return HttpUrlUtils.format(
            UriComponentsBuilder.fromUriString(issuer).path(endpoint).build().toUriString());
    }

}
