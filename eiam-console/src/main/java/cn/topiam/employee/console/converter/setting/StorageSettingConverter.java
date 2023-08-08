/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.converter.setting;

import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.jackjson.encrypt.EncryptionModule;
import cn.topiam.employee.common.storage.StorageConfig;
import cn.topiam.employee.common.storage.StorageProviderException;
import cn.topiam.employee.common.storage.enums.StorageProvider;
import cn.topiam.employee.common.storage.impl.AliYunOssStorage;
import cn.topiam.employee.common.storage.impl.MinIoStorage;
import cn.topiam.employee.common.storage.impl.QiNiuKodoStorage;
import cn.topiam.employee.common.storage.impl.TencentCosStorage;
import cn.topiam.employee.console.pojo.result.setting.StorageProviderConfigResult;
import cn.topiam.employee.console.pojo.save.setting.StorageConfigSaveParam;
import cn.topiam.employee.support.validation.ValidationUtils;

import jakarta.validation.ValidationException;
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
        StorageConfig.StorageConfigBuilder builder = StorageConfig.builder();
        builder.provider(provider);
        ObjectMapper objectMapper = EncryptionModule.deserializerEncrypt();
        try {
            //阿里云
            if (provider.equals(StorageProvider.ALIYUN_OSS)) {
                AliYunOssStorage.Config config = objectMapper
                    .readValue(param.getConfig().toJSONString(), AliYunOssStorage.Config.class);
                config.setDomain(getUrl(config.getDomain()));
                config.setEndpoint(getUrl(config.getEndpoint()));
                builder.config(config);
                validateEntity(ValidationUtils.validateEntity(config));

                AliYunOssStorage.Config unencryptedConfig = new AliYunOssStorage.Config();
                BeanUtils.copyProperties(config, unencryptedConfig);
                unencryptedConfig
                    .setAccessKeySecret(param.getConfig().getString("accessKeySecret"));
                checkStorage(AliYunOssStorage::new, unencryptedConfig);
            }
            //腾讯
            else if (provider.equals(StorageProvider.TENCENT_COS)) {
                TencentCosStorage.Config config = objectMapper
                    .readValue(param.getConfig().toJSONString(), TencentCosStorage.Config.class);
                config.setDomain(getUrl(config.getDomain()));
                builder.config(config);
                validateEntity(ValidationUtils.validateEntity(config));

                TencentCosStorage.Config unencryptedConfig = new TencentCosStorage.Config();
                BeanUtils.copyProperties(config, unencryptedConfig);
                unencryptedConfig.setSecretKey(param.getConfig().getString("secretKey"));
                checkStorage(TencentCosStorage::new, unencryptedConfig);
            }
            //七牛
            else if (provider.equals(StorageProvider.QINIU_KODO)) {
                QiNiuKodoStorage.Config config = objectMapper
                    .readValue(param.getConfig().toJSONString(), QiNiuKodoStorage.Config.class);
                config.setDomain(getUrl(config.getDomain()));
                builder.config(config);
                validateEntity(ValidationUtils.validateEntity(config));

                QiNiuKodoStorage.Config unencryptedConfig = new QiNiuKodoStorage.Config();
                BeanUtils.copyProperties(config, unencryptedConfig);
                unencryptedConfig.setSecretKey(param.getConfig().getString("secretKey"));
                checkStorage(QiNiuKodoStorage::new, unencryptedConfig);
            }
            //Minio
            else if (provider.equals(StorageProvider.MINIO)) {
                MinIoStorage.Config config = objectMapper
                    .readValue(param.getConfig().toJSONString(), MinIoStorage.Config.class);
                config.setEndpoint(getUrl(config.getEndpoint()));
                config.setDomain(getUrl(config.getDomain()));
                builder.config(config);
                validateEntity(ValidationUtils.validateEntity(config));

                MinIoStorage.Config unencryptedConfig = new MinIoStorage.Config();
                BeanUtils.copyProperties(config, unencryptedConfig);
                unencryptedConfig.setSecretKey(param.getConfig().getString("secretKey"));
                checkStorage(MinIoStorage::new, unencryptedConfig);
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

    private static void checkStorage(Consumer<StorageConfig> consumer,
                                     StorageConfig.Config config) {
        try {
            consumer.accept(StorageConfig.builder().config(config).build());
        } catch (Exception e) {
            throw new StorageProviderException("存储配置异常, 请检查配置信息");
        }
    }

    private static void validateEntity(ValidationUtils.ValidationResult<?> validationResult) {
        if (Objects.requireNonNull(validationResult).isHasErrors()) {
            throw new ValidationException(validationResult.getMessage());
        }
    }

    @NotNull
    private static String getUrl(String url) {
        return url.replaceAll("/+$", "");
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
