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
package cn.topiam.employee.support.lock;

import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.stereotype.Component;

import cn.topiam.employee.support.constant.EiamConstants;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.util.Md5Utils;
import cn.topiam.employee.support.util.SpelUtils;
import static cn.topiam.employee.support.constant.EiamConstants.COLON;

/**
 * 通过AOP实现锁功能
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/9/16
 */
@Aspect
@Component
public class LockAspect {
    private final RedissonClient redis;

    public LockAspect(RedissonClient redis) {
        this.redis = redis;
    }

    /**
     * 返回结果之前
     */
    @Around(value = "@annotation(lock)", argNames = "pjp,lock")
    public Object around(ProceedingJoinPoint pjp, Lock lock) throws Throwable {
        RLock rLock = null;
        try {
            // 根据请求参数、方法进行加锁
            Object[] args = pjp.getArgs();
            String suffix;
            if (!StringUtils.isBlank(lock.spEL())) {
                MethodSignature signature = (MethodSignature) pjp.getSignature();
                suffix = SpelUtils.parser(pjp.getTarget(), lock.spEL(), signature.getMethod(),
                    args);
            } else {
                suffix = Md5Utils
                    .md532(String.join(String.valueOf(pjp.getTarget()), Arrays.toString(args)));
            }
            String namespaces = StringUtils.isNotBlank(lock.namespaces())
                ? lock.namespaces() + EiamConstants.COLON
                : "";
            String key = getTopiamLockKeyPrefix() + namespaces;
            rLock = redis.getLock(key + suffix);
            boolean tryLock = rLock.tryLock();
            if (tryLock) {
                Object proceed = pjp.proceed();
                //释放锁
                rLock.unlock();
                return proceed;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (!Objects.isNull(rLock) && rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
        throw new TopIamLockException();
    }

    /**
     * 获取Lock key 前缀
     *
     * @return {@link String}
     */
    public static String getTopiamLockKeyPrefix() {
        return ApplicationContextHelp.getBean(CacheProperties.class).getRedis().getKeyPrefix()
               + COLON + "lock" + COLON + "keys";
    }
}
