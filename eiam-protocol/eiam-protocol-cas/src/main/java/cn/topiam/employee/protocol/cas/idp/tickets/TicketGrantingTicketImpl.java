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
package cn.topiam.employee.protocol.cas.idp.tickets;

import java.util.Objects;

import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.protocol.cas.idp.util.TicketUtils;
import static cn.topiam.employee.protocol.cas.idp.constant.ProtocolConstants.PREFIX_ST;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 16:25
 */
public class TicketGrantingTicketImpl implements TicketGrantingTicket {
    private final String      id;
    private final long        createTime;

    private final UserDetails userDetails;

    TicketGrantingTicketImpl(final String id, final UserDetails userDetails) {
        this.id = id;
        this.userDetails = userDetails;
        this.createTime = System.currentTimeMillis();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public UserDetails getUserDetails() {
        return this.userDetails;
    }

    @Override
    public long getCreateTime() {
        return createTime;
    }

    @Override
    public synchronized ServiceTicket grantServiceTicket(final String service) {
        return new ServiceTicketImpl(TicketUtils.generateTicket(PREFIX_ST), this, service);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Ticket ticket = (Ticket) obj;
        return Objects.equals(this.id, ticket.getId())
               && Objects.equals(this.createTime, ticket.getCreateTime());
    }

    @Override
    public int hashCode() {
        return id.hashCode() + Long.hashCode(createTime);
    }
}
