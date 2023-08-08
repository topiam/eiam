/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.jackjson.encrypt;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/22 21:53
 */
public class EncryptedSerializerModifier extends BeanSerializerModifier {

    private final JsonEncryptType jsonEncryptType;

    public EncryptedSerializerModifier() {
        this.jsonEncryptType = null;
    }

    public EncryptedSerializerModifier(JsonEncryptType jsonEncryptType) {
        this.jsonEncryptType = jsonEncryptType;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        /*
            遍历beanProperties处理Encrypt.class注解
         */
        List<BeanPropertyWriter> newWriter = new ArrayList<>();
        for (BeanPropertyWriter writer : beanProperties) {
            JsonPropertyEncrypt annotation = writer.getAnnotation(JsonPropertyEncrypt.class);
            if (null == annotation) {
                newWriter.add(writer);
            } else {
                JsonEncryptType deserializer = jsonEncryptType;
                if (jsonEncryptType == null) {
                    deserializer = annotation.deserializer();
                }
                JsonSerializer<Object> serializer = new EncryptedJsonSerializer(
                    writer.getSerializer(), deserializer);
                writer.assignSerializer(serializer);
                newWriter.add(writer);
            }
        }

        return newWriter;
    }
}
