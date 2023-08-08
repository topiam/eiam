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

import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.geo.GeoLocationProviderConfig;
import cn.topiam.employee.common.geo.maxmind.MaxmindProviderConfig;
import cn.topiam.employee.common.jackjson.encrypt.EncryptionModule;
import cn.topiam.employee.console.pojo.result.setting.EmailProviderConfigResult;
import cn.topiam.employee.console.pojo.result.setting.GeoIpProviderResult;
import cn.topiam.employee.console.pojo.save.setting.GeoIpProviderSaveParam;
import cn.topiam.employee.console.pojo.save.setting.MailProviderSaveParam;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.validation.ValidationUtils;

import jakarta.validation.ValidationException;
import static cn.topiam.employee.common.geo.maxmind.MaxmindGeoLocationServiceImpl.MAXMIND;
import static cn.topiam.employee.common.geo.maxmind.MaxmindGeoLocationServiceImpl.SHA256_URL;
import static cn.topiam.employee.core.setting.constant.GeoIpProviderConstants.IPADDRESS_SETTING_NAME;

/**
 * 地理位置设置转换器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/10/1 23:18
 */
@Mapper(componentModel = "spring")
public interface GeoLocationSettingConverter {

    Logger log = LoggerFactory.getLogger(GeoLocationSettingConverter.class);

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
           //@formatter:off
           //根据提供商封装参数
           if (MAXMIND.getProvider().equals(param.getProvider())) {
               desc = MAXMIND.getName();
               MaxmindProviderConfig maxmindProviderConfig = param.getConfig().to(MaxmindProviderConfig.class);
               ValidationUtils.ValidationResult<?> validationResult = ValidationUtils.validateEntity(maxmindProviderConfig);
               entity.setValue(objectMapper.writeValueAsString(new GeoLocationProviderConfig(MAXMIND, maxmindProviderConfig)));
               // 验证
               if (Objects.requireNonNull(validationResult).isHasErrors()) {
                   throw new ValidationException(validationResult.getMessage());
               }
               try {
                   ResponseEntity<String> checkConnect = ApplicationContextHelp.getBean(RestTemplate.class).getForEntity(
                           String.format(SHA256_URL,
                                   maxmindProviderConfig.getSessionKey()), String.class);
                   HttpStatusCode statusCode = checkConnect.getStatusCode();
                   if (statusCode.isError()) {
                       log.error("MAXMIND调用失败:[{}]", checkConnect);
                       throw new TopIamException("注册码错误或连接异常");
                   }
               }
               catch (Exception e) {
                   log.error("MAXMIND调用异常: [{}]", e.getMessage());
                   throw new TopIamException("注册码错误或连接异常");
               }
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
           if (MAXMIND.equals(setting.getProvider())) {
               MaxmindProviderConfig config = (MaxmindProviderConfig) setting.getConfig();
               //@formatter:off
               return GeoIpProviderResult.builder()
                       .provider(setting.getProvider().getProvider())
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
