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

import java.util.List;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.impl.XSStringBuilder;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.impl.AttributeBuilder;
import org.opensaml.saml.saml2.core.impl.AttributeStatementBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.topiam.employee.application.saml2.model.Saml2SsoModel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * AttributeStatements 生成
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/1 22:50
 */
@RequiredArgsConstructor
@Getter
public class AttributeStatementGenerator {
    private static final Logger                          logger = LoggerFactory
        .getLogger(AttributeStatementGenerator.class);
    /**
     * 断言属性
     */
    private final List<Saml2SsoModel.AttributeStatement> attributeStatements;

    public org.opensaml.saml.saml2.core.AttributeStatement generateAttributeStatements() {
        org.opensaml.saml.saml2.core.AttributeStatement attributeStatement = new AttributeStatementBuilder()
            .buildObject();
        // XSStringBuilder
        XSStringBuilder stringBuilder = (XSStringBuilder) XMLObjectProviderRegistrySupport
            .getBuilderFactory().getBuilder(XSString.TYPE_NAME);

        for (Saml2SsoModel.AttributeStatement statement : getAttributeStatements()) {
            Attribute attributeName = new AttributeBuilder().buildObject();
            attributeName.setNameFormat(statement.getNameFormat().getValue());
            //value
            XSString value = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME,
                XSString.TYPE_NAME);
            value.setValue(statement.getValue());
            attributeName.getAttributeValues().add(value);
            //name
            attributeName.setName(statement.getKey());
            attributeStatement.getAttributes().add(attributeName);
        }
        return attributeStatement;
    }
}
