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

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.common.crypto.EncryptContextHelp;
import cn.topiam.employee.common.crypto.EncryptionModule;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.entity.setting.config.SmsConfig;
import cn.topiam.employee.common.enums.MessageNoticeChannel;
import cn.topiam.employee.common.message.enums.MailProvider;
import cn.topiam.employee.common.message.enums.MailSafetyType;
import cn.topiam.employee.common.message.enums.SmsProvider;
import cn.topiam.employee.common.message.mail.MailProviderConfig;
import cn.topiam.employee.common.message.sms.SmsProviderConfig;
import cn.topiam.employee.common.message.sms.aliyun.AliyunSmsProviderConfig;
import cn.topiam.employee.common.message.sms.qiniu.QiNiuSmsProviderConfig;
import cn.topiam.employee.common.message.sms.tencent.TencentSmsProviderConfig;
import cn.topiam.employee.console.pojo.result.setting.EmailProviderConfigResult;
import cn.topiam.employee.console.pojo.save.setting.MailProviderSaveParam;
import cn.topiam.employee.console.pojo.save.setting.SmsProviderSaveParam;
import cn.topiam.employee.console.pojo.setting.SmsProviderConfigResult;
import cn.topiam.employee.support.validation.ValidationHelp;
import static cn.topiam.employee.core.context.SettingContextHelp.getSmsProviderConfig;
import static cn.topiam.employee.core.setting.constant.MessageSettingConstants.MESSAGE_PROVIDER_EMAIL;
import static cn.topiam.employee.core.setting.constant.MessageSettingConstants.MESSAGE_SMS_PROVIDER;

/**
 * 消息设置转换器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/10/1 23:18
 */
@Mapper(componentModel = "spring")
public interface MessageSettingConverter {
    /**
     * 邮件提供商配置转实体类
     *
     * @param param {@link MailProviderSaveParam}
     * @return {@link SettingEntity}
     */
    default SettingEntity mailProviderConfigToEntity(MailProviderSaveParam param) {
        SettingEntity entity = new SettingEntity();
        entity.setName(MESSAGE_PROVIDER_EMAIL);
        String desc = MessageNoticeChannel.MAIL.getDesc();
        ValidationHelp.ValidationResult<?> validationResult = null;
        //@formatter:off
        MailProviderConfig.MailProviderConfigBuilder builder =
                MailProviderConfig.builder()
                        .username(param.getUsername())
                        .secret(EncryptContextHelp.encrypt(param.getSecret()));
        //根据提供商封装参数
        if (MailProvider.CUSTOMIZE.equals(param.getProvider())) {
            desc = desc + MailProvider.CUSTOMIZE.getName();
            builder
                    .provider(MailProvider.CUSTOMIZE)
                    .smtpUrl(param.getSmtpUrl())
                    .port(param.getPort())
                    .safetyType(param.getSafetyType());
            validationResult = ValidationHelp.validateEntity(builder.build());
        }
        //阿里云
        if (MailProvider.ALIYUN.equals(param.getProvider())) {
            desc = desc + MailProvider.ALIYUN.getName();
            builder
                    .provider(MailProvider.ALIYUN)
                    .smtpUrl(MailProvider.ALIYUN.getSmtpUrl())
                    .port(MailProvider.ALIYUN.getSslPort())
                    .safetyType(MailSafetyType.SSL);
            validationResult = ValidationHelp.validateEntity(builder.build());
        }
        //腾讯
        if (MailProvider.TENCENT.equals(param.getProvider())) {
            desc = desc + MailProvider.TENCENT.getName();
            builder
                    .provider(MailProvider.TENCENT)
                    .smtpUrl(MailProvider.TENCENT.getSmtpUrl())
                    .port(MailProvider.TENCENT.getSslPort())
                    .safetyType(MailSafetyType.SSL);
            validationResult = ValidationHelp.validateEntity(builder.build());
        }
        //网易
        if (MailProvider.NETEASE.equals(param.getProvider())) {
            desc = desc + MailProvider.NETEASE.getName();
            builder
                    .provider(MailProvider.NETEASE)
                    .smtpUrl(MailProvider.NETEASE.getSmtpUrl())
                    .port(MailProvider.NETEASE.getSslPort())
                    .safetyType(MailSafetyType.SSL);
            validationResult = ValidationHelp.validateEntity(builder.build());
        }
        // 验证
        if (Objects.requireNonNull(validationResult).isHasErrors()) {
            throw new ValidationException(validationResult.getMessage());
        }
        entity.setValue(JSONObject.toJSONString(builder.build(), JSONWriter.Feature.WriteClassName));
        entity.setDesc(desc);
        //@formatter:no
        return entity;
    }

