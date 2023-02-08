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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.io.OutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import static cn.topiam.employee.protocol.cas.idp.constant.ProtocolConstants.*;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/1/2 20:23
 */
public class Response20GeneratorImpl implements ResponseGenerator {

    private static final Logger       logger = LoggerFactory
        .getLogger(Response20GeneratorImpl.class);

    private final HttpServletResponse response;
    private final DocumentBuilder     documentBuilder;
    private String                    message;

    public Response20GeneratorImpl(DocumentBuilder documentBuilder, HttpServletResponse response) {
        this.response = response;
        this.documentBuilder = documentBuilder;
    }

    @Override
    public void genFailedMessage(String serviceTicketId) {
        Document document = documentBuilder.newDocument();
        Element serviceResponse = document.createElement(SERVICE_RESPONSE);
        serviceResponse.setAttribute("xmlns:cas", SERVICE_ATTRIBUTES);

        Element failElement = document.createElement(AUTHENTICATION_FAILED);
        failElement.setAttribute("code", INVALID_TICKET);
        failElement.setTextContent(String.format("未能够识别出目标 '%s'票根", serviceTicketId));
        serviceResponse.appendChild(failElement);
        document.appendChild(serviceResponse);
        this.message = parseDocumentToString(serviceResponse);
    }

    @Override
    public void genSucceedMessage(String casUser, Map<String, Object> attributes) {
        buildDocument(casUser, attributes, false);
    }

    @Override
    public void sendMessage() throws IOException {
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println(message);
        out.flush();
    }

    protected void buildDocument(String casUser, Map<String, Object> attributes,
                                 boolean appendAttributes) {
        Document document = documentBuilder.newDocument();
        OutputFormat outputFormat = OutputFormat.createCompactFormat();
        outputFormat.setExpandEmptyElements(true);
        Element serviceResponse = document.createElement(SERVICE_RESPONSE);
        serviceResponse.setAttribute("xmlns:cas", SERVICE_ATTRIBUTES);
        Element successElement = document.createElement(AUTHENTICATION_SUCCESS);
        serviceResponse.appendChild(successElement);
        Element userElement = document.createElement(CAS_USER);
        userElement.setTextContent(casUser);
        successElement.appendChild(userElement);
        if (appendAttributes && attributes.size() > 0) {
            Element attributeElement = document.createElement(CAS_ATTRIBUTES);
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof List) {
                    for (Object valueItem : (List) value) {
                        Element entryElement = document.createElement("cas:" + entry.getKey());
                        entryElement.setTextContent(String.valueOf(valueItem));
                        attributeElement.appendChild(entryElement);
                    }
                } else {
                    Element entryElement = document.createElement("cas:" + entry.getKey());
                    entryElement.setTextContent(String.valueOf(entry.getValue()));
                    attributeElement.appendChild(entryElement);
                }
            }
            successElement.appendChild(attributeElement);
        }
        document.appendChild(serviceResponse);
        this.message = parseDocumentToString(serviceResponse);
    }

    private String parseDocumentToString(Element node) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            //序列化不保留标头
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            DOMSource domSource = new DOMSource(node);
            transformer.transform(domSource, new StreamResult(bos));
            return bos.toString(StandardCharsets.UTF_8);
        } catch (TransformerException e) {
            logger.error("xmlUtils failed to parseDocumentToString:" + e.getMessage(), e);
        }
        return "";
    }
}
