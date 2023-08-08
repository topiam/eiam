/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.annotation;

import java.lang.annotation.*;

import cn.topiam.employee.audit.event.type.EventType;

/**
 * Audit
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/28 21:56
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Audit {

    /**
     * 类型
     *
     * @return {@link Class}
     */
    EventType type();

    /**
     * 审计内容 支持SPEL表达式
     *
     * @return {@link String}
     */
    String content() default "";
}
