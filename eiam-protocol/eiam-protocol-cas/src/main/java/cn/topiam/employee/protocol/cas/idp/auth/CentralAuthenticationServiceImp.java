package cn.topiam.employee.protocol.cas.idp.auth;

import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.protocol.cas.idp.tickets.*;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/1/1 16:25
 */
@Service(value = "cas-authentication-service")
public class CentralAuthenticationServiceImp implements CentralAuthenticationService {
    final TicketRegistry ticketRegistry;
    final TicketFactory  ticketFactory;

    public CentralAuthenticationServiceImp(TicketRegistry ticketRegistry,
                                           TicketFactory ticketFactory) {
        this.ticketRegistry = ticketRegistry;
        this.ticketFactory = ticketFactory;
    }

    @Override
    public TicketGrantingTicket createTicketGrantingTicket(UserDetails userDetails,
                                                           String sessionId) {
        TicketGrantingTicketFactory ticketGrantingTicketFactory = ticketFactory
            .get(TicketGrantingTicket.class);
        TicketGrantingTicket ticketGrantingTicket = ticketGrantingTicketFactory.create(userDetails,
            sessionId);
        ticketRegistry.addTicket(ticketGrantingTicket);
        return ticketGrantingTicket;
    }

    @Override
    public ServiceTicket grantServiceTicket(final String tgtId, final String service) {
        TicketGrantingTicket ticketGrantingTicket = this.getTicket(tgtId,
            TicketGrantingTicket.class);
        ServiceTicketFactory serviceTicketFactory = ticketFactory.get(ServiceTicket.class);
        ServiceTicket serviceTicket = serviceTicketFactory.create(ticketGrantingTicket, service);
        ticketRegistry.addTicket(serviceTicket);
        return serviceTicket;
    }

    @Override
    public <T extends Ticket> T getTicket(String id, Class<T> clazz) {
        return ticketRegistry.getTicket(id, clazz);
    }

    @Override
    public ServiceTicket validateServiceTicket(String serviceTicketId, String service) {
        try {
            ServiceTicket serviceTicket = ticketRegistry
                .getTicket(Preconditions.checkNotNull(serviceTicketId), ServiceTicket.class);
            TicketGrantingTicket ticketGrantingTicket = Preconditions.checkNotNull(serviceTicket)
                .getTicketGrantingTicket();
            Preconditions.checkNotNull(ticketGrantingTicket);
            return serviceTicket;
        } catch (NullPointerException e) {
            return null;
        } finally {
            this.ticketRegistry.deleteTicket(serviceTicketId);
        }
    }

    @Override
    public void destroyTicketGrantingTicket(final String ticketGrantingTicketId) {
        TicketGrantingTicket ticketGrantingTicket = this.getTicket(ticketGrantingTicketId,
            TicketGrantingTicket.class);
        // TODO: 通知客户端销毁ticket
        ticketRegistry.deleteTicket(ticketGrantingTicketId);
    }
}
