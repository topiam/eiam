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

import java.time.Duration;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * EiamElasticsearchConfiguration
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/1/17
 */
@Configuration
public class EiamElasticsearchConfiguration {

    @Bean
    public RestClientBuilderCustomizer keepAliveCustomizer() {
        return new KeepAliveCustomizer();
    }

    static class KeepAliveCustomizer implements RestClientBuilderCustomizer {
        @Override
        public void customize(RestClientBuilder builder) {

        }

        @Override
        public void customize(HttpAsyncClientBuilder builder) {
            builder.setKeepAliveStrategy((response, context) -> Duration.ofMinutes(5).toMillis());
        }

        @Override
        public void customize(RequestConfig.Builder builder) {
        }
    }
}
