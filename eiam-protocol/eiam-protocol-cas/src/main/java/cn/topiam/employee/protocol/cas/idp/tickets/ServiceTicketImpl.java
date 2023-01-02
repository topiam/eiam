package cn.topiam.employee.protocol.cas.idp.tickets;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 16:25
 */
public class ServiceTicketImpl extends TicketGrantingTicketImpl implements ServiceTicket {
    private TicketGrantingTicket ticketGrantingTicket;
    private String               service;

    ServiceTicketImpl(final String id, final TicketGrantingTicket ticket, final String service) {
        super(id, ticket.getUserDetails());
        this.ticketGrantingTicket = ticket;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public String getService() {
        return this.service;
    }

    @Override
    public TicketGrantingTicket getTicketGrantingTicket() {
        return ticketGrantingTicket;
    }
}
