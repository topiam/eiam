/*
 * eiam-protocol-cas - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.topiam.employee.protocol.cas.idp.auth;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.topiam.employee.protocol.cas.idp.tickets.ServiceTicket;
import cn.topiam.employee.protocol.cas.idp.tickets.Ticket;
import cn.topiam.employee.protocol.cas.idp.tickets.TicketGrantingTicket;

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
