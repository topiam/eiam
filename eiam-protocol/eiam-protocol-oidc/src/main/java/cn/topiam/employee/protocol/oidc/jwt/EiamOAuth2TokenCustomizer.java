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
package cn.topiam.employee.protocol.oidc.jwt;

import java.security.Principal;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import static org.springframework.security.oauth2.core.oidc.OidcScopes.*;

import static cn.topiam.employee.support.constant.EiamConstants.DEFAULT_DATE_TIME_FORMATTER;

/**
 * 令牌定制器
 *
 * @author SanLi
 * Created by qinggang.zuo@gmail.com / 2689170096@qq.com on  2022/12/26 16:44
 */
@SuppressWarnings({ "unused", "AlibabaClassNamingShouldBeCamel" })
public class EiamOAuth2TokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Override
    public void customize(JwtEncodingContext context) {
        //@formatter:off
        Set<String> authorizedScopes = context.getAuthorizedScopes();
        JwsHeader.Builder headers = context.getJwsHeader();
        JwtClaimsSet.Builder claims = context.getClaims();
        if (context.getTokenType().getValue().equals(OidcParameterNames.ID_TOKEN)) {
            OAuth2Authorization auth2Authorization = context.getAuthorization();
            Authentication authentication = auth2Authorization.getAttribute(Principal.class.getName());
            UserDetails principal = (UserDetails) authentication.getPrincipal();
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            Long appId = applicationContext.getAppId();
            UserRepository userRepository = ApplicationContextHelp.getBean(UserRepository.class);
            UserEntity user = userRepository.findByUsername(principal.getUsername());
            // Customize headers/claims for id_token
            if (authorizedScopes.contains(EMAIL)) {
                claims.claim(StandardClaimNames.EMAIL, StringUtils.defaultString(user.getEmail(), ""));
                claims.claim(StandardClaimNames.EMAIL_VERIFIED, !Objects.isNull(user.getEmailVerified()) && user.getEmailVerified());
            }
            if (authorizedScopes.contains(PHONE)) {
                claims.claim(StandardClaimNames.PHONE_NUMBER, StringUtils.defaultString(user.getPhone(), ""));
                claims.claim(StandardClaimNames.PHONE_NUMBER_VERIFIED, !Objects.isNull(user.getPhoneVerified()) && user.getPhoneVerified());
            }
            if (authorizedScopes.contains(PROFILE)) {
                claims.claim(StandardClaimNames.NAME, StringUtils.defaultString(user.getFullName(), ""));
                claims.claim(StandardClaimNames.NICKNAME, StringUtils.defaultString(user.getNickName(), ""));
                claims.claim(StandardClaimNames.UPDATED_AT, user.getUpdateTime().format(DEFAULT_DATE_TIME_FORMATTER));
            }
        }
        //@formatter:on
    }
}
