package cn.topiam.employee.protocol.cas.idp.tickets;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 16:25
 */
public interface TicketFactory {
    <T extends TicketFactory> T get(Class<? extends Ticket> clazz);
}
