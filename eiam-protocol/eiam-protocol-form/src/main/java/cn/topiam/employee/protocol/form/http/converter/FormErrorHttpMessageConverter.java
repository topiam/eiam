/*
 * eiam-protocol-form - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.form.http.converter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.*;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import cn.topiam.employee.protocol.code.http.converter.HttpMessageConverters;
import cn.topiam.employee.protocol.form.endpoint.FormParameterNames;
import cn.topiam.employee.protocol.form.exception.FormError;

/**
 * A {@link HttpMessageConverter} for an {@link FormError Jwt Error}.
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/7/19 21:57
 */
public class FormErrorHttpMessageConverter extends AbstractHttpMessageConverter<FormError> {

    private static final Charset                                         DEFAULT_CHARSET          = StandardCharsets.UTF_8;

    private static final ParameterizedTypeReference<Map<String, Object>> STRING_OBJECT_MAP        = new ParameterizedTypeReference<>() {
                                                                                                  };

    private final GenericHttpMessageConverter<Object>                    jsonMessageConverter     = HttpMessageConverters
        .getJsonMessageConverter();

    protected Converter<Map<String, String>, FormError>                  errorConverter           = new FormErrorConverter();

    protected Converter<FormError, Map<String, String>>                  errorParametersConverter = new FormErrorParametersConverter();

    public FormErrorHttpMessageConverter() {
        super(DEFAULT_CHARSET, MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }

    @Override
    protected boolean supports(@NotNull Class<?> clazz) {
        return FormError.class.isAssignableFrom(clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected FormError readInternal(@NotNull Class<? extends FormError> clazz,
                                     @NotNull HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
        try {
            Map<String, Object> errorParameters = (Map<String, Object>) this.jsonMessageConverter
                .read(STRING_OBJECT_MAP.getType(), null, inputMessage);
            return this.errorConverter.convert(errorParameters.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, (entry) -> String.valueOf(entry.getValue()))));
        } catch (Exception ex) {
            throw new HttpMessageNotReadableException(
                "An error occurred reading the Jwt Error: " + ex.getMessage(), ex, inputMessage);
        }
    }

    @Override
    protected void writeInternal(@NotNull FormError formError,
                                 @NotNull HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {
        try {
            Map<String, String> errorParameters = this.errorParametersConverter.convert(formError);
            if (MapUtils.isNotEmpty(errorParameters)) {
                this.jsonMessageConverter.write(errorParameters, STRING_OBJECT_MAP.getType(),
                    MediaType.APPLICATION_JSON, outputMessage);
            }
        } catch (Exception ex) {
            throw new HttpMessageNotWritableException(
                "An error occurred writing the Jwt Error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Sets the {@link Converter} used for converting the Jwt Error parameters to an
     * {@link FormError}.
     * @param errorConverter the {@link Converter} used for converting to an
     * {@link FormError}
     */
    public final void setErrorConverter(Converter<Map<String, String>, FormError> errorConverter) {
        Assert.notNull(errorConverter, "errorConverter cannot be null");
        this.errorConverter = errorConverter;
    }

    /**
     * Sets the {@link Converter} used for converting the {@link FormError} to a
     * {@code Map} representation of the Jwt Error parameters.
     * @param errorParametersConverter the {@link Converter} used for converting to a
     * {@code Map} representation of the Error parameters
     */
    public final void setErrorParametersConverter(Converter<FormError, Map<String, String>> errorParametersConverter) {
        Assert.notNull(errorParametersConverter, "errorParametersConverter cannot be null");
        this.errorParametersConverter = errorParametersConverter;
    }

    /**
     * A {@link Converter} that converts the provided Jwt Error parameters to an
     * {@link FormError}.
     */
    private static class FormErrorConverter implements Converter<Map<String, String>, FormError> {

        @Override
        public FormError convert(Map<String, String> parameters) {
            String errorCode = parameters.get(FormParameterNames.ERROR);
            String errorDescription = parameters.get(FormParameterNames.ERROR_DESCRIPTION);
            String errorUri = parameters.get(FormParameterNames.ERROR_URI);
            return new FormError(errorCode, errorDescription, errorUri);
        }

    }

    /**
     * A {@link Converter} that converts the provided {@link FormError} to a {@code Map}
     * representation of Jwt Error parameters.
     */
    private static class FormErrorParametersConverter implements
                                                      Converter<FormError, Map<String, String>> {

        @Override
        public Map<String, String> convert(@NonNull FormError formError) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put(FormParameterNames.ERROR, formError.getErrorCode());
            if (StringUtils.hasText(formError.getDescription())) {
                parameters.put(FormParameterNames.ERROR_DESCRIPTION, formError.getDescription());
            }
            if (StringUtils.hasText(formError.getUri())) {
                parameters.put(FormParameterNames.ERROR_URI, formError.getUri());
            }
            return parameters;
        }
    }

}
