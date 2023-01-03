package cn.topiam.employee.protocol.cas.idp.tickets;

import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.protocol.cas.idp.util.TicketUtils;

import java.util.Objects;

import static cn.topiam.employee.protocol.cas.idp.constant.ProtocolConstants.PREFIX_ST;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 16:25
 */
public class TicketGrantingTicketImpl implements TicketGrantingTicket {
    private final String      id;
    private final long        createTime;

    private final UserDetails userDetails;

    TicketGrantingTicketImpl(final String id, final UserDetails userDetails) {
        this.id = id;
        this.userDetails = userDetails;
        this.createTime = System.currentTimeMillis();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    public UserDetails getUserDetails() {
        return this.userDetails;
    }

    @Override
    public long getCreateTime() {
        return createTime;
    }

    @Override
    public synchronized ServiceTicket grantServiceTicket(final String service) {
        return new ServiceTicketImpl(TicketUtils.generateTicket(PREFIX_ST), this, service);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Ticket ticket = (Ticket) obj;
        return Objects.equals(this.id, ticket.getId())
               && Objects.equals(this.createTime, ticket.getCreateTime());
    }

    @Override
    public int hashCode() {
        return id.hashCode() + Long.hashCode(createTime);
    }
}
