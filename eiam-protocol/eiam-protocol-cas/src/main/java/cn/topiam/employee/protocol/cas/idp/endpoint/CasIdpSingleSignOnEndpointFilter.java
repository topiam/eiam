package cn.topiam.employee.protocol.cas.idp.endpoint;

import cn.topiam.employee.application.ApplicationService;
import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.application.CasApplicationService;
import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.core.protocol.CasSsoModel;
import cn.topiam.employee.core.security.savedredirect.HttpSessionRedirectCache;
import cn.topiam.employee.core.security.savedredirect.RedirectCache;
import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.core.security.util.SecurityUtils;
import cn.topiam.employee.protocol.cas.idp.auth.CentralAuthenticationService;
import cn.topiam.employee.protocol.cas.idp.tickets.ServiceTicket;
import cn.topiam.employee.protocol.cas.idp.tickets.TicketGrantingTicket;
import cn.topiam.employee.support.exception.TopIamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static cn.topiam.employee.common.constants.AuthorizeConstants.FE_LOGIN;
import static cn.topiam.employee.core.security.util.SecurityUtils.isAuthenticated;
import static cn.topiam.employee.protocol.cas.idp.constant.ProtocolConstants.SERVICE;
import static cn.topiam.employee.protocol.cas.idp.constant.ProtocolConstants.TICKET;

/**
 * CAS 单点登录
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 17:38
 */
@SuppressWarnings("DuplicatedCode")
public class CasIdpSingleSignOnEndpointFilter extends OncePerRequestFilter
                                              implements OrderedFilter {
    private static final Logger                logger                  = LoggerFactory
        .getLogger(CasIdpSingleSignOnEndpointFilter.class);
    private static final RequestMatcher        CAS_SSO_REQUEST_MATCHER = new AntPathRequestMatcher(
        ProtocolConstants.CasEndpointConstants.CAS_LOGIN_PATH, HttpMethod.GET.name());
    private final RedirectCache                redirectCache           = new HttpSessionRedirectCache();

    /**
     * 应用配置
     */
    private final ApplicationServiceLoader     applicationServiceLoader;

    private final SessionRegistry              sessionRegistry;

    private final CentralAuthenticationService centralAuthenticationService;

    public CasIdpSingleSignOnEndpointFilter(ApplicationServiceLoader applicationServiceLoader,
                                            SessionRegistry sessionRegistry,
                                            CentralAuthenticationService centralAuthenticationService) {
        this.applicationServiceLoader = applicationServiceLoader;
        this.sessionRegistry = sessionRegistry;
        this.centralAuthenticationService = centralAuthenticationService;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (CAS_SSO_REQUEST_MATCHER.matches(request)) {
            if (!isAuthenticated()) {
                //Saved Redirect
                if (!CollectionUtils.isEmpty(request.getParameterMap())) {
                    redirectCache.saveRedirect(request, response,
                        RedirectCache.RedirectType.REQUEST);
                }
                //跳转登录
                response.sendRedirect(ServerContextHelp.getPortalPublicBaseUrl() + FE_LOGIN);
                return;
            }
            UserDetails userDetails = SecurityUtils.getCurrentUser();
            List<SessionInformation> sessionInformations = sessionRegistry
                .getAllSessions(userDetails.getUsername(), false);
            if (sessionInformations.size() != 1) {
                throw new TopIamException("用户身份出现异常");
            }
            String sessionId = sessionInformations.get(0).getSessionId();
            //获取应用配置
            ApplicationContext applicationContext = ApplicationContextHolder
                .getApplicationContext();
            ApplicationService applicationService = applicationServiceLoader
                .getApplicationService(applicationContext.getAppTemplate());
            CasSsoModel ssoModel = ((CasApplicationService) applicationService)
                .getSsoModel(applicationContext.getAppId());

            String service = request.getParameter(SERVICE);

            TicketGrantingTicket ticketGrantingTicket = centralAuthenticationService
                .getTicket(sessionId, TicketGrantingTicket.class);

            if (ticketGrantingTicket == null) {
                ticketGrantingTicket = centralAuthenticationService
                    .createTicketGrantingTicket(userDetails, sessionId);
            }
            ServiceTicket serviceTicket = centralAuthenticationService
                .grantServiceTicket(ticketGrantingTicket.getId(), service);

            response.sendRedirect(UriComponentsBuilder.fromHttpUrl(ssoModel.getSsoCallbackUrl())
                .queryParam(TICKET, serviceTicket.getId()).build().toString());
        }
        filterChain.doFilter(request, response);
    }

    public static RequestMatcher getRequestMatcher() {
        return CAS_SSO_REQUEST_MATCHER;
    }
}
