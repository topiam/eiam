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
import java.util.*;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cn.topiam.employee.authentication.alipay.AlipayIdpOAuth2Config;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.support.trace.TraceUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.CLIENT_ID;

import static cn.topiam.employee.authentication.alipay.constant.AlipayAuthenticationConstants.*;
import static cn.topiam.employee.authentication.common.IdentityProviderType.ALIPAY_OAUTH;
import static cn.topiam.employee.authentication.common.constant.AuthenticationConstants.PROVIDER_CODE;

/**
 * 支付宝 登录请求重定向过滤器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/8/19 17:56
 */
@SuppressWarnings("DuplicatedCode")
public class AlipayAuthorizationRequestRedirectFilter extends OncePerRequestFilter {
    /**
     * 重定向策略
     */
    private final RedirectStrategy                                           authorizationRedirectStrategy  = new DefaultRedirectStrategy();

    /**
     * 认证请求存储库
     */
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository = new HttpSessionOAuth2AuthorizationRequestRepository();

    private static final StringKeyGenerator                                  DEFAULT_STATE_GENERATOR        = new Base64StringKeyGenerator(
        Base64.getUrlEncoder());
    private final IdentityProviderRepository                                 identityProviderRepository;

    public AlipayAuthorizationRequestRedirectFilter(IdentityProviderRepository identityProviderRepository) {
        this.identityProviderRepository = identityProviderRepository;
    }

    /**
     * AntPathRequestMatcher
     */
    public static final AntPathRequestMatcher ALIPAY_REQUEST_MATCHER = new AntPathRequestMatcher(
        ALIPAY_OAUTH.getAuthorizationPathPrefix() + "/" + "{" + PROVIDER_CODE + "}",
        HttpMethod.GET.name());

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException,
                                                                      IOException {
        RequestMatcher.MatchResult matcher = ALIPAY_REQUEST_MATCHER.matcher(request);
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
        AlipayIdpOAuth2Config config = JSONObject.parseObject(entity.getConfig(),
            AlipayIdpOAuth2Config.class);
        Assert.notNull(config, "支付宝登录配置不能为空");
        //构建授权请求
        //@formatter:off
        HashMap<@Nullable String, @Nullable Object> attributes = Maps.newHashMap();
        OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.authorizationCode()
                .clientId(config.getAppId())
                .scopes(Sets.newHashSet(USER_INFO_SCOPE))
                .authorizationUri(AUTHORIZATION_REQUEST)
                .redirectUri(AlipayLoginAuthenticationFilter.getLoginUrl(optional.get().getCode()))
                .state(DEFAULT_STATE_GENERATOR.generateKey())
                .attributes(attributes);
        builder.parameters(parameters -> {
            parameters.put(APP_ID, parameters.get(CLIENT_ID));
            parameters.remove(CLIENT_ID);
        });
        //@formatter:on
        this.sendRedirectForAuthorization(request, response, builder.build());
    }

    /**
     * getRequestMatcher
     *
     * @return {@link RequestMatcher}
     */
    public static RequestMatcher getRequestMatcher() {
        return ALIPAY_REQUEST_MATCHER;
    }

    private void sendRedirectForAuthorization(HttpServletRequest request,
                                              HttpServletResponse response,
                                              OAuth2AuthorizationRequest authorizationRequest) throws IOException {
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request,
            response);
        this.authorizationRedirectStrategy.sendRedirect(request, response,
            authorizationRequest.getAuthorizationRequestUri());
    }
}
