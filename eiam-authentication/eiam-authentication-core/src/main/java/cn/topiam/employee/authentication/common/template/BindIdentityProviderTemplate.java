/*
 * eiam-authentication-core - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.common.template;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.compress.utils.CharsetNames;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.support.exception.TemplateNotExistException;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.extern.slf4j.Slf4j;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/12/2 18:18
 */
@Slf4j
public class BindIdentityProviderTemplate {

    public BindIdentityProviderTemplate() {
        configFreemarkerTemplate();
    }

    /**
     * 返回绑定模版
     *
     * @param response       {@link HttpServletResponse}
     * @param result       {@link ApiRestResult <Void>}
     */
    public void sendAuthorizationResponse(HttpServletResponse response,
                                          ApiRestResult<Void> result) throws IOException {
        //@formatter:off
        try {
            response.setCharacterEncoding(CharsetNames.UTF_8);
            response.setContentType(ContentType.TEXT_HTML.getMimeType());
            Template template = freemarkerTemplateConfiguration.getTemplate("bind_redirect.ftlh");
            Map<String, Object> data = new HashMap<>(16);
            data.put("nonce", System.currentTimeMillis());
            ObjectMapper objectMapper = new ObjectMapper();
            data.put("result", objectMapper.writeValueAsString(result));
            template.process(data, response.getWriter());
        } catch (Exception e) {
            log.error("返回绑定提供商Template异常", e);
            //Html
            response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
        //@formatter:on
    }

    private void configFreemarkerTemplate() {
        try {
            //模板存放路径
            freemarkerTemplateConfiguration
                .setTemplateLoader(new ClassTemplateLoader(this.getClass(), "/template/"));
            //编码
            freemarkerTemplateConfiguration.setDefaultEncoding(CharsetNames.UTF_8);
            //国际化
            freemarkerTemplateConfiguration.setLocale(new Locale("zh_CN"));
        } catch (Exception exception) {
            throw new TemplateNotExistException(exception);
        }
    }

    /**
     * freemarker 配置实例化
     */
    private final Configuration freemarkerTemplateConfiguration = new Configuration(
        Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
}
