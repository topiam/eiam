/*
 * eiam-portal - Employee Identity and Access Management
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
package cn.topiam.employee.portal.configuration;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import cn.topiam.employee.EiamPortalApplication;
import cn.topiam.employee.support.util.AppVersionUtils;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import static cn.topiam.employee.support.constant.EiamConstants.V1_API_PATH;

/**
 * ApiConfiguration
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2019/5/16 21:28
 */
@Configuration
@RequiredArgsConstructor
public class PortalApiConfiguration {
    /**
     * 账户 RestAPI
     *
     * @return {@link GroupedOpenApi}
     */
    @Bean
    public GroupedOpenApi accountRestApi() {
        return GroupedOpenApi.builder().group("账户管理").pathsToMatch(V1_API_PATH + "/account/**")
            .build();
    }

    /**
     * 应用 RestAPI
     *
     * @return {@link GroupedOpenApi}
     */
    @Bean
    public GroupedOpenApi appRestApi() {
        return GroupedOpenApi.builder().group("应用管理").pathsToMatch(V1_API_PATH + "/app/**").build();
    }

    /**
     * API INFO
     *
     * @return {@link Info}
     */
    private Info info() {
        Contact contact = new Contact();
        contact.setEmail("support@topiam.cn");
        contact.setName("TopIAM");
        contact.setUrl("https://eiam.topiam.cn");
        return new Info()
            //title
            .title(environment.getProperty("spring.application.name"))
            //描述
            .description("REST API 文档")
            //服务条款网址
            .termsOfService("https://eiam.topiam.cn")
            //内容
            .contact(contact)
            //版本
            .version(AppVersionUtils.getVersion(EiamPortalApplication.class));
    }

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(info());
    }

    private final Environment environment;
}
