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
package cn.topiam.employee.support.preview;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;

/**
 * 演示环境
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2019/11/23
 */
@Aspect
public class DemoEnvAspect implements Ordered {

    private final boolean isOpen;

    public DemoEnvAspect(boolean demo) {
        this.isOpen = demo;
    }

    /**
     * 配置切面
     */
    @Pointcut(value = "@annotation(environment)")
    public void log(Preview environment) {
    }

    /**
     * 请求Controller 日志处理
     *
     * @param joinPoint {@link JoinPoint}
     */
    @Before(value = "log(environment)", argNames = "joinPoint,environment")
    public void demoBefore(JoinPoint joinPoint, Preview environment) {
        //1.如果系统没开启演示环境，忽略注解
        //2.如果系统整体开启了，根据注解值进行判断是否拦截并提示，演示环境不允许操作
        if (isOpen) {
            throw new DemoEnvDoesNotAllowOperationException();
        }
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 100;
    }
}
