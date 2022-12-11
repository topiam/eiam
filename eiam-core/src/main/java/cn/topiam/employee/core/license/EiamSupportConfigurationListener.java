/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.license;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;
import static cn.topiam.employee.core.configuration.EiamSupportProperties.DEFAULT_PREFIX;

/**
 * TopIamSupportConfigurationListener
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/3/11
 */
public class EiamSupportConfigurationListener implements
                                              ApplicationListener<ApplicationEnvironmentPreparedEvent>,
                                              Ordered {

    /**
     * 控制台的公开 URL
     */
    public static String CONSOLE_PUBLIC_BASE_URL      = DEFAULT_PREFIX
                                                        + ".server.console-public-base-url";

    /**
     * 门户端的公开 URL
     */
    public static String PORTAL_PUBLIC_BASE_URL       = DEFAULT_PREFIX
                                                        + ".server.portal-public-base-url";

    /**
     * 同步器的公开 URL
     */
    public static String SYNCHRONIZER_PUBLIC_BASE_URL = DEFAULT_PREFIX
                                                        + ".server.synchronizer-public-base-url";

    /**
     * openapi的公开 URL
     */
    public static String OPENAPI_PUBLIC_BASE_URL      = DEFAULT_PREFIX
                                                        + ".server.openapi-public-base-url";

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        // check topiam.server.console-public-base-url
        String consolePublicBaseUrl = environment.getProperty(CONSOLE_PUBLIC_BASE_URL);
        Assert.isTrue(!StringUtils.isBlank(consolePublicBaseUrl),
            CONSOLE_PUBLIC_BASE_URL + " must be configured!");
        // check topiam.server.portal-public-base-url
        String portalPublicBaseUrl = environment.getProperty(PORTAL_PUBLIC_BASE_URL);
        Assert.isTrue(!StringUtils.isBlank(portalPublicBaseUrl),
            PORTAL_PUBLIC_BASE_URL + " must be configured!");
        // check topiam.server.synchronizer-public-base-url
        String synchronizerPublicBaseUrl = environment.getProperty(SYNCHRONIZER_PUBLIC_BASE_URL);
        Assert.isTrue(!StringUtils.isBlank(synchronizerPublicBaseUrl),
            SYNCHRONIZER_PUBLIC_BASE_URL + " must be configured!");
        // check topiam.server.synchronizer-public-base-url
        String openapiPublicBaseUrl = environment.getProperty(OPENAPI_PUBLIC_BASE_URL);
        Assert.isTrue(!StringUtils.isBlank(openapiPublicBaseUrl),
            OPENAPI_PUBLIC_BASE_URL + " must be configured!");

    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 20;
    }
}
