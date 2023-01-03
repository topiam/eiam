/*
 * eiam-protocol-oidc - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.oidc.authentication.implicit;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponseType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import cn.topiam.employee.protocol.oidc.util.EiamOAuth2Utils;
import static cn.topiam.employee.protocol.oidc.util.EiamOAuth2Utils.getParameters;

/**
 * OAuth2AuthenticationImplicitAuthenticationConverter
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/27 22:50
 */
@SuppressWarnings({ "unused", "AlibabaClassNamingShouldBeCamel" })
public class EiamOAuth2AuthenticationImplicitAuthenticationConverter implements
                                                                     AuthenticationConverter {
    private static final String         DEFAULT_ERROR_URI        = "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1";
    private static final RequestMatcher OIDC_REQUEST_MATCHER     = createOidcRequestMatcher();
    private static final Authentication ANONYMOUS_AUTHENTICATION = new AnonymousAuthenticationToken(
        "anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

    @Override
    public Authentication convert(HttpServletRequest request) {

        MultiValueMap<String, String> parameters = getParameters(request);

        //GET 请求为授权请求，POST为请求确认。
        boolean authorizationRequest = false;
        if (HttpMethod.GET.name().equals(request.getMethod())
            || OIDC_REQUEST_MATCHER.matches(request)) {
            authorizationRequest = true;

            // response_type (REQUIRED)
            String responseType = request.getParameter(OAuth2ParameterNames.RESPONSE_TYPE);
            if (!StringUtils.hasText(responseType)
                || parameters.get(OAuth2ParameterNames.RESPONSE_TYPE).size() != 1) {
                throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.RESPONSE_TYPE);
            }
            //由于隐式授权过滤器在授权码之前，所以如果response_type=code，返回null，上层处理后进入后面过滤器处理。
            else if (responseType.equals(OAuth2AuthorizationResponseType.CODE.getValue())) {
                return null;
            } else if (!responseType.equals(OAuth2AuthorizationResponseType.TOKEN.getValue())) {
                throwError(OAuth2ErrorCodes.UNSUPPORTED_RESPONSE_TYPE,
                    OAuth2ParameterNames.RESPONSE_TYPE);
            }
        }

        String authorizationUri = request.getRequestURL().toString();

        // client_id (REQUIRED)
        String clientId = parameters.getFirst(OAuth2ParameterNames.CLIENT_ID);
        if (!StringUtils.hasText(clientId)
            || parameters.get(OAuth2ParameterNames.CLIENT_ID).size() != 1) {
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.CLIENT_ID);
            return null;
        }

        Authentication principal = SecurityContextHolder.getContext().getAuthentication();
        if (principal == null) {
            principal = ANONYMOUS_AUTHENTICATION;
        }

        // redirect_uri (OPTIONAL)
        String redirectUri = parameters.getFirst(OAuth2ParameterNames.REDIRECT_URI);
        if (StringUtils.hasText(redirectUri)
            && parameters.get(OAuth2ParameterNames.REDIRECT_URI).size() != 1) {
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.REDIRECT_URI);
        }

        // scope (OPTIONAL)
        Set<String> scopes = null;
        if (authorizationRequest) {
            String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
            if (StringUtils.hasText(scope)
                && parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
                throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.SCOPE);
            }
            if (StringUtils.hasText(scope)) {
                scopes = new HashSet<>(
                    Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
            }
        } else {
            // Consent request
            if (parameters.containsKey(OAuth2ParameterNames.SCOPE)) {
                scopes = new HashSet<>(parameters.get(OAuth2ParameterNames.SCOPE));
            }
        }

        // state
        // RECOMMENDED for Authorization Request
        String state = parameters.getFirst(OAuth2ParameterNames.STATE);
        if (authorizationRequest) {
            if (StringUtils.hasText(state)
                && parameters.get(OAuth2ParameterNames.STATE).size() != 1) {
                throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.STATE);
            }
        } else {
            // REQUIRED for Authorization Consent Request
            if (!StringUtils.hasText(state)
                || parameters.get(OAuth2ParameterNames.STATE).size() != 1) {
                throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.STATE);
            }
        }
        Map<String, Object> additionalParameters = new HashMap<>(16);
        return new EiamOAuth2AuthorizationImplicitAuthenticationToken(authorizationUri, clientId,
            principal, redirectUri, state, scopes, additionalParameters);
    }

    private static RequestMatcher createOidcRequestMatcher() {
        RequestMatcher postMethodMatcher = request -> "POST".equals(request.getMethod());
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
        EiamOAuth2Utils.throwError(errorCode, parameterName, DEFAULT_ERROR_URI);
    }
}
