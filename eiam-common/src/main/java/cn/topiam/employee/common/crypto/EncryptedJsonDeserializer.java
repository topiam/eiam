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

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/22 21:53
 */
public class EncryptedJsonDeserializer extends JsonDeserializer<Object> {

    private final Type deserializerType;

    public EncryptedJsonDeserializer(Type deserializer) {
        this.deserializerType = deserializer;
    }

    @Override
    public Object deserialize(final JsonParser parser,
                              final DeserializationContext context) throws IOException {
        String value = parser.getValueAsString();
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if (Type.ENCRYPT == deserializerType) {
            return EncryptContextHelp.encrypt(value);
        } else if (Type.DECRYPT == deserializerType) {
            return EncryptContextHelp.decrypt(value);
        }
        return value;
    }
}
