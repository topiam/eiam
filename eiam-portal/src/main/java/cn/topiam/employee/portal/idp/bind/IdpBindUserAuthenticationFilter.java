/*
 * eiam-portal - Employee Identity and Access Management Program
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
package cn.topiam.employee.portal.idp.bind;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.enums.EventType;
import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.authentication.common.modal.IdpUser;
import cn.topiam.employee.authentication.common.service.UserIdpService;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.po.UserIdpBindPo;
import cn.topiam.employee.common.enums.SecretType;
import cn.topiam.employee.common.repository.account.UserIdpRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.core.security.authentication.IdpAuthentication;
import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.core.security.util.SecurityUtils;
import cn.topiam.employee.portal.pojo.request.AccountBindIdpRequest;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.context.ServletContextHelp;
import cn.topiam.employee.support.trace.TraceUtils;
import cn.topiam.employee.support.util.AesUtils;
import cn.topiam.employee.support.validation.ValidationHelp;

import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.authentication.common.filter.AbstractIdpAuthenticationProcessingFilter.TOPIAM_USER_BIND_IDP;
import static cn.topiam.employee.common.constants.AuthorizeConstants.USER_BIND_IDP;
import static cn.topiam.employee.portal.constant.PortalConstants.BIND_ACCOUNT;
import static cn.topiam.employee.portal.constant.PortalConstants.TOPIAM_BIND_STATE_COOKIE_NAME;
import static cn.topiam.employee.support.constant.EiamConstants.TOPIAM_BIND_MFA_SECRET;

/**
 * Idp 绑定用户认证过滤器
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/1 23:53
 */
