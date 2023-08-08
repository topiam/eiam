/*
 * eiam-core - Employee Identity and Access Management
 * Copyright © 2022-Present Jinan Yuanchuang Network Technology Co., Ltd. (support@topiam.cn)
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

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.topiam.employee.common.constant.AuditConstants;
import static cn.topiam.employee.common.constant.StorageConstants.STORAGE_GROUP_NAME;
import static cn.topiam.employee.common.constant.StorageConstants.STORAGE_PATH;
import static cn.topiam.employee.support.constant.EiamConstants.CONTEXT_ENDPOINT;
import static cn.topiam.employee.support.constant.EiamConstants.CONTEXT_ENDPOINT_GROUP_NAME;

/**
 * ApiConfiguration
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2019/5/16 21:28
 */
@Configuration
public class EiamApiConfiguration {

    /**
     * 存储 RestAPI
     *
     * @return {@link GroupedOpenApi}
     */
    @Bean
    public GroupedOpenApi storageRestApi() {
        return GroupedOpenApi.builder().group(STORAGE_GROUP_NAME).pathsToMatch(STORAGE_PATH + "/**")
            .build();
    }

    /**
     * 审计 RestAPI
     *
     * @return {@link GroupedOpenApi}
     */
    @Bean
    public GroupedOpenApi auditRestApi() {
        return GroupedOpenApi.builder().group(AuditConstants.AUDIT_GROUP_NAME)
            .pathsToMatch(AuditConstants.AUDIT_PATH + "/**").build();
    }

    /**
     * 系统上下文
     *
     * @return {@link GroupedOpenApi}
     */
    @Bean
    public GroupedOpenApi contextRestApi() {
        return GroupedOpenApi.builder().group(CONTEXT_ENDPOINT_GROUP_NAME)
            .pathsToMatch(CONTEXT_ENDPOINT + "/**").build();
    }

}
