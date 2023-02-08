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
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.enums.MfaFactor;
import cn.topiam.employee.core.security.util.UserUtils;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.DesensitizationUtil;
import cn.topiam.employee.support.util.HttpResponseUtils;

import lombok.Builder;
import lombok.Data;
import static cn.topiam.employee.authentication.mfa.constant.MfaAuthenticationConstants.LOGIN_MFA_FACTORS;
import static cn.topiam.employee.core.context.SettingContextHelp.getMfaFactors;

/**
 * MfaAuthenticationMfaFactorsFilter
 *
 * @author SanLi
 * Created by qinggang.zuo@gmail.com / 2689170096@qq.com on  2023/1/2 13:28
 */
public class MfaAuthenticationMfaFactorsFilter extends OncePerRequestFilter {

    public final static String         DEFAULT_FILTER_PROCESSES_URI = LOGIN_MFA_FACTORS;

    public static final RequestMatcher LOGIN_MFA_FACTORS_MATCHER    = new AntPathRequestMatcher(
        DEFAULT_FILTER_PROCESSES_URI, HttpMethod.GET.name());

    @Override
    @SuppressWarnings("AlibabaAvoidComplexCondition")
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException,
                                                                      IOException {
        if (!getRequestMatcher().matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        UserEntity user = UserUtils.getUser();
        List<MfaFactorResult> list = new ArrayList<>();
        List<MfaFactor> factors = getMfaFactors();
        for (MfaFactor provider : factors) {
            MfaFactorResult result = MfaFactorResult.builder().build();
            result.setFactor(provider);
            result.setUsable(false);
            //sms
            if (provider.equals(MfaFactor.SMS_OTP) && StringUtils.isNotBlank(user.getPhone())) {
                result.setTarget(DesensitizationUtil.phoneEncrypt(user.getPhone()));
                result.setUsable(true);
            }
            //otp
            if (provider.equals(MfaFactor.EMAIL_OTP) && StringUtils.isNotBlank(user.getEmail())) {
                result.setTarget(DesensitizationUtil.emailEncrypt(user.getEmail()));
                result.setUsable(true);
            }
            //totp
            if (provider.equals(MfaFactor.APP_TOTP)
                && (!Objects.isNull(user.getTotpBind()) && user.getTotpBind())) {
                result.setUsable(true);
            }
            list.add(result);
        }
        HttpResponseUtils.flushResponseJson(response, HttpStatus.OK.value(),
            ApiRestResult.ok(list));
    }

    public static RequestMatcher getRequestMatcher() {
        return LOGIN_MFA_FACTORS_MATCHER;
    }

    /**
     * Mfa 登录方式
     *
     * @author TopIAM
     * Created by support@topiam.cn on  2022/8/13 21:29
     */
    @Builder
    @Data
    public static class MfaFactorResult implements Serializable {

        @Serial
        private static final long serialVersionUID = 7255002979319970337L;
        /**
         * provider
         */
        private MfaFactor         factor;
        /**
         * 可用
         */
        private Boolean           usable;
        /**
         * 目标
         */
        private String            target;
    }
}
