/*
 * eiam-authentication-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.authentication.common.filter;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.authentication.common.IdentityProviderType;
import cn.topiam.employee.authentication.common.exception.IdentityProviderNotExistException;
import cn.topiam.employee.authentication.common.modal.IdpUser;
import cn.topiam.employee.authentication.common.service.UserIdpService;
import cn.topiam.employee.common.entity.authentication.IdentityProviderEntity;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.core.security.authentication.IdpAuthentication;
import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.core.security.util.UserUtils;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.HttpResponseUtils;

import lombok.Getter;

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
     * @param provider   {@link  IdentityProviderType}
     * @param providerCode {@link  String}
     * @param info       {@link  JSONObject}
     * @return {@link  Authentication}
     */
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response,
                                                IdentityProviderType provider, String providerCode,
                                                IdpUser info) throws IOException {
        IdentityProviderEntity identityProvider = identityProviderRepository
            .findByCodeAndEnabledIsTrue(providerCode)
            .orElseThrow(IdentityProviderNotExistException::new);
        String providerId = String.valueOf(identityProvider.getId());
        info.setProviderId(providerId);
        info.setProviderType(provider);
        //调用接口查询是否已绑定
        if (!userIdpService.checkUserIdpIsAlreadyBind(info.getOpenId(), providerId)) {
            logger.debug("【" + providerId + "】用户【" + info.getOpenId() + "】未绑定");
            //是否自动绑定
            if (!userIdpService.isAutoBindUserIdp(providerId)) {
                setUserBindSessionContent(request, info);
                return new IdpAuthentication(provider.value(), providerId);
            }
            //调用接口进行绑定操作
            info.setProviderId(providerId);
            if (!userIdpService.bindUserIdp(UserUtils.getUser().getId().toString(), info)) {
                ApiRestResult<Object> errorResult = ApiRestResult.err();
                String errMsg = "【" + providerId + "】用户【" + info.getOpenId() + "】绑定失败";
                logger.error(errMsg);
                errorResult.message(errMsg);
                HttpResponseUtils.flushResponseJson(response, HttpStatus.FORBIDDEN.value(),
                    errorResult);
                return null;
            }
            logger.debug("【" + providerId + "】用户【" + info.getOpenId() + "】绑定成功");
            //绑定成功，直接走认证认证
            return authenticate(info.getOpenId(), provider, providerId, request);
        }
        logger.debug("【" + providerId + "】用户【" + info.getOpenId() + "】已绑定");
        //存在绑定更新更新账户信息
        if (!userIdpService.updateUser(info, providerId)) {
            ApiRestResult<Object> errorResult = ApiRestResult.err();
            logger.error("钉钉扫码登录更新用户信息失败");
            errorResult.message("更新用户信息失败");
            HttpResponseUtils.flushResponseJson(response, HttpStatus.FORBIDDEN.value(),
                errorResult);
            return null;
        }
        return authenticate(info.getOpenId(), provider, providerId, request);
    }

    /**
     * 设置用户绑定session值
     *
     * @param request    {@link  HttpServletRequest}
     * @param info     {@link  IdpUser}
     */
    private void setUserBindSessionContent(HttpServletRequest request, IdpUser info) {
        request.getSession().setAttribute(TOPIAM_USER_BIND_IDP, JSONObject.toJSONString(info));
    }

    public static final String TOPIAM_USER_BIND_IDP = "TOPIAM_USER_BIND_IDP";

    /**
     * 认证
     *
     * @param openId     {@link String }
     * @param provider   {@link IdentityProviderType }
     * @param providerId {@link String }
     * @param request    {@link HttpServletRequest }
     * @return {@link Authentication }
     */
    public Authentication authenticate(String openId, IdentityProviderType provider,
                                       String providerId, HttpServletRequest request) {
        //认证
        UserDetails userDetails = userIdpService.getUserDetails(openId, providerId);
        IdpAuthentication token = new IdpAuthentication(userDetails, provider.value(), providerId,
            true, userDetails.getAuthorities());
        // Allow subclasses to set the "details" property
        token.setDetails(this.authenticationDetailsSource.buildDetails(request));
        return token;
    }

    public IdentityProviderEntity getIdentityProviderEntity(String code) {
        Optional<IdentityProviderEntity> optional = getIdentityProviderRepository()
            .findByCodeAndEnabledIsTrue(code);
        if (optional.isEmpty()) {
            //无效身份提供商
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_IDP);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        return optional.get();
    }

    public OAuth2AuthorizationRequest getOAuth2AuthorizationRequest(HttpServletRequest request,
                                                                    HttpServletResponse response) {
        OAuth2AuthorizationRequest authorizationRequest = getAuthorizationRequestRepository()
            .removeAuthorizationRequest(request, response);
        if (authorizationRequest == null) {
            OAuth2Error oauth2Error = new OAuth2Error(AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        return authorizationRequest;
    }

    public static final String                                               INVALID_STATE_PARAMETER_ERROR_CODE         = "invalid_state_parameter";
    public static final String                                               INVALID_CODE_PARAMETER_ERROR_CODE          = "invalid_code_parameter";

    public static final String                                               AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE = "authorization_request_not_found";
    public static final String                                               GET_USERINFO_ERROR_CODE                    = "get_userinfo_error_code";
    public static final String                                               AUTH_CODE                                  = "authCode";
    public static final String                                               INVALID_IDP                                = "invalid_idp";
    public static final String                                               INVALID_IDP_CONFIG                         = "invalid_idp_config";

    /**
     * 授权请求存储库
     */
    @Getter
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository             = new HttpSessionOAuth2AuthorizationRequestRepository();

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
     * @param defaultFilterProcessesUrl  the {@link String}
     * @param userIdpService             {@link  UserIdpService}
     * @param identityProviderRepository {@link IdentityProviderRepository}
     */
    protected AbstractIdpAuthenticationProcessingFilter(String defaultFilterProcessesUrl,
                                                        UserIdpService userIdpService,
                                                        IdentityProviderRepository identityProviderRepository) {
        super(new AntPathRequestMatcher(defaultFilterProcessesUrl));
        this.userIdpService = userIdpService;
        this.identityProviderRepository = identityProviderRepository;
    }

}
