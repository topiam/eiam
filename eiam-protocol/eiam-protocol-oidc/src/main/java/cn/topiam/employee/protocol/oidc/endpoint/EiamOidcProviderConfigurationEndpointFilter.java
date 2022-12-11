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
package cn.topiam.employee.protocol.oidc.endpoint;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.server.authorization.oidc.OidcProviderConfiguration;
import org.springframework.security.oauth2.server.authorization.oidc.http.converter.OidcProviderConfigurationHttpMessageConverter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.common.entity.app.po.AppOidcConfigPO;
import cn.topiam.employee.common.repository.app.AppOidcConfigRepository;
import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.support.util.HttpUrlUtils;
import static cn.topiam.employee.common.constants.ProtocolConstants.APP_CODE;
import static cn.topiam.employee.common.constants.ProtocolConstants.OidcEndpointConstants.OIDC_AUTHORIZE_PATH;
import static cn.topiam.employee.common.constants.ProtocolConstants.OidcEndpointConstants.WELL_KNOWN_OPENID_CONFIGURATION;

/**
 * A {@code Filter} that processes OpenID Provider Configuration Requests.
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/26 19:40
 */
public final class EiamOidcProviderConfigurationEndpointFilter extends AbstractEiamEndpointFilter {

    private final RequestMatcher                                requestMatcher;
    private final OidcProviderConfigurationHttpMessageConverter providerConfigurationHttpMessageConverter = new OidcProviderConfigurationHttpMessageConverter();

    public EiamOidcProviderConfigurationEndpointFilter(AppOidcConfigRepository appOidcConfigRepository) {
        super(appOidcConfigRepository);
        Assert.notNull(appOidcConfigRepository, "appOidcConfigRepository cannot be null");
        this.requestMatcher = new AntPathRequestMatcher(WELL_KNOWN_OPENID_CONFIGURATION,
            HttpMethod.GET.name());
    }

    public EiamOidcProviderConfigurationEndpointFilter(AppOidcConfigRepository appOidcConfigRepository,
                                                       RequestMatcher requestMatcher) {
        super(appOidcConfigRepository);
        Assert.notNull(appOidcConfigRepository, "appOidcConfigRepository cannot be null");
        Assert.notNull(requestMatcher, "requestMatcher cannot be null");
        this.requestMatcher = requestMatcher;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException,
                                                                      IOException {

        if (!this.requestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        RequestMatcher.MatchResult matcher = requestMatcher.matcher(request);
        //验证应用
        AppOidcConfigPO config = getApplicationConfig(matcher.getVariables().get(APP_CODE));
        StringSubstitutor sub = new StringSubstitutor(matcher.getVariables(), "{", "}");
        //@formatter:off
        OidcProviderConfiguration providerConfiguration = OidcProviderConfiguration.builder()
            .issuer(sub.replace(HttpUrlUtils.format(ServerContextHelp.getPortalPublicBaseUrl() + OIDC_AUTHORIZE_PATH)))
            //Endpoint
            .authorizationEndpoint(asUrl(ServerContextHelp.getPortalPublicBaseUrl(), sub.replace(ProtocolConstants.OidcEndpointConstants.AUTHORIZATION_ENDPOINT)))
            .tokenEndpoint(asUrl(ServerContextHelp.getPortalPublicBaseUrl(), sub.replace(ProtocolConstants.OidcEndpointConstants.TOKEN_ENDPOINT)))
            .tokenEndpointAuthenticationMethods(clientAuthenticationMethods(config.getClientAuthMethods()))
            .jwkSetUrl(asUrl(ServerContextHelp.getPortalPublicBaseUrl(), sub.replace(ProtocolConstants.OidcEndpointConstants.JWK_SET_ENDPOINT)))
            .userInfoEndpoint(asUrl(ServerContextHelp.getPortalPublicBaseUrl(), sub.replace(ProtocolConstants.OidcEndpointConstants.OIDC_USER_INFO_ENDPOINT)))
            //响应类型
            .responseTypes(strings -> strings.addAll(config.getResponseTypes()))
            //grant_type
            .grantTypes(strings -> strings.addAll(config.getAuthGrantTypes()))
            .tokenRevocationEndpoint(asUrl(ServerContextHelp.getPortalPublicBaseUrl(), sub.replace(ProtocolConstants.OidcEndpointConstants.TOKEN_REVOCATION_ENDPOINT)))
            .tokenRevocationEndpointAuthenticationMethods(clientAuthenticationMethods(config.getClientAuthMethods()))
            .tokenIntrospectionEndpoint(asUrl(ServerContextHelp.getPortalPublicBaseUrl(), sub.replace(ProtocolConstants.OidcEndpointConstants.TOKEN_INTROSPECTION_ENDPOINT)))
            .tokenIntrospectionEndpointAuthenticationMethods(clientAuthenticationMethods(config.getClientAuthMethods()))
            .subjectType("public")
            //id令牌签名算法
            .idTokenSigningAlgorithm(config.getIdTokenSignatureAlgorithm())
            //scope
            .scopes(strings -> strings.addAll(config.getGrantScopes())).build();
         //@formatter:on
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        this.providerConfigurationHttpMessageConverter.write(providerConfiguration,
            MediaType.APPLICATION_JSON, httpResponse);
    }

    /**
     * 客户端认证支持方法
     *
     * @param clientAuthMethods {@link String}
     * @return {@link List}
     */
    private static Consumer<List<String>> clientAuthenticationMethods(Set<String> clientAuthMethods) {
        return (authenticationMethods) -> authenticationMethods.addAll(clientAuthMethods);
    }

    private static String asUrl(String issuer, String endpoint) {
        return HttpUrlUtils.format(
            UriComponentsBuilder.fromUriString(issuer).path(endpoint).build().toUriString());
    }

}
