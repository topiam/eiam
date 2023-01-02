package cn.topiam.employee.protocol.cas.idp.auth;

import cn.topiam.employee.protocol.cas.idp.tickets.ServiceTicket;
import cn.topiam.employee.protocol.cas.idp.tickets.Ticket;
import cn.topiam.employee.protocol.cas.idp.tickets.TicketGrantingTicket;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 21:42
 */
@SuppressWarnings("ALL")
@Service
public class CentralCacheService {
    private static final int                    DEFAULT_ST_EXPIRED_TIME  = 10;
    //将Service Ticket存放到redis，一次性使用，10秒内过期
    private static final int                    DEFAULT_TGT_EXPIRED_TIME = 7200;
    //TicketGrantingTicket 默认两个小时过期
    private final RedisTemplate<Object, Object> redisTemplate;

    public CentralCacheService(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(Ticket ticket) {
        long timeout = 0;
        if (ticket instanceof ServiceTicket) {
            timeout = DEFAULT_ST_EXPIRED_TIME;
        }
        if (ticket instanceof TicketGrantingTicket) {
            timeout = DEFAULT_TGT_EXPIRED_TIME;
        }
        redisTemplate.opsForValue().set(ticket.getId(), ticket, timeout, TimeUnit.SECONDS);
    }

    public Ticket get(String id) {
        return (Ticket) redisTemplate.opsForValue().get(id);
    }

    public void remove(String id) {
        redisTemplate.delete(id);
    }
}
