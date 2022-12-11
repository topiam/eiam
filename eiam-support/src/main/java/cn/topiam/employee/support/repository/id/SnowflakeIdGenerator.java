/*
 * eiam-support - Employee Identity and Access Management Program
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
package cn.topiam.employee.support.repository.id;

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import cn.topiam.employee.support.snowflake.Snowflake;

/**
 * SnowflakeIdGenerator
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/12 19:27
 */
public class SnowflakeIdGenerator implements IdentifierGenerator {

    private final Snowflake SNOWFLAKE = new Snowflake();

    public SnowflakeIdGenerator() {

    }

    private Type type;

    /**
     * Configure this instance, given the value of parameters
     * specified by the user as <tt>&lt;param&gt;</tt> elements.
     * This method is called just once, following instantiation.
     *
     * @param type            The id property type descriptor
     * @param params          param values, keyed by parameter name
     * @param serviceRegistry Access to service that may be needed.
     */
    @Override
    public void configure(Type type, Properties params,
                          ServiceRegistry serviceRegistry) throws MappingException {
        this.type = type;
    }

    /**
     * Generate a new identifier.
     *
     * @param session The session from which the request originates
     * @param object  the entity or collection (idbag) for which the id is being generated
     * @return a new identifier
     * @throws HibernateException Indicates trouble generating the identifier
     */
    @Override
    public Serializable generate(SharedSessionContractImplementor session,
                                 Object object) throws HibernateException {

        long id = SNOWFLAKE.nextId();
        if (type instanceof StringType) {
            return String.valueOf(id);
        }
        return id;
    }
}
