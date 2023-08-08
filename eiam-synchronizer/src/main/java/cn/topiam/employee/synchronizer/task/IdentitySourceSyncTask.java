/*
 * eiam-synchronizer - Employee Identity and Access Management
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
package cn.topiam.employee.synchronizer.task;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.StopWatch;

import cn.topiam.employee.common.enums.TriggerType;
import cn.topiam.employee.identitysource.core.IdentitySource;
import cn.topiam.employee.identitysource.core.IdentitySourceConfig;
import cn.topiam.employee.support.trace.TraceUtils;

import lombok.RequiredArgsConstructor;
import static cn.topiam.employee.support.lock.LockAspect.getTopiamLockKeyPrefix;

/**
 * 身份源同步任务
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/20 21:41
 */
@RequiredArgsConstructor
public class IdentitySourceSyncTask implements Runnable {
    private final AlternativeJdkIdGenerator idGenerator = new AlternativeJdkIdGenerator();
    private static final Logger             logger      = LoggerFactory
        .getLogger(IdentitySourceSyncTask.class);

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        logger.info("[任务触发]-同步身份源[{}]数据开始", name);
        String traceId = idGenerator.generateId().toString();
        TraceUtils.put(traceId);
        RLock lock = redissonClient.getLock(getTopiamLockKeyPrefix() + id);
        boolean tryLock = false;
        try {
            tryLock = lock.tryLock(1, TimeUnit.SECONDS);
            if (tryLock) {
                identitySource.sync(TriggerType.JOB);
            }
        } catch (InterruptedException ignored) {

        } finally {
            stopWatch.stop();
            if (tryLock && lock.isLocked()) {
                lock.unlock();
            }
            TraceUtils.remove();
            logger.info("[任务触发]-同步身份源[{}]数据结束, 执行时长: {} ms", name, stopWatch.getTotalTimeMillis());
        }
    }

    /**
     * ID
     */
    private final String                                         id;

    /**
     * 名称
     */
    private final String                                         name;

    /**
     * RedissonClient
     */
    private final RedissonClient                                 redissonClient;

    /**
     * 身份源
     */
    private final IdentitySource<? extends IdentitySourceConfig> identitySource;

}
