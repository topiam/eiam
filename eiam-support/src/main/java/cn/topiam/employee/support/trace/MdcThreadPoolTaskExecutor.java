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

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * MdcThreadPoolTaskExecutor
 * @author TopIAM
 * Created by support@topiam.cn on  2020/8/6
 */
public class MdcThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    private static final long         serialVersionUID = 1735074699085892566L;
    final private boolean             useFixedContext;
    final private Map<String, String> fixedContext;

    /**
     * Pool where task threads take MDC from the submitting thread.
     */
    public static MdcThreadPoolTaskExecutor newWithInheritedMdc(int corePoolSize,
                                                                int maximumPoolSize,
                                                                long keepAliveTime, TimeUnit unit,
                                                                int queueCapacity) {
        return new MdcThreadPoolTaskExecutor(null, corePoolSize, maximumPoolSize, keepAliveTime,
            unit, queueCapacity);
    }

    private MdcThreadPoolTaskExecutor(Map<String, String> fixedContext, int corePoolSize,
                                      int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                      int queueCapacity) {
        setCorePoolSize(corePoolSize);
        setMaxPoolSize(maximumPoolSize);
        setKeepAliveSeconds((int) unit.toSeconds(keepAliveTime));
        setQueueCapacity(queueCapacity);
        this.fixedContext = fixedContext;
        useFixedContext = (fixedContext != null);
    }

    private Map<String, String> getContextForTask() {
        return useFixedContext ? fixedContext : MDC.getCopyOfContextMap();
    }

    /**
     * All executions will have MDC injected. {@code ThreadPoolExecutor}'s submission methods ({@code submit()} etc.)
     * all delegate to this.
     */
    @Override
    public void execute(@NonNull Runnable command) {
        super.execute(wrap(command, getContextForTask()));
    }

    @NonNull
    @Override
    public Future<?> submit(@NonNull Runnable task) {
        return super.submit(wrap(task, getContextForTask()));
    }

    @NonNull
    @Override
    public <T> Future<T> submit(@NonNull Callable<T> task) {
        return super.submit(wrap(task, getContextForTask()));
    }

    private static <T> Callable<T> wrap(final Callable<T> task, final Map<String, String> context) {
        return () -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            try {
                return task.call();
            } finally {
                if (previous == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(previous);
                }
            }
        };
    }

    /**
     * wrap
     * @param runnable {@link Runnable}
     * @param context {@link Map}
     * @return  {@link Runnable}
     */
    private static Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return () -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            try {
                runnable.run();
            } finally {
                if (previous == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(previous);
                }
            }
        };
    }
}
