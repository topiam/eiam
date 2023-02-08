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
        factoryMap = new HashMap<>(16);
        factoryMap.put(TicketGrantingTicket.class.getCanonicalName(),
            this.ticketGrantingTicketFactory);
        factoryMap.put(ServiceTicket.class.getCanonicalName(), this.serviceTicketFactory);
    }

    @Override
    public <T extends TicketFactory> T get(final Class<? extends Ticket> clazz) {
        return (T) this.factoryMap.get(clazz.getCanonicalName());
    }
}
