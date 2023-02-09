/*
 * eiam-common - Employee Identity and Access Management Program
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
package cn.topiam.employee.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistry;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.saml2.binding.decoding.impl.HTTPPostDecoder;
import org.opensaml.saml.saml2.binding.decoding.impl.HTTPRedirectDeflateDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import cn.topiam.employee.support.exception.TopIamException;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.security.IdentifierGenerationStrategy;
import net.shibboleth.utilities.java.support.security.impl.SecureRandomIdentifierGenerationStrategy;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import static org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport.getMarshallerFactory;
import static org.opensaml.saml.common.xml.SAMLConstants.POST_METHOD;

/**
 * SAML utils
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/18 21:54
 */
public class SamlUtils {
    private static final Logger        logger      = LoggerFactory.getLogger(SamlUtils.class);
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    /**
     * transformSamlObject2String
     *
     * @param samlObject {@link  SAMLObject}
     * @return {@link String}
     */
    public static String transformSamlObject2String(SAMLObject samlObject) {
        try {
            return SerializeSupport.nodeToString(
                Objects.requireNonNull(getMarshallerFactory().getMarshaller(samlObject))
                    .marshall(samlObject));
        } catch (Exception e) {
            throw new TopIamException(e.getMessage());
        }
    }

    private static final IdentifierGenerationStrategy IDENTIFIER_GENERATION_STRATEGY;

    static {
        IDENTIFIER_GENERATION_STRATEGY = new SecureRandomIdentifierGenerationStrategy();

    }

    public static <T> T buildSamlObject(final Class<T> clazz) {
        T object;
        try {
            XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport
                .getBuilderFactory();
            QName defaultElementName = (QName) clazz.getDeclaredField("DEFAULT_ELEMENT_NAME")
                .get(null);
            object = (T) builderFactory.getBuilder(defaultElementName)
                .buildObject(defaultElementName);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new IllegalArgumentException("Could not create SAML object");
        }

        return object;
    }

    public static String generateSecureRandomId() {
        return IDENTIFIER_GENERATION_STRATEGY.generateIdentifier();
    }

    public static void logSamlObject(final XMLObject object) {
        Element element = null;

        if (object instanceof SignableSAMLObject && ((SignableSAMLObject) object).isSigned()
            && object.getDOM() != null) {
            element = object.getDOM();
        } else {
            try {
                Marshaller out = XMLObjectProviderRegistrySupport.getMarshallerFactory()
                    .getMarshaller(object);
                out.marshall(object);
                element = object.getDOM();

            } catch (MarshallingException e) {
                logger.error(e.getMessage(), e);
            }
        }

        String xmlString = SerializeSupport.prettyPrintXML(element);

        logger.info("\n" + xmlString);

    }

    /**
     * 初始化openSaml
     */
    public static void initOpenSaml() {
        if (INITIALIZED.compareAndSet(false, true)) {
            logger.trace("Initializing OpenSAML");
            XMLObjectProviderRegistry registry = new XMLObjectProviderRegistry();
            ConfigurationService.register(XMLObjectProviderRegistry.class, registry);
            registry.setParserPool(getParserPool());
            try {
                InitializationService.initialize();
                logger.debug("Initialized OpenSAML");
            } catch (InitializationException e) {
                logger.error(e.getMessage(), e);
            }
        }
        logger.debug("Refused to re-initialize OpenSAML");
    }

    @SuppressWarnings("HttpUrlsUsage")
    public static ParserPool getParserPool() {
        BasicParserPool parserPool = new BasicParserPool();
        parserPool.setMaxPoolSize(100);
        parserPool.setCoalescing(true);
        parserPool.setIgnoreComments(true);
        parserPool.setIgnoreElementContentWhitespace(true);
        parserPool.setNamespaceAware(true);
        parserPool.setExpandEntityReferences(false);
        parserPool.setXincludeAware(false);

        final Map<String, Boolean> features = new HashMap<>(16);
        features.put("http://xml.org/sax/features/external-general-entities", Boolean.FALSE);
        features.put("http://xml.org/sax/features/external-parameter-entities", Boolean.FALSE);
        features.put("http://apache.org/xml/features/disallow-doctype-decl", Boolean.TRUE);
        features.put("http://apache.org/xml/features/validation/schema/normalized-value",
            Boolean.FALSE);
        features.put("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
        parserPool.setBuilderFeatures(features);
        parserPool.setBuilderAttributes(new HashMap<>(16));
        try {
            parserPool.initialize();
        } catch (ComponentInitializationException e) {
            logger.error(e.getMessage(), e);
        }

        return parserPool;
    }

    /**
     * 获取 Message Context
     *
     * @param request {@link HttpServletRequest}
     * @return {@link MessageContext}
     */
    public static MessageContext getMessageContext(HttpServletRequest request) {
        try {
            if (request.getMethod().equals(POST_METHOD)) {
                HTTPPostDecoder decoder = new HTTPPostDecoder();
                decoder.setHttpServletRequestSupplier(() -> request);
                decoder.initialize();
                decoder.decode();
                return decoder.getMessageContext();
            }
            //GET
            HTTPRedirectDeflateDecoder decoder = new HTTPRedirectDeflateDecoder();
            decoder.setHttpServletRequestSupplier(() -> request);
            decoder.initialize();
            decoder.decode();
            return decoder.getMessageContext();
        } catch (Exception e) {
            throw new TopIamException(e.getMessage(), e);
        }
    }
}
