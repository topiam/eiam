/*
 * eiam-protocol-oidc - Employee Identity and Access Management
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
package cn.topiam.eiam.protocol.oidc.context;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContext;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.application.exception.AppNotConfigException;
import cn.topiam.employee.application.exception.AppNotExistException;
import cn.topiam.employee.application.oidc.OidcApplicationService;
import cn.topiam.employee.application.oidc.model.OidcProtocolConfig;
import cn.topiam.employee.common.exception.app.AppAccessDeniedException;
import cn.topiam.employee.core.context.ContextService;
import cn.topiam.employee.protocol.code.EndpointMatcher;
import cn.topiam.employee.support.security.userdetails.Application;
import cn.topiam.employee.support.security.userdetails.UserDetails;
import cn.topiam.employee.support.security.util.SecurityUtils;
import cn.topiam.employee.support.util.IpUtils;
import cn.topiam.employee.support.util.UrlUtils;

import lombok.Getter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.springframework.security.oauth2.server.authorization.settings.ConfigurationSettingNames.Token.*;

import static cn.topiam.employee.common.constant.ProtocolConstants.APP_CODE;
import static cn.topiam.employee.common.constant.ProtocolConstants.OidcEndpointConstants.*;
import static cn.topiam.employee.support.util.HttpRequestUtils.getRequestHeaders;

/**
 * 上下文过滤器
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/6/26 23:55
 */
public final class OidcAuthorizationServerContextFilter extends OncePerRequestFilter {

    public static final String             SEPARATE = "----------------------------------------------------------";

    @Getter
    private final List<EndpointMatcher>    endpointMatchers;

    private final ApplicationServiceLoader applicationServiceLoader;

