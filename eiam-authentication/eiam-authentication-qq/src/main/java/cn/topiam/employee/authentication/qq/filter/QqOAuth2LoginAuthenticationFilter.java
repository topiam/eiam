/*
 * eiam-authentication-qq - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.qq.filter;

import java.io.IOException;
import java.util.HashMap;
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
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.authentication.common.authentication.IdpUserDetails;
import cn.topiam.employee.authentication.common.filter.AbstractIdpAuthenticationProcessingFilter;
import cn.topiam.employee.authentication.common.service.UserIdpService;
import cn.topiam.employee.authentication.qq.QqIdpOauthConfig;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.core.help.ServerHelp;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.trace.TraceUtils;
import cn.topiam.employee.support.util.HttpClientUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static com.nimbusds.oauth2.sdk.GrantType.AUTHORIZATION_CODE;

import static cn.topiam.employee.authentication.common.IdentityProviderType.QQ;
import static cn.topiam.employee.authentication.common.constant.AuthenticationConstants.*;
import static cn.topiam.employee.portal.idp.qq.constant.QqAuthenticationConstants.URL_GET_ACCESS_TOKEN;
import static cn.topiam.employee.portal.idp.qq.constant.QqAuthenticationConstants.URL_GET_OPEN_ID;

/**
 * QQ登录
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/8 21:11
 */
@SuppressWarnings({ "AlibabaClassNamingShouldBeCamel", "DuplicatedCode" })
public class QqOAuth2LoginAuthenticationFilter extends AbstractIdpAuthenticationProcessingFilter {
    final String                              ERROR_CODE                   = "error";
    public final static String                DEFAULT_FILTER_PROCESSES_URI = QQ.getLoginPathPrefix()
                                                                             + "/" + "{"
                                                                             + PROVIDER_CODE + "}";
    public static final AntPathRequestMatcher REQUEST_MATCHER              = new AntPathRequestMatcher(
        DEFAULT_FILTER_PROCESSES_URI, HttpMethod.GET.name());

    /**
     * Creates a new instance
     *
     * @param identityProviderRepository the {@link IdentityProviderRepository}
     * @param userIdpService  {@link  UserIdpService}
     */
    public QqOAuth2LoginAuthenticationFilter(IdentityProviderRepository identityProviderRepository,
                                             UserIdpService userIdpService) {
        super(DEFAULT_FILTER_PROCESSES_URI, userIdpService, identityProviderRepository);
    }

    /**
     * QQ认证
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
        QqIdpOauthConfig config = JSONObject.parseObject(provider.getConfig(),
            QqIdpOauthConfig.class);
        if (Objects.isNull(config)) {
            logger.error("未查询到QQ登录配置");
            //无效身份提供商
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_IDP_CONFIG);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        //获取access token
        HashMap<String, String> param = new HashMap<>(16);
        param.put(OAuth2ParameterNames.GRANT_TYPE, AUTHORIZATION_CODE.getValue());
        param.put(OAuth2ParameterNames.CLIENT_ID, config.getAppId().trim());
        param.put(OAuth2ParameterNames.CLIENT_SECRET, config.getAppKey().trim());
        param.put(OAuth2ParameterNames.CODE, code.trim());
        param.put(OAuth2ParameterNames.REDIRECT_URI, getLoginUrl(provider.getCode()));
        param.put("fmt", "json");
        //注意：QQ不能使用编码后的get请求，否则会报 {"error_description":"redirect uri is illegal","error":100010}
        JSONObject result = JSON.parseObject(HttpClientUtils.doGet(URL_GET_ACCESS_TOKEN, param));
        if (!Objects.isNull(result.getString(ERROR_CODE))) {
            logger.error("获取access_token发生错误: {}" + result.toJSONString());
            throw new TopIamException("获取access_token发生错误:  " + result.toJSONString());
        }
        // 获取openId信息
        param = new HashMap<>(16);
        param.put(OAuth2ParameterNames.ACCESS_TOKEN,
            result.getString(OAuth2ParameterNames.ACCESS_TOKEN));
        param.put("fmt", "json");
        result = JSON.parseObject(HttpClientUtils.doGet(URL_GET_OPEN_ID, param));
        if (!Objects.isNull(result.getString(ERROR_CODE))) {
            logger.error("获取QQ用户OpenID发生错误: {}" + result.toJSONString());
            throw new TopIamException("获取QQ用户OpenID发生错误:  " + result.toJSONString());
        }
        // 返回
        String openId = result.getString(OidcScopes.OPENID);
        IdpUserDetails idpUserDetails = IdpUserDetails.builder().openId(openId).providerType(QQ)
            .providerCode(providerCode).providerId(providerId).build();
        return attemptAuthentication(request, response, idpUserDetails);

    }

    public static String getLoginUrl(String providerId) {
        String url = ServerHelp.getPortalPublicBaseUrl() + "/" + QQ.getLoginPathPrefix() + "/"
                     + providerId;
        return url.replaceAll("(?<!(http:|https:))/+", "/");
    }

    public static RequestMatcher getRequestMatcher() {
        return REQUEST_MATCHER;
    }
}
