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
package cn.topiam.employee.core.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import static cn.topiam.employee.core.configuration.EiamSupportProperties.DEFAULT_PREFIX;

/**
 * TopIam Support Properties
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/5 22:19
 */
@Data
@ConfigurationProperties(value = DEFAULT_PREFIX)
public class EiamSupportProperties {
    /**
     * topiam
     */
    public static final String DEFAULT_PREFIX = "topiam";

    /**
     * 演示环境
     */
    @NestedConfigurationProperty
    private final Demo         demo           = new Demo();

    /**
     * 安全相关
     */
    @NestedConfigurationProperty
    private final Security     security       = new Security();

    /**
     * 异步配置
     */
    @NestedConfigurationProperty
    private final Async        async          = new Async();

    /**
     * 服务器配置
     */
    @NestedConfigurationProperty
    private final Server       server         = new Server();

    /**
     * 演示环境
     */
    @Getter
    @Setter
    public static class Demo {
        /**
         * 是否开启
         */
        boolean open = false;
    }

    /**
     * 安全配置
     */
    @Getter
    @Setter
    public static class Security {

    }

    /**
     * 异步
     *
     * @author TopIAM
     * Created by support@topiam.cn on  2021/10/5 22:54
     */
    @Getter
    @Setter
    public static class Async {
        /**
         *核心池大小
         */
        private int corePoolSize  = 2;
        /**
         * 连接池最大连接数
         */
        private int maxPoolSize   = 50;
        /**
         * 队列
         */
        private int queueCapacity = 10000;
    }

    /**
     * Server
     *
     * @author TopIAM
     * Created by support@topiam.cn on  2022/3/31 21:14
     */
    @Getter
    @Setter
    public static class Server {
        /**
         * 最终用户访问 TopIAM 控制台的公开 URL，此设置不能以斜杠 (/) 结尾。
         */
        private String consolePublicBaseUrl;

        /**
         * 最终用户访问 TopIAM 门户端 的公开 URL，此设置不能以斜杠 (/) 结尾。
         */
        private String portalPublicBaseUrl;

        /**
         *  最终用户访问 TopIAM 同步器 的公开 URL，此设置不能以斜杠 (/) 结尾。
         */
        private String synchronizerPublicBaseUrl;

        /**
         *  最终用户访问 TopIAM OpenAPI 的公开 URL，此设置不能以斜杠 (/) 结尾。
         */
        private String openApiPublicBaseUrl;
    }
}
