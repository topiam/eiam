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

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.authentication.mfa.email.EmailOtpProviderValidator;
import cn.topiam.employee.authentication.mfa.sms.SmsOtpProviderValidator;
import cn.topiam.employee.authentication.mfa.totp.TotpProviderValidator;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.enums.MfaFactor;
import cn.topiam.employee.core.security.mfa.MfaAuthentication;
import cn.topiam.employee.core.security.mfa.exception.MfaRequiredException;
import cn.topiam.employee.core.security.util.UserUtils;
import static cn.topiam.employee.authentication.mfa.constant.MfaAuthenticationConstants.MFA_VALIDATE;
import static cn.topiam.employee.common.enums.MfaFactor.SMS_OTP;

/**
 * MFA 认证过滤器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/29 22:23
 */
public class MfaAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final Logger               logger                        = LoggerFactory
        .getLogger(MfaAuthenticationFilter.class);
    public static final String         SPRING_SECURITY_FORM_CODE_KEY = "otp";
    public static final String         SPRING_SECURITY_FORM_TOTP_KEY = "totp";

    public static final String         SPRING_SECURITY_FORM_TYPE_KEY = "type";
    public final static String         DEFAULT_FILTER_PROCESSES_URI  = MFA_VALIDATE;

    public static final RequestMatcher MFA_LOGIN_MATCHER             = new AntPathRequestMatcher(
        DEFAULT_FILTER_PROCESSES_URI, HttpMethod.POST.name());

    protected MfaAuthenticationFilter() {
        super(MFA_LOGIN_MATCHER);
    }

    protected static RequestMatcher getRequestMatcher() {
        return MFA_LOGIN_MATCHER;
    }

    /**
     * Performs actual authentication.
     * <p>
     * The implementation should do one of the following:
     * <ol>
     * <li>Return a populated authentication token for the authenticated user, indicating
     * successful authentication</li>
     * <li>Return null, indicating that the authentication process is still in progress.
     * Before returning, the implementation should perform any additional work required to
     * complete the process.</li>
     * <li>Throw an <tt>AuthenticationException</tt> if the authentication process
     * fails</li>
     * </ol>
     *
     * @param request  from which to extract parameters and perform the authentication
     * @param response the response, which may be needed if the implementation has to do a
     *                 redirect as part of a multi-stage authentication process (such as OpenID).
     * @return the authenticated user token, or null if authentication is incomplete.
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        UserEntity user = UserUtils.getUser();
        MfaAuthentication authentication = (MfaAuthentication) SecurityContextHolder.getContext()
            .getAuthentication();
        boolean result = false;
        //获取类型
        MfaFactor type = MfaFactor.getType(request.getParameter(SPRING_SECURITY_FORM_TYPE_KEY));
        if (Objects.isNull(type)) {
            throw new MfaRequiredException("MFA 类型不存在");
        }
        //SMS OPT
        if (SMS_OTP.equals(type)) {
            String otp = request.getParameter(SPRING_SECURITY_FORM_CODE_KEY);
            if (StringUtils.isBlank(otp)) {
                throw new MfaRequiredException("OTP 参数不存在");
            }
            result = smsOtpProviderValidator.validate(otp);
        }
        //Mail OPT
        if (MfaFactor.EMAIL_OTP.equals(type)) {
            String otp = request.getParameter(SPRING_SECURITY_FORM_CODE_KEY);
            if (StringUtils.isBlank(otp)) {
                throw new MfaRequiredException("OTP 参数不存在");
            }
            result = emailOtpProviderValidator.validate(otp);
        }
        //TOTP
        if (MfaFactor.APP_TOTP.equals(type)) {
            long totp = Long.parseLong(request.getParameter(SPRING_SECURITY_FORM_TOTP_KEY));
            result = totpProviderValidator.validate(String.valueOf(totp));
        }
        if (!result) {
            logger.error("用户ID: [{}] 用户名: [{}]  {} 认证失败", type.getDesc(), user.getId(),
                user.getUsername());
            return authentication;
        }
        logger.error("用户ID: [{}] 用户名: [{}]  {} 认证成功", type.getDesc(), user.getId(),
            user.getUsername());
        //认证成功
        authentication.setValidated(true);
        return authentication;
    }

    protected final EmailOtpProviderValidator emailOtpProviderValidator = new EmailOtpProviderValidator();
    protected final SmsOtpProviderValidator   smsOtpProviderValidator   = new SmsOtpProviderValidator();
    protected final TotpProviderValidator     totpProviderValidator     = new TotpProviderValidator();
}
