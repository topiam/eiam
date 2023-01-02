package cn.topiam.employee.protocol.cas.idp.tickets;

import cn.topiam.employee.core.security.userdetails.UserDetails;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 16:25
 */
public interface TicketGrantingTicketFactory extends TicketFactory {

    TicketGrantingTicket create(UserDetails userDetails, String sessionId);
}
