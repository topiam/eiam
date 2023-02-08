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
package cn.topiam.employee.protocol.oidc.authentication.password;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import cn.topiam.employee.protocol.oidc.util.EiamOAuth2Utils;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.PASSWORD;

import static cn.topiam.employee.protocol.oidc.util.EiamOAuth2Utils.getParameters;

/**
 *OAuth2AuthorizationPasswordAuthenticationConverter
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/27 22:50
 */
@SuppressWarnings({ "AlibabaClassNamingShouldBeCamel" })
public class EiamOAuth2AuthorizationPasswordAuthenticationConverter implements
                                                                    AuthenticationConverter {
    public static final String DEFAULT_ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";

    @Override
    public Authentication convert(HttpServletRequest request) {

        MultiValueMap<String, String> parameters = getParameters(request);

        // grant_type (REQUIRED)
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!AuthorizationGrantType.PASSWORD.getValue().equals(grantType)) {
            return null;
        }

        String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
        if (StringUtils.hasText(scope) && parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
            EiamOAuth2Utils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.SCOPE,
                DEFAULT_ERROR_URI);
        }
        Set<String> requestedScopes = null;
        if (StringUtils.hasText(scope)) {
            requestedScopes = new HashSet<>(
                Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
        }

        // username (REQUIRED)
        String username = parameters.getFirst(OAuth2ParameterNames.USERNAME);
        if (!StringUtils.hasText(username)
            || parameters.get(OAuth2ParameterNames.USERNAME).size() != 1) {
            EiamOAuth2Utils.throwError(OAuth2ErrorCodes.INVALID_REQUEST,
                OAuth2ParameterNames.USERNAME, DEFAULT_ERROR_URI);
        }

        // password (REQUIRED)
        String password = parameters.getFirst(PASSWORD);
        if (!StringUtils.hasText(password) || parameters.get(PASSWORD).size() != 1) {
            EiamOAuth2Utils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, PASSWORD,
                DEFAULT_ERROR_URI);
        }

        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        if (clientPrincipal == null) {
            EiamOAuth2Utils.throwError(OAuth2ErrorCodes.INVALID_REQUEST,
                OAuth2ErrorCodes.INVALID_CLIENT, DEFAULT_ERROR_URI);
        }

        Map<String, Object> additionalParameters = parameters.entrySet().stream()
            .filter(e -> !e.getKey().equals(OAuth2ParameterNames.GRANT_TYPE)
                         && !e.getKey().equals(OAuth2ParameterNames.SCOPE))
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

        return new EiamOAuth2AuthorizationPasswordAuthenticationToken(
            AuthorizationGrantType.PASSWORD, clientPrincipal, requestedScopes,
            additionalParameters);

    }

}
