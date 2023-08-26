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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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
import com.alipay.easysdk.kernel.Client;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.kernel.Context;

import cn.topiam.employee.authentication.alipay.AlipayIdpOAuth2Config;
import cn.topiam.employee.authentication.alipay.client.AlipayClient;
import cn.topiam.employee.authentication.alipay.client.AlipaySystemOauthTokenResponse;
import cn.topiam.employee.authentication.alipay.client.AlipaySystemUserInfoShareResponse;
import cn.topiam.employee.authentication.common.authentication.IdpUserDetails;
import cn.topiam.employee.authentication.common.filter.AbstractIdpAuthenticationProcessingFilter;
import cn.topiam.employee.authentication.common.service.UserIdpService;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.core.help.ServerHelp;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.trace.TraceUtils;
import cn.topiam.employee.support.util.HttpUrlUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static com.taobao.api.Constants.SDK_VERSION;

import static cn.topiam.employee.authentication.alipay.constant.AlipayAuthenticationConstants.AUTH_CODE;
import static cn.topiam.employee.authentication.common.IdentityProviderType.ALIPAY_OAUTH;
import static cn.topiam.employee.authentication.common.constant.AuthenticationConstants.*;
import static cn.topiam.employee.authentication.common.constant.AuthenticationConstants.INVALID_STATE_PARAMETER_ERROR_CODE;

/**
 * 支付宝 登录过滤器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/8/19 17:58
 */
@SuppressWarnings("DuplicatedCode")
public class AlipayLoginAuthenticationFilter extends AbstractIdpAuthenticationProcessingFilter {
    public final static String                DEFAULT_FILTER_PROCESSES_URI = ALIPAY_OAUTH
        .getLoginPathPrefix() + "/" + "{" + PROVIDER_CODE + "}";
    public static final AntPathRequestMatcher REQUEST_MATCHER              = new AntPathRequestMatcher(
        DEFAULT_FILTER_PROCESSES_URI, HttpMethod.GET.name());

    /**
     * Creates a new instance
     *
     * @param userIdpService             {@link  UserIdpService}
     * @param identityProviderRepository {@link IdentityProviderRepository}
     */
    public AlipayLoginAuthenticationFilter(UserIdpService userIdpService,
                                           IdentityProviderRepository identityProviderRepository) {
        super(REQUEST_MATCHER, userIdpService, identityProviderRepository);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException,
                                                                              IOException,
                                                                              ServletException {
        OAuth2AuthorizationRequest authorizationRequest = getOauth2AuthorizationRequest(request,
            response);
        TraceUtils.put(UUID.randomUUID().toString());
        RequestMatcher.MatchResult matcher = REQUEST_MATCHER.matcher(request);
        Map<String, String> variables = matcher.getVariables();
        String providerCode = variables.get(PROVIDER_CODE);
        String providerId = getIdentityProviderId(providerCode);
        //code 支付宝为auth_code
        String code = request.getParameter(AUTH_CODE);
        if (StringUtils.isEmpty(code)) {
            logger.error("支付宝登录 auth_code 参数不存在，认证失败");
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_CODE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        // state
        String state = request.getParameter(OAuth2ParameterNames.STATE);
        if (StringUtils.isEmpty(state)) {
            logger.error("支付宝登录 state 参数不存在，认证失败");
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_STATE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        //验证state
        if (!authorizationRequest.getState().equals(state)) {
            logger.error("支付宝登录 state 匹配不一致，认证失败");
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_STATE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        //获取身份提供商
        IdentityProviderEntity provider = getIdentityProviderEntity(providerCode);
        AlipayIdpOAuth2Config idpOauthConfig = JSONObject.parseObject(provider.getConfig(),
            AlipayIdpOAuth2Config.class);
        if (Objects.isNull(idpOauthConfig)) {
            logger.error("未查询到支付宝登录配置");
            //无效身份提供商
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_IDP_CONFIG);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        try {
            AlipayClient client = new AlipayClient(
                new Client(new Context(getConfig(idpOauthConfig), SDK_VERSION)));
            AlipaySystemOauthTokenResponse token = client.getOauthToken(code);
            if (!StringUtils.isBlank(token.getCode())) {
                logger.error("支付宝认证获取 access_token 失败: [" + token.getHttpBody() + "]");
                throw new TopIamException(token.getSubMsg());
            }
            String accessToken = token.getAccessToken();
            AlipaySystemUserInfoShareResponse userInfo = client.getUserInfo(accessToken);
            if (!StringUtils.isBlank(userInfo.getCode())) {
                logger.error("支付宝认证获取用户信息失败: [" + userInfo.getHttpBody() + "]");
                throw new TopIamException(userInfo.getSubMsg());
            }
            //执行逻辑
            IdpUserDetails idpUserDetails = IdpUserDetails.builder().openId(token.getOpenId())
                .providerType(ALIPAY_OAUTH).providerCode(providerCode).providerId(providerId)
                .avatarUrl(userInfo.getAvatar()).nickName(userInfo.getNickName()).build();
            return attemptAuthentication(request, response, idpUserDetails);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Config getConfig(AlipayIdpOAuth2Config idpOauthConfig) {
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = "openapi.alipay.com";
        config.appId = idpOauthConfig.getAppId();
        config.signType = "RSA2";
        config.alipayPublicKey = idpOauthConfig.getAlipayPublicKey();
        config.merchantPrivateKey = idpOauthConfig.getAppPrivateKey();
        return config;
    }

    public static String getLoginUrl(String providerId) {
        String url = ServerHelp.getPortalPublicBaseUrl() + ALIPAY_OAUTH.getLoginPathPrefix() + "/"
                     + providerId;
        return HttpUrlUtils.format(url);
    }

    public static RequestMatcher getRequestMatcher() {
        return REQUEST_MATCHER;
    }
}
