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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import cn.topiam.employee.EiamPortalApplication;
import cn.topiam.employee.support.util.VersionUtils;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

/**
 * ApiConfiguration
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2019/5/16 21:28
 */
@Configuration
public class PortalApiConfiguration {

    /**
     * API INFO
     *
     * @return {@link Info}
     */
    private Info info() {
        Contact contact = new Contact();
        contact.setEmail("support@topiam.cn");
        contact.setName("TOPIAM");
        contact.setUrl("https://eiam.topiam.cn");
        return new Info()
            //title
            .title(environment.getProperty("spring.application.name"))
            //描述
            .description("TOPIAM 门户端 REST API 文档")
            //服务条款网址
            .termsOfService("https://eiam.topiam.cn")
            //内容
            .contact(contact)
            //版本
            .version(VersionUtils.getVersion(EiamPortalApplication.class));
    }

    /**
     * 定义openapi
     *
     * @return {@link OpenAPI}
     */
    @Bean
    public OpenAPI openApi() {
        OpenAPI openApi = new OpenAPI();
        openApi.setComponents(new Components());
        openApi.setPaths(new Paths());
        openApi.setInfo(info());
        return openApi;
    }

    private final Environment environment;

    public PortalApiConfiguration(Environment environment) {
        this.environment = environment;
    }
}
