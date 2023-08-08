/*
 * eiam-core - Employee Identity and Access Management
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
package cn.topiam.employee.core.listener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AlternativeJdkIdGenerator;

import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.support.trace.TraceUtils;
import cn.topiam.employee.support.util.AesUtils;
import static cn.topiam.employee.common.constant.SettingConstants.AES_SECRET;
import static cn.topiam.employee.support.constant.EiamConstants.COLON;
import static cn.topiam.employee.support.lock.LockAspect.getTopiamLockKeyPrefix;

/**
 * ConsoleAesInitializeListener
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/22 21:44
 */
@Component
public class EiamAesSecretInitializeListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onApplicationEvent(@NonNull ContextRefreshedEvent applicationPreparedEvent) {
        //@formatter:off
        String traceId = idGenerator.generateId().toString();
        TraceUtils.put(traceId);
        RLock lock = redissonClient.getLock(getTopiamLockKeyPrefix() + COLON + "aes");
        boolean tryLock = false;
        try {
             tryLock = lock.tryLock(1, TimeUnit.SECONDS);
             if (tryLock){
                 SettingEntity optional = settingRepository.findByName(AES_SECRET);
                 if (Objects.isNull(optional)) {
                     // 保存AES秘钥
                     saveInitAesSecret(AesUtils.generateKey());
                 }
             }

        } catch (Exception exception) {
            int exitCode = SpringApplication.exit(applicationPreparedEvent.getApplicationContext(),
                    () -> 0);
            System.exit(exitCode);
        } finally {
            if (tryLock && lock.isLocked()) {
                lock.unlock();
            }
            TraceUtils.remove();
        }
        //@formatter:on
    }

    /**
     * 保存管理员
     *
     * @param secret {@link String}
     */
    private void saveInitAesSecret(String secret) {
        SettingEntity setting = new SettingEntity();
        setting.setName(AES_SECRET);
        setting.setValue(secret);
        setting.setDesc("Project aes secret");
        setting.setRemark("This aes secret is automatically created during system initialization.");
        settingRepository.save(setting);
    }

    private final AlternativeJdkIdGenerator idGenerator = new AlternativeJdkIdGenerator();

    private final SettingRepository         settingRepository;

    private final RedissonClient            redissonClient;

    public EiamAesSecretInitializeListener(SettingRepository settingRepository,
                                           RedissonClient redissonClient) {
        this.settingRepository = settingRepository;
        this.redissonClient = redissonClient;
    }

}
