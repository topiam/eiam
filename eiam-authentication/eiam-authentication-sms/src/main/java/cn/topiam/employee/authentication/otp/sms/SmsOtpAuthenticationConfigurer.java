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
package cn.topiam.employee.authentication.otp.sms;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.core.security.otp.OtpContextHelp;

/**
 * 认证配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/10 22:58
 */
public class SmsOtpAuthenticationConfigurer extends
                                            AbstractAuthenticationFilterConfigurer<HttpSecurity, SmsOtpAuthenticationConfigurer, SmsOtpAuthenticationFilter> {

    /**
     * Create the {@link RequestMatcher} given a loginProcessingUrl
     *
     * @param loginProcessingUrl creates the {@link RequestMatcher} based upon the
     *                           loginProcessingUrl
     * @return the {@link RequestMatcher} to use based upon the loginProcessingUrl
     */
    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl);
    }

    @Override
    public void init(HttpSecurity http) throws Exception {
        //OTP
        SmsOtpAuthenticationFilter loginAuthenticationFilter = getAbstractOtpAuthenticationFilter();
        this.setAuthenticationFilter(loginAuthenticationFilter);
        //处理URL
        super.loginProcessingUrl(loginAuthenticationFilter.getFilterProcessesUri());
        super.init(http);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        SendSmsOtpFilter sendOtpFilter = getAbstractSendOtpFilter();
        http.addFilterAfter(sendOtpFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(this.getAuthenticationFilter(), sendOtpFilter.getClass());
        super.configure(http);
    }

    public RequestMatcher getRequestMatcher() {
        return getAbstractOtpAuthenticationFilter().getRequestMatcher();
    }

    public SmsOtpAuthenticationFilter getAbstractOtpAuthenticationFilter() {
        return new SmsOtpAuthenticationFilter(userDetailsService, otpContextHelp);
    }

    public SendSmsOtpFilter getAbstractSendOtpFilter() {
        return new SendSmsOtpFilter(userRepository, otpContextHelp);
    }

    private final UserRepository     userRepository;
    private final UserDetailsService userDetailsService;

    private final OtpContextHelp     otpContextHelp;

    public SmsOtpAuthenticationConfigurer(UserRepository userRepository,
                                          UserDetailsService userDetailsService,
                                          OtpContextHelp otpContextHelp) {
        Assert.notNull(userDetailsService, "userRepository must not be null");
        Assert.notNull(userDetailsService, "userDetailsService must not be null");
        Assert.notNull(otpContextHelp, "otpContextHelp must not be null");
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.otpContextHelp = otpContextHelp;
    }

    public static SmsOtpAuthenticationConfigurer smsOtp(UserRepository userRepository,
                                                        UserDetailsService userDetailsService,
                                                        OtpContextHelp otpContextHelp) {
        return new SmsOtpAuthenticationConfigurer(userRepository, userDetailsService,
            otpContextHelp);
    }
}
