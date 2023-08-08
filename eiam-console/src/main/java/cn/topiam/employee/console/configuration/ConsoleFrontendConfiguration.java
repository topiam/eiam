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

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * 控制台前端配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on   2022/12/4 21:49
 */
@Configuration
public class ConsoleFrontendConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射静态资源根目录到  frontend
        registry.addResourceHandler("/**").addResourceLocations("classpath:/fe/")
            .resourceChain(true).addResolver(new PathResourceResolver() {
                // 后端匹配不到路由时转给前端
                @Override
                protected Resource getResource(@NotNull String resourcePath,
                                               @NotNull Resource location) throws IOException {
                    Resource resource = super.getResource(resourcePath, location);
                    if (resource == null) {
                        resource = super.getResource("index.html", location);
                    }
                    return resource;
                }
            });
    }
}
