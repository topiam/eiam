/*
 * eiam-portal - Employee Identity and Access Management Program
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
package cn.topiam.employee.portal.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;

import cn.topiam.employee.common.constants.AuthorizeConstants;
import cn.topiam.employee.common.enums.SecretType;
import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.core.security.authentication.IdpAuthentication;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.HttpResponseUtils;
import cn.topiam.employee.support.util.HttpUrlUtils;
import static cn.topiam.employee.support.constant.EiamConstants.*;
import static cn.topiam.employee.support.context.ServletContextHelp.acceptIncludeTextHtml;
import static cn.topiam.employee.support.result.ApiRestResult.SUCCESS;

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
     * <p>
     * 这里主要是处理，两种类型，一种是 MediaType 为 "text/html" 一种为 "application/json"
     * MediaType 为 "application/json" 方式，由前端根据规则进行处理
     * MediaType 为 "text/html" 方式，后端根据规则进行重定向
     * <p>
     * 标准前端form登录，前端传递 redirect_uri 登陆成功后返回 redirect_uri，前端跳转。
     * 社交oauth2登录，前端传递 redirect_uri ，社交回调到后端API处理接口，登陆成功后需要使用后端重定向方式跳转。
     * <p>
     * 涉及到SP方发起登录，类似SAML2这种，SP请求后端SSO API 接口，IDP 未登录，重定向到登录页面，
     * 前端通过上述两种方式进行登录，登陆成功后，需要携带参数重定向到SSO API 接口。
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        //@formatter:off
        boolean isTextHtml = acceptIncludeTextHtml(request);
        //Clear Authentication Attributes
        clearAuthenticationAttributes(request);
        if (response.isCommitted()) {
            return;
        }
        //TODO IDP 未关联
        if (authentication instanceof IdpAuthentication && !((IdpAuthentication) authentication).getAssociated()) {
            if (!isTextHtml) {
                HttpResponseUtils.flushResponseJson(response, HttpStatus.BAD_REQUEST.value(),
                        ApiRestResult.builder().status(REQUIRE_USER_BIND).message(REQUIRE_USER_BIND)
                                .build());
                return;
            }
            //跳转登录，前端会有接口获取状态，并进行展示绑定页面
            response.sendRedirect(HttpUrlUtils
                    .format(ServerContextHelp.getPortalPublicBaseUrl() + AuthorizeConstants.FE_LOGIN));
            return;
        }
        if (!isTextHtml) {
            HttpResponseUtils.flushResponseJson(response, HttpStatus.OK.value(),
                ApiRestResult.builder().status(SUCCESS).build());
            return;
        }
        response.sendRedirect(HttpUrlUtils.format(ServerContextHelp.getPortalPublicBaseUrl() + API_PATH + "/templates/jump"));
        //@formatter:on
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
}
