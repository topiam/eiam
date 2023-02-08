/*
 * eiam-protocol-cas - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.cas.idp.endpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;

import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.protocol.cas.idp.auth.CentralAuthenticationService;
import cn.topiam.employee.protocol.cas.idp.tickets.ServiceTicket;
import cn.topiam.employee.protocol.cas.idp.xml.Response20GeneratorImpl;
import cn.topiam.employee.protocol.cas.idp.xml.Response30GeneratorImpl;
import cn.topiam.employee.protocol.cas.idp.xml.ResponseGenerator;
import static cn.topiam.employee.protocol.cas.idp.constant.ProtocolConstants.SERVICE;
import static cn.topiam.employee.protocol.cas.idp.constant.ProtocolConstants.TICKET;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/1/2 13:38
 */
public class Cas30IdpValidateEndpointFilter extends OncePerRequestFilter implements OrderedFilter {

    private final ApplicationServiceLoader     applicationServiceLoader;

    private final SessionRegistry              sessionRegistry;

    private final CentralAuthenticationService centralAuthenticationService;

    private static final OrRequestMatcher      orRequestMatcher;

    private static final RequestMatcher        CAS20_VALIDATE_MATCHER = new AntPathRequestMatcher(
        ProtocolConstants.CasEndpointConstants.CAS_VALIDATE_V2_PATH, HttpMethod.GET.name());

    private static final RequestMatcher        CAS30_VALIDATE_MATCHER = new AntPathRequestMatcher(
        ProtocolConstants.CasEndpointConstants.CAS_VALIDATE_V3_PATH, HttpMethod.GET.name());

    static {
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        requestMatchers.add(CAS20_VALIDATE_MATCHER);
        requestMatchers.add(CAS30_VALIDATE_MATCHER);
        orRequestMatcher = new OrRequestMatcher(requestMatchers);
    }

    private final DocumentBuilder documentBuilder;

    public Cas30IdpValidateEndpointFilter(ApplicationServiceLoader applicationServiceLoader,
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
    //Cas30Validate MUST perform the same validation tasks as Cas20Validate and additionally return user attributes in the CAS response.
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (orRequestMatcher.matches(request)) {
            ResponseGenerator generator = CAS20_VALIDATE_MATCHER.matches(request)
                ? new Response20GeneratorImpl(documentBuilder, response)
                : new Response30GeneratorImpl(documentBuilder, response);
            String ticketId = request.getParameter(TICKET);
            String service = request.getParameter(SERVICE);
            ServiceTicket serviceTicket = centralAuthenticationService
                .validateServiceTicket(ticketId, service);
            if (serviceTicket == null) {
                generator.genFailedMessage(ticketId);
            } else {
                UserDetails userDetails = serviceTicket.getTicketGrantingTicket().getUserDetails();
                // TODO: 2023/1/2 Cas30需要根据配置返回额外的属性配置
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
