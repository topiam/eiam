/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.context;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.PortMapper;
import org.springframework.security.web.PortMapperImpl;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.PortResolverImpl;

import cn.topiam.employee.core.configuration.EiamSupportProperties;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.context.PublicBaseUrlBuilder;
import cn.topiam.employee.support.context.ServletContextHelp;

import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.support.context.PublicBaseUrlBuilder.HTTP;
import static cn.topiam.employee.support.context.PublicBaseUrlBuilder.HTTPS;

/**
 * ServerContextHelp
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/10/13 20:26
 */
@Slf4j
public final class ServerContextHelp {
    /**
     * 获取控制台基础网址
     *
     * @return {@link  String}
     */
    public static String getConsolePublicBaseUrl() {
        EiamSupportProperties properties = ApplicationContextHelp
            .getBean(EiamSupportProperties.class);
        return properties.getServer().getConsolePublicBaseUrl();
    }

    /**
     * 获取门户基础网址
     *
     * @return {@link  String}
     */
    public static String getPortalPublicBaseUrl() {
        EiamSupportProperties properties = ApplicationContextHelp
            .getBean(EiamSupportProperties.class);
        return properties.getServer().getPortalPublicBaseUrl();
    }

    /**
     * 获取同步基础网址
     *
     * @return {@link  String}
     */
    public static String getSynchronizerPublicBaseUrl() {
        EiamSupportProperties properties = ApplicationContextHelp
            .getBean(EiamSupportProperties.class);
        return properties.getServer().getSynchronizerPublicBaseUrl();
    }

    /**
     * 获取OpenAPI基础网址
     *
     * @return {@link  String}
     */
    public static String getOpenApiPublicBaseUrl() {
        EiamSupportProperties properties = ApplicationContextHelp
            .getBean(EiamSupportProperties.class);
        return properties.getServer().getOpenApiPublicBaseUrl();
    }

    /**
     * 获取基础网址
     *
     * @return {@link  String}
     */
    public static String getPublicBaseUrl() {
        HttpServletRequest request = ServletContextHelp.getRequest();
        int serverPort = PORT_RESOLVER.getServerPort(request);
        Integer httpsPort = PORT_MAPPER.lookupHttpsPort(serverPort);
        //@formatter:off
        PublicBaseUrlBuilder urlBuilder = new PublicBaseUrlBuilder();
        urlBuilder.setScheme(request.getScheme());
        urlBuilder.setServerName(request.getServerName());
        if (HTTPS.equals(request.getScheme())){
            urlBuilder.setPort(httpsPort);
        }
        if (HTTP.equals(request.getScheme())){
            urlBuilder.setPort(serverPort);
        }
        return urlBuilder.getUrl();
        //@formatter:on
    }

    private static final PortResolver PORT_RESOLVER = new PortResolverImpl();
    private static final PortMapper   PORT_MAPPER   = new PortMapperImpl();

}
