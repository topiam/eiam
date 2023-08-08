/*
 * eiam-openapi - Employee Identity and Access Management
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
package cn.topiam.employee.openapi.authorization;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.openapi.constants.OpenApiStatus;
import cn.topiam.employee.support.util.HttpResponseUtils;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/25 21:55
 */
public final class AccessTokenAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Collect error details from the provided parameters and format according to RFC
     * 6750, specifically {@code error}, {@code error_description}, {@code error_uri}, and
     * {@code scope}.
     * @param request that resulted in an <code>AuthenticationException</code>
     * @param httpServletResponse so that the user agent can begin authentication
     * @param authException that caused the invocation
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse httpServletResponse,
                         AuthenticationException authException) {
        Response response = new Response();
        response.setCode(OpenApiStatus.INVALID_ACCESS_TOKEN.getCode());
        response.setMsg(OpenApiStatus.INVALID_ACCESS_TOKEN.getDesc());
        HttpResponseUtils.flushResponseJson(httpServletResponse, HttpStatus.OK.value(),
            objectMapper, response);
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Data
    public static class Response {

        /**
         * code
         */
        @JsonProperty(value = "code")
        @Schema(name = "code")
        private String code;

        /**
         * msg
         */
        @JsonProperty(value = "msg")
        @Schema(name = "msg")
        private String msg;
    }

}
