/*
 * eiam-authentication-feishu - Employee Identity and Access Management Program
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
package cn.topiam.employee.authentication.feishu.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicHeader;
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
import com.nimbusds.oauth2.sdk.GrantType;

import cn.topiam.employee.authentication.common.filter.AbstractIdpAuthenticationProcessingFilter;
import cn.topiam.employee.authentication.common.modal.IdpUser;
import cn.topiam.employee.authentication.common.service.UserIdpService;
import cn.topiam.employee.authentication.feishu.FeiShuIdpScanCodeConfig;
import cn.topiam.employee.common.entity.authentication.IdentityProviderEntity;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.support.util.HttpClientUtils;
import static cn.topiam.employee.authentication.common.IdentityProviderType.FEISHU_OAUTH;
import static cn.topiam.employee.authentication.common.constant.AuthenticationConstants.PROVIDER_CODE;
import static cn.topiam.employee.authentication.feishu.constant.FeiShuAuthenticationConstants.*;

/**
 * 飞书扫码登录过滤器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/8 21:11
 */
public class FeiShuLoginAuthenticationFilter extends AbstractIdpAuthenticationProcessingFilter {

    public final static String                DEFAULT_FILTER_PROCESSES_URI = FEISHU_OAUTH
        .getLoginPathPrefix() + "/*";
    public static final AntPathRequestMatcher REQUEST_MATCHER              = new AntPathRequestMatcher(
        FEISHU_OAUTH.getLoginPathPrefix() + "/" + "{" + PROVIDER_CODE + "}", HttpMethod.GET.name());

    /**
     * Creates a new instance
     *
     * @param identityProviderRepository the {@link IdentityProviderRepository}
     * @param authenticationUserDetails  {@link  UserIdpService}
     */
    public FeiShuLoginAuthenticationFilter(IdentityProviderRepository identityProviderRepository,
                                           UserIdpService authenticationUserDetails) {
        super(DEFAULT_FILTER_PROCESSES_URI, authenticationUserDetails, identityProviderRepository);
    }

    /**
     * 飞书认证
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
        RequestMatcher.MatchResult matcher = REQUEST_MATCHER.matcher(request);
        Map<String, String> variables = matcher.getVariables();
        String providerCode = variables.get(PROVIDER_CODE);
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
        IdentityProviderEntity provider = getIdentityProviderEntity(providerCode);
        FeiShuIdpScanCodeConfig config = JSONObject.parseObject(provider.getConfig(),
            FeiShuIdpScanCodeConfig.class);
        if (Objects.isNull(config)) {
            logger.error("未查询到飞书扫码登录配置");
            //无效身份提供商
            OAuth2Error oauth2Error = new OAuth2Error(
                AbstractIdpAuthenticationProcessingFilter.INVALID_IDP_CONFIG);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        //获取access token
        HashMap<String, String> param = new HashMap<>(16);
        param.put(CLIENT_ID, config.getAppId());
        param.put(CLIENT_SECRET, config.getAppSecret());
        param.put(OAuth2ParameterNames.CODE, code);
        param.put(OAuth2ParameterNames.REDIRECT_URI, getLoginUrl(provider.getCode()));
        param.put(OAuth2ParameterNames.GRANT_TYPE, GrantType.AUTHORIZATION_CODE.getValue());
        JSONObject result = JSON.parseObject(HttpClientUtils.post(ACCESS_TOKEN, param));
        // 获取user信息
        param = new HashMap<>(16);
        BasicHeader authorization = new BasicHeader(
            "Authorization", result.getString(OAuth2ParameterNames.TOKEN_TYPE) + " "
                             + result.getString(OAuth2ParameterNames.ACCESS_TOKEN));
        result = JSON.parseObject(HttpClientUtils.get(USER_INFO, param, authorization));
        // 返回
        IdpUser idpUser = IdpUser.builder().openId(result.getString(OPEN_ID)).build();
        return attemptAuthentication(request, response, FEISHU_OAUTH, providerCode, idpUser);
    }

    public static String getLoginUrl(String providerId) {
        String url = ServerContextHelp.getPortalPublicBaseUrl() + FEISHU_OAUTH.getLoginPathPrefix()
                     + "/" + providerId;
        return url.replaceAll("(?<!(http:|https:))/+", "/");
    }

    public static RequestMatcher getRequestMatcher() {
        return REQUEST_MATCHER;
    }
}
