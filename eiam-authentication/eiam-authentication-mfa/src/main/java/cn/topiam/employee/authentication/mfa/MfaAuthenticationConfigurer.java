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

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import cn.topiam.employee.core.security.otp.OtpContextHelp;

/**
 * Mfa Authentication Configurer
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/10 22:58
 */
public final class MfaAuthenticationConfigurer<H extends HttpSecurityBuilder<H>> extends
                                              AbstractAuthenticationFilterConfigurer<H, MfaAuthenticationConfigurer<H>, MfaAuthenticationFilter> {

    private final OtpContextHelp           otpContextHelp;
    private final MfaAuthenticationHandler mfaAuthenticationHandler;

    public MfaAuthenticationConfigurer(OtpContextHelp otpContextHelp,
                                       AuthenticationSuccessHandler successHandler,
                                       AuthenticationFailureHandler authenticationFailureHandler) {
        Assert.notNull(otpContextHelp, "otpContextHelp must not be null");
        Assert.notNull(successHandler, "successHandler must not be null");
        Assert.notNull(authenticationFailureHandler,
            "authenticationFailureHandler must not be null");
        this.otpContextHelp = otpContextHelp;
        mfaAuthenticationHandler = new MfaAuthenticationHandler(successHandler,
            authenticationFailureHandler);
    }

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
    public void init(H http) throws Exception {
        //设置登录成功失败处理器
        super.successHandler(mfaAuthenticationHandler);
        super.failureHandler(mfaAuthenticationHandler);
        //MFA认证
        MfaAuthenticationFilter loginAuthenticationFilter = new MfaAuthenticationFilter();
        this.setAuthenticationFilter(loginAuthenticationFilter);
        //处理URL
        super.loginProcessingUrl(MfaAuthenticationFilter.DEFAULT_FILTER_PROCESSES_URI);
        super.init(http);
    }

    @Override
    public void configure(H http) throws Exception {
        //Mfa认证方式
        http.addFilterBefore(new MfaAuthenticationMfaFactorsFilter(),
            UsernamePasswordAuthenticationFilter.class);
        //Mfa认证方式
        http.addFilterAfter(new MfaAuthenticationSendOtpFilter(otpContextHelp),
            MfaAuthenticationMfaFactorsFilter.class);
        //Mfa认证
        http.addFilterAfter(this.getAuthenticationFilter(),
            MfaAuthenticationMfaFactorsFilter.class);
        super.configure(http);
    }

    public static RequestMatcher getRequestMatcher() {
        return MfaAuthenticationFilter.getRequestMatcher();
    }

}
