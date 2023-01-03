package cn.topiam.employee.protocol.cas.idp.endpoint;

import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.protocol.cas.idp.auth.CentralAuthenticationService;
import cn.topiam.employee.protocol.cas.idp.tickets.ServiceTicket;
import cn.topiam.employee.protocol.cas.idp.xml.ResponseGenerator;
import cn.topiam.employee.protocol.cas.idp.xml.ResponseGeneratorImpl;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static cn.topiam.employee.protocol.cas.idp.constant.ProtocolConstants.SERVICE;
import static cn.topiam.employee.protocol.cas.idp.constant.ProtocolConstants.TICKET;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/1/2 13:38
 */
public class CasIdpValidateEndpointFilter extends OncePerRequestFilter implements OrderedFilter {

    private final ApplicationServiceLoader     applicationServiceLoader;

    private final SessionRegistry              sessionRegistry;

    private final CentralAuthenticationService centralAuthenticationService;

    private static final OrRequestMatcher      orRequestMatcher;

    static {
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        requestMatchers.add(new AntPathRequestMatcher(
            ProtocolConstants.CasEndpointConstants.CAS_VALIDATE_PATH, HttpMethod.GET.name()));
        requestMatchers.add(new AntPathRequestMatcher(
            ProtocolConstants.CasEndpointConstants.CAS_VALIDATE_V2_PATH, HttpMethod.GET.name()));
        requestMatchers.add(new AntPathRequestMatcher(
            ProtocolConstants.CasEndpointConstants.CAS_VALIDATE_V3_PATH, HttpMethod.GET.name()));
        orRequestMatcher = new OrRequestMatcher(requestMatchers);
    }

    private final DocumentBuilder documentBuilder;

    public CasIdpValidateEndpointFilter(ApplicationServiceLoader applicationServiceLoader,
                                        SessionRegistry sessionRegistry,
                                        CentralAuthenticationService centralAuthenticationService,
                                        DocumentBuilder documentBuilder) {
        this.applicationServiceLoader = applicationServiceLoader;
        this.sessionRegistry = sessionRegistry;
        this.centralAuthenticationService = centralAuthenticationService;
        this.documentBuilder = documentBuilder;

    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (orRequestMatcher.matches(request)) {
            ResponseGenerator generator = new ResponseGeneratorImpl(documentBuilder, response);
            String ticketId = request.getParameter(TICKET);
            String service = request.getParameter(SERVICE);
            ServiceTicket serviceTicket = centralAuthenticationService
                .validateServiceTicket(ticketId, service);
            if (serviceTicket == null) {
                generator.genFailedMessage(ticketId);
            } else {
                UserDetails userDetails = serviceTicket.getTicketGrantingTicket().getUserDetails();
                // TODO: 2023/1/2 根据配置返回额外的属性配置
                generator.genSucceedMessage(userDetails.getUsername(), new HashMap<>());
            }
            generator.sendMessage();
        }
        filterChain.doFilter(request, response);

    }

    public static RequestMatcher getRequestMatcher() {
        return orRequestMatcher;
    }

}
