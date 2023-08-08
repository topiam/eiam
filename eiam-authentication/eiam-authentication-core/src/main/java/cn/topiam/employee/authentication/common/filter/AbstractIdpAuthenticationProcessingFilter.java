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

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.authentication.common.IdentityProviderType;
import cn.topiam.employee.authentication.common.authentication.IdpAuthentication;
import cn.topiam.employee.authentication.common.authentication.IdpNotBindAuthentication;
import cn.topiam.employee.authentication.common.authentication.IdpUserDetails;
import cn.topiam.employee.authentication.common.exception.IdentityProviderNotExistException;
import cn.topiam.employee.authentication.common.service.UserIdpService;
import cn.topiam.employee.authentication.common.util.AuthenticationUtils;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.security.userdetails.UserDetails;
import cn.topiam.employee.support.util.HttpResponseUtils;

import lombok.Getter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 身份验证处理过滤器
 * <p>
 * 用于处理
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/8 21:49
 */
public abstract class AbstractIdpAuthenticationProcessingFilter extends
                                                                AbstractAuthenticationProcessingFilter {

    /**
     * 用户认证
     *
     * @param request    {@link  HttpServletRequest}
     * @param response   {@link  HttpServletResponse}
     * @param idpUserDetails       {@link  JSONObject}
     * @return {@link  Authentication}
     */
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response,
                                                IdpUserDetails idpUserDetails) throws IOException {
        String providerId = idpUserDetails.getProviderId();
        //调用接口查询是否已绑定
        if (!userIdpService.checkIdpUserIsExistBind(idpUserDetails.getOpenId(), providerId)) {
            logger.debug("【" + providerId + "】用户【" + idpUserDetails.getOpenId() + "】未绑定");
            //是否自动绑定
            IdpNotBindAuthentication token = new IdpNotBindAuthentication(idpUserDetails);
            token.setDetails(this.authenticationDetailsSource.buildDetails(request));
            return token;
        }
        logger.debug("【" + providerId + "】用户【" + idpUserDetails.getOpenId() + "】已绑定");
        //存在绑定更新更新账户信息
        if (!userIdpService.updateUser(idpUserDetails, providerId)) {
            ApiRestResult<Object> errorResult = ApiRestResult.err();
            logger.error("【" + providerId + "】用户【" + idpUserDetails.getOpenId() + "】更新信息失败");
            errorResult.message("更新用户信息失败");
            HttpResponseUtils.flushResponseJson(response, HttpStatus.FORBIDDEN.value(),
                errorResult);
            return null;
        }
        return authenticate(idpUserDetails.getOpenId(), providerId,
            idpUserDetails.getProviderType(), request);
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
        UserDetails userDetails = userIdpService.getUserDetails(openId, providerId);
        IdpAuthentication token = new IdpAuthentication(userDetails, providerType, providerId,
            userDetails.getAuthorities());
        // Allow subclasses to set the "details" property
        token.setDetails(this.authenticationDetailsSource.buildDetails(request));
        return token;
    }

    public IdentityProviderEntity getIdentityProviderEntity(String code) {
        return AuthenticationUtils.getIdentityProviderEntity(code, identityProviderRepository);
    }

    public String getIdentityProviderId(String providerCode) {
        IdentityProviderEntity identityProvider = identityProviderRepository
            .findByCodeAndEnabledIsTrue(providerCode)
            .orElseThrow(IdentityProviderNotExistException::new);
        return String.valueOf(identityProvider.getId());
    }

    public OAuth2AuthorizationRequest getOauth2AuthorizationRequest(HttpServletRequest request,
                                                                    HttpServletResponse response) {
        return AuthenticationUtils.getOAuth2AuthorizationRequest(request, response,
            authorizationRequestRepository);
    }

    public static final String                                               GET_USERINFO_ERROR_CODE = "get_userinfo_error_code";
    public static final String                                               AUTH_CODE               = "authCode";

    public static final String                                               INVALID_IDP_CONFIG      = "invalid_idp_config";

    /**
     * 授权请求存储库
     */
    @Getter
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

    /**
     * 认证用户详情
     */
    @Getter
    private final UserIdpService                                             userIdpService;

    /**
     * 身份提供者存储库
     */
    @Getter
    private final IdentityProviderRepository                                 identityProviderRepository;

    /**
     * Creates a new instance
     *
     * @param defaultFilterProcessesUrl      the {@link String}
     * @param userIdpService                 {@link  UserIdpService}
     * @param identityProviderRepository     {@link IdentityProviderRepository}
     */
    protected AbstractIdpAuthenticationProcessingFilter(String defaultFilterProcessesUrl,
                                                        UserIdpService userIdpService,
                                                        IdentityProviderRepository identityProviderRepository) {
        super(new AntPathRequestMatcher(defaultFilterProcessesUrl));
        this.userIdpService = userIdpService;
        this.identityProviderRepository = identityProviderRepository;
        this.authorizationRequestRepository = new HttpSessionOAuth2AuthorizationRequestRepository();
    }
}
