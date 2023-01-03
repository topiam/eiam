package cn.topiam.employee.protocol.cas.idp.tickets;

import cn.topiam.employee.core.security.userdetails.UserDetails;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 16:25
 * <p>
 * 其实TGT可以理解为用户登陆的session，包含了用户的信息
 */
public interface TicketGrantingTicket extends Ticket {
    ServiceTicket grantServiceTicket(String service);

    UserDetails getUserDetails();
}
