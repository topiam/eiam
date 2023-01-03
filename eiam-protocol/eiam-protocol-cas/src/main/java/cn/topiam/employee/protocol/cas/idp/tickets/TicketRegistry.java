package cn.topiam.employee.protocol.cas.idp.tickets;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 16:25
 */
public interface TicketRegistry {

    void addTicket(Ticket ticket);

    Ticket getTicket(String id);

    <T extends Ticket> T getTicket(String id, Class<T> clazz);

    void updateTicket(Ticket ticket);

    void deleteTicket(String ticketId);
}
