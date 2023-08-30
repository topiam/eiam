/*
 * eiam-authentication-wechatwork - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.wechatwork.filter;

import java.io.IOException;
import java.util.HashMap;
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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import cn.topiam.employee.authentication.common.authentication.IdpUserDetails;
import cn.topiam.employee.authentication.common.filter.AbstractIdpAuthenticationProcessingFilter;
import cn.topiam.employee.authentication.common.service.UserIdpService;
import cn.topiam.employee.authentication.wechatwork.WeChatWorkIdpScanCodeConfig;
import cn.topiam.employee.authentication.wechatwork.constant.WeChatWorkAuthenticationConstants;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.core.help.ServerHelp;
import cn.topiam.employee.support.trace.TraceUtils;
import cn.topiam.employee.support.util.HttpClientUtils;
import cn.topiam.employee.support.util.HttpUrlUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.employee.authentication.common.IdentityProviderType.WECHAT_WORK_QR;
import static cn.topiam.employee.authentication.common.constant.AuthenticationConstants.*;

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
        .getLoginPathPrefix() + "/" + "{" + PROVIDER_CODE + "}";
    public static final AntPathRequestMatcher REQUEST_MATCHER              = new AntPathRequestMatcher(
        DEFAULT_FILTER_PROCESSES_URI, HttpMethod.GET.name());

    /**
     * Creates a new instance
     *
     * @param identityProviderRepository the {@link IdentityProviderRepository}
     * @param userIdpService  {@link  UserIdpService}
     */
    public WeChatWorkScanCodeLoginAuthenticationFilter(IdentityProviderRepository identityProviderRepository,
                                                       UserIdpService userIdpService) {
        super(REQUEST_MATCHER, userIdpService, identityProviderRepository);
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
        TraceUtils.put(UUID.randomUUID().toString());
        OAuth2AuthorizationRequest authorizationRequest = getOauth2AuthorizationRequest(request,
            response);
        RequestMatcher.MatchResult matcher = REQUEST_MATCHER.matcher(request);
        Map<String, String> variables = matcher.getVariables();
        String providerCode = variables.get(PROVIDER_CODE);
        String providerId = getIdentityProviderId(providerCode);
        //code
        String code = request.getParameter(OAuth2ParameterNames.CODE);
        if (StringUtils.isEmpty(code)) {
            logger.error("企业微信扫码登录 code 参数不存在，认证失败");
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_CODE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        // state
        String state = request.getParameter(OAuth2ParameterNames.STATE);
        if (StringUtils.isEmpty(state)) {
            logger.error("企业微信扫码登录 state 参数不存在，认证失败");
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_STATE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        if (!authorizationRequest.getState().equals(state)) {
            logger.error("企业微信扫码登录 state 匹配不一致，认证失败");
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_STATE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        //获取身份提供商
        IdentityProviderEntity provider = getIdentityProviderEntity(providerCode);
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
        IdpUserDetails idpUserDetails = IdpUserDetails.builder().openId(userId)
            .providerType(WECHAT_WORK_QR).providerCode(providerCode).providerId(providerId).build();
        return attemptAuthentication(request, response, idpUserDetails);
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
        String url = ServerHelp.getPortalPublicBaseUrl() + WECHAT_WORK_QR.getLoginPathPrefix() + "/"
                     + providerId;
        return HttpUrlUtils.format(url);

    }

    public static RequestMatcher getRequestMatcher() {
        return REQUEST_MATCHER;
    }
}
