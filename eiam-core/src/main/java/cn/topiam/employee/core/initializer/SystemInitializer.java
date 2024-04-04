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
package cn.topiam.employee.core.initializer;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AlternativeJdkIdGenerator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.geo.GeoLocationProviderConfig;
import cn.topiam.employee.common.jackjson.encrypt.EncryptionModule;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.support.trace.TraceUtils;
import cn.topiam.employee.support.util.AesUtils;
import static cn.topiam.employee.common.constant.SettingConstants.AES_SECRET;
import static cn.topiam.employee.common.geo.ip2region.Ip2regionGeoLocationServiceImpl.IP2REGION;
import static cn.topiam.employee.core.setting.constant.GeoIpProviderConstants.IPADDRESS_SETTING_NAME;
import static cn.topiam.employee.support.constant.EiamConstants.COLON;
import static cn.topiam.employee.support.constant.EiamConstants.TOPIAM_INIT_AUTHENTICATION;
import static cn.topiam.employee.support.lock.LockAspect.getTopiamLockKeyPrefix;

/**
 * SystemInitializer
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2024/04/04 21:24
 */
@Component
public class SystemInitializer implements ApplicationListener<ApplicationReadyEvent>, Ordered {

    private final Logger logger = LoggerFactory.getLogger(SystemInitializer.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        String traceId = idGenerator.generateId().toString();
        TraceUtils.put(traceId);
        RLock lock = redissonClient.getLock(getTopiamLockKeyPrefix() + COLON + "system_init");
        boolean tryLock = false;
        try {
            SecurityContextHolder.getContext().setAuthentication(TOPIAM_INIT_AUTHENTICATION);
            tryLock = lock.tryLock(1, TimeUnit.SECONDS);
            if (tryLock) {
                //init 加密秘钥
                initEncryptSecret();
                //init IP 提供商
                initIpProvider();
            }
        } catch (Exception e) {
            int exitCode = SpringApplication.exit(event.getApplicationContext(), () -> 0);
            System.exit(exitCode);
        } finally {
            if (tryLock && lock.isLocked()) {
                lock.unlock();
            }
            TraceUtils.remove();
            SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        }
    }

    /**
     * 初始化加密秘钥
     */
    private void initEncryptSecret() {
        SettingEntity optional = settingRepository.findByName(AES_SECRET);
        if (Objects.isNull(optional)) {
            SettingEntity setting = new SettingEntity();
            setting.setName(AES_SECRET);
            setting.setValue(AesUtils.generateKey());
            setting.setDesc("Project aes secret");
            setting.setRemark(
                "This aes secret is automatically created during system initialization.");
            settingRepository.save(setting);
        }
    }

    /**
     * 初始化IP地址提供商
     *
     * @throws JsonProcessingException JsonProcessingException
     */
    private void initIpProvider() throws JsonProcessingException {
        //@formatter:off
        SettingEntity optional = settingRepository.findByName(IPADDRESS_SETTING_NAME);
        if (Objects.isNull(optional)) {
            logger.info("初始化系统默认IP地址提供商");
            SettingEntity setting = new SettingEntity();
            setting.setName(IPADDRESS_SETTING_NAME);
            ObjectMapper objectMapper = EncryptionModule.deserializerDecrypt();
            // 指定序列化输入的类型
            objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
            setting.setValue(objectMapper.writeValueAsString(new GeoLocationProviderConfig(IP2REGION, null)));
            setting.setDesc(IP2REGION.getName());
            setting.setRemark("The system initializes the default configuration.");
            settingRepository.save(setting);
        }
        //@formatter:on
    }

    private final AlternativeJdkIdGenerator idGenerator = new AlternativeJdkIdGenerator();

    /**
     * RedissonClient
     */
    private final RedissonClient            redissonClient;

    /**
     * SettingRepository
     */
    private final SettingRepository         settingRepository;

    /**
     *
     * @param redissonClient {@link RedissonClient}
     * @param settingRepository {@link SettingRepository}
     */
    public SystemInitializer(RedissonClient redissonClient, SettingRepository settingRepository) {
        this.redissonClient = redissonClient;
        this.settingRepository = settingRepository;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 1;
    }
}
