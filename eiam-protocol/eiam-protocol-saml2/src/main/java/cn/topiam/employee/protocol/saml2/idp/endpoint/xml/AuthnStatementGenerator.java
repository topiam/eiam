/*
 * eiam-protocol-saml2 - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.saml2.idp.endpoint.xml;

import java.time.Instant;

import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.impl.AuthnContextBuilder;
import org.opensaml.saml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml.saml2.core.impl.AuthnStatementBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.topiam.employee.common.enums.app.AuthnContextClassRefType;
import cn.topiam.employee.support.context.ServletContextHelp;

/**
 * 属性语句生成器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/1 22:52
 */
public class AuthnStatementGenerator {
    private static final Logger            logger = LoggerFactory
        .getLogger(AuthnStatementGenerator.class);
    private final AuthnContextClassRefType authnContextClassRefType;

    public AuthnStatementGenerator(AuthnContextClassRefType authnContextClassRefType) {
        this.authnContextClassRefType = authnContextClassRefType;
    }

    public AuthnStatement generateAuthnStatements() {
        AuthnStatement authnStatement = new AuthnStatementBuilder().buildObject();
        AuthnContext authnContext = new AuthnContextBuilder().buildObject();
        AuthnContextClassRef authnContextClassRef = new AuthnContextClassRefBuilder().buildObject();
        authnContextClassRef.setURI(authnContextClassRefType.getValue());
        authnContext.setAuthnContextClassRef(authnContextClassRef);
        authnStatement.setAuthnContext(authnContext);
        authnStatement.setAuthnInstant(Instant.now());
        //当从 SP 登出时 需要通过 SessionIndex 来确定出会话
        authnStatement.setSessionIndex(ServletContextHelp.getSession().getId());
        return authnStatement;
    }
}
