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
package cn.topiam.employee.core.configuration;

import java.util.Objects;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.common.constant.SettingConstants;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.jackjson.encrypt.EncryptionModule;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.common.storage.Storage;
import cn.topiam.employee.common.storage.StorageConfig;
import cn.topiam.employee.common.storage.StorageFactory;
import cn.topiam.employee.common.storage.impl.NoneStorage;
import cn.topiam.employee.core.setting.constant.StorageProviderSettingConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 存储配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/10 21:01
 */
@Slf4j
@Configuration
public class EiamStorageConfiguration {

    /**
     * 存储配置
     *
     * @return {@link Storage}
     */
    @Bean(name = StorageProviderSettingConstants.STORAGE_BEAN_NAME)
    @RefreshScope
    public Storage storage() {
        SettingEntity setting = repository
            .findByName(StorageProviderSettingConstants.STORAGE_PROVIDER_KEY);
        ObjectMapper objectMapper = EncryptionModule.deserializerDecrypt();
        // 指定序列化输入的类型
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        try {
            if (!Objects.isNull(setting)
                && !setting.getValue().equals(SettingConstants.NOT_CONFIG)) {
                return StorageFactory
                    .getStorage(objectMapper.readValue(setting.getValue(), StorageConfig.class));
            }
        } catch (Exception e) {
            log.error("Create storage Exception: {}", e.getMessage(), e);
        }
        return new NoneStorage();
    }

    private final SettingRepository repository;

    public EiamStorageConfiguration(SettingRepository settingRepository) {
        this.repository = settingRepository;
    }

}
