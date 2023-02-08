/*
 * eiam-authentication-sms - Employee Identity and Access Management Program
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
package cn.topiam.employee.authentication.sms;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.topiam.employee.authentication.sms.exception.PhoneNotExistException;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.core.security.otp.OtpContextHelp;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.HttpResponseUtils;
import static cn.topiam.employee.authentication.sms.constant.SmsAuthenticationConstants.PHONE_KEY;
import static cn.topiam.employee.authentication.sms.constant.SmsAuthenticationConstants.SMS_SEND_OTP;
import static cn.topiam.employee.common.enums.MessageNoticeChannel.SMS;
import static cn.topiam.employee.common.enums.SmsType.LOGIN;

/**
 * 发送短信OPT
 *
 * @author SanLi
 * Created by qinggang.zuo@gmail.com / 2689170096@qq.com on  2023/1/1 22:01
 */
public class SendSmsCaptchaFilter extends OncePerRequestFilter {
    public final static String         DEFAULT_FILTER_PROCESSES_URI = SMS_SEND_OTP;

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
        String phone = request.getParameter(PHONE_KEY);
        if (StringUtils.isBlank(phone)) {
            throw new PhoneNotExistException();
        }
        //判断是否存在用户
        UserEntity user = userRepository.findByPhone(phone);
        if (Objects.isNull(user)) {
            HttpResponseUtils.flushResponseJson(response, HttpStatus.OK.value(),
                ApiRestResult.ok());
            return;
        }
        //发送OPT
        otpContextHelp.sendOtp(phone, LOGIN.getCode(), SMS);
    }

    public static RequestMatcher getRequestMatcher() {
        return SMS_SEND_OPT_MATCHER;
    }

    private final UserRepository userRepository;
    private final OtpContextHelp otpContextHelp;

    public SendSmsCaptchaFilter(UserRepository userRepository, OtpContextHelp otpContextHelp) {
        this.userRepository = userRepository;
        this.otpContextHelp = otpContextHelp;
    }
}
