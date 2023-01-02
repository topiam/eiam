package cn.topiam.employee.protocol.cas.idp.tickets;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 16:25
 */
public class DefaultServiceTicketFactory implements ServiceTicketFactory {
    @Override
    public ServiceTicket create(final TicketGrantingTicket ticketGrantingTicket,
                                final String service) {
        if (ticketGrantingTicket == null) {
            return null;
        }
        return ticketGrantingTicket.grantServiceTicket(service);
    }

    @Override
    public <T extends TicketFactory> T get(final Class<? extends Ticket> clazz) {
        return (T) this;
    }
}
