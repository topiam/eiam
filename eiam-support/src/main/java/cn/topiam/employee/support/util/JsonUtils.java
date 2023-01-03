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
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/6 22:01
 */
public class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String writeValueAsString(Object object) throws JsonUtilException {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            throw new JsonUtilException(e);
        }
    }

    public static byte[] writeValueAsBytes(Object object) throws JsonUtilException {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(object);
        } catch (IOException e) {
            throw new JsonUtilException(e);
        }
    }

    public static <T> T readValue(String s, Class<T> clazz) throws JsonUtilException {
        try {
            if (hasText(s)) {
                return OBJECT_MAPPER.readValue(s, clazz);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new JsonUtilException(e);
        }
    }

    public static <T> T readValue(byte[] data, Class<T> clazz) throws JsonUtilException {
        try {
            if (data != null && data.length > 0) {
                return OBJECT_MAPPER.readValue(data, clazz);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new JsonUtilException(e);
        }
    }

    public static <T> T readValue(String s, TypeReference<T> typeReference) {
        try {
            if (hasText(s)) {
                return OBJECT_MAPPER.readValue(s, typeReference);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new JsonUtilException(e);
        }
    }

    public static <T> T readValue(byte[] data, TypeReference<T> typeReference) {
        try {
            if (data != null && data.length > 0) {
                return OBJECT_MAPPER.readValue(data, typeReference);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new JsonUtilException(e);
        }
    }

    public static <T> T convertValue(Object object, Class<T> toClazz) throws JsonUtilException {
        try {
            if (object == null) {
                return null;
            } else {
                return OBJECT_MAPPER.convertValue(object, toClazz);
            }
        } catch (IllegalArgumentException e) {
            throw new JsonUtilException(e);
        }
    }

    public static JsonNode readTree(JsonParser p) {
        try {
            return OBJECT_MAPPER.readTree(p);
        } catch (IOException e) {
            throw new JsonUtilException(e);
        }
    }

    public static JsonNode readTree(String s) {
        try {
            if (hasText(s)) {
                return OBJECT_MAPPER.readTree(s);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new JsonUtilException(e);
        }
    }

    public static class JsonUtilException extends RuntimeException {

        private static final long serialVersionUID = -4804245225960963421L;

        public JsonUtilException(Throwable cause) {
            super(cause);
        }

    }

    public static String serializeExcludingProperties(Object object,
                                                      String... propertiesToExclude) {
        String serialized = JsonUtils.writeValueAsString(object);
        Map<String, Object> properties = JsonUtils.readValue(serialized,
            new TypeReference<Map<String, Object>>() {
            });
        for (String property : propertiesToExclude) {
            if (property.contains(".")) {
                String[] split = property.split("\\.", 2);
                if (properties.containsKey(split[0])) {
                    Object inner = properties.get(split[0]);
                    properties.put(split[0],
                        JsonUtils.readValue(serializeExcludingProperties(inner, split[1]),
                            new TypeReference<Map<String, Object>>() {
                            }));
                }
            } else {
                properties.remove(property);
            }
        }
        return JsonUtils.writeValueAsString(properties);
    }

    public static String getNodeAsString(JsonNode node, String fieldName, String defaultValue) {
        JsonNode typeNode = node.get(fieldName);
        return typeNode == null ? defaultValue : typeNode.asText(defaultValue);
    }

    public static int getNodeAsInt(JsonNode node, String fieldName, int defaultValue) {
        JsonNode typeNode = node.get(fieldName);
        return typeNode == null ? defaultValue : typeNode.asInt(defaultValue);
    }

    public static boolean getNodeAsBoolean(JsonNode node, String fieldName, boolean defaultValue) {
        JsonNode typeNode = node.get(fieldName);
        return typeNode == null ? defaultValue : typeNode.asBoolean(defaultValue);
    }

    public static Date getNodeAsDate(JsonNode node, String fieldName) {
        JsonNode typeNode = node.get(fieldName);
        long date = typeNode == null ? -1 : typeNode.asLong(-1);
        if (date == -1) {
            return null;
        } else {
            return new Date(date);
        }
    }

    public static Map<String, Object> getNodeAsMap(JsonNode node) {
        return OBJECT_MAPPER.convertValue(node, Map.class);
    }

    public static boolean hasLength(CharSequence str) {
        return !(str == null || str.length() == 0);
    }

    public static boolean hasText(CharSequence str) {
        if (!hasLength(str)) {
            return false;
        }

        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
