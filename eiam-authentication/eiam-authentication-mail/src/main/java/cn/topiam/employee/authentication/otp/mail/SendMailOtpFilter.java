/*
 * eiam-authentication-mail - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.otp.mail;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.topiam.employee.authentication.otp.mail.constant.MailOtpAuthenticationConstants;
import cn.topiam.employee.authentication.otp.mail.exception.MailNotExistException;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.core.security.otp.OtpContextHelp;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.HttpResponseUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.employee.common.constant.AuthorizeConstants.LOGIN_PATH;
import static cn.topiam.employee.common.enums.MessageNoticeChannel.MAIL;

/**
 * 发送OTP
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/1/1 22:01
 */
public class SendMailOtpFilter extends OncePerRequestFilter {

    /**
     * 邮件验证码登录路径
     */
    public static final String         LOGIN_MAIL_SEND       = LOGIN_PATH + "/mail/send";

    public static final RequestMatcher MAIL_SEND_OPT_MATCHER = new AntPathRequestMatcher(
        LOGIN_MAIL_SEND, HttpMethod.POST.name());

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException,
                                                                      IOException {
        if (!getRequestMatcher().matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String recipient = request.getParameter(MailOtpAuthenticationConstants.RECIPIENT_KEY);
        if (StringUtils.isBlank(recipient)) {
            throw new MailNotExistException();
        }
        boolean isSend = sendOtp(recipient);
        //发送OTP
        if (isSend) {
            HttpResponseUtils.flushResponseJson(response, HttpStatus.OK.value(),
                ApiRestResult.ok());
            return;
        }
        HttpResponseUtils.flushResponseJson(response, HttpStatus.OK.value(),
            ApiRestResult.err().message("请输入正确的邮箱"));
    }

    public RequestMatcher getRequestMatcher() {
        return MAIL_SEND_OPT_MATCHER;
    }

    public boolean sendOtp(String recipient) {
        //判断是否存在用户
        UserEntity user = userRepository.findByEmail(recipient);
        if (!Objects.isNull(user)) {
            otpContextHelp.sendOtp(recipient, MailType.LOGIN.getCode(), MAIL);
            return true;
        }
        return false;
    }

    private final UserRepository userRepository;
    private final OtpContextHelp otpContextHelp;

    public SendMailOtpFilter(UserRepository userRepository, OtpContextHelp otpContextHelp) {
        this.userRepository = userRepository;
        this.otpContextHelp = otpContextHelp;
    }
}
