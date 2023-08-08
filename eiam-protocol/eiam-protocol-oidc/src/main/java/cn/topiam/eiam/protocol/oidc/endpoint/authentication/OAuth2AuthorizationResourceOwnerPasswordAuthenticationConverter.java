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

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import cn.topiam.eiam.protocol.oidc.authentication.OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken;

import jakarta.servlet.http.HttpServletRequest;
import static cn.topiam.eiam.protocol.oidc.endpoint.OAuth2EndpointUtils.throwError;
import static cn.topiam.employee.support.util.HttpRequestUtils.getParameters;

/**
 * 密码模式认证转换器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/26 22:20
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public final class OAuth2AuthorizationResourceOwnerPasswordAuthenticationConverter implements
                                                                                   AuthenticationConverter {
    static final String ACCESS_TOKEN_REQUEST_ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";

    @Nullable
    @Override
    public Authentication convert(HttpServletRequest request) {
        // grant_type (必填)
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken.PASSWORD.getValue()
            .equals(grantType)) {
            return null;
        }
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        //获取参数
        MultiValueMap<String, String> parameters = getParameters(request);
        // username (必填)
        String username = parameters.getFirst(OAuth2ParameterNames.USERNAME);
        if (!StringUtils.hasText(username)
            || parameters.get(OAuth2ParameterNames.USERNAME).size() != 1) {
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.USERNAME,
                ACCESS_TOKEN_REQUEST_ERROR_URI);
        }
        // password (必填)
        String password = parameters.getFirst(OAuth2ParameterNames.PASSWORD);
        if (!StringUtils.hasText(password)
            || parameters.get(OAuth2ParameterNames.PASSWORD).size() != 1) {
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.PASSWORD,
                ACCESS_TOKEN_REQUEST_ERROR_URI);
        }
        // scope (OPTIONAL)
        String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
        if (StringUtils.hasText(scope) && parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.SCOPE,
                ACCESS_TOKEN_REQUEST_ERROR_URI);
        }
        Set<String> requestedScopes = null;
        if (StringUtils.hasText(scope)) {
            requestedScopes = new HashSet<>(
                Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
        }
        //额外参数
        Map<String, Object> additionalParameters = new HashMap<>(16);
        parameters.forEach((key, value) -> {
            if (!key.equals(OAuth2ParameterNames.GRANT_TYPE)
                && !key.equals(OAuth2ParameterNames.CLIENT_ID)
                && !key.equals(OAuth2ParameterNames.USERNAME)
                && !key.equals(OAuth2ParameterNames.PASSWORD)) {
                additionalParameters.put(key,
                    (value.size() == 1) ? value.get(0) : value.toArray(new String[0]));
            }
        });
        return new OAuth2AuthorizationResourceOwnerPasswordAuthenticationToken(username, password,
            requestedScopes, clientPrincipal, additionalParameters);
    }

}
