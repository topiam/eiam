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

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/22 21:53
 */
public class EncryptedDeserializerModifier extends BeanDeserializerModifier {

    private final Type type;

    public EncryptedDeserializerModifier() {
        this.type = null;
    }

    public EncryptedDeserializerModifier(Type type) {
        this.type = type;
    }

    @Override
    public BeanDeserializerBuilder updateBuilder(DeserializationConfig config,
                                                 BeanDescription beanDesc,
                                                 BeanDeserializerBuilder builder) {
        var properties = builder.getProperties();
        while (properties.hasNext()) {
            var property = properties.next();
            Encrypt annotation = property.getAnnotation(Encrypt.class);
            if (annotation != null) {
                Type deserializer = type;
                if (type == null) {
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
