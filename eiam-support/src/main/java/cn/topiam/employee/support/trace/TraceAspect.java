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
package cn.topiam.employee.support.trace;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.JdkIdGenerator;

import lombok.Data;

/**
 * 日志追踪加入MDC
 * @author TopIAM
 * Created by support@topiam.cn on  2019/4/23
 */
@Data
@Aspect
@Component
public class TraceAspect {

    public static final String   TRACE_ID       = "TRACE_ID";
    private final JdkIdGenerator jdkIdGenerator = new JdkIdGenerator();

    /**
     * 返回结果之前
     */
    @Before(value = "@annotation(trace)")
    public void logBefore(final JoinPoint joinPoint, Trace trace) {
        //添加MDC
        TraceUtils.put(jdkIdGenerator.generateId().toString());
    }

    /**
     * 之后操作
     *
     * @param trace Trace
     */
    @After(value = "@annotation(trace)")
    public void logDoAfterReturning(Trace trace) {
        //清除MDC
        TraceUtils.remove();
    }

}
