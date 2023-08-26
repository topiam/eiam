/*
 * eiam-authentication-gitee - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.gitee.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

import cn.topiam.employee.authentication.common.authentication.IdpUserDetails;
import cn.topiam.employee.authentication.common.filter.AbstractIdpAuthenticationProcessingFilter;
import cn.topiam.employee.authentication.common.service.UserIdpService;
import cn.topiam.employee.authentication.gitee.GiteeIdpOAuth2Config;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.core.help.ServerHelp;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.util.HttpClientUtils;
import cn.topiam.employee.support.util.HttpUrlUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;
import static org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames.CLIENT_ID;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.CLIENT_SECRET;

import static cn.topiam.employee.authentication.common.IdentityProviderType.*;
import static cn.topiam.employee.authentication.common.constant.AuthenticationConstants.*;
import static cn.topiam.employee.authentication.gitee.constant.GiteeAuthenticationConstants.*;

/**
 * Gitee登录过滤器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/8 21:11
 */
@SuppressWarnings("DuplicatedCode")
public class GiteeLoginAuthenticationFilter extends AbstractIdpAuthenticationProcessingFilter {

    public final static String                DEFAULT_FILTER_PROCESSES_URI = GITEE_OAUTH
        .getLoginPathPrefix() + "/" + "{" + PROVIDER_CODE + "}";
    public static final AntPathRequestMatcher REQUEST_MATCHER              = new AntPathRequestMatcher(
        DEFAULT_FILTER_PROCESSES_URI, HttpMethod.GET.name());

    /**
     * Creates a new instance
     *
     * @param identityProviderRepository the {@link IdentityProviderRepository}
     * @param userIdpService  {@link  UserIdpService}
     */
    public GiteeLoginAuthenticationFilter(IdentityProviderRepository identityProviderRepository,
                                          UserIdpService userIdpService) {
        super(REQUEST_MATCHER, userIdpService, identityProviderRepository);
    }

    /**
     * Gitee认证
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
        RequestMatcher.MatchResult matcher = REQUEST_MATCHER.matcher(request);
        Map<String, String> variables = matcher.getVariables();
        String providerCode = variables.get(PROVIDER_CODE);
        String providerId = getIdentityProviderId(providerCode);
        //code
        String code = request.getParameter(OAuth2ParameterNames.CODE);
        if (StringUtils.isEmpty(code)) {
            logger.error("Gitee 登录 code 参数不存在，认证失败");
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_CODE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        // state
        String state = request.getParameter(OAuth2ParameterNames.STATE);
        if (StringUtils.isEmpty(state)) {
            logger.error("Gitee 登录 state 参数不存在，认证失败");
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_STATE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        if (!authorizationRequest.getState().equals(state)) {
            logger.error("Gitee 登录 state 匹配不一致，认证失败");
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_STATE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        //获取身份提供商
        IdentityProviderEntity provider = getIdentityProviderEntity(providerCode);
        GiteeIdpOAuth2Config config = JSONObject.parseObject(provider.getConfig(),
            GiteeIdpOAuth2Config.class);
        if (Objects.isNull(config)) {
            logger.error("未查询到 Gitee 登录配置");
            //无效身份提供商
            OAuth2Error oauth2Error = new OAuth2Error(
                AbstractIdpAuthenticationProcessingFilter.INVALID_IDP_CONFIG);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        //获取access token
        HashMap<String, String> param = new HashMap<>(16);
        param.put(CLIENT_ID, config.getClientId());
        param.put(CLIENT_SECRET, config.getClientSecret());
        param.put(OAuth2ParameterNames.CODE, code);
        param.put(OAuth2ParameterNames.REDIRECT_URI, getLoginUrl(providerCode));
        param.put(OAuth2ParameterNames.GRANT_TYPE, AUTHORIZATION_CODE.getValue());
        JSONObject result = JSON.parseObject(HttpClientUtils.post(ACCESS_TOKEN, param));
        if (result.containsKey(ERROR_CODE)) {
            logger.error("获取access_token发生错误:  " + result.toJSONString());
            throw new TopIamException("获取access_token发生错误:  " + result.toJSONString());
        }
        // 获取user信息
        param = new HashMap<>(16);
        param.put(OAuth2ParameterNames.ACCESS_TOKEN,
            result.getString(OAuth2ParameterNames.ACCESS_TOKEN));
        result = JSON.parseObject(HttpClientUtils.get(USER_INFO, param));
        if (result.containsKey(ERROR_CODE)) {
            logger.error("获取Gitee用户个人信息发生错误:  " + result.toJSONString());
            throw new TopIamException("获取Gitee用户个人信息发生错误:  " + result.toJSONString());
        }
        // 返回
        //@formatter:off
        IdpUserDetails idpUserDetails = IdpUserDetails.builder()
                .openId(param.get(ID))
                .nickName((String) result.get("name"))
                .avatarUrl((String) result.get("avatar_uri"))
                .email((String) result.get("email"))
                .providerCode(providerCode)
                .providerId(providerId)
                .providerType(GITEE_OAUTH)
                .additionalInfo(new HashMap<>(result))
                .build();
        //@formatter:on
        return attemptAuthentication(request, response, idpUserDetails);
    }

    public static String getLoginUrl(String providerId) {
        String url = ServerHelp.getPortalPublicBaseUrl() + GITEE_OAUTH.getLoginPathPrefix() + "/"
                     + providerId;
        return HttpUrlUtils.format(url);
    }

    public static RequestMatcher getRequestMatcher() {
        return REQUEST_MATCHER;
    }
}
