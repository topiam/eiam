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
package cn.topiam.employee.core.listener;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

/**
 *  应用就绪监听器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/23 19:44
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 20 + 2)
public class EiamReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {
    /**
     * HTTP
     */
    public static final String HTTP                 = "http";
    /**
     * HTTPS
     */
    public static final String HTTPS                = "https";
    /**
     * SSL
     */
    public static final String SERVER_SSL_KEY_STORE = "server.ssl.key-store";

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigurableEnvironment env = event.getApplicationContext().getEnvironment();
        String protocol = HTTP;
        if (env.getProperty(SERVER_SSL_KEY_STORE) != null) {
            protocol = HTTPS;
        }
        String port = env.getProperty("local.server.port");
        String hostAddress = null;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String application = ObjectUtils.isEmpty(env.getProperty("spring.application.name"))
            ? "application"
            : env.getProperty("spring.application.name");
        //@formatter:off
        log.info("\n----------------------------------------------------------\n\t"
                        + "名称:\t'{}' is running! Access URLs:\n\t"
                        + "本地:\t {}://localhost:{}\n\t"
                        + "外部:\t {}://{}:{}\n\t"
                        + "API:\t {}://{}:{}/swagger-ui.html\n\t"
                        + "环境:\t {}\n\t"
                        + "用时:\t {}\n----------------------------------------------------------",
                application, protocol, port, protocol, hostAddress, port, protocol, hostAddress, port, env.getActiveProfiles(), event.getTimeTaken().getSeconds() + "s");
        //@formatter:on
    }
}
