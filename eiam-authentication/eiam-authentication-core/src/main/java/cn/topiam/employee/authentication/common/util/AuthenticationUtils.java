/*
 * eiam-authentication-core - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.common.util;

import java.util.Optional;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.employee.authentication.common.constant.AuthenticationConstants.AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE;

/**
 * AuthenticationUtils
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/4/19 21:43
 */
public class AuthenticationUtils {
    public static final String INVALID_IDP = "invalid_idp";

    /**
     *
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @param authorizationRequestRepository {@link AuthorizationRequestRepository}
     * @return {@link OAuth2AuthorizationRequest}
     */
    public static OAuth2AuthorizationRequest getOAuth2AuthorizationRequest(HttpServletRequest request,
                                                                           HttpServletResponse response,
                                                                           AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository) {
        OAuth2AuthorizationRequest authorizationRequest = authorizationRequestRepository
            .removeAuthorizationRequest(request, response);
        if (authorizationRequest == null) {
            OAuth2Error oauth2Error = new OAuth2Error(AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        return authorizationRequest;
    }

    public static IdentityProviderEntity getIdentityProviderEntity(String providerCode,
                                                                   IdentityProviderRepository identityProviderRepository) {
        Optional<IdentityProviderEntity> optional = identityProviderRepository
            .findByCodeAndEnabledIsTrue(providerCode);
        if (optional.isEmpty()) {
            //无效身份提供商
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_IDP);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        return optional.get();
    }

}
