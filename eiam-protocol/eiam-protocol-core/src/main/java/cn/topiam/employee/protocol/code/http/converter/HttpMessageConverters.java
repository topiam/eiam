/*
 * eiam-protocol-core - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.code.http.converter;

import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ClassUtils;

/**
 * Utility methods for {@link HttpMessageConverter}'s.
 *
 * @author Joe Grandja
 * @author luamas
 * @since 5.1
 */
public final class HttpMessageConverters {

    private static final boolean JACKSON2_PRESENT;

    private static final boolean GSON_PRESENT;

    private static final boolean JSONB_PRESENT;

    static {
        ClassLoader classLoader = HttpMessageConverters.class.getClassLoader();
        JACKSON2_PRESENT = ClassUtils
            .isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader)
                           && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator",
                               classLoader);
        GSON_PRESENT = ClassUtils.isPresent("com.google.gson.Gson", classLoader);
        JSONB_PRESENT = ClassUtils.isPresent("jakarta.json.bind.Jsonb", classLoader);
    }

    private HttpMessageConverters() {
    }

    public static GenericHttpMessageConverter<Object> getJsonMessageConverter() {
        if (JACKSON2_PRESENT) {
            return new MappingJackson2HttpMessageConverter();
        }
        if (GSON_PRESENT) {
            return new GsonHttpMessageConverter();
        }
        if (JSONB_PRESENT) {
            return new JsonbHttpMessageConverter();
        }
        return null;
    }

}