    /**
     * 短信提供商配置转实体类
     *
     * @param param {@link SmsProviderSaveParam}
     * @return {@link SettingEntity}
     */
    default SettingEntity smsProviderConfigToEntity(SmsProviderSaveParam param) {
        ValidationHelp.ValidationResult<?> validationResult = null;
        String desc = MessageNoticeChannel.SMS.getDesc();
        SmsProviderConfig providerConfig = new SmsProviderConfig();
        ObjectMapper objectMapper = EncryptionModule.deserializerEncrypt();
        try {
            // 七牛云
            if (SmsProvider.QINIU.equals(param.getProvider())) {
                QiNiuSmsProviderConfig smsConfig = objectMapper.readValue(param.getConfig().toJSONString(), QiNiuSmsProviderConfig.class);
                validationResult = ValidationHelp.validateEntity(smsConfig);
                providerConfig = smsConfig;
                desc = desc + SmsProvider.QINIU.getDesc();
            }
            // 阿里云
            else if (SmsProvider.ALIYUN.equals(param.getProvider())) {
                AliyunSmsProviderConfig smsConfig = objectMapper.readValue(param.getConfig().toJSONString(), AliyunSmsProviderConfig.class);
                validationResult = ValidationHelp.validateEntity(smsConfig);
                providerConfig = smsConfig;
                desc = desc + SmsProvider.ALIYUN.getDesc();
            }
            // 腾讯云
            else if (SmsProvider.TENCENT.equals(param.getProvider())) {
                TencentSmsProviderConfig smsConfig = objectMapper.readValue(param.getConfig().toJSONString(), TencentSmsProviderConfig.class);
                validationResult = ValidationHelp.validateEntity(smsConfig);
                providerConfig = smsConfig;
                desc = desc + SmsProvider.TENCENT.getDesc();
            }
            //判断并处理参数验证异常
            boolean hasErrors = Objects.requireNonNull(validationResult).isHasErrors();
            if (hasErrors) {
                throw new ValidationException(validationResult.getMessage());
            }
            //封装
            SmsConfig smsConfig=new SmsConfig();
            smsConfig.setConfig(providerConfig);
            smsConfig.setProvider(param.getProvider());
            smsConfig.setLanguage(param.getLanguage());
            smsConfig.setTemplates(param.getTemplates());
            //保存
            SettingEntity entity = new SettingEntity();
            entity.setName(MESSAGE_SMS_PROVIDER);
            // 指定序列化输入的类型（这里不要移动到上面，只有序列化时才使用）
            String value = objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                    ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY).writeValueAsString(smsConfig);
            entity.setValue(value);
            entity.setDesc(desc);
            return entity;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 实体转邮件提供商配置
     *
     * @param entity {@link SettingEntity}
     * @return {@link EmailProviderConfigResult}
     */
    default EmailProviderConfigResult entityToMailProviderConfig(SettingEntity entity) {
        //没有数据，默认未启用
        if (Objects.isNull(entity)) {
            return EmailProviderConfigResult.builder().enabled(false).build();
        }
        String config = entity.getValue();
        // 根据提供商序列化
        MailProviderConfig setting = JSONObject.parseObject(config, MailProviderConfig.class);
        //@formatter:off
        return EmailProviderConfigResult.builder()
                .provider(setting.getProvider())
                .port(setting.getPort())
                .safetyType(setting.getSafetyType())
                .username(setting.getUsername())
                .secret(EncryptContextHelp.decrypt(setting.getSecret()))
                .smtpUrl(setting.getSmtpUrl())
                .enabled(true)
                .build();
        //@formatter:on
    }

    /**
     * 实体转短信提供商配置
     *
     * @param entity {@link SettingEntity}
     * @return {@link SmsProviderConfigResult}
     */
    default SmsProviderConfigResult entityToSmsProviderConfig(SettingEntity entity) {
        if (Objects.isNull(entity)) {
            return SmsProviderConfigResult.builder().enabled(false).build();
        }
        SmsConfig config = getSmsProviderConfig();
        //@formatter:off
        SmsProviderConfigResult.SmsProviderConfigResultBuilder builder = SmsProviderConfigResult.builder()
                .enabled(true)
                .language(config.getLanguage().getLocale())
                .templates(config.getTemplates())
                .provider(config.getProvider())
                .config(config.getConfig());
        //@formatter:on
        return builder.build();
    }

}
