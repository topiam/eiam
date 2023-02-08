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
package cn.topiam.employee.protocol.saml2.idp.endpoint;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.topiam.employee.application.ApplicationService;
import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.application.saml2.Saml2ApplicationService;
import cn.topiam.employee.application.saml2.model.Saml2ProtocolConfig;
import cn.topiam.employee.common.constants.ProtocolConstants;
import static cn.topiam.employee.common.util.SamlUtils.initOpenSaml;
import static cn.topiam.employee.common.util.SamlUtils.transformSamlObject2String;
import static cn.topiam.employee.protocol.saml2.idp.util.Saml2Utils.getEntityDescriptor;

/**
 * Saml Idp 元数据端点
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/7 22:46
 */
public class Saml2IdpMetadataEndpointFilter extends OncePerRequestFilter implements OrderedFilter {

    private final Logger               logger                             = LoggerFactory
        .getLogger(Saml2IdpMetadataEndpointFilter.class);
    public static final RequestMatcher SAML2_IDP_METADATA_REQUEST_MATCHER = new AntPathRequestMatcher(
        ProtocolConstants.Saml2EndpointConstants.SAML_METADATA_PATH, HttpMethod.GET.name());

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request     {@link HttpServletRequest}
     * @param response    {@link HttpServletResponse}
     * @param filterChain {@link FilterChain}
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException,
                                                                      ServletException {
        if (SAML2_IDP_METADATA_REQUEST_MATCHER.matches(request)) {
            ApplicationContext applicationContext = ApplicationContextHolder
                .getApplicationContext();
            ApplicationService applicationService = applicationServiceLoader
                .getApplicationService(applicationContext.getAppTemplate());
            Saml2ProtocolConfig config = ((Saml2ApplicationService) applicationService)
                .getProtocolConfig(String.valueOf(applicationContext.getAppId()));
            // Generate MetadataXml
            EntityDescriptor entityDescriptor = getEntityDescriptor(config);
            String metadataXml = transformSamlObject2String(entityDescriptor);
            // Response
            response.setContentType(MediaType.APPLICATION_XML_VALUE);
            response.setContentLength(metadataXml.length());
            response.getWriter().write(metadataXml);
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * SAML2应用配置
     */
    private final ApplicationServiceLoader applicationServiceLoader;

    public Saml2IdpMetadataEndpointFilter(ApplicationServiceLoader applicationServiceLoader) {
        this.applicationServiceLoader = applicationServiceLoader;
        // 初始化 OpenSAML
        initOpenSaml();
        // BouncyCastleProvider
        java.security.Security
            .addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    public static RequestMatcher getRequestMatcher() {
        return SAML2_IDP_METADATA_REQUEST_MATCHER;
    }
}
