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
package cn.topiam.employee.authentication.common.filter;

import java.io.IOException;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.authentication.common.IdentityProviderAuthenticationService;
import cn.topiam.employee.authentication.common.IdentityProviderType;
import cn.topiam.employee.authentication.common.authentication.IdentityProviderAuthentication;
import cn.topiam.employee.authentication.common.authentication.IdentityProviderNotBindAuthentication;
import cn.topiam.employee.authentication.common.authentication.IdentityProviderUserDetails;
import cn.topiam.employee.authentication.common.client.IdentityProviderConfig;
import cn.topiam.employee.authentication.common.client.RegisteredIdentityProviderClient;
import cn.topiam.employee.authentication.common.client.RegisteredIdentityProviderClientRepository;
import cn.topiam.employee.authentication.common.exception.UserBindIdentityProviderException;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.support.context.ApplicationContextService;
import cn.topiam.employee.support.security.userdetails.UserDetails;
import cn.topiam.employee.support.security.util.SecurityUtils;

import lombok.Getter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import static cn.topiam.employee.authentication.common.constant.AuthenticationConstants.*;

/**
 * 身份验证处理过滤器
 * <p>
 * 用于处理
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2021/12/8 21:49
 */
@Getter
public abstract class AbstractIdentityProviderAuthenticationProcessingFilter extends
                                                                             AbstractAuthenticationProcessingFilter {
    public static final String INVALID_IDP = "invalid_idp";

    /**
     * 用户认证
     *
     * @param request    {@link  HttpServletRequest}
     * @param response   {@link  HttpServletResponse}
     * @param identityProviderUserDetails       {@link  JSONObject}
     * @return {@link  Authentication}
     */
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response,
                                                IdentityProviderUserDetails identityProviderUserDetails) throws IOException {

        String providerId = identityProviderUserDetails.getProviderId();
        HttpSession session = request.getSession(false);
        // 登录后绑定
        if (SecurityUtils.isAuthenticated()) {
            UserDetails currentUser = SecurityUtils.getCurrentUser();
            logger.debug("用户【" + currentUser.getUsername() + "】绑定【"
                         + identityProviderUserDetails.getOpenId() + "】");
            // 绑定当前提供商用户
            identityProviderAuthenticationService.bindUserIdp(currentUser.getId(),
                identityProviderUserDetails);
            session.setAttribute(BIND_AFTER_AUTH, true);
            session.setAttribute(BIND_REDIRECT, Boolean.TRUE);
            return SecurityUtils.getSecurityContext().getAuthentication();
        }

        // 未绑定身份源
        if (!identityProviderAuthenticationService
            .checkIdpUserIsExistBind(identityProviderUserDetails.getOpenId(), providerId)) {
            logger.debug(
                "【" + providerId + "】用户【" + identityProviderUserDetails.getOpenId() + "】未绑定");
            // 自动绑定
            UserRepository userRepository = ApplicationContextService.getBean(UserRepository.class);
            Optional<UserEntity> userOptional = userRepository
                .findByExternalId(identityProviderUserDetails.getOpenId());
            if (userOptional.isPresent()) {
                try {
                    identityProviderAuthenticationService.bindUserIdp(
                        String.valueOf(userOptional.get().getId()), identityProviderUserDetails);
                } catch (UserBindIdentityProviderException e) {
                    logger.error("【" + providerId + "】用户【" + identityProviderUserDetails.getOpenId()
                                 + "】自动绑定失败",
                        e);
                    return getIdpNotBindAuthentication(request, identityProviderUserDetails);
                }
            } else {
                return getIdpNotBindAuthentication(request, identityProviderUserDetails);
            }
        }

        logger.debug("【" + providerId + "】用户【" + identityProviderUserDetails.getOpenId() + "】已绑定");
        // 存在绑定更新更新账户信息
        if (!identityProviderAuthenticationService.updateThirdPartyUser(identityProviderUserDetails,
            providerId)) {
            logger.error(
                "【" + providerId + "】用户【" + identityProviderUserDetails.getOpenId() + "】更新信息失败");
        }
        session.setAttribute(BIND_REDIRECT, Boolean.TRUE);
        return authenticate(identityProviderUserDetails.getOpenId(), providerId,
            identityProviderUserDetails.getProviderType(), request);
    }

    /**
     * 未绑定身份源返回
     *
     * @param request {@link HttpServletRequest}
     * @param identityProviderUserDetails {@link IdentityProviderUserDetails}
     * @return {@link IdentityProviderNotBindAuthentication}
     */
    @NotNull
    private IdentityProviderNotBindAuthentication getIdpNotBindAuthentication(HttpServletRequest request,
                                                                              IdentityProviderUserDetails identityProviderUserDetails) {
        IdentityProviderNotBindAuthentication token = new IdentityProviderNotBindAuthentication(
            identityProviderUserDetails);
        token.setDetails(this.authenticationDetailsSource.buildDetails(request));
        return token;
    }

    /**
     * 认证
     *
     * @param openId     {@link String }
     * @param providerType   {@link IdentityProviderType }
     * @param providerId {@link String }
     * @param request    {@link HttpServletRequest }
     * @return {@link Authentication }
     */
    public Authentication authenticate(String openId, String providerId,
                                       IdentityProviderType providerType,
                                       HttpServletRequest request) {
        //认证
        UserDetails userDetails = identityProviderAuthenticationService.getUserDetails(openId,
            providerId);
        IdentityProviderAuthentication token = new IdentityProviderAuthentication(userDetails,
            providerType, providerId, userDetails.getAuthorities());
        // Allow subclasses to set the "details" property
        token.setDetails(this.authenticationDetailsSource.buildDetails(request));
        return token;
    }

    public <T extends IdentityProviderConfig> RegisteredIdentityProviderClient<T> getRegisteredIdentityProviderClient(String code) {
        Optional<RegisteredIdentityProviderClient<T>> optional = registeredIdentityProviderClientRepository
            .findByCode(code);
        if (optional.isEmpty()) {
            //无效身份提供商
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_IDP);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        return optional.get();
    }

    public <T extends IdentityProviderConfig> String getIdentityProviderId(String providerCode) {
        RegisteredIdentityProviderClient<T> identityProvider = getRegisteredIdentityProviderClient(
            providerCode);
        return String.valueOf(identityProvider.getId());
    }

    public OAuth2AuthorizationRequest getOauth2AuthorizationRequest(HttpServletRequest request,
                                                                    HttpServletResponse response) {
        OAuth2AuthorizationRequest authorizationRequest = authorizationRequestRepository
            .removeAuthorizationRequest(request, response);
        if (authorizationRequest == null) {
            OAuth2Error oauth2Error = new OAuth2Error(AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        return authorizationRequest;
    }

    public static final String                                               GET_USERINFO_ERROR_CODE = "get_userinfo_error_code";

    public static final String                                               INVALID_IDP_CONFIG      = "invalid_idp_config";

    /**
     * 授权请求存储库
     */
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

    /**
     * 认证用户详情
     */
    private final IdentityProviderAuthenticationService                      identityProviderAuthenticationService;

    /**
     * 身份提供者存储库
     */
    private final RegisteredIdentityProviderClientRepository                 registeredIdentityProviderClientRepository;

    /**
     * Creates a new instance
     *
     * @param requiresAuthenticationRequestMatcher the {@link RequestMatcher}
     * @param identityProviderAuthenticationService                       {@link  IdentityProviderAuthenticationService}
     * @param registeredIdentityProviderClientRepository           {@link RegisteredIdentityProviderClientRepository}
     */
    protected AbstractIdentityProviderAuthenticationProcessingFilter(RequestMatcher requiresAuthenticationRequestMatcher,
                                                                     IdentityProviderAuthenticationService identityProviderAuthenticationService,
                                                                     RegisteredIdentityProviderClientRepository registeredIdentityProviderClientRepository) {
        super(requiresAuthenticationRequestMatcher);
        this.identityProviderAuthenticationService = identityProviderAuthenticationService;
        this.registeredIdentityProviderClientRepository = registeredIdentityProviderClientRepository;
        this.authorizationRequestRepository = new HttpSessionOAuth2AuthorizationRequestRepository();
    }
}
