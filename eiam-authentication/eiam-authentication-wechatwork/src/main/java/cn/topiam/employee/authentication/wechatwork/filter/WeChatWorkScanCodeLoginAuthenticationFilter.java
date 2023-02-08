/*
 * eiam-authentication-wechatwork - Employee Identity and Access Management Program
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
package cn.topiam.employee.authentication.wechatwork.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import cn.topiam.employee.authentication.common.filter.AbstractIdpAuthenticationProcessingFilter;
import cn.topiam.employee.authentication.common.modal.IdpUser;
import cn.topiam.employee.authentication.common.service.UserIdpService;
import cn.topiam.employee.authentication.wechatwork.WeChatWorkIdpScanCodeConfig;
import cn.topiam.employee.authentication.wechatwork.constant.WeChatWorkAuthenticationConstants;
import cn.topiam.employee.common.entity.authentication.IdentityProviderEntity;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.support.trace.TraceUtils;
import cn.topiam.employee.support.util.HttpClientUtils;
import static cn.topiam.employee.authentication.common.IdentityProviderType.WECHAT_WORK_QR;
import static cn.topiam.employee.authentication.common.constant.AuthenticationConstants.PROVIDER_CODE;

/**
 * 企业微信扫码登录
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/8 21:11
 */
@SuppressWarnings("DuplicatedCode")
public class WeChatWorkScanCodeLoginAuthenticationFilter extends
                                                         AbstractIdpAuthenticationProcessingFilter {
    final String                              ERROR_CODE                   = "errcode";
    final String                              SUCCESS                      = "0";
    public final static String                DEFAULT_FILTER_PROCESSES_URI = WECHAT_WORK_QR
        .getLoginPathPrefix() + "/*";
    public static final AntPathRequestMatcher REQUEST_MATCHER              = new AntPathRequestMatcher(
        WECHAT_WORK_QR.getLoginPathPrefix() + "/" + "{" + PROVIDER_CODE + "}",
        HttpMethod.GET.name());

    /**
     * Creates a new instance
     *
     * @param identityProviderRepository the {@link IdentityProviderRepository}
     * @param authenticationUserDetails  {@link  UserIdpService}
     */
    public WeChatWorkScanCodeLoginAuthenticationFilter(IdentityProviderRepository identityProviderRepository,
                                                       UserIdpService authenticationUserDetails) {
        super(DEFAULT_FILTER_PROCESSES_URI, authenticationUserDetails, identityProviderRepository);
    }

    /**
     * 企业微信认证
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
        OAuth2AuthorizationRequest authorizationRequest = getOAuth2AuthorizationRequest(request,
            response);
        TraceUtils.put(UUID.randomUUID().toString());
        RequestMatcher.MatchResult matcher = REQUEST_MATCHER.matcher(request);
        Map<String, String> variables = matcher.getVariables();
        String providerId = variables.get(PROVIDER_CODE);
        //code
        String code = request.getParameter(OAuth2ParameterNames.CODE);
        if (StringUtils.isEmpty(code)) {
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_CODE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        // state
        String state = request.getParameter(OAuth2ParameterNames.STATE);
        if (StringUtils.isEmpty(state)) {
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_STATE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        if (!authorizationRequest.getState().equals(state)) {
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_STATE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        //获取身份提供商
        IdentityProviderEntity provider = getIdentityProviderEntity(providerId);
        WeChatWorkIdpScanCodeConfig config = JSONObject.parseObject(provider.getConfig(),
            WeChatWorkIdpScanCodeConfig.class);
        if (Objects.isNull(config)) {
            logger.error("未查询到企业微信扫码登录配置");
            //无效身份提供商
            OAuth2Error oauth2Error = new OAuth2Error(
                AbstractIdpAuthenticationProcessingFilter.INVALID_IDP_CONFIG);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        HashMap<String, String> param = new HashMap<>(16);
        // 获取user信息
        param.put(OAuth2ParameterNames.ACCESS_TOKEN, getToken(config));
        param.put(OAuth2ParameterNames.CODE, code);
        JSONObject result = JSON.parseObject(
            HttpClientUtils.get(WeChatWorkAuthenticationConstants.GET_USER_INFO, param));
        if (!Objects.equals(result.getString(ERROR_CODE), SUCCESS)) {
            logger.error("获取企业微信用户个人信息失败: {}" + result.toJSONString());
            OAuth2Error oauth2Error = new OAuth2Error(
                AbstractIdpAuthenticationProcessingFilter.GET_USERINFO_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        // 返回
        String userId = StringUtils.defaultString(result.getString("UserId"),
            result.getString("OpenId"));
        IdpUser idpUser = IdpUser.builder().openId(userId).build();
        return attemptAuthentication(request, response, WECHAT_WORK_QR, providerId, idpUser);
    }

    /**
     * 获取token
     *
     * @param config {@link WeChatWorkIdpScanCodeConfig}
     * @return {@link String}
     */
    public String getToken(WeChatWorkIdpScanCodeConfig config) {
        if (!Objects.isNull(cache)) {
            cache.getIfPresent(OAuth2ParameterNames.ACCESS_TOKEN);
        }
        //获取access token
        HashMap<String, String> param = new HashMap<>(16);
        param.put("corpid", config.getCorpId().trim());
        param.put("corpsecret", config.getAppSecret().trim());
        JSONObject result = JSON
            .parseObject(HttpClientUtils.get(WeChatWorkAuthenticationConstants.GET_TOKEN, param));
        if (!Objects.equals(result.getString(ERROR_CODE), SUCCESS)) {
            logger.error("获取access_token发生错误: {}" + result.toJSONString());
            throw new OAuth2AuthenticationException(
                "获取access_token发生错误:  " + result.toJSONString());
        }
        //放入缓存
        cache = CacheBuilder.newBuilder()
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .expireAfterWrite(result.getInteger(OAuth2ParameterNames.EXPIRES_IN), TimeUnit.SECONDS)
            .build();
        cache.put(OAuth2ParameterNames.ACCESS_TOKEN,
            result.getString(OAuth2ParameterNames.ACCESS_TOKEN));
        return cache.getIfPresent(OAuth2ParameterNames.ACCESS_TOKEN);
    }

    /**
     * 缓存
     */
    private Cache<String, String> cache;

    public static String getLoginUrl(String providerId) {
        String url = ServerContextHelp.getPortalPublicBaseUrl()
                     + WECHAT_WORK_QR.getLoginPathPrefix() + "/" + providerId;
        return url.replaceAll("(?<!(http:|https:))/+", "/");

    }

    public static RequestMatcher getRequestMatcher() {
        return REQUEST_MATCHER;
    }
}
