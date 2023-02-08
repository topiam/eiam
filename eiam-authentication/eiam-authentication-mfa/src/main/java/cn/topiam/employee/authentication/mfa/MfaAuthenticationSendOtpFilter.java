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
import java.io.Serializable;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.common.enums.MessageNoticeChannel;
import cn.topiam.employee.common.enums.SmsType;
import cn.topiam.employee.common.exception.LoginOtpActionNotSupportException;
import cn.topiam.employee.common.util.RequestUtils;
import cn.topiam.employee.core.security.mfa.MfaAuthentication;
import cn.topiam.employee.core.security.otp.OtpContextHelp;
import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.core.security.util.SecurityUtils;
import cn.topiam.employee.core.security.util.UserUtils;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.HttpResponseUtils;
import cn.topiam.employee.support.validation.ValidationHelp;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import static cn.topiam.employee.authentication.mfa.constant.MfaAuthenticationConstants.OTP_SEND_OTP;

/**
 * 发送短信OPT
 *
 * @author SanLi
 * Created by qinggang.zuo@gmail.com / 2689170096@qq.com on  2023/1/1 22:01
 */
public class MfaAuthenticationSendOtpFilter extends OncePerRequestFilter {
    public final static String         DEFAULT_FILTER_PROCESSES_URI = OTP_SEND_OTP;

    public static final RequestMatcher SMS_SEND_OPT_MATCHER         = new AntPathRequestMatcher(
        DEFAULT_FILTER_PROCESSES_URI, HttpMethod.POST.name());

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException,
                                                                      IOException {
        if (!getRequestMatcher().matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        SecurityContext securityContext = SecurityUtils.getSecurityContext();
        Authentication authentication = securityContext.getAuthentication();
        //非MFA对象
        if (!(authentication instanceof MfaAuthentication)) {
            HttpResponseUtils.flushResponseJson(response, HttpStatus.UNAUTHORIZED.value(),
                ApiRestResult.ok());
            return;
        }
        Map<String, Object> params = RequestUtils.getParams(request);
        String value = OBJECT_MAPPER.writeValueAsString(params);
        SendOtpRequest sendOtpRequest = OBJECT_MAPPER.readValue(value, SendOtpRequest.class);
        ValidationHelp.ValidationResult<SendOtpRequest> validationResult = ValidationHelp
            .validateEntity(sendOtpRequest);
        if (validationResult.isHasErrors()) {
            throw new ConstraintViolationException(validationResult.getConstraintViolations());
        }
        //MFA，从会话上下文中获取手机号及邮箱信息
        UserDetails principal = (UserDetails) ((MfaAuthentication) authentication).getFirst()
            .getPrincipal();
        UserEntity user = UserUtils.getUser(principal.getId());
        String email = user.getEmail();
        if (MessageNoticeChannel.MAIL.equals(sendOtpRequest.getChannel())) {
            send(email, MessageNoticeChannel.MAIL);
            HttpResponseUtils.flushResponseJson(response, HttpStatus.OK.value(),
                ApiRestResult.ok());
            return;
        }
        String phone = user.getPhone();
        if (MessageNoticeChannel.SMS.equals(sendOtpRequest.getChannel())) {
            send(phone, MessageNoticeChannel.SMS);
            HttpResponseUtils.flushResponseJson(response, HttpStatus.OK.value(),
                ApiRestResult.ok());
            return;
        }
        throw new LoginOtpActionNotSupportException();
    }

    /**
     * 发送
     *
     * @param target {@link String}
     * @param channel  {@link MessageNoticeChannel}
     */
    private void send(String target, MessageNoticeChannel channel) {
        String type;
        if (channel == MessageNoticeChannel.MAIL) {
            type = MailType.AGAIN_VERIFY.getCode();
        } else {
            type = SmsType.AGAIN_VERIFY.getCode();
        }
        otpContextHelp.sendOtp(target, type, channel);
    }

    /**
     * 发送 OTP 请求
     */
    @Data
    public static class SendOtpRequest implements Serializable {
        /**
         * 渠道
         */
        @Parameter(description = "channel")
        @javax.validation.constraints.NotNull(message = "消息渠道不能为空")
        private MessageNoticeChannel channel;
    }

    public static RequestMatcher getRequestMatcher() {
        return SMS_SEND_OPT_MATCHER;
    }

    private final OtpContextHelp      otpContextHelp;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public MfaAuthenticationSendOtpFilter(OtpContextHelp otpContextHelp) {
        this.otpContextHelp = otpContextHelp;
    }
}
