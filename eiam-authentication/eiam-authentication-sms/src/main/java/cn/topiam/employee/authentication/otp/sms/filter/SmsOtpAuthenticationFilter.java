/*
 * eiam-authentication-sms - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.otp.sms.filter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import cn.topiam.employee.authentication.common.IdentityProviderType;
import cn.topiam.employee.authentication.common.authentication.OtpAuthentication;
import cn.topiam.employee.authentication.otp.sms.constant.SmsOtpAuthenticationConstants;
import cn.topiam.employee.authentication.otp.sms.exception.CaptchaNotExistException;
import cn.topiam.employee.authentication.otp.sms.exception.PhoneNotExistException;
import cn.topiam.employee.common.enums.MessageNoticeChannel;
import cn.topiam.employee.common.enums.SmsType;
import cn.topiam.employee.core.security.otp.OtpContextHelp;
import cn.topiam.employee.support.exception.InfoValidityFailException;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.HttpResponseUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.employee.common.constant.AuthorizeConstants.OTP_LOGIN;
import static cn.topiam.employee.support.exception.enums.ExceptionStatus.EX000102;

/**
 * AbstractOTPAuthenticationFilter
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/16 21:34
 */
public class SmsOtpAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final Logger               logger                       = LoggerFactory
        .getLogger(SmsOtpAuthenticationFilter.class);
    /**
     * 请求方法
     */
    public static final String         METHOD                       = "POST";

    private String                     recipientParameter           = SmsOtpAuthenticationConstants.RECIPIENT_KEY;
    private String                     codeParameter                = SmsOtpAuthenticationConstants.CODE_KEY;

    public final static String         DEFAULT_FILTER_PROCESSES_URI = OTP_LOGIN + "/sms";

    public static final RequestMatcher SMS_LOGIN_MATCHER            = new AntPathRequestMatcher(
        DEFAULT_FILTER_PROCESSES_URI, HttpMethod.POST.name());

    /**
     * 是否值处理POST请求
     */
    private boolean                    postOnly                     = true;

    public RequestMatcher              captchaLoginMatcher;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            if (postOnly && !METHOD.equalsIgnoreCase(request.getMethod())) {
                throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
            }
            // 获取手机号/邮箱
            String recipient = StringUtils.defaultString(obtainUsername(request), "").trim();
            if (StringUtils.isBlank(recipient)) {
                throw new PhoneNotExistException();
            }
            String code = StringUtils.defaultString(obtainCode(request), "").trim();
            if (StringUtils.isBlank(code)) {
                throw new CaptchaNotExistException();
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(recipient);
            IdentityProviderType type = checkOtp(recipient, code);
            OtpAuthentication authentication = new OtpAuthentication(userDetails, recipient,
                type.value(), userDetails.getAuthorities());
            // Allow subclasses to set the "details" property
            setDetails(request, authentication);
            return authentication;
        } catch (Exception e) {
            HttpResponseUtils.flushResponseJson(response, HttpStatus.BAD_REQUEST.value(),
                ApiRestResult.builder().status(EX000102.getCode()).message(EX000102.getMessage())
                    .build());
            return null;
        }
    }

    public IdentityProviderType checkOtp(String recipient, String code) {
        Boolean checkOtp = otpContextHelp.checkOtp(SmsType.LOGIN.getCode(),
            MessageNoticeChannel.SMS, recipient, code);
        if (!checkOtp) {
            logger.error("用户手机号: [{}], 验证码: [{}] 认证失败", recipient, code);
            throw new InfoValidityFailException(EX000102.getMessage());
        }
        return IdentityProviderType.SMS;
    }

    public String getFilterProcessesUri() {
        return DEFAULT_FILTER_PROCESSES_URI;
    }

    /**
     * Enables subclasses to override the composition of the username, such as
     * by including additional values and a separator.
     *
     * @param request so that request attributes can be retrieved
     * @return the username that will be presented in the
     * <code>Authentication</code> request token to the
     * <code>AuthenticationManager</code>
     */
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(recipientParameter);
    }

    protected String obtainCode(HttpServletRequest request) {
        return request.getParameter(codeParameter);
    }

    /**
     * Provided so that subclasses may configure what is put into the
     * authentication request's details property.
     *
     * @param request     that an authentication request is being created for
     * @param authRequest the authentication request object that should have its details
     *                    set
     */
    protected void setDetails(HttpServletRequest request, OtpAuthentication authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    /**
     * Defines whether only HTTP POST requests will be allowed by this filter.
     * If set to true, and an authentication request is received which is not a
     * POST request, an exception will be raised immediately and authentication
     * will not be attempted. The <tt>unsuccessfulAuthentication()</tt> method
     * will be called as if handling a failed authentication.
     * <p>
     * Defaults to <tt>true</tt> but may be overridden by subclasses.
     */
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getRecipientParameter() {
        return recipientParameter;
    }

    public final String getCodeParameter() {
        return codeParameter;
    }

    public void setRecipientParameter(String recipientParameter) {
        Assert.hasText(recipientParameter, "Phone parameter must not be empty or null");
        this.recipientParameter = recipientParameter;
    }

    public void setCodeParameter(String codeParameter) {
        Assert.hasText(codeParameter, "Code parameter must not be empty or null");
        this.codeParameter = codeParameter;
    }

    public RequestMatcher getRequestMatcher() {
        return captchaLoginMatcher;
    }

    private final UserDetailsService userDetailsService;

    private final OtpContextHelp     otpContextHelp;

    public SmsOtpAuthenticationFilter(UserDetailsService userDetailsService,
                                      OtpContextHelp otpContextHelp) {
        super(SMS_LOGIN_MATCHER);
        this.userDetailsService = userDetailsService;
        this.captchaLoginMatcher = SMS_LOGIN_MATCHER;
        this.otpContextHelp = otpContextHelp;
    }
}
