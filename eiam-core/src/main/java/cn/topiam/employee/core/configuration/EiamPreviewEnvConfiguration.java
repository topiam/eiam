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
package cn.topiam.employee.core.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.topiam.employee.support.preview.DemoEnvAspect;

/**
 * 演示环境
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/5 22:16
 */
@Configuration
@EnableConfigurationProperties(EiamSupportProperties.class)
public class EiamPreviewEnvConfiguration {

    public EiamPreviewEnvConfiguration(EiamSupportProperties properties) {
        this.demo = properties.getDemo();
    }

    /**
     * 演示环境
     *
     * @return {@link DemoEnvAspect}
     */
    @Bean
    @ConditionalOnMissingBean
    public DemoEnvAspect demoEnvironmentAspect() {
        return new DemoEnvAspect(demo.isOpen());
    }

    /**
     * properties
     */
    private final EiamSupportProperties.Demo demo;
}
