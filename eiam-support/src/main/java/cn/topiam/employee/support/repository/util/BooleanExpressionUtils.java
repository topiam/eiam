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
package cn.topiam.employee.support.repository.util;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;

import cn.topiam.employee.support.util.DateUtils;

/**
 * 当使用BooleanExpression进行查询配置时，使用的工具类
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/29 22:06
 */
public class BooleanExpressionUtils {

    /**
     * 整合判断表达式
     *
     * @param value 需要根据其值判断是否执行整合过程
     * @param exp 之前的表达式
     * @param subExp 最新的表达式
     * @return BooleanExpression 整合后的表达式
     */
    public static BooleanExpression addExpression(String value, BooleanExpression exp,
                                                  BooleanExpression subExp) {
        return StringUtils.isNotBlank(value) ? addSubExpression(exp, subExp) : exp;
    }

    /**
     * 整合判断表达式
     *
     * @param exp 之前的表达式
     * @param subExp 最新的表达式
     * @return BooleanExpression 整合后的表达式
     */
    public static BooleanExpression addSubExpression(BooleanExpression exp,
                                                     BooleanExpression subExp) {
        return exp == null ? subExp : exp.and(subExp);
    }

    /**
     * 拼接两侧like字符串
     * @param property {@link  String}
     * @return String 拼接好的like字符串
     */
    public static String like(String property) {
        return "%" + property + "%";
    }

    /**
     * 拼接左侧like字符串
     *
     * @param property {@link  String}
     * @return String 拼接好的like字符串
     */
    public static String leftLike(String property) {
        return "%" + property;
    }

    /**
     * 拼接右侧like字符串
     *
     * @param property  {@link  String}
     * @return String 拼接好的like字符串
     */
    public static String rightLike(String property) {
        return property + "%";
    }

    /**
     * 拼接左侧或右侧like字符串
     *
     * @param property  {@link  String}
     * @return String 拼接好的like字符串
     */
    public static String like(String property, boolean leftLike) {
        return leftLike ? "%" + property : property + "%";
    }

    /**
     * 当输入值不为空或""时，将其转换为长整形并返回，否则返回0
     *
     * @param value 需要被转型的字符串
     * @return Integer 转型后的整形
     */
    public static Integer toInteger(String value) throws NumberFormatException {
        return StringUtils.isNotBlank(value) ? Integer.parseInt(value) : 0;
    }

    /**
     * 当输入值不为空或""时，将其转换为长整形并返回，否则返回0L
     *
     * @param value 需要被转型的字符串
     * @return Long 转型后的长整形
     */
    public static Long toLong(String value) throws NumberFormatException {
        return StringUtils.isNotBlank(value) ? Long.parseLong(value) : 0L;
    }

    /**
     * 当输入值不为空或""时，将其转换为Boolean类型对象并返回，否则返回false
     *
     * @param value 需要被转型的字符串
     * @return Boolean 转型后的Boolean类型对象
     */
    public static Boolean toBoolean(String value) {
        return StringUtils.isNotBlank(value) ? Boolean.valueOf(value) : Boolean.FALSE;
    }

    /**
     * 当输入值不为空或""时，将其转换为LocalDateTime类型对象并返回，否则返回LocalDateTime.now()当前本地日期时间
     *
     * @param value 需要被转型的字符串
     * @return LocalDateTime 转型后的LocalDateTime类型对象
     */
    public static LocalDateTime toLocalDateTime(String value) {
        return StringUtils.isNotBlank(value) ? DateUtils.stringToLocalDateTime(value)
            : LocalDateTime.now();
    }
}
