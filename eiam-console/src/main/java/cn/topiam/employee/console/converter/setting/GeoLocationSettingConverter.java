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
import cn.topiam.employee.common.geo.GeoLocationProviderConfig;
import cn.topiam.employee.common.geo.maxmind.MaxmindProviderConfig;
import cn.topiam.employee.common.geo.maxmind.enums.GeoLocationProvider;
import cn.topiam.employee.console.pojo.result.setting.EmailProviderConfigResult;
import cn.topiam.employee.console.pojo.result.setting.GeoIpProviderResult;
import cn.topiam.employee.console.pojo.save.setting.GeoIpProviderSaveParam;
import cn.topiam.employee.console.pojo.save.setting.MailProviderSaveParam;
import cn.topiam.employee.support.validation.ValidationHelp;
import static cn.topiam.employee.core.setting.constant.GeoIpProviderConstants.IPADDRESS_SETTING_NAME;

/**
 * 地理位置设置转换器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/10/1 23:18
 */
@Mapper(componentModel = "spring")
public interface GeoLocationSettingConverter {
    /**
     * 地理位置配置转实体类
     *
     * @param param {@link MailProviderSaveParam}
     * @return {@link SettingEntity}
     */
    default SettingEntity geoLocationProviderConfigToEntity(GeoIpProviderSaveParam param) {
        ObjectMapper objectMapper = EncryptionModule.serializerEncrypt();
        // 指定序列化输入的类型
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        try {
            SettingEntity entity = new SettingEntity();
            entity.setName(IPADDRESS_SETTING_NAME);
            String desc = null;
            ValidationHelp.ValidationResult<?> validationResult = null;
           //@formatter:off
           //根据提供商封装参数
           if (GeoLocationProvider.MAXMIND.equals(param.getProvider())) {
               desc = GeoLocationProvider.MAXMIND.getName();
               MaxmindProviderConfig maxmindProviderConfig = param.getConfig().to(MaxmindProviderConfig.class);
               validationResult = ValidationHelp.validateEntity(maxmindProviderConfig);
               entity.setValue(objectMapper.writeValueAsString(new GeoLocationProviderConfig(param.getProvider(), maxmindProviderConfig)));
           }
           // 验证
           if (Objects.requireNonNull(validationResult).isHasErrors()) {
               throw new ValidationException(validationResult.getMessage());
           }
           entity.setDesc(desc);
           //@formatter:no
           return entity;
       }catch (JsonProcessingException e){
           throw  new RuntimeException(e);
       }
    }

    /**
     * 实体转地理位置提供商配置
     *
     * @param entity {@link SettingEntity}
     * @return {@link EmailProviderConfigResult}
     */
    default GeoIpProviderResult entityToGeoLocationProviderConfig(SettingEntity entity) {
        //没有数据，默认未启用
        if (Objects.isNull(entity)) {
            return null;
        }
       try {
           String value = entity.getValue();
           ObjectMapper objectMapper = EncryptionModule.deserializerDecrypt();
           // 指定序列化输入的类型
           objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                   ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
           // 根据提供商序列化
           GeoLocationProviderConfig setting = objectMapper.readValue(value, GeoLocationProviderConfig.class);
           if (GeoLocationProvider.MAXMIND.equals(setting.getProvider())) {
               MaxmindProviderConfig config = (MaxmindProviderConfig) setting.getConfig();
               //@formatter:off
               return GeoIpProviderResult.builder()
                       .provider(setting.getProvider())
                       .config(config)
                       .enabled(true)
                       .build();
           }
           //@formatter:on
            return null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
