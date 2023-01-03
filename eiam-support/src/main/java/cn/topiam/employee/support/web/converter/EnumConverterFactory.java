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
package cn.topiam.employee.support.web.converter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import lombok.AllArgsConstructor;

/**
 * 枚举类的转换器
 * 如果枚举类中有工厂方法(静态方法)被标记为{@link EnumConvert },则调用该方法转为枚举对象
 * @author TopIAM
 * Created by support@topiam.cn on  2020/8/20
 */
public class EnumConverterFactory implements ConverterFactory<String, Enum<?>> {

    private static final ConcurrentMap<Class<? extends Enum<?>>, EnumMvcConverterHolder> HOLDER_MAPPER = new ConcurrentHashMap<>(
        16);

    /**
     * Get the converter to convert from S to target type T, where T is also an instance of R.
     * @param targetType the target type to convert to
     * @return a converter from S to T
     */
    @SuppressWarnings("unchecked")
    @Override
    @NonNull
    public <T extends Enum<?>> Converter<String, T> getConverter(@NonNull Class<T> targetType) {
        EnumMvcConverterHolder holder = HOLDER_MAPPER.computeIfAbsent(targetType,
            EnumMvcConverterHolder::createHolder);
        assert holder.converter != null;
        return (Converter<String, T>) holder.converter;
    }

    @AllArgsConstructor
    static class EnumMvcConverterHolder {
        @Nullable
        final EnumMvcConverter<?> converter;

        static EnumMvcConverterHolder createHolder(Class<?> targetType) {
            List<Method> methodList = MethodUtils.getMethodsListWithAnnotation(targetType,
                EnumConvert.class, false, true);
            if (CollectionUtils.isEmpty(methodList)) {
                return new EnumMvcConverterHolder(null);
            }
            Assert.isTrue(methodList.size() == 1, "@EnumConvertMethod 只能标记在一个工厂方法（静态方法）上");
            Method method = methodList.get(0);
            Assert.isTrue(Modifier.isStatic(method.getModifiers()),
                "@EnumConvertMethod 只能标记在工厂方法（静态方法）上");
            return new EnumMvcConverterHolder(new EnumMvcConverter<>(method));
        }

    }

    static class EnumMvcConverter<T extends Enum<T>> implements Converter<String, T> {

        private final Method method;

        public EnumMvcConverter(Method method) {
            this.method = method;
            this.method.setAccessible(true);
        }

        @Override
        public T convert(String source) {
            if (source.isEmpty()) {
                // reset the enum value to null.
                return null;
            }
            try {
                //noinspection unchecked
                return (T) method.invoke(null, source);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

    }
}
