/*
 * eiam-console - Employee Identity and Access Management Program
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
package cn.topiam.employee.console.converter.setting;

import java.util.Objects;

import javax.validation.ValidationException;

import org.mapstruct.Mapper;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.common.crypto.EncryptionModule;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.storage.StorageConfig;
import cn.topiam.employee.common.storage.enums.StorageProvider;
import cn.topiam.employee.common.storage.impl.AliYunOssStorage;
import cn.topiam.employee.common.storage.impl.LocalStorage;
import cn.topiam.employee.common.storage.impl.MinIoStorage;
import cn.topiam.employee.common.storage.impl.QiNiuKodoStorage;
import cn.topiam.employee.console.pojo.result.setting.StorageProviderConfigResult;
import cn.topiam.employee.console.pojo.save.setting.StorageConfigSaveParam;
import cn.topiam.employee.support.validation.ValidationHelp;
import static cn.topiam.employee.core.setting.constant.StorageProviderSettingConstants.STORAGE_PROVIDER_KEY;

/**
 * 消息设置转换器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/10/1 23:18
 */
@Mapper(componentModel = "spring")
public interface StorageSettingConverter {
    /**
     * 存储提供商配置转实体类
     *
     * @param param {@link StorageConfigSaveParam}
     * @return {@link SettingEntity}
     */
    default SettingEntity storageConfigSaveParamToEntity(StorageConfigSaveParam param) {
        SettingEntity entity = new SettingEntity();
        StorageProvider provider = param.getProvider();
        ValidationHelp.ValidationResult<?> validationResult = null;
        StorageConfig.StorageConfigBuilder builder = StorageConfig.builder();
        builder.provider(provider);
        ObjectMapper objectMapper = EncryptionModule.deserializerEncrypt();
        try {
            //阿里云
            if (provider.equals(StorageProvider.ALIYUN_OSS)) {
                AliYunOssStorage.Config config = objectMapper
                    .readValue(param.getConfig().toJSONString(), AliYunOssStorage.Config.class);
                builder.config(config);
                validationResult = ValidationHelp.validateEntity(config);
            }
            //腾讯
            else if (provider.equals(StorageProvider.TENCENT_COS)) {
                AliYunOssStorage.Config config = objectMapper
                    .readValue(param.getConfig().toJSONString(), AliYunOssStorage.Config.class);
                builder.config(config);
                validationResult = ValidationHelp.validateEntity(config);
            }
            //七牛
            else if (provider.equals(StorageProvider.QINIU_KODO)) {
                QiNiuKodoStorage.Config config = objectMapper
                    .readValue(param.getConfig().toJSONString(), QiNiuKodoStorage.Config.class);
                builder.config(config);
                validationResult = ValidationHelp.validateEntity(config);
            }
            //MiNio
            else if (provider.equals(StorageProvider.MINIO)) {
                MinIoStorage.Config config = objectMapper
                    .readValue(param.getConfig().toJSONString(), MinIoStorage.Config.class);
                builder.config(config);
                validationResult = ValidationHelp.validateEntity(config);
            }
            //本机
            else if (provider.equals(StorageProvider.LOCAL)) {
                LocalStorage.Config config = objectMapper
                    .readValue(param.getConfig().toJSONString(), LocalStorage.Config.class);
                builder.config(config);
                validationResult = ValidationHelp.validateEntity(config);
            }
            if (Objects.requireNonNull(validationResult).isHasErrors()) {
                throw new ValidationException(validationResult.getMessage());
            }
            entity.setName(STORAGE_PROVIDER_KEY);
            // 指定序列化输入的类型
            objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
            entity.setValue(objectMapper.writeValueAsString(builder.build()));
            entity.setDesc(provider.getDesc());
            return entity;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 实体转存储提供商配置
     *
     * @param entity {@link SettingEntity}
     * @return {@link StorageProviderConfigResult}
     */
    default StorageProviderConfigResult entityToStorageProviderConfig(SettingEntity entity) {
        if (Objects.isNull(entity)) {
            return StorageProviderConfigResult.builder().enabled(false).build();
        }
        ObjectMapper objectMapper = EncryptionModule.deserializerDecrypt();
        // 指定序列化输入的类型
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        try {
            String value = entity.getValue();
            StorageConfig storageConfig = objectMapper.readValue(value, StorageConfig.class);
            // 开启配置、并没有配置
            //@formatter:off
            return StorageProviderConfigResult.builder()
                    .provider(storageConfig.getProvider())
                    .enabled(true)
                    .config(storageConfig.getConfig()).build();
            //@formatter:on
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
