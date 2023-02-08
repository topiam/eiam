/*
 * eiam-common - Employee Identity and Access Management Program
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
package cn.topiam.employee.common.crypto;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/22 21:53
 */
public class EncryptedJsonSerializer extends JsonSerializer<Object> {

    /**
     * 默认序列化工具对象
     */
    private final JsonSerializer<Object> serializer;
    private final Type                   serializerType;

    public EncryptedJsonSerializer() {
        this.serializer = null;
        this.serializerType = null;
    }

    public EncryptedJsonSerializer(JsonSerializer<Object> serializer, Type type) {
        this.serializer = serializer;
        this.serializerType = type;
    }

    @Override
    public void serialize(Object obj, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        StringWriter stringWriter = new StringWriter();
        ObjectCodec objectCodec = jsonGenerator.getCodec();
        JsonGenerator nestedGenerator = null;

        //空对象或空字符串不处理。
        if (obj == null || StringUtils.isEmpty(String.valueOf(obj))) {
            if (serializer == null) {
                serializerProvider.defaultSerializeValue(obj, jsonGenerator);
            } else {
                serializer.serialize(obj, jsonGenerator, serializerProvider);
            }
            return;
        }
        /*
            生成一个新的JsonGenerator，用于将obj写入。
         */
        if (objectCodec instanceof ObjectMapper) {
            nestedGenerator = objectCodec.getFactory().createGenerator(stringWriter);
        }

        if (nestedGenerator == null) {
            throw new NullPointerException("nestedGenerator == null");
        }

        /*
            将数据写入到新生成的JsonGenerator中
         */
        if (serializer == null) {
            serializerProvider.defaultSerializeValue(obj, nestedGenerator);
        } else {
            serializer.serialize(obj, nestedGenerator, serializerProvider);
        }

        nestedGenerator.close();
        /*
            JsonGenerator会生成一个带双引号的字符串， 将数据加密后写入。
         */
        String value = stringWriter.getBuffer().toString();
        try {
            String newValue = value.substring(1, value.length() - 1);
            if (StringUtils.isNotEmpty(newValue)) {
                if (Type.ENCRYPT == serializerType) {
                    newValue = EncryptContextHelp.encrypt(newValue);
                } else if (Type.DECRYPT == serializerType) {
                    newValue = EncryptContextHelp.decrypt(value);
                }
            }
            jsonGenerator.writeString(newValue);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
