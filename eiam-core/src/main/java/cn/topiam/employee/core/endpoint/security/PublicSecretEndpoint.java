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
package cn.topiam.employee.core.endpoint.security;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.common.enums.SecretType;
import cn.topiam.employee.support.constant.EiamConstants;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.AesUtils;

import lombok.Builder;
import lombok.Data;
import static cn.topiam.employee.support.util.HttpResponseUtils.flushResponse;
import static cn.topiam.employee.support.util.HttpResponseUtils.flushResponseJson;

/**
 * 获取加密key
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/5/20 19:32
 */
@Component
@WebServlet(PublicSecretEndpoint.PUBLIC_SECRET_PATH)
public class PublicSecretEndpoint extends HttpServlet {

    public static final String  PUBLIC_SECRET_PATH = EiamConstants.API_PATH + "/public_secret";

    private static final String TYPE               = "type";

    /**
     * 获取加密key
     *
     * @param req  {@link HttpServletRequest}
     * @param resp {@link HttpServletResponse}
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        //调用工具类生成秘钥
        String key = AesUtils.generateKey();
        if (StringUtils.isBlank(req.getParameter(TYPE))) {
            flushResponseJson(resp, HttpStatus.BAD_REQUEST.value(),
                ApiRestResult.err().message("加密类型不能为空").build());
            return;
        }
        SecretType type = SecretType.getType(req.getParameter(TYPE));
        if (Objects.isNull(type)) {
            flushResponseJson(resp, HttpStatus.BAD_REQUEST.value(),
                ApiRestResult.err().message("不支持的加密类型").build());
            return;
        }
        //保存会话
        req.getSession().setAttribute(type.getKey(), key);
        flushResponse(resp, JSONObject.toJSONString(ApiRestResult.builder()
            .result(PublicSecretResult.builder().secret(key).build()).build()));
    }

    @Data
    @Builder
    public static class PublicSecretResult implements Serializable {

        @Serial
        private static final long serialVersionUID = 1868620270063512851L;

        /**
         * 秘钥
         */
        private String            secret;

    }
}
