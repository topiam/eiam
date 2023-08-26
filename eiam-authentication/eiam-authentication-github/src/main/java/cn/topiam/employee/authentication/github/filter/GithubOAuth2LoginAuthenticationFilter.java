/*
 * eiam-authentication-github - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.github.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;

import cn.topiam.employee.authentication.common.authentication.IdpUserDetails;
import cn.topiam.employee.authentication.common.filter.AbstractIdpAuthenticationProcessingFilter;
import cn.topiam.employee.authentication.common.service.UserIdpService;
import cn.topiam.employee.authentication.github.GithubIdpOauthConfig;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.core.help.ServerHelp;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.trace.TraceUtils;
import cn.topiam.employee.support.util.HttpUrlUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.employee.authentication.common.IdentityProviderType.GITHUB_OAUTH;
import static cn.topiam.employee.authentication.common.constant.AuthenticationConstants.*;
import static cn.topiam.employee.authentication.github.constant.GithubAuthenticationConstants.*;

/**
 * GITHUB登录
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/8 21:11
 */
@SuppressWarnings({ "AlibabaClassNamingShouldBeCamel", "DuplicatedCode" })
public class GithubOAuth2LoginAuthenticationFilter extends
                                                   AbstractIdpAuthenticationProcessingFilter {
    final String                              ERROR_CODE                   = "error";
    public final static String                DEFAULT_FILTER_PROCESSES_URI = GITHUB_OAUTH
        .getLoginPathPrefix() + "/" + "{" + PROVIDER_CODE + "}";
    public static final AntPathRequestMatcher REQUEST_MATCHER              = new AntPathRequestMatcher(
        DEFAULT_FILTER_PROCESSES_URI, HttpMethod.GET.name());

    /**
     * Creates a new instance
     *
     * @param identityProviderRepository the {@link IdentityProviderRepository}
     * @param userIdpService  {@link  UserIdpService}
     */
    public GithubOAuth2LoginAuthenticationFilter(IdentityProviderRepository identityProviderRepository,
                                                 UserIdpService userIdpService) {
        super(REQUEST_MATCHER, userIdpService, identityProviderRepository);
    }

    /**
     * GITHUB认证
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
        GithubIdpOauthConfig config = JSONObject.parseObject(provider.getConfig(),
            GithubIdpOauthConfig.class);
        if (Objects.isNull(config)) {
            logger.error("未查询到GITHUB登录配置");
            //无效身份提供商
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_IDP_CONFIG);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        //获取access token
        HashMap<String, String> param = new HashMap<>(16);
        param.put(OAuth2ParameterNames.CLIENT_ID, config.getClientId().trim());
        param.put(OAuth2ParameterNames.CLIENT_SECRET, config.getClientSecret().trim());
        param.put(OAuth2ParameterNames.CODE, code.trim());
        param.put(OAuth2ParameterNames.REDIRECT_URI, getLoginUrl(provider.getCode()));
        JSONObject result = request(URL_GET_ACCESS_TOKEN, HttpMethod.POST, null, param);
        if (Objects.nonNull(result.getString(ERROR_CODE))) {
            logger.error("获取access_token发生错误: {}" + result.toJSONString());
            throw new TopIamException("获取access_token发生错误:  " + result.toJSONString());
        }
        // 获取id信息
        result = request(URL_GET_USER_INFO, HttpMethod.GET,
            result.getString(OAuth2ParameterNames.TOKEN_TYPE) + " "
                                                            + result.getString(
                                                                OAuth2ParameterNames.ACCESS_TOKEN),
            param);
        if (!Objects.isNull(result.getString(ERROR_CODE))) {
            logger.error("获取GITHUB用户OpenID发生错误: {}" + result.toJSONString());
            throw new TopIamException("获取GITHUB用户OpenID发生错误:  " + result.toJSONString());
        }
        // 返回
        String id = result.getString("id");
        IdpUserDetails idpUserDetails = IdpUserDetails.builder().openId(id)
            .providerType(GITHUB_OAUTH).providerCode(providerCode).providerId(providerId).build();
        return attemptAuthentication(request, response, idpUserDetails);

    }

    private static JSONObject request(String url, HttpMethod method, String authorization,
                                      HashMap<String, String> param) {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Lists.newArrayList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (StringUtils.isNotBlank(authorization)) {
            headers.set("Authorization", authorization);
        }
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(param, headers);
        ResponseEntity<String> responseEntity = client.exchange(url, method, requestEntity,
            String.class);
        return JSON.parseObject(responseEntity.getBody());
    }

    public static String getLoginUrl(String providerId) {
        String url = ServerHelp.getPortalPublicBaseUrl() + "/" + GITHUB_OAUTH.getLoginPathPrefix()
                     + "/" + providerId;
        return HttpUrlUtils.format(url);
    }

    public static RequestMatcher getRequestMatcher() {
        return REQUEST_MATCHER;
    }
}
