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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/2/2 19:38
 */
public class Response10GeneratorImpl implements ResponseGenerator {

    private static final Logger       logger = LoggerFactory
        .getLogger(Response20GeneratorImpl.class);

    private final HttpServletResponse response;
    private String                    message;

    public Response10GeneratorImpl(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void genFailedMessage(String serviceTicketId) {
        this.message = "no\n";
    }

    @Override
    public void genSucceedMessage(String casUser, Map<String, Object> attributes) {
        this.message = String.format("yes\n%s\n", casUser);
    }

    @Override
    public void sendMessage() throws IOException {
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println(message);
        out.flush();
    }
}
