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
package cn.topiam.employee.core.security.decrypt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.common.enums.SecretType;
import cn.topiam.employee.support.context.ServletContextHelp;
import cn.topiam.employee.support.util.AesUtils;
import static cn.topiam.employee.support.constant.EiamConstants.TOPIAM_ENCRYPT_SECRET;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/10 19:25
 */
@Component
@ControllerAdvice
public class DecryptRequestBodyAdvice extends RequestBodyAdviceAdapter {
    private final Logger        logger  = LoggerFactory.getLogger(DecryptRequestBodyAdvice.class);

    private static final String ENCRYPT = "encrypt";

    @Override
    public boolean supports(MethodParameter methodParameter, @NonNull Type targetType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasMethodAnnotation(DecryptRequestBody.class)
               || methodParameter.hasParameterAnnotation(DecryptRequestBody.class);
    }

    @NotNull
    @Override
    public HttpInputMessage beforeBodyRead(@NonNull final HttpInputMessage inputMessage,
                                           @NonNull MethodParameter parameter,
                                           @NonNull Type targetType,
                                           @NonNull Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        String bodyStr = IOUtils.toString(inputMessage.getBody(), StandardCharsets.UTF_8);
        String encrypt = JSONObject.parseObject(bodyStr).getString(ENCRYPT);
        if (StringUtils.isBlank(encrypt)) {
            throw new EncryptSecretNotExistException();
        }
        HttpSession session = ServletContextHelp.getSession();
        try {
            String key = (String) session.getAttribute(SecretType.ENCRYPT.getKey());
            if (StringUtils.isBlank(key)) {
                throw new DecryptException("encrypt 参数不能为空", HttpStatus.BAD_REQUEST);
            }
            String decrypt = AesUtils.decrypt(encrypt, key);
            final ByteArrayInputStream stream = new ByteArrayInputStream(
                decrypt.getBytes(StandardCharsets.UTF_8));
            return new HttpInputMessage() {
                @NotNull
                @Override
                public InputStream getBody() {
                    return stream;
                }

                @NotNull
                @Override
                public HttpHeaders getHeaders() {
                    return inputMessage.getHeaders();
                }
            };
        } catch (Exception e) {
            logger.error("@Decrypt 数据解密发生异常: {}", e.getMessage());
        } finally {
            //clear session secret
            session.removeAttribute(TOPIAM_ENCRYPT_SECRET);
        }
        return super.beforeBodyRead(inputMessage, parameter, targetType, converterType);
    }
}
