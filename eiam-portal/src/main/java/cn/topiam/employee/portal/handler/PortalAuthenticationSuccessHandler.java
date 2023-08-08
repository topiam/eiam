/*
 * eiam-portal - Employee Identity and Access Management
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
package cn.topiam.employee.portal.handler;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;

import com.google.common.collect.Lists;

import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.authentication.common.IdentityProviderType;
import cn.topiam.employee.authentication.common.authentication.IdpAuthentication;
import cn.topiam.employee.authentication.common.authentication.IdpNotBindAuthentication;
import cn.topiam.employee.authentication.common.authentication.OtpAuthentication;
import cn.topiam.employee.common.constant.AuthorizeConstants;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.support.enums.SecretType;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.security.authentication.AuthenticationProvider;
import cn.topiam.employee.support.security.authentication.WebAuthenticationDetails;
import cn.topiam.employee.support.security.userdetails.UserDetails;
import cn.topiam.employee.support.util.HttpResponseUtils;
import cn.topiam.employee.support.util.HttpUrlUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import static cn.topiam.employee.audit.event.type.EventType.LOGIN_PORTAL;
import static cn.topiam.employee.core.help.ServerHelp.getPortalPublicBaseUrl;
import static cn.topiam.employee.support.constant.EiamConstants.*;
import static cn.topiam.employee.support.context.ServletContextHelp.isHtmlRequest;
import static cn.topiam.employee.support.security.authentication.AuthenticationProvider.USERNAME_PASSWORD;
import static cn.topiam.employee.support.security.savedredirect.JumpController.JUMP_PATH;

/**
 * 认证成功处理程序
 * <p>
 * 返回JSON
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/2 22:11
 */
public class PortalAuthenticationSuccessHandler extends
                                                AbstractAuthenticationTargetUrlRequestHandler
                                                implements
                                                org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    private final Logger        logger            = LoggerFactory
        .getLogger(PortalAuthenticationSuccessHandler.class);
    private static final String REQUIRE_USER_BIND = "require_user_bind";

    /**
     * Called when a user has been successfully authenticated.
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        //@formatter:off
        boolean isHtmlRequest = isHtmlRequest(request);
        //Clear Authentication Attributes
        clearAuthenticationAttributes(request);
        if (response.isCommitted()) {
            return;
        }
        //用户与 IDP 未关联
        if (authentication instanceof IdpNotBindAuthentication) {
            if (!isHtmlRequest) {
                HttpResponseUtils.flushResponseJson(response, HttpStatus.BAD_REQUEST.value(),
                        ApiRestResult.builder().status(REQUIRE_USER_BIND).message(REQUIRE_USER_BIND)
                                .build());
                return;
            }
            //跳转登录，前端会有接口获取状态，并进行展示绑定页面
            response.sendRedirect(HttpUrlUtils
                    .format(getPortalPublicBaseUrl() + AuthorizeConstants.FE_LOGIN));
            return;
        }
        //更新 principal
        fillPrincipal(authentication,request);
        //更新认证次数
        updateAuthSuccessCount(authentication);
        //记录审计日志
        List<Target> targets= Lists.newArrayList(Target.builder().type(TargetType.PORTAL).build());
        auditEventPublish.publish(LOGIN_PORTAL, authentication, EventStatus.SUCCESS,targets);
        //响应
        if (isHtmlRequest){
            response.sendRedirect(HttpUrlUtils.format(getPortalPublicBaseUrl() + JUMP_PATH));
            return;
        }
        HttpResponseUtils.flushResponseJson(response, HttpStatus.OK.value(), ApiRestResult.ok());
        //@formatter:on
    }

    /**
     * 更新认证次数
     *
     * @param authentication {@link Authentication}
     */
    private void updateAuthSuccessCount(Authentication authentication) {
        //认证次数+1
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        userRepository.updateAuthSucceedInfo(principal.getId(), details.getGeoLocation().getIp(),
            details.getAuthenticationTime());
    }

    /**
     * 清除AuthenticationAttributes
     *
     * @param request {@link HttpServletRequest}
     */
    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            //清理登录加密秘钥
            session.removeAttribute(SecretType.LOGIN.getKey());
            //清理验证码
            session.removeAttribute(CAPTCHA_CODE_SESSION);
            //清理保存请求
            session.removeAttribute(SAVED_REQUEST);
            //清理认证异常
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }

    private void fillPrincipal(Authentication authentication, HttpServletRequest request) {
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        //认证类型
        details.setAuthenticationProvider(geAuthType(authentication));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 获取认证类型
     *
     * @param authentication {@link Authentication}
     * @return {@link String}
     */
    public static AuthenticationProvider geAuthType(Authentication authentication) {
        //用户名密码
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return USERNAME_PASSWORD;
        }
        //身份提供商
        if (authentication instanceof IdpAuthentication) {
            IdentityProviderType type = ((IdpAuthentication) authentication).getProviderType();
            return new AuthenticationProvider(type.value(), type.name());
        }
        //短信/邮箱验证码登录
        if (authentication instanceof OtpAuthentication) {
            String type = ((OtpAuthentication) authentication).getType();
            return new AuthenticationProvider(type, "");
        }
        throw new IllegalArgumentException("未知认证对象");
    }

    private final UserRepository    userRepository;
    private final AuditEventPublish auditEventPublish;

    public PortalAuthenticationSuccessHandler(UserRepository userRepository,
                                              AuditEventPublish auditEventPublish) {
        this.userRepository = userRepository;
        this.auditEventPublish = auditEventPublish;
    }
}
