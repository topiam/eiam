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

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.View;

import cn.topiam.employee.support.error.TopIamErrorAttributes;
import cn.topiam.employee.support.error.TopIamErrorStaticView;

/**
 *
 * ErrorConfiguration
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/5 22:16
 */
@Configuration
@AutoConfigureBefore(value = { ErrorMvcAutoConfiguration.class })
public class EiamErrorConfiguration {

    /**
     * defaultErrorView
     *
     * @return {@link  View}
     */
    @Bean(name = "error")
    public View defaultErrorView() {
        return new TopIamErrorStaticView();
    }

    /**
     * errorAttributes
     *
     * @return {@link  DefaultErrorAttributes}
     */
    @Bean
    public TopIamErrorAttributes errorAttributes() {
        return new TopIamErrorAttributes();
    }
}
