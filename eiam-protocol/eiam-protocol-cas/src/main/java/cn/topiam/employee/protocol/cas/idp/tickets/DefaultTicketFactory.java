package cn.topiam.employee.protocol.cas.idp.tickets;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 16:25
 */
public class DefaultTicketFactory implements TicketFactory {
    private Map<String, Object>         factoryMap;
    private ServiceTicketFactory        serviceTicketFactory        = new DefaultServiceTicketFactory();
    private TicketGrantingTicketFactory ticketGrantingTicketFactory = new DefaultTicketGrantingTicketFactory();

    public void initialize() {
        serviceTicketFactory = new DefaultServiceTicketFactory();
        ticketGrantingTicketFactory = new DefaultTicketGrantingTicketFactory();
        factoryMap = new HashMap<>();
        factoryMap.put(TicketGrantingTicket.class.getCanonicalName(),
            this.ticketGrantingTicketFactory);
        factoryMap.put(ServiceTicket.class.getCanonicalName(), this.serviceTicketFactory);
    }

    @Override
    public <T extends TicketFactory> T get(final Class<? extends Ticket> clazz) {
        return (T) this.factoryMap.get(clazz.getCanonicalName());
    }
}
