/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.authentication;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import com.google.common.collect.Lists;

import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.authentication.common.IdentityProviderType;
import cn.topiam.employee.authentication.common.authentication.IdentityProviderAuthentication;
import cn.topiam.employee.authentication.common.authentication.OtpAuthentication;
import cn.topiam.employee.common.repository.setting.AdministratorRepository;
import cn.topiam.employee.support.enums.SecretType;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.security.authentication.AuthenticationProvider;
import cn.topiam.employee.support.security.authentication.WebAuthenticationDetails;
import cn.topiam.employee.support.security.constant.SecurityConstants;
import cn.topiam.employee.support.security.password.authentication.NeedChangePasswordAuthenticationToken;
import cn.topiam.employee.support.security.userdetails.UserDetails;
import cn.topiam.employee.support.util.HttpResponseUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.employee.audit.event.type.EventType.LOGIN_CONSOLE;
import static cn.topiam.employee.support.constant.EiamConstants.CAPTCHA_CODE_SESSION;
import static cn.topiam.employee.support.security.authentication.AuthenticationProvider.USERNAME_PASSWORD;

/**
 * 认证成功处理程序
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/2 22:11
 */
public class ConsoleAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * Called when a user has been successfully authenticated.
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        ApiRestResult<String> result = ApiRestResult.<String> builder().result("success").build();
        request.getSession().removeAttribute(SecretType.LOGIN.getKey());
        request.getSession().removeAttribute(CAPTCHA_CODE_SESSION);
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        //更新 principal
        fillPrincipal(authentication, request, response);
        //更新认证次数
        updateAuthSuccessCount(authentication);
        //记录审计日志
        List<Target> targets = Lists.newArrayList(Target.builder().type(TargetType.CONSOLE)
            .id(principal.getId()).name(principal.getUsername()).build());
        auditEventPublish.publish(LOGIN_CONSOLE, authentication, EventStatus.SUCCESS, targets);

        if (principal.getNeedChangePassword()) {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext
                .setAuthentication(new NeedChangePasswordAuthenticationToken(authentication));
            securityContextRepository.saveContext(securityContext, request, response);
            HttpResponseUtils.flushResponseJson(response, HttpStatus.OK.value(),
                ApiRestResult.ok().status(SecurityConstants.REQUIRE_RESET_PASSWORD));
            return;
        }

        HttpResponseUtils.flushResponseJson(response, HttpStatus.OK.value(), result);
    }

    private void fillPrincipal(Authentication authentication, HttpServletRequest request,
                               HttpServletResponse response) {
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        //认证类型
        details.setAuthenticationProvider(geAuthType(authentication));
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        securityContextRepository.saveContext(securityContext, request, response);
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
        administratorRepository.updateAuthSucceedInfo(principal.getId(),
            details.getGeoLocation().getIp(), details.getAuthenticationTime());
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
        if (authentication instanceof IdentityProviderAuthentication) {
            IdentityProviderType type = ((IdentityProviderAuthentication) authentication)
                .getProviderType();
            return new AuthenticationProvider(type.value(), type.name());
        }
        //短信/邮箱验证码登录
        if (authentication instanceof OtpAuthentication) {
            String type = ((OtpAuthentication) authentication).getType();
            return new AuthenticationProvider(type, "");
        }
        throw new IllegalArgumentException("未知认证对象");
    }

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final AdministratorRepository   administratorRepository;
    private final AuditEventPublish         auditEventPublish;

    public ConsoleAuthenticationSuccessHandler(AdministratorRepository administratorRepository,
                                               AuditEventPublish auditEventPublish) {
        this.administratorRepository = administratorRepository;
        this.auditEventPublish = auditEventPublish;
    }
}
