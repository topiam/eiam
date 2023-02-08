/*
 * eiam-protocol-saml2 - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.saml2.idp.endpoint;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.collect.Lists;

import cn.topiam.employee.application.ApplicationService;
import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.application.saml2.Saml2ApplicationService;
import cn.topiam.employee.application.saml2.model.Saml2SsoModel;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.core.security.savedredirect.HttpSessionRedirectCache;
import cn.topiam.employee.core.security.savedredirect.RedirectCache;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.exception.TopIamException;

import lombok.AllArgsConstructor;
import static cn.topiam.employee.audit.enums.EventType.APP_SSO;
import static cn.topiam.employee.common.constants.AuthorizeConstants.FE_LOGIN;
import static cn.topiam.employee.core.security.util.SecurityUtils.isAuthenticated;
import static cn.topiam.employee.protocol.saml2.constant.ProtocolConstants.IDP_SAML2_SSO_INITIATOR;

/**
 * IDP 发起单点登录端点
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/7 22:46
 */
@SuppressWarnings("DuplicatedCode")
@AllArgsConstructor
public class Saml2InitSingleSignOnEndpointFilter extends OncePerRequestFilter
                                                 implements OrderedFilter {

    private static final RequestMatcher SAML2_INIT_SINGLE_SIGN_MATCHER = new AntPathRequestMatcher(
        IDP_SAML2_SSO_INITIATOR, HttpMethod.POST.name());
    private final RedirectCache         redirectCache                  = new HttpSessionRedirectCache();

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request     {@link HttpServletRequest}
     * @param response    {@link HttpServletResponse}
     * @param filterChain {@link FilterChain}
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException,
                                                                      IOException {
        boolean success = true;
        String result = null;
        if (!isAuthenticated()) {
            //Saved Redirect
            redirectCache.saveRedirect(request, response, RedirectCache.RedirectType.REQUEST);
            //跳转登录
            response.sendRedirect(ServerContextHelp.getPortalPublicBaseUrl() + FE_LOGIN);
            return;
        }
        //@formatter:off
        if (SAML2_INIT_SINGLE_SIGN_MATCHER.matches(request)) {
            //获取应用配置
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            ApplicationService applicationService = applicationServiceLoader.getApplicationService(applicationContext.getAppTemplate());
            Saml2SsoModel modal = ((Saml2ApplicationService) applicationService).getSsoModel(String.valueOf(applicationContext.getAppId()));
            try{
                //获取 RelayState
                String relayState = StringUtils.defaultString(modal.getRelayState());
                Saml2IdpSingleSignOnEndpointFilter.httpBinding(response, modal, relayState);
                return;
            }catch (Exception e){
                success=false;
                result=e.getMessage();
                throw new TopIamException(e.getMessage());
            }finally {
                AuditEventPublish publish = ApplicationContextHelp.getBean(AuditEventPublish.class);
                publish.publish(APP_SSO, Lists.newArrayList(Target.builder().id(modal.getAppId()).type(TargetType.APPLICATION).build()),result,success? EventStatus.SUCCESS:EventStatus.FAIL);
            }
        }
        filterChain.doFilter(request, response);
        //@formatter:on
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * SAML2应用配置
     */
    private final ApplicationServiceLoader applicationServiceLoader;

    public static RequestMatcher getRequestMatcher() {
        return SAML2_INIT_SINGLE_SIGN_MATCHER;
    }
}
