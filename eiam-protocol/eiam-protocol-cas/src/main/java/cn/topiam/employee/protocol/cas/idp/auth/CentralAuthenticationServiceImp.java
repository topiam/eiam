/*
 * eiam-protocol-cas - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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

import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.protocol.cas.idp.tickets.*;

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
