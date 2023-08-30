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
package cn.topiam.employee.authentication.feishu.filter;

import java.io.IOException;
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

import cn.topiam.employee.authentication.feishu.FeiShuIdpScanCodeConfig;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.RESPONSE_TYPE;

import static cn.topiam.employee.authentication.common.IdentityProviderType.FEISHU_OAUTH;
import static cn.topiam.employee.authentication.common.constant.AuthenticationConstants.PROVIDER_CODE;
import static cn.topiam.employee.authentication.feishu.constant.FeiShuAuthenticationConstants.*;
import static cn.topiam.employee.authentication.feishu.filter.FeiShuLoginAuthenticationFilter.getLoginUrl;

/**
 * 飞书认证过滤器
 *
 * https://open.feishu.cn/document/common-capabilities/sso/web-application-sso/qr-sdk-documentation
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/8 21:11
 */
public class FeiShuAuthorizationRequestRedirectFilter extends OncePerRequestFilter {

    private final Logger                                                     logger                            = LoggerFactory
        .getLogger(FeiShuAuthorizationRequestRedirectFilter.class);

    /**
     * AntPathRequestMatcher
     */
    public static final AntPathRequestMatcher                                FEI_SHU_SCAN_CODE_REQUEST_MATCHER = new AntPathRequestMatcher(
        FEISHU_OAUTH.getAuthorizationPathPrefix() + "/" + "{" + PROVIDER_CODE + "}",
        HttpMethod.GET.name());

    /**
     * 认证请求存储库
     */
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository    = new HttpSessionOAuth2AuthorizationRequestRepository();

    private static final StringKeyGenerator                                  DEFAULT_STATE_GENERATOR           = new Base64StringKeyGenerator(
        Base64.getUrlEncoder());
    private final IdentityProviderRepository                                 identityProviderRepository;

    public FeiShuAuthorizationRequestRedirectFilter(IdentityProviderRepository identityProviderRepository) {
        this.identityProviderRepository = identityProviderRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException,
                                                                      ServletException {
        RequestMatcher.MatchResult matcher = FEI_SHU_SCAN_CODE_REQUEST_MATCHER.matcher(request);
        if (!matcher.isMatch()) {
            filterChain.doFilter(request, response);
            return;
        }
        Map<String, String> variables = matcher.getVariables();
        String providerCode = variables.get(PROVIDER_CODE);
        Optional<IdentityProviderEntity> optional = identityProviderRepository
            .findByCodeAndEnabledIsTrue(providerCode);
        if (optional.isEmpty()) {
            throw new NullPointerException("未查询到身份提供商信息");
        }
        IdentityProviderEntity entity = optional.get();
        FeiShuIdpScanCodeConfig config = JSONObject.parseObject(entity.getConfig(),
            FeiShuIdpScanCodeConfig.class);
        Assert.notNull(config, "飞书扫码登录配置不能为空");
        //构建授权请求
        //@formatter:off
        HashMap<@Nullable String, @Nullable Object> attributes = Maps.newHashMap();
        attributes.put(RESPONSE_TYPE, CODE);
        OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.authorizationCode()
                .clientId(config.getAppId())
                .authorizationUri(AUTHORIZATION_REQUEST)
                .redirectUri(getLoginUrl(optional.get().getCode()))
                .state(DEFAULT_STATE_GENERATOR.generateKey())
                .attributes(attributes);
        //@formatter:on
        builder.parameters(parameters -> {
            HashMap<String, Object> linkedParameters = new LinkedHashMap<>();
            parameters.forEach((key, value) -> {
                if (OAuth2ParameterNames.CLIENT_ID.equals(key)) {
                    linkedParameters.put(CLIENT_ID, value);
                }
                if (OAuth2ParameterNames.STATE.equals(key)) {
                    linkedParameters.put(OAuth2ParameterNames.STATE, value);
                }
                if (OAuth2ParameterNames.REDIRECT_URI.equals(key)) {
                    linkedParameters.put(OAuth2ParameterNames.REDIRECT_URI, value);
                }
                if (RESPONSE_TYPE.equals(key)) {
                    linkedParameters.put(RESPONSE_TYPE, value);
                }
            });
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

    /**
     * 重定向策略
     */
    private final RedirectStrategy authorizationRedirectStrategy = new DefaultRedirectStrategy();

    public static RequestMatcher getRequestMatcher() {
        return FEI_SHU_SCAN_CODE_REQUEST_MATCHER;
    }
}
