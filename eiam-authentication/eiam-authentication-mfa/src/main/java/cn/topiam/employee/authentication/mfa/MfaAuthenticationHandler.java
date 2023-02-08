/*
 * eiam-authentication-mfa - Employee Identity and Access Management Program
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
package cn.topiam.employee.authentication.mfa;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.Assert;

import cn.topiam.employee.common.constants.AuthorizeConstants;
import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.core.security.mfa.MfaAuthentication;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.HttpResponseUtils;
import cn.topiam.employee.support.util.HttpUrlUtils;
import static cn.topiam.employee.core.context.SettingContextHelp.isMfaEnabled;
import static cn.topiam.employee.support.constant.EiamConstants.CAPTCHA_CODE_SESSION;
import static cn.topiam.employee.support.constant.EiamConstants.SAVED_REQUEST;
import static cn.topiam.employee.support.context.ServletContextHelp.acceptIncludeTextHtml;
import static cn.topiam.employee.support.exception.enums.ExceptionStatus.EX000102;

/**
 * 认证处理器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/28 23:36
 */
@SuppressWarnings("DuplicatedCode")
public class MfaAuthenticationHandler implements AuthenticationSuccessHandler,
                                      AuthenticationFailureHandler {
    private static final String                REQUIRE_MFA = "require_mfa";

    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;

    public MfaAuthenticationHandler(AuthenticationSuccessHandler successHandler,
                                    AuthenticationFailureHandler failureHandler) {
        Assert.notNull(successHandler, "userIdpService must not be null");
        Assert.notNull(failureHandler, "userIdpService must not be null");
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
    }

    /**
     * Called when an authentication attempt fails.
     *
     * @param request   the request during which the authentication attempt occurred.
     * @param response  the response.
     * @param exception the exception which was thrown to reject the authentication
     *                  request.
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException,
                                                                           ServletException {
        failureHandler.onAuthenticationFailure(request, response, exception);
    }

    /**
     * Called when a user has been successfully authenticated.
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     *                       the authentication process.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException,
                                                                       ServletException {
        boolean isTextHtml = acceptIncludeTextHtml(request);
        //TODO MFA启用、但是对象非MFA，说明需要MFA认证
        if (isMfaEnabled() && !(authentication instanceof MfaAuthentication)) {
            SecurityContextHolder.getContext()
                .setAuthentication(new MfaAuthentication(authentication));
            //Clear Authentication Attributes
            clearAuthenticationAttributes(request);
            if (response.isCommitted()) {
                return;
            }
            if (!isTextHtml) {
                HttpResponseUtils.flushResponseJson(response, HttpStatus.BAD_REQUEST.value(),
                    ApiRestResult.builder().status(REQUIRE_MFA).message(REQUIRE_MFA).build());
                return;
            }
            //跳转登录，前端会有接口获取状态，并进行展示 MFA
            response.sendRedirect(HttpUrlUtils
                .format(ServerContextHelp.getPortalPublicBaseUrl() + AuthorizeConstants.FE_LOGIN));
            return;
        }
        //TODO Mfa 验证成功
        if (authentication instanceof MfaAuthentication
            && ((MfaAuthentication) authentication).getValidated()) {
            SecurityContextHolder.getContext()
                .setAuthentication(((MfaAuthentication) authentication).getFirst());
            successHandler.onAuthenticationSuccess(request, response, authentication);
            return;
        }
        //TODO Mfa 验证失败
        if (authentication instanceof MfaAuthentication
            && !((MfaAuthentication) authentication).getValidated()) {
            HttpResponseUtils.flushResponseJson(response, HttpStatus.BAD_REQUEST.value(),
                ApiRestResult.builder().status(EX000102.getCode()).message(EX000102.getMessage())
                    .build());
            return;
        }
        successHandler.onAuthenticationSuccess(request, response, authentication);
    }

    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            //清理验证码
            session.removeAttribute(CAPTCHA_CODE_SESSION);
            //清理保存请求
            session.removeAttribute(SAVED_REQUEST);
            //清理认证异常
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}
