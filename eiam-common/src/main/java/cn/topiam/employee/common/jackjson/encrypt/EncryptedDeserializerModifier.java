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

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/22 21:53
 */
public class EncryptedDeserializerModifier extends BeanDeserializerModifier {

    private final JsonEncryptType jsonEncryptType;

    public EncryptedDeserializerModifier() {
        this.jsonEncryptType = null;
    }

    public EncryptedDeserializerModifier(JsonEncryptType jsonEncryptType) {
        this.jsonEncryptType = jsonEncryptType;
    }

    @Override
    public BeanDeserializerBuilder updateBuilder(DeserializationConfig config,
                                                 BeanDescription beanDesc,
                                                 BeanDeserializerBuilder builder) {
        var properties = builder.getProperties();
        while (properties.hasNext()) {
            var property = properties.next();
            JsonPropertyEncrypt annotation = property.getAnnotation(JsonPropertyEncrypt.class);
            if (annotation != null) {
                JsonEncryptType deserializer = jsonEncryptType;
                if (jsonEncryptType == null) {
                    deserializer = annotation.deserializer();
                }
                builder.addOrReplaceProperty(
                    property.withValueDeserializer(new EncryptedJsonDeserializer(deserializer)),
                    true);
            }
        }
        return builder;
    }
}
