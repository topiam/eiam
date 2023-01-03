package cn.topiam.employee.protocol.cas.idp.tickets;

import cn.topiam.employee.protocol.cas.idp.auth.CentralCacheService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 16:25
 */
@Slf4j
@Service
public class DefaultTicketRegistry implements TicketRegistry {

    CentralCacheService cacheService;

    public DefaultTicketRegistry(CentralCacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public void addTicket(final Ticket ticket) {
        cacheService.save(ticket);
    }

    @Override
    public Ticket getTicket(final String ticketId) {
        return cacheService.get(ticketId);
    }

    @Override
    public <T extends Ticket> T getTicket(final String id, final Class<T> clazz) {
        Preconditions.checkNotNull(clazz, "clazz cannot be null");
        Ticket ticket = this.getTicket(id);
        if (ticket == null) {
            return null;
        }

        if (!clazz.isAssignableFrom(ticket.getClass())) {
            throw new ClassCastException("Ticket [" + ticket.getId() + " is of type "
                                         + ticket.getClass() + " when we were expecting " + clazz);
        }
        return (T) ticket;
    }

    @Override
    public void updateTicket(final Ticket ticket) {
        addTicket(ticket);
    }

    @Override
    public void deleteTicket(final String id) {
        cacheService.remove(id);
    }

}
