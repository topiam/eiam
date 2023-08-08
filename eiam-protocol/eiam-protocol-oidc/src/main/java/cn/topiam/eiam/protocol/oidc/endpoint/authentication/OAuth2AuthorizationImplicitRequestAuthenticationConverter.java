/*
 * eiam-protocol-oidc - Employee Identity and Access Management
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
package cn.topiam.eiam.protocol.oidc.endpoint.authentication;

import java.util.*;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponseType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import cn.topiam.eiam.protocol.oidc.authentication.OAuth2AuthorizationImplicitRequestAuthenticationException;
import cn.topiam.eiam.protocol.oidc.authentication.OAuth2AuthorizationImplicitRequestAuthenticationToken;

import jakarta.servlet.http.HttpServletRequest;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import static cn.topiam.employee.support.util.HttpRequestUtils.getParameters;

/**
 * OAuth2 授权简化模式请求身份验证转换器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/26 22:47
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public final class OAuth2AuthorizationImplicitRequestAuthenticationConverter implements
                                                                             AuthenticationConverter {
    public static final OAuth2AuthorizationResponseType TOKEN                    = new OAuth2AuthorizationResponseType(
        "token");
    public static final OAuth2AuthorizationResponseType ID_TOKEN                 = new OAuth2AuthorizationResponseType(
        "id_token");

    private static final String                         DEFAULT_ERROR_URI        = "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1";
    private static final Authentication                 ANONYMOUS_AUTHENTICATION = new AnonymousAuthenticationToken(
        "anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    private static final RequestMatcher                 OIDC_REQUEST_MATCHER     = createOidcRequestMatcher();

    @Override
    public Authentication convert(HttpServletRequest request) {
        if (!GET.name().equals(request.getMethod()) && !OIDC_REQUEST_MATCHER.matches(request)) {
            return null;
        }

        MultiValueMap<String, String> parameters = getParameters(request);

        // response_type (必填)
        Set<String> responseTypes = null;
        String responseType = request.getParameter(OAuth2ParameterNames.RESPONSE_TYPE);
        if (!StringUtils.hasText(responseType)
            || parameters.get(OAuth2ParameterNames.RESPONSE_TYPE).size() != 1) {
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.RESPONSE_TYPE);
        }
        if (StringUtils.hasText(responseType)) {
            responseTypes = new HashSet<>(
                Arrays.asList(StringUtils.delimitedListToStringArray(responseType, " ")));
            // 响应类型不是 token、id_token 模式抛出异常
            if (!responseTypes.contains(TOKEN.getValue())
                && !responseTypes.contains(ID_TOKEN.getValue())) {
                throwError(OAuth2ErrorCodes.UNSUPPORTED_RESPONSE_TYPE,
                    OAuth2ParameterNames.RESPONSE_TYPE);
            }
        }

        String authorizationUri = request.getRequestURL().toString();

        // client_id (必填)
        String clientId = parameters.getFirst(OAuth2ParameterNames.CLIENT_ID);
        if (!StringUtils.hasText(clientId)
            || parameters.get(OAuth2ParameterNames.CLIENT_ID).size() != 1) {
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.CLIENT_ID);
        }

        Authentication principal = SecurityContextHolder.getContext().getAuthentication();
        if (principal == null) {
            principal = ANONYMOUS_AUTHENTICATION;
        }

        // redirect_uri (必填)
        String redirectUri = parameters.getFirst(OAuth2ParameterNames.REDIRECT_URI);
        if (!StringUtils.hasText(redirectUri)
            || parameters.get(OAuth2ParameterNames.REDIRECT_URI).size() != 1) {
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.REDIRECT_URI);
        }

        // scope (自选)
        Set<String> scopes = null;
        String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
        if (StringUtils.hasText(scope) && parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.SCOPE);
        }
        if (StringUtils.hasText(scope)) {
            scopes = new HashSet<>(
                Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
        }

        // state (推荐)
        String state = parameters.getFirst(OAuth2ParameterNames.STATE);
        if (StringUtils.hasText(state) && parameters.get(OAuth2ParameterNames.STATE).size() != 1) {
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.STATE);
        }

        Map<String, Object> additionalParameters = new HashMap<>(16);
        parameters.forEach((key, value) -> {
            if (!key.equals(OAuth2ParameterNames.RESPONSE_TYPE)
                && !key.equals(OAuth2ParameterNames.CLIENT_ID)
                && !key.equals(OAuth2ParameterNames.REDIRECT_URI)
                && !key.equals(OAuth2ParameterNames.SCOPE)
                && !key.equals(OAuth2ParameterNames.STATE)) {
                additionalParameters.put(key,
                    (value.size() == 1) ? value.get(0) : value.toArray(new String[0]));
            }
        });
        return new OAuth2AuthorizationImplicitRequestAuthenticationToken(authorizationUri, clientId,
            principal, redirectUri, state, scopes, responseTypes, additionalParameters);
    }

    private static RequestMatcher createOidcRequestMatcher() {
        RequestMatcher postMethodMatcher = request -> POST.name().equals(request.getMethod());
        RequestMatcher responseTypeParameterMatcher = request -> request
            .getParameter(OAuth2ParameterNames.RESPONSE_TYPE) != null;
        RequestMatcher openidScopeMatcher = request -> {
            String scope = request.getParameter(OAuth2ParameterNames.SCOPE);
            return StringUtils.hasText(scope) && scope.contains(OidcScopes.OPENID);
        };
        return new AndRequestMatcher(postMethodMatcher, responseTypeParameterMatcher,
            openidScopeMatcher);
    }

    private static void throwError(String errorCode, String parameterName) {
        OAuth2Error error = new OAuth2Error(errorCode, "OAuth 2.0 Parameter: " + parameterName,
            OAuth2AuthorizationImplicitRequestAuthenticationConverter.DEFAULT_ERROR_URI);
        throw new OAuth2AuthorizationImplicitRequestAuthenticationException(error, null);
    }

}
