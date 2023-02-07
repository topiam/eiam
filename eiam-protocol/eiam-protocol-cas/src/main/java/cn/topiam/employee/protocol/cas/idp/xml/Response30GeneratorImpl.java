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
package cn.topiam.employee.protocol.cas.idp.xml;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/2/2 19:56
 */
public class Response30GeneratorImpl extends Response20GeneratorImpl {

    private static final Logger logger = LoggerFactory.getLogger(Response20GeneratorImpl.class);

    public Response30GeneratorImpl(DocumentBuilder documentBuilder, HttpServletResponse response) {
        super(documentBuilder, response);
    }

    @Override
    public void genSucceedMessage(String casUser, Map<String, Object> attributes) {
        buildDocument(casUser, attributes, true);
    }
}
