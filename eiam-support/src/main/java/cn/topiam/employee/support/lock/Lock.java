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
package cn.topiam.employee.support.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁注解
 * 默认等待30秒
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/16 22:55
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {
    /**
     * 命名空间
     * @return {@link String}
     */
    String namespaces() default "";

    /**
     * 等待时间
     * @return {@link Long}
     */
    long time() default 1800;

    /**
     * 时间单位
     * @return {@link TimeUnit}
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    String spEL() default "";
}
