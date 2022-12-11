/*
 * eiam-authentication-sms - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.authentication.sms.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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

import cn.topiam.employee.core.security.authentication.SmsAuthentication;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.HttpResponseUtils;
import static cn.topiam.employee.common.constants.AuthorizeConstants.SMS_LOGIN;
import static cn.topiam.employee.support.exception.enums.ExceptionStatus.EX000102;

/**
 * SmsAuthenticationFilter
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/16 21:34
 */
public class SmsAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String         PHONE_KEY                    = "phone";
    /**
     * 请求方法
     */
    public static final String         METHOD                       = "POST";

    private String                     phoneParameter               = PHONE_KEY;
    /**
     * 是否值处理POST请求
     */
    private boolean                    postOnly                     = true;

    public final static String         DEFAULT_FILTER_PROCESSES_URI = SMS_LOGIN;

    public static final RequestMatcher SMS_LOGIN_MATCHER            = new AntPathRequestMatcher(
        DEFAULT_FILTER_PROCESSES_URI, HttpMethod.POST.name());

    public SmsAuthenticationFilter(UserDetailsService userDetailsService) {
        super(SMS_LOGIN_MATCHER);
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            if (postOnly && !METHOD.equalsIgnoreCase(request.getMethod())) {
                throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
            }
            // 获取手机号
            String phone = StringUtils.defaultString(obtainUsername(request), "").trim();
            UserDetails userDetails = userDetailsService.loadUserByUsername(phone);
            SmsAuthentication authentication = new SmsAuthentication(userDetails, phone,
                userDetails.getAuthorities());
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
        return request.getParameter(phoneParameter);
    }

    /**
     * Provided so that subclasses may configure what is put into the
     * authentication request's details property.
     *
     * @param request     that an authentication request is being created for
     * @param authRequest the authentication request object that should have its details
     *                    set
     */
    protected void setDetails(HttpServletRequest request, SmsAuthentication authRequest) {
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

    public final String getPhoneParameter() {
        return phoneParameter;
    }

    public void setPhoneParameter(String phoneParameter) {
        Assert.hasText(phoneParameter, "Mobile parameter must not be empty or null");
        this.phoneParameter = phoneParameter;
    }

    public static RequestMatcher getRequestMatcher() {
        return SMS_LOGIN_MATCHER;
    }

    private final UserDetailsService userDetailsService;
}
