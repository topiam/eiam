/*
 * eiam-protocol-saml2 - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.saml2.idp.endpoint.xml;

import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.impl.StatusBuilder;
import org.opensaml.saml.saml2.core.impl.StatusCodeBuilder;

/**
 * 状态生成器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/1 22:39
 */
public class StatusGenerator {

    public Status generateStatus(String value) {
        Status status = builderStatus();

        StatusCode statusCode = builderStatusCode(value);

        status.setStatusCode(statusCode);

        return status;
    }

    public Status builderStatus() {
        return new StatusBuilder().buildObject();
    }

    public StatusCode builderStatusCode(String value) {
        StatusCode statusCode = new StatusCodeBuilder().buildObject();
        statusCode.setValue(value);

        return statusCode;
    }

}
