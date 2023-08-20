/*
 * eiam-authentication-dingtalk - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.dingtalk.filter;

import java.io.IOException;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cn.topiam.employee.authentication.dingtalk.DingTalkIdpScanCodeConfig;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.HttpResponseUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.CODE;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.RESPONSE_TYPE;

import static cn.topiam.employee.authentication.common.IdentityProviderType.DINGTALK_QR;
import static cn.topiam.employee.authentication.common.constant.AuthenticationConstants.PROVIDER_CODE;
import static cn.topiam.employee.authentication.dingtalk.constant.DingTalkAuthenticationConstants.APP_ID;
import static cn.topiam.employee.authentication.dingtalk.constant.DingTalkAuthenticationConstants.SCAN_CODE_URL_AUTHORIZE;
import static cn.topiam.employee.authentication.dingtalk.filter.DingtalkScanCodeAuthenticationFilter.getLoginUrl;

/**
 * 微信扫码登录请求重定向过滤器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/20 21:22
 */
@SuppressWarnings("ALL")
public class DingtalkScanCodeAuthorizationRequestGetFilter extends OncePerRequestFilter {

    private final Logger                                                     logger                             = LoggerFactory
        .getLogger(DingtalkScanCodeAuthorizationRequestGetFilter.class);

    /**
     * AntPathRequestMatcher
     */
    public static final AntPathRequestMatcher                                DINGTALK_SCAN_CODE_REQUEST_MATCHER = new AntPathRequestMatcher(
        DINGTALK_QR.getAuthorizationPathPrefix() + "/" + "{" + PROVIDER_CODE + "}",
        HttpMethod.GET.name());

    /**
     * 认证请求存储库
     */
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository     = new HttpSessionOAuth2AuthorizationRequestRepository();

    private static final StringKeyGenerator                                  DEFAULT_STATE_GENERATOR            = new Base64StringKeyGenerator(
        Base64.getUrlEncoder());
    private final IdentityProviderRepository                                 identityProviderRepository;

    public DingtalkScanCodeAuthorizationRequestGetFilter(IdentityProviderRepository identityProviderRepository) {
        this.identityProviderRepository = identityProviderRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException,
                                                                      ServletException {
        RequestMatcher.MatchResult matcher = DINGTALK_SCAN_CODE_REQUEST_MATCHER.matcher(request);
        if (!matcher.isMatch()) {
            filterChain.doFilter(request, response);
            return;
        }
        Map<String, String> variables = matcher.getVariables();
        //校验身份提供商
        String providerCode = variables.get(PROVIDER_CODE);
        Optional<IdentityProviderEntity> optional = identityProviderRepository
            .findByCodeAndEnabledIsTrue(providerCode);
        if (optional.isEmpty()) {
            logger.error("身份提供商不存在");
            throw new NullPointerException("身份提供商不存在");
        }
        IdentityProviderEntity entity = optional.get();
        DingTalkIdpScanCodeConfig config = JSONObject.parseObject(entity.getConfig(),
            DingTalkIdpScanCodeConfig.class);
        Assert.notNull(config, "钉钉登录配置不能为空");
        //构建授权请求
        //@formatter:off
        HashMap<@Nullable String, @Nullable Object> attributes = Maps.newHashMap();
        String redirect = request.getParameter("redirect");
        if (StringUtils.isNoneBlank()) {
            attributes.put("redirect", redirect);
        }
        OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.authorizationCode()
                .clientId(config.getAppKey())
                .scopes(Sets.newHashSet("snsapi_login"))
                .authorizationUri(SCAN_CODE_URL_AUTHORIZE)
                .redirectUri(getLoginUrl(optional.get().getCode()))
                .state(DEFAULT_STATE_GENERATOR.generateKey())
                .attributes(attributes);
        builder.parameters(parameters -> {
            parameters.put(APP_ID, parameters.get(OAuth2ParameterNames.CLIENT_ID));
            parameters.remove(OAuth2ParameterNames.CLIENT_ID);
            parameters.put(RESPONSE_TYPE, CODE);
        });
        //@formatter:on
        this.writeForAuthorization(request, response, builder.build());
    }

    private void writeForAuthorization(HttpServletRequest request, HttpServletResponse response,
                                       OAuth2AuthorizationRequest authorizationRequest) throws IOException {
        this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request,
            response);
        HttpResponseUtils.flushResponseJson(response, HttpStatus.OK.value(),
            ApiRestResult.ok(authorizationRequest.getAuthorizationRequestUri()));
    }

    public static RequestMatcher getRequestMatcher() {
        return DINGTALK_SCAN_CODE_REQUEST_MATCHER;
    }
}
