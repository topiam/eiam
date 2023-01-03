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
