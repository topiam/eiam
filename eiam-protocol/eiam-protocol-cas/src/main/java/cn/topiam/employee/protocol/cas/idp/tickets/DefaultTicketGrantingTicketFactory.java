package cn.topiam.employee.protocol.cas.idp.tickets;

import cn.topiam.employee.core.security.userdetails.UserDetails;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 16:25
 */
public class DefaultTicketGrantingTicketFactory implements TicketGrantingTicketFactory {

    @Override
    public <T extends TicketFactory> T get(final Class<? extends Ticket> clazz) {
        return (T) this;
    }

    @Override
    // TODO: 2023/1/2 TGT本来应该以TGT-xxx命名，此处为了兼容系统session，直接使用sessionId作为TGT的id
    public TicketGrantingTicket create(UserDetails userDetails, String sessionId) {
        return new TicketGrantingTicketImpl(sessionId, userDetails);
    }
}
