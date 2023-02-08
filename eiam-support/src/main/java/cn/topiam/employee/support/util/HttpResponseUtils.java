/*
 * eiam-support - Employee Identity and Access Management Program
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
package cn.topiam.employee.support.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * HttpResponseUtils
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/3 21:25
 */
public class HttpResponseUtils {
    private static final String       NULL          = "null";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 将数据刷新写回web端
     *
     * @param response        {@link HttpServletResponse}  response对象
     * @param responseContent {@link String}  返回的数据
     */
    public static void flushResponse(HttpServletResponse response, String responseContent) {
        try {
            response.setCharacterEncoding("UTF-8");
            // 针对ajax中页面编码为GBK的情况，一定要加上以下两句
            response.setHeader("Cache-Control", "no-cache");
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            if (StringUtils.isNoneBlank(responseContent)) {
                writer.write(responseContent);
            } else {
                writer.write("");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回JSON数据
     *
     * @param response HttpServletResponse
     * @param status   状态码
     * @param data     数据
     */
    public static void flushResponseJson(HttpServletResponse response, int status, Object data) {
        try {
            response.setCharacterEncoding("UTF-8");
            // 针对ajax中页面编码为GBK的情况，一定要加上以下两句
            response.setHeader("Cache-Control", "no-cache");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(status);
            PrintWriter writer = response.getWriter();
            if (ObjectUtils.isNotEmpty(data)) {
                String value = OBJECT_MAPPER.writeValueAsString(data);
                // 指定序列化输入的类型
                writer.write(value);
            } else {
                writer.write("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wrap the optional into a {@link ResponseEntity} with an {@link HttpStatus#OK} status, or if it's empty, it
     * returns a {@link ResponseEntity} with {@link HttpStatus#NOT_FOUND}.
     *
     * @param <X>           type of the response
     * @param maybeResponse response to return if present
     * @return response containing {@code maybeResponse} if present or {@link HttpStatus#NOT_FOUND}
     */
    public static <X> ResponseEntity<X> wrapOrNotFound(Optional<X> maybeResponse) {
        return wrapOrNotFound(maybeResponse, null);
    }

    /**
     * Wrap the optional into a {@link ResponseEntity} with an {@link HttpStatus#OK} status with the headers, or if it's
     * empty, it returns a {@link ResponseEntity} with {@link HttpStatus#NOT_FOUND}.
     *
     * @param <X>           type of the response
     * @param maybeResponse response to return if present
     * @param header        headers to be added to the response
     * @return response containing {@code maybeResponse} if present or {@link HttpStatus#NOT_FOUND}
     */
    public static <X> ResponseEntity<X> wrapOrNotFound(Optional<X> maybeResponse,
                                                       HttpHeaders header) {
        return maybeResponse.map(response -> ResponseEntity.ok().headers(header).body(response))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
