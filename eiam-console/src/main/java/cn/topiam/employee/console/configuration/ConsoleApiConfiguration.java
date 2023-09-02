/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.configuration;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import cn.topiam.employee.EiamConsoleApplication;
import cn.topiam.employee.common.constant.AuthnConstants;
import cn.topiam.employee.support.util.AppVersionUtils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import static cn.topiam.employee.common.constant.AccountConstants.ACCOUNT_API_DOC_GROUP_NAME;
import static cn.topiam.employee.common.constant.AccountConstants.ACCOUNT_API_PATHS;
import static cn.topiam.employee.common.constant.AnalysisConstants.ANALYSIS_GROUP_NAME;
import static cn.topiam.employee.common.constant.AnalysisConstants.ANALYSIS_PATH;
import static cn.topiam.employee.common.constant.AppConstants.*;
import static cn.topiam.employee.common.constant.AuthnConstants.AUTHN_PATH;
import static cn.topiam.employee.common.constant.SettingConstants.SETTING_GROUP_NAME;
import static cn.topiam.employee.common.constant.SettingConstants.SETTING_PATH;

/**
 * ApiConfiguration
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2019/5/16 21:28
 */
@Configuration
public class ConsoleApiConfiguration {

    public ConsoleApiConfiguration(Environment environment) {
        this.environment = environment;
    }

    /**
     * 账户 RestAPI
     *
     * @return {@link GroupedOpenApi}
     */
    @Bean
    public GroupedOpenApi accountRestApi() {
        return GroupedOpenApi.builder().group(ACCOUNT_API_DOC_GROUP_NAME)
            .pathsToMatch(ACCOUNT_API_PATHS).build();
    }

    /**
     * 应用管理 RestAPI
     *
     * @return {@link GroupedOpenApi}
     */
    @Bean
    public GroupedOpenApi applicationRestApi() {
        return GroupedOpenApi.builder().group(APP_GROUP_NAME).pathsToMatch(APP_PATH + "/**")
            .build();
    }

    /**
     * 系统认证 RestAPI
     *
     * @return {@link GroupedOpenApi}
     */
    @Bean
    public GroupedOpenApi authenticationRestApi() {
        return GroupedOpenApi.builder().group(AuthnConstants.AUTHENTICATION_GROUP_NAME)
            .pathsToMatch(AUTHN_PATH + "/**").build();
    }

    /**
     * 分析 RestAPI
     *
     * @return {@link GroupedOpenApi}
     */
    @Bean
    public GroupedOpenApi analysisRestApi() {
        return GroupedOpenApi.builder().group(ANALYSIS_GROUP_NAME)
            .pathsToMatch(ANALYSIS_PATH + "/**").build();
    }

    /**
     * 系统设置 RestAPI
     *
     * @return {@link GroupedOpenApi}
     */
    @Bean
    public GroupedOpenApi settingRestApi() {
        return GroupedOpenApi.builder().group(SETTING_GROUP_NAME).pathsToMatch(SETTING_PATH + "/**")
            .build();
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
            .version(AppVersionUtils.getVersion(EiamConsoleApplication.class));
    }

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(info());
    }

    private final Environment environment;

}
