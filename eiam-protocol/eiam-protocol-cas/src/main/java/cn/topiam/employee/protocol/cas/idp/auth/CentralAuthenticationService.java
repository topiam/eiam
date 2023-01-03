package cn.topiam.employee.protocol.cas.idp.auth;

import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.protocol.cas.idp.tickets.ServiceTicket;
import cn.topiam.employee.protocol.cas.idp.tickets.Ticket;
import cn.topiam.employee.protocol.cas.idp.tickets.TicketGrantingTicket;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/1/2 23:43
 */
public interface CentralAuthenticationService {
    TicketGrantingTicket createTicketGrantingTicket(UserDetails userDetails, String sessionId);

    ServiceTicket grantServiceTicket(String tgtId, String service);

    <T extends Ticket> T getTicket(String id, Class<T> clazz);

    ServiceTicket validateServiceTicket(String id, String service);

    void destroyTicketGrantingTicket(String var1);
}
