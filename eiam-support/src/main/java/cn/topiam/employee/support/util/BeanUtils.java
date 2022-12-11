/*
 * eiam-support - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.google.common.collect.Maps;

/**
 * BeanUtils
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/10/2
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {
    /**
     * 将对象转换为map
     *
     * @param bean {@link T}
     * @return {@link Map}
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key.toString(), beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 将map集合中的数据转化为指定对象的同名属性中
     * @param map {@link Map}
     * @param clazz {@link Class}
     * @return {@link T}
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz) {
        T bean;
        try {
            bean = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException
                | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

    /**
     * 将source的非空属性复制到target，如果需要将source的全部属性复制到target请参考
     * {@link org.springframework.beans.BeanUtils#copyProperties(Object, Object)}  }
     * <p>
     * 注意：来源和目标只需要属性名相同即可，不需要有继承或者派生关系
     * <p/>
     *
     * @param source 来源bean
     * @param target 目标bean
     */
    public static void merge(Object source, Object target) throws BeansException {
        merge(source, target, null, (String[]) null);
    }

    /**
     * 将source的非空属性复制到target，如果需要将source的全部属性复制到target请参考
     * {@link org.springframework.beans.BeanUtils#copyProperties(Object, Object, String...)}  }
     *
     * @param source           来源bean
     * @param target           目标bean
     * @param ignoreProperties 要忽略的属性名的数组
     * @throws BeansException 复制失败
     */
    public static void merge(Object source, Object target,
                             @Nullable String... ignoreProperties) throws BeansException {
        merge(source, target, null, ignoreProperties);
    }

    /**
     * 将source的非空属性复制到target，如果需要将source的全部属性复制到target请参考
     * {@link org.springframework.beans.BeanUtils#copyProperties(Object, Object, Class, String...)}  }
     * <p>
     * 注意：<br />
     * 1. 来源和目标只需要属性名相同即可，不需要有继承或者派生关系<br />
     * 2. 目标需要满足<code>editable<code/>指定的类或者接口<br />
     * <p/>
     *
     * @param source           来源bean
     * @param target           目标bean
     * @param editable         限制target必须为指定的类或者接口
     * @param ignoreProperties 要忽略的属性名的数组
     * @throws BeansException 复制失败
     */
    public static void merge(Object source, Object target, @Nullable Class<?> editable,
                             @Nullable String... ignoreProperties) throws BeansException {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();

        if (Objects.nonNull(editable)) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName()
                                                   + "] not assignable to Editable class ["
                                                   + editable.getName() + "]");
            }
            actualEditable = editable;
        }

        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (Objects.nonNull(ignoreProperties)
            ? Arrays.asList(ignoreProperties)
            : null);
        boolean check;
        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            check = Objects.nonNull(writeMethod)
                    && (Objects.isNull(ignoreList) || !ignoreList.contains(targetPd.getName()));
            if (check) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(),
                    targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (Objects.nonNull(readMethod) && ClassUtils.isAssignable(
                        writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (!Modifier
                                .isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            if (Objects.nonNull(value)) {
                                writeMethod.invoke(target, value);
                            }
                        } catch (Throwable ex) {
                            throw new FatalBeanException(
                                "Could not copy property '" + targetPd.getName()
                                                         + "' from source to target",
                                ex);
                        }
                    }
                }
            }
        }
    }
}