    public OidcAuthorizationServerContextFilter(List<EndpointMatcher> endpointMatchers,
                                                ApplicationServiceLoader applicationServiceLoader) {
        Assert.notNull(endpointMatchers, "requestMatchers cannot be null");
        Assert.notNull(applicationServiceLoader, "applicationServiceLoader cannot be null");
        this.applicationServiceLoader = applicationServiceLoader;
        this.endpointMatchers = endpointMatchers;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException,
                                                                      IOException {
        boolean match = false, access = false;
        Map<String, String> variables = new HashMap<>(16);
        for (EndpointMatcher endpointMatcher : endpointMatchers) {
            RequestMatcher requestMatcher = endpointMatcher.getRequestMatcher();
            if (requestMatcher.matches(request)) {
                match = true;
                access = endpointMatcher.getAccess();
                variables = requestMatcher.matcher(request).getVariables();
            }
        }
        if (!match) {
            filterChain.doFilter(request, response);
            return;
        }
        String appCode = variables.get(APP_CODE);
        //校验访问权限（未登录不校验访问权限）
        if (access && SecurityUtils.isAuthenticated()) {
            UserDetails userDetails = SecurityUtils.getCurrentUser();
            Collection<Application> applications = userDetails.getApplications();
            if (applications.stream()
                .noneMatch(application -> application.getCode().equals(appCode))) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    new AppAccessDeniedException().getMessage());
                return;
            }
        }
        try {
            //@formatter:off
            if (this.logger.isTraceEnabled()) {
                String body = IOUtils.toString(request.getInputStream(),StandardCharsets.UTF_8).replaceAll("\\s+", " ");
                String logs = "\n" +
                        "┣ " + SEPARATE + "\n" +
                        "┣ App: " + appCode + "\n" +
                        "┣ Request url: " + request.getMethod() + " " + request.getRequestURL() + "\n" +
                        "┣ Request ip: " + IpUtils.getIpAddr(request) + "\n" +
                        "┣ Request headers: " + JSONObject.toJSONString(getRequestHeaders(request)) + "\n" +
                        "┣ Request parameters: " + JSONObject.toJSONString(request.getParameterMap()) + "\n" +
                        "┣ Request payload: " + StringUtils.defaultIfBlank(body, "-") + "\n" +
                        "┣ " + SEPARATE;
                logger.trace(logs);
            }
            OidcApplicationService applicationService = (OidcApplicationService) applicationServiceLoader.getApplicationServiceByAppCode(appCode);
            OidcProtocolConfig config = applicationService.getProtocolConfig(appCode);
            if (Objects.isNull(config)) {
                throw new AppNotExistException();
            }
            if (!config.getConfigured()){
                throw new AppNotConfigException();
            }
            //封装 ProviderSettings
            StringSubstitutor sub = new StringSubstitutor(variables, "{", "}");
            AuthorizationServerSettings providerSettings = AuthorizationServerSettings.builder()
                    .issuer(sub.replace(UrlUtils.format(ContextService.getPortalPublicBaseUrl() + OIDC_AUTHORIZE_PATH)))
                    .authorizationEndpoint(sub.replace(AUTHORIZATION_ENDPOINT))
                    .tokenEndpoint(sub.replace(TOKEN_ENDPOINT))
                    .jwkSetEndpoint(sub.replace(JWK_SET_ENDPOINT))
                    .oidcClientRegistrationEndpoint(sub.replace(OIDC_CLIENT_REGISTRATION_ENDPOINT))
                    .tokenIntrospectionEndpoint(sub.replace(TOKEN_INTROSPECTION_ENDPOINT))
                    .tokenRevocationEndpoint(sub.replace(TOKEN_REVOCATION_ENDPOINT))
                    .oidcUserInfoEndpoint(sub.replace(OIDC_USER_INFO_ENDPOINT))
                    .oidcLogoutEndpoint(sub.replace(OIDC_LOGOUT_ENDPOINT))
                    .deviceAuthorizationEndpoint(sub.replace(DEVICE_AUTHORIZATION_ENDPOINT))
                    .deviceVerificationEndpoint(sub.replace(DEVICE_VERIFICATION_ENDPOINT))
                    .setting(ACCESS_TOKEN_FORMAT,config.getAccessTokenFormat())
                    .setting(REUSE_REFRESH_TOKENS,config.getReuseRefreshToken())
                    .setting(REFRESH_TOKEN_TIME_TO_LIVE,config.getRefreshTokenTimeToLive())
                    .setting(AUTHORIZATION_CODE_TIME_TO_LIVE,config.getAuthorizationCodeTimeToLive())
                    .setting(DEVICE_CODE_TIME_TO_LIVE,config.getDeviceCodeTimeToLive())
                    .setting(ACCESS_TOKEN_TIME_TO_LIVE,config.getAccessTokenTimeToLive())
                    .setting(ID_TOKEN_SIGNATURE_ALGORITHM,config.getIdTokenSignatureAlgorithm())
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
            //设置上下文
            AuthorizationServerContextHolder.setContext(providerContext);
            ApplicationContextHolder.setContext(new DefaultApplicationContext(config));
            filterChain.doFilter(request, response);
            //@formatter:on
        } finally {
            AuthorizationServerContextHolder.resetContext();
            ApplicationContextHolder.resetContext();
        }
    }

    private record DefaultApplicationContext(OidcProtocolConfig config) implements ApplicationContext {

        private DefaultApplicationContext {
            Assert.notNull(config, "config cannot be null");
        }

    /**
     * 获取应用ID
     *
     * @return {@link String}
     */
    @Override
    public String getAppId() {
        return this.config.getAppId();
    }

    /**
     * 获取客户端ID
     *
     * @return {@link String}
     */
    @Override
    public String getClientId() {
        return config.getClientId();
    }

    /**
     * 获取应用编码
     *
     * @return {@link String}
     */
    @Override
    public String getAppCode() {
        return config.getAppCode();
    }

    /**
     * 获取应用模版
     *
     * @return {@link String}
     */
    @Override
    public String getAppTemplate() {
        return config.getAppTemplate();
    }

    /**
     * 获取协议配置
     *
     * @return {@link Map}
     */
    @Override
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>(16);
        config.put(OidcProtocolConfig.class.getName(), this.config);
        return config;
    }

}}
