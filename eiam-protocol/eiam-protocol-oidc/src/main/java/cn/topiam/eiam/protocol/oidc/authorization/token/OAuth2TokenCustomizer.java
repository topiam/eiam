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
package cn.topiam.eiam.protocol.oidc.authorization.token;

import java.security.Principal;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.support.security.userdetails.UserDetails;
import static org.springframework.security.oauth2.core.oidc.OidcScopes.*;

import static cn.topiam.employee.support.constant.EiamConstants.DEFAULT_DATE_TIME_FORMATTER;

/**
 * 令牌定制器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/26 21:44
 */
@SuppressWarnings({ "AlibabaClassNamingShouldBeCamel" })
public class OAuth2TokenCustomizer implements
                                   org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer<JwtEncodingContext> {
    private final UserRepository userRepository;

    public OAuth2TokenCustomizer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void customize(JwtEncodingContext context) {
        //@formatter:off
        Set<String> authorizedScopes = context.getAuthorizedScopes();
        if (context.getTokenType().getValue().equals(OidcParameterNames.ID_TOKEN)) {
            OAuth2Authorization authorization = context.getAuthorization();
            if (!Objects.isNull(authorization)){
                Authentication authentication = authorization.getAttribute(Principal.class.getName());
                if (!Objects.isNull(authentication)){
                    UserDetails principal = (UserDetails) authentication.getPrincipal();
                    Optional<UserEntity> optional = userRepository.findById(Long.valueOf(principal.getId()));
                    if (optional.isPresent()){
                        UserEntity user = optional.get();
                        boolean hasCustomClaims=false;
                        OidcUserInfo.Builder userInfoBuilder = OidcUserInfo.builder();
                        // Customize headers/claims for id_token
                        if (authorizedScopes.contains(EMAIL)) {
                            hasCustomClaims=true;
                            userInfoBuilder.email(StringUtils.defaultString(user.getEmail(), ""));
                            userInfoBuilder.emailVerified(!Objects.isNull(user.getEmailVerified()) && user.getEmailVerified());
                        }
                        if (authorizedScopes.contains(PHONE)) {
                            hasCustomClaims=true;
                            userInfoBuilder.phoneNumber(StringUtils.defaultString(user.getPhone(), ""));
                            userInfoBuilder.phoneNumberVerified(!Objects.isNull(user.getPhoneVerified()) && user.getPhoneVerified());
                        }
                        if (authorizedScopes.contains(PROFILE)) {
                            hasCustomClaims=true;
                            userInfoBuilder.preferredUsername(StringUtils.defaultString(user.getFullName(), ""));
                            userInfoBuilder.nickname(StringUtils.defaultString(user.getNickName(), ""));
                            userInfoBuilder.updatedAt(user.getUpdateTime().format(DEFAULT_DATE_TIME_FORMATTER));
                        }
                        if (hasCustomClaims){
                            context.getClaims().claims(claims ->
                                    claims.putAll(userInfoBuilder.build().getClaims()));
                        }
                    }
                }
            }
        }
        //@formatter:on
    }
}
