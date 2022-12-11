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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * 反射的 Utils 函数集合 提供访问私有变量, 获取泛型类型 Class, 提取集合中元素属性等 Utils 函数
 *
 * @author TopIAM
 */
@Slf4j
public class ReflectionUtils {

    /**
     * 通过反射, 获得定义 Class 时声明的父类的泛型参数的类型 如: public EmployeeDao extends
     * BaseDao<Employee, String>
     *
     * @param clazz clazz
     * @param index index
     * @return Class<?>
     */
    public static Class<?> getSuperClassGenricType(Class<?> clazz, int index) {
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            log.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            log.warn("Index: " + index + ", Size of " + clazz.getSimpleName()
                     + "'s Parameterized Type: " + params.length);
            return Object.class;
        }

        if (!(params[index] instanceof Class)) {
            log.warn(clazz.getSimpleName()
                     + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        return (Class<?>) params[index];
    }

    /**
     * 通过反射, 获得 Class 定义中声明的父类的泛型参数类型 如:
     *
     * @param clazz clazz
     * @return Object
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getSuperGenericType(Class<?> clazz) {
        return (Class<T>) getSuperClassGenricType(clazz, 0);
    }

    /**
     * 获取实体类内 & 父类内的所有字段,如果父类存在和子类相同的属性，排除父类的，使用子类的
     *
     * @param clazz clazz
     * @return List
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> result = new LinkedList<>(Arrays.asList(fields));
        Class<?> superClass = clazz.getSuperclass();
        if (superClass.equals(Object.class)) {
            return result;
        }
        // 获取父类全部字段
        List<Field> superAllFields = getAllFields(superClass);
        // 过滤排除
        for (Field i : result) {
            superAllFields = superAllFields.stream().filter(j -> !i.getName().equals(j.getName()))
                .collect(Collectors.toList());
        }
        result.addAll(superAllFields);
        return result;
    }
}
