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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.alibaba.fastjson2.JSONObject;
import com.aliyun.dingtalkcontact_1_0.models.GetUserHeaders;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponse;
import com.aliyun.dingtalkoauth2_1_0.Client;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponse;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import cn.topiam.employee.authentication.common.authentication.IdpUserDetails;
import cn.topiam.employee.authentication.common.filter.AbstractIdpAuthenticationProcessingFilter;
import cn.topiam.employee.authentication.common.service.UserIdpService;
import cn.topiam.employee.authentication.dingtalk.DingTalkIdpOauthConfig;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.core.help.ServerHelp;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.trace.TraceUtils;
import cn.topiam.employee.support.util.HttpUrlUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.employee.authentication.common.IdentityProviderType.DINGTALK_OAUTH;
import static cn.topiam.employee.authentication.common.IdentityProviderType.DINGTALK_QR;
import static cn.topiam.employee.authentication.common.constant.AuthenticationConstants.*;
import static cn.topiam.employee.authentication.dingtalk.constant.DingTalkAuthenticationConstants.AUTH_CODE;

/**
 * 钉钉认证过滤器
 * <p>
 * https://open.dingtalk.com/document/orgapp-server/tutorial-obtaining-user-personal-information
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/8 21:11
 */
@SuppressWarnings("DuplicatedCode")
public class DingtalkOauthAuthenticationFilter extends AbstractIdpAuthenticationProcessingFilter {
    public final static String                DEFAULT_FILTER_PROCESSES_URI = DINGTALK_OAUTH
        .getLoginPathPrefix() + "/" + "{" + PROVIDER_CODE + "}";
    /**
     * AntPathRequestMatcher
     */
    public static final AntPathRequestMatcher REQUEST_MATCHER              = new AntPathRequestMatcher(
        DEFAULT_FILTER_PROCESSES_URI, HttpMethod.GET.name());

    /**
     * Creates a new instance
     *
     * @param identityProviderRepository the {@link IdentityProviderRepository}
     * @param userIdpService  {@link  UserIdpService}
     */
    public DingtalkOauthAuthenticationFilter(IdentityProviderRepository identityProviderRepository,
                                             UserIdpService userIdpService) {
        super(REQUEST_MATCHER, userIdpService, identityProviderRepository);
    }

    /**
     * 钉钉认证
     *
     * @param request  {@link  HttpServletRequest}
     * @param response {@link  HttpServletRequest}
     * @return {@link  HttpServletRequest}
     * @throws AuthenticationException {@link  AuthenticationException} AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException,
                                                                              IOException {
        OAuth2AuthorizationRequest authorizationRequest = getOauth2AuthorizationRequest(request,
            response);
        TraceUtils.put(UUID.randomUUID().toString());
        RequestMatcher.MatchResult matcher = REQUEST_MATCHER.matcher(request);
        Map<String, String> variables = matcher.getVariables();
        String providerCode = variables.get(PROVIDER_CODE);
        String providerId = getIdentityProviderId(providerCode);
        //code 钉钉新版登录为 authCode
        String code = request.getParameter(AUTH_CODE);
        if (StringUtils.isEmpty(code)) {
            logger.error("钉钉登录 code 参数不存在，认证失败");
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_CODE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        // state
        String state = request.getParameter(OAuth2ParameterNames.STATE);
        if (StringUtils.isEmpty(state)) {
            logger.error("钉钉登录 state 参数不存在，认证失败");
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_STATE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        //验证state
        if (!authorizationRequest.getState().equals(state)) {
            logger.error("钉钉登录 state 匹配不一致，认证失败");
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_STATE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        //获取身份提供商
        IdentityProviderEntity provider = getIdentityProviderEntity(providerCode);
        DingTalkIdpOauthConfig idpOauthConfig = JSONObject.parseObject(provider.getConfig(),
            DingTalkIdpOauthConfig.class);
        if (Objects.isNull(idpOauthConfig)) {
            logger.error("未查询到钉钉登录配置");
            //无效身份提供商
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_IDP_CONFIG);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        String accessToken = getToken(code, idpOauthConfig);
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        GetUserHeaders getUserHeaders = new GetUserHeaders();
        getUserHeaders.xAcsDingtalkAccessToken = accessToken;
        //获取用户个人信息，如需获取当前授权人的信息，unionId参数必须传me
        GetUserResponse user;
        try {
            com.aliyun.dingtalkcontact_1_0.Client client = new com.aliyun.dingtalkcontact_1_0.Client(
                config);
            user = client.getUserWithOptions("me", getUserHeaders, new RuntimeOptions());
        } catch (Exception e) {
            logger.error("钉钉认证获取用户信息失败: {}", e);
            throw new TopIamException("钉钉认证获取用户信息失败", e);
        }
        //执行逻辑
        IdpUserDetails idpUserDetails = IdpUserDetails.builder().openId(user.getBody().getOpenId())
            .providerType(DINGTALK_QR).providerCode(providerCode).providerId(providerId).build();
        return attemptAuthentication(request, response, idpUserDetails);
    }

    /**
     * 获取token
     *
     * @param authCode {@link  String}
     * @param config   {@link  DingTalkIdpOauthConfig}
     * @return {@link String}
     */
    public String getToken(String authCode, DingTalkIdpOauthConfig config) {
        if (!Objects.isNull(cache)) {
            cache.getIfPresent(OAuth2ParameterNames.ACCESS_TOKEN);
        }
        Config clientConfig = new Config();
        clientConfig.protocol = "https";
        clientConfig.regionId = "central";
        try {
            Client client = new Client(clientConfig);
            GetUserTokenRequest getUserTokenRequest = new GetUserTokenRequest()
                //应用基础信息-应用信息的AppKey
                .setClientId(config.getAppKey())
                //应用基础信息-应用信息的AppSecret
                .setClientSecret(config.getAppSecret()).setCode(authCode)
                .setGrantType("authorization_code");
            //获取用户个人token
            GetUserTokenResponse getUserTokenResponse = client.getUserToken(getUserTokenRequest);
            GetUserTokenResponseBody body = getUserTokenResponse.getBody();
            //放入缓存
            cache = CacheBuilder.newBuilder()
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .expireAfterWrite(body.getExpireIn(), TimeUnit.SECONDS).build();
            cache.put(OAuth2ParameterNames.ACCESS_TOKEN, body.getAccessToken());
            return cache.getIfPresent(OAuth2ParameterNames.ACCESS_TOKEN);
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    /**
     * 缓存
     */
    private Cache<String, String> cache;

    public static String getLoginUrl(String providerId) {
        String url = ServerHelp.getPortalPublicBaseUrl() + DINGTALK_OAUTH.getLoginPathPrefix() + "/"
                     + providerId;
        return HttpUrlUtils.format(url);
    }

    public static RequestMatcher getRequestMatcher() {
        return REQUEST_MATCHER;
    }
}