@Slf4j
public class IdpBindUserAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    public final static String         DEFAULT_FILTER_PROCESSES_URI = USER_BIND_IDP;
    public static final RequestMatcher IDP_BIND_USER_MATCHER        = new AntPathRequestMatcher(
        DEFAULT_FILTER_PROCESSES_URI, HttpMethod.POST.name());

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
        //@formatter:off
        TraceUtils.put(UUID.randomUUID().toString());
        SecurityContext securityContext = SecurityUtils.getSecurityContext();
        Authentication authentication = securityContext.getAuthentication();
        if (!(authentication instanceof IdpAuthentication)){
            return null;
        }
        Object value = request.getSession().getAttribute(TOPIAM_USER_BIND_IDP);
        AccountBindIdpRequest idpRequest = new AccountBindIdpRequest(request.getParameter("username"),request.getParameter("password"));
        ValidationHelp.ValidationResult<AccountBindIdpRequest> requestValidationResult = ValidationHelp.validateEntity(idpRequest);
        if (requestValidationResult.isHasErrors()){
            throw new ConstraintViolationException(requestValidationResult.getConstraintViolations());
        }
        //参数为空
        if (Objects.isNull(value)) {
            String content = "用户 [" + idpRequest.getUsername() + "] 绑定 IDP 失败, 参数无效";
            log.error(content);
            auditEventPublish.publish(EventType.BIND_IDP_USER, content, EventStatus.SUCCESS);
            throw new UserBindIdpException("user_bind_idp_invalid_argument_error", content);
        }
        idpRequest.setPassword(idpRequest.getPassword());
        //会话上下文数据转 UserInfo
        IdpUser idpUserInfo = JSONObject.parseObject((String) value, IdpUser.class);
        //验证
        UserEntity user = authnUserBindValidate(idpRequest, idpUserInfo.getProviderId(),idpUserInfo.getOpenId());
        //认证
        Boolean bind = userIdpService.bindUserIdp(user.getId().toString(),idpUserInfo);
        if (bind){
            String content="用户 ["+idpRequest.getUsername()+"] 绑定 IDP 成功";
            UserDetails userDetails = userIdpService.getUserDetails(idpUserInfo.getOpenId(), idpUserInfo.getProviderId());
            IdpAuthentication token = new IdpAuthentication(userDetails, idpUserInfo.getProviderType().value(), idpUserInfo.getProviderId(), true, userDetails.getAuthorities());
            // Allow subclasses to set the "details" property
            token.setDetails(this.authenticationDetailsSource.buildDetails(request));
            removeState(request,response);
            //Audit
            log.info(content);
            auditEventPublish.publish(EventType.BIND_IDP_USER,content,EventStatus.SUCCESS);
            return token;
        }
        return null;
        //@formatter:on
    }

    private void removeState(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse) {
        //移除state
        httpServletRequest.getSession().removeAttribute(TOPIAM_BIND_MFA_SECRET);
        Cookie cookie = new Cookie(TOPIAM_BIND_STATE_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        cookie.setPath(BIND_ACCOUNT);
        httpServletResponse.addCookie(cookie);
    }

    private UserEntity authnUserBindValidate(AccountBindIdpRequest request, String providerId,
                                             String openId) {
        HttpServletRequest servletRequest = ServletContextHelp.getRequest();
        //根据用户名查询用户
        UserRepository userRepository = ApplicationContextHelp.getBean(UserRepository.class);
        UserEntity user = userRepository.findByUsername(request.getUsername());
        if (Objects.isNull(user)) {
            String content = "用户 [" + request.getUsername() + "] 绑定 IDP 失败, 未查询到用户信息";
            log.error(content);
            auditEventPublish.publish(EventType.BIND_IDP_USER, content, EventStatus.SUCCESS);
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        //拿到秘钥，解密
        try {
            String secret = (String) servletRequest.getSession()
                .getAttribute(SecretType.LOGIN.getKey());
            request.setPassword(AesUtils.decrypt(request.getPassword(), secret));
        } catch (Exception exception) {
            String content = "用户 [" + request.getUsername() + "] 绑定 IDP 失败, 密码解密异常";
            log.error(content, exception);
            auditEventPublish.publish(EventType.BIND_IDP_USER, content, EventStatus.SUCCESS);
            throw new UserBindIdpException();
        }
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!matches) {
            String content = "用户 [" + request.getUsername() + "] 绑定 IDP 失败, 用户密码验证失败";
            log.error(content);
            auditEventPublish.publish(EventType.BIND_IDP_USER, content, EventStatus.SUCCESS);
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        //是否绑定
        Optional<UserIdpBindPo> bindEntity = userIdpRepository.findByIdpIdAndUserId(providerId,
            user.getId());
        if (bindEntity.isPresent()) {
            String content = "用户 [" + request.getUsername() + "] 绑定 IDP 失败, 用户已存在绑定";
            log.error(content);
            auditEventPublish.publish(EventType.BIND_IDP_USER, content, EventStatus.SUCCESS);
            throw new UsernameNotFoundException("用户已存在绑定");
        }
        //是否绑定
        bindEntity = userIdpRepository.findByIdpIdAndOpenId(providerId, openId);
        if (bindEntity.isPresent()) {
            String content = "用户 [" + request.getUsername() + "] 绑定 IDP 失败, 已存在其他用户绑定";
            log.error(content);
            auditEventPublish.publish(EventType.BIND_IDP_USER, content, EventStatus.SUCCESS);
            throw new UsernameNotFoundException("已存在其他用户绑定");
        }
        return user;
    }

    /**
     * 认证用户详情
     */
    private final UserIdpService    userIdpService;

    private final UserIdpRepository userIdpRepository;

    private final PasswordEncoder   passwordEncoder;

    private final AuditEventPublish auditEventPublish;

    protected IdpBindUserAuthenticationFilter(UserIdpService userIdpService,
                                              UserIdpRepository userIdpRepository,
                                              PasswordEncoder passwordEncoder,
                                              AuditEventPublish auditEventPublish) {
        super(IDP_BIND_USER_MATCHER);
        Assert.notNull(userIdpService, "userIdpService must not be null");
        Assert.notNull(userIdpRepository, "userIdpRepository must not be null");
        Assert.notNull(passwordEncoder, "passwordEncoder must not be null");
        Assert.notNull(auditEventPublish, "auditEventPublish must not be null");
        this.userIdpService = userIdpService;
        this.userIdpRepository = userIdpRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditEventPublish = auditEventPublish;
    }

}
