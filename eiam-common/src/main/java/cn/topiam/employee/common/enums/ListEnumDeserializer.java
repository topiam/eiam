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
package cn.topiam.employee.common.enums;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

import cn.topiam.employee.support.enums.BaseEnum;

/**
 * ListEnumDeserializer
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/11/27 22:00
 */
public class ListEnumDeserializer extends JsonDeserializer<List<BaseEnum>>
                                  implements ContextualDeserializer {
    /**
     * 记录枚举字段的类，用于获取其定义的所有枚举值
     */
    private Class<? extends BaseEnum> propertyClass;

    public ListEnumDeserializer() {
    }

    public ListEnumDeserializer(Class<? extends BaseEnum> propertyClass) {
        this.propertyClass = propertyClass;
    }

    @Override
    public List<BaseEnum> deserialize(JsonParser jp,
                                      DeserializationContext cxt) throws IOException {
        ArrayNode treeNode = jp.readValueAsTree();
        Field field;
        try {
            field = jp.getCurrentValue().getClass().getDeclaredField(jp.currentName());
        } catch (NoSuchFieldException e) {
            return null;
        }
        field.setAccessible(true);
        if (!field.getType().equals(List.class)) {
            return null;
        }
        List<BaseEnum> result = new ArrayList<>();
        Iterator<JsonNode> elements = treeNode.elements();
        while (elements.hasNext()) {
            String value = elements.next().asText();
            try {
                //设置值
                BaseEnum anEnum = Arrays.stream(propertyClass.getEnumConstants())
                    .filter(e -> Objects.equals(e.getCode(), value)).findAny()
                    .orElseThrow(() -> new IllegalArgumentException(
                        "No such code of " + propertyClass.getSimpleName()));
                result.add(anEnum);
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (result.isEmpty()) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
                                                BeanProperty property) {
        // 获取枚举字段的类型Class
        return new ListEnumDeserializer(
            (Class<? extends BaseEnum>) property.getType().getContentType().getRawClass());
    }
}
