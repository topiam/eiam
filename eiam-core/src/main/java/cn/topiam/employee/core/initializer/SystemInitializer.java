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

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AlternativeJdkIdGenerator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import cn.topiam.employee.common.entity.account.OrganizationEntity;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.geo.GeoLocationProviderConfig;
import cn.topiam.employee.common.jackjson.encrypt.EncryptionModule;
import cn.topiam.employee.common.repository.account.OrganizationRepository;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.support.init.Initializer;
import cn.topiam.employee.support.security.util.SecurityUtils;
import cn.topiam.employee.support.trace.TraceUtils;
import cn.topiam.employee.support.util.AesUtils;
import static cn.topiam.employee.common.constant.SettingConstants.AES_SECRET;
import static cn.topiam.employee.common.enums.account.OrganizationType.DEPARTMENT;
import static cn.topiam.employee.common.geo.ip2region.Ip2regionGeoLocationServiceImpl.IP2REGION;
import static cn.topiam.employee.core.setting.GeoIpProviderConstants.IPADDRESS_SETTING_NAME;
import static cn.topiam.employee.support.constant.EiamConstants.*;
import static cn.topiam.employee.support.lock.LockAspect.getTopiamLockKeyPrefix;
import static cn.topiam.employee.support.security.userdetails.DataOrigin.INPUT;

/**
 * SystemInitializer
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2024/04/04 21:24
 */
@Component
public class SystemInitializer implements Initializer {

    private final Logger logger = LoggerFactory.getLogger(SystemInitializer.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(ApplicationContext applicationContext) {
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
                //初始化组织机构
                initRootOrganization();
            }
        } catch (Exception e) {
            int exitCode = SpringApplication.exit(applicationContext, () -> 0);
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

    /**
     * 初始化组织机构
     */
    private void initRootOrganization() {
        //@formatter:off
        Optional<OrganizationEntity> optional = organizationRepository.findById(ROOT_NODE);
        if (optional.isEmpty()) {
            logger.info("初始化父级组织");
            OrganizationEntity organization = new OrganizationEntity();
            organization.setId(ROOT_NODE);
            organization.setName(ROOT_DEPT_NAME);
            organization.setCode(ROOT_NODE);
            organization.setPath(PATH_SEPARATOR+ROOT_NODE);
            organization.setDisplayPath(PATH_SEPARATOR+ROOT_DEPT_NAME);
            organization.setType(DEPARTMENT);
            organization.setDataOrigin(INPUT.getType());
            organization.setLeaf(false);
            organization.setEnabled(true);
            organization.setOrder(0L);
            organization.setCreateBy(SecurityUtils.getCurrentUserName());
            organization.setCreateTime(LocalDateTime.now());
            organization.setUpdateBy(SecurityUtils.getCurrentUserName());
            organization.setUpdateTime(LocalDateTime.now());
            organization.setRemark("Root organization");
            organizationRepository.batchSave(Lists.newArrayList(organization));
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
     * OrganizationRepository
     */
    private final OrganizationRepository    organizationRepository;

    /**
     *
     * @param redissonClient {@link RedissonClient}
     * @param settingRepository {@link SettingRepository}
     * @param organizationRepository {@link OrganizationRepository}
     */
    public SystemInitializer(RedissonClient redissonClient, SettingRepository settingRepository,
                             OrganizationRepository organizationRepository) {
        this.redissonClient = redissonClient;
        this.settingRepository = settingRepository;
        this.organizationRepository = organizationRepository;
    }
}
