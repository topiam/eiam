/*
 * eiam-authentication-wechat - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.wechat.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cn.topiam.employee.authentication.wechat.WeChatIdpScanCodeConfig;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.support.trace.TraceUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.employee.authentication.common.IdentityProviderType.WECHAT_QR;
import static cn.topiam.employee.authentication.common.constant.AuthenticationConstants.PROVIDER_CODE;
import static cn.topiam.employee.authentication.wechat.constant.WeChatAuthenticationConstants.QrConnect.*;

/**
 * 微信扫码登录请求重定向过滤器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/20 21:22
 */
@SuppressWarnings("DuplicatedCode")
public class WeChatScanCodeAuthorizationRequestRedirectFilter extends OncePerRequestFilter {

    private final Logger                                                     logger                            = LoggerFactory
        .getLogger(WeChatScanCodeAuthorizationRequestRedirectFilter.class);

    /**
     * AntPathRequestMatcher
     */
    public static final AntPathRequestMatcher                                WE_CHAT_SCAN_CODE_REQUEST_MATCHER = new AntPathRequestMatcher(
        WECHAT_QR.getAuthorizationPathPrefix() + "/" + "{" + PROVIDER_CODE + "}",
        HttpMethod.GET.name());

    /**
     * 重定向策略
     */
    private final RedirectStrategy                                           authorizationRedirectStrategy     = new DefaultRedirectStrategy();

    /**
     * 认证请求存储库
     */
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository    = new HttpSessionOAuth2AuthorizationRequestRepository();

    private static final StringKeyGenerator                                  DEFAULT_STATE_GENERATOR           = new Base64StringKeyGenerator(
        Base64.getUrlEncoder());
    private final IdentityProviderRepository                                 identityProviderRepository;

    public WeChatScanCodeAuthorizationRequestRedirectFilter(IdentityProviderRepository identityProviderRepository) {
        this.identityProviderRepository = identityProviderRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException,
                                                                      ServletException {
        RequestMatcher.MatchResult matcher = WE_CHAT_SCAN_CODE_REQUEST_MATCHER.matcher(request);
        if (!matcher.isMatch()) {
            filterChain.doFilter(request, response);
            return;
        }
        TraceUtils.put(UUID.randomUUID().toString());
        Map<String, String> variables = matcher.getVariables();
        String providerCode = variables.get(PROVIDER_CODE);
        Optional<IdentityProviderEntity> optional = identityProviderRepository
            .findByCodeAndEnabledIsTrue(providerCode);
        if (optional.isEmpty()) {
            throw new NullPointerException("未查询到身份提供商信息");
        }
        IdentityProviderEntity entity = optional.get();
        WeChatIdpScanCodeConfig config = JSONObject.parseObject(entity.getConfig(),
            WeChatIdpScanCodeConfig.class);
        Assert.notNull(config, "微信扫码登录配置不能为空");
        //构建授权请求
        //@formatter:off
        HashMap<@Nullable String, @Nullable Object> attributes = Maps.newHashMap();
        OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.authorizationCode()
                .clientId(config.getAppId())
                .scopes(Sets.newHashSet(SNSAPI_LOGIN))
                .authorizationUri(QR_CONNECT_AUTHORIZATION_REQUEST)
                .redirectUri(WeChatScanCodeLoginAuthenticationFilter.getLoginUrl(optional.get().getCode()))
                .state(DEFAULT_STATE_GENERATOR.generateKey())
                .attributes(attributes);
        //@formatter:on
        builder.parameters(parameters -> {
            HashMap<String, Object> linkedParameters = new LinkedHashMap<>();
            parameters.forEach((key, value) -> {
                if (OAuth2ParameterNames.CLIENT_ID.equals(key)) {
                    linkedParameters.put(APP_ID, value);
                }
                if (OAuth2ParameterNames.SCOPE.equals(key)) {
                    linkedParameters.put(OAuth2ParameterNames.SCOPE, value);
                }
                if (OAuth2ParameterNames.STATE.equals(key)) {
                    linkedParameters.put(OAuth2ParameterNames.STATE, value);
                }
                if (OAuth2ParameterNames.REDIRECT_URI.equals(key)) {
                    linkedParameters.put(OAuth2ParameterNames.REDIRECT_URI, value);
                }
            });
            linkedParameters.put(HREF, STYLE_BASE64);
            parameters.clear();
            parameters.putAll(linkedParameters);
        });
        this.sendRedirectForAuthorization(request, response, builder.build());
    }

    private void sendRedirectForAuthorization(HttpServletRequest request,
                                              HttpServletResponse response,
                                              OAuth2AuthorizationRequest authorizationRequest) throws IOException {
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request,
            response);
        this.authorizationRedirectStrategy.sendRedirect(request, response,
            authorizationRequest.getAuthorizationRequestUri());
    }

    private static final String STYLE        = ""
                                               + ".impowerBox .qrcode {width: 280px;border: none;margin-top:10px;}\n"
                                               + ".impowerBox .title {display: none;}\n"
                                               + ".impowerBox .info {display: none;}\n"
                                               + ".status_icon {display: none}\n"
                                               + ".impowerBox .status {text-align: center;} ";
    private static final String STYLE_BASE64 = "data:text/css;base64," + Base64.getEncoder()
        .encodeToString(STYLE.getBytes(StandardCharsets.UTF_8));

    public static RequestMatcher getRequestMatcher() {
        return WE_CHAT_SCAN_CODE_REQUEST_MATCHER;
    }
}
