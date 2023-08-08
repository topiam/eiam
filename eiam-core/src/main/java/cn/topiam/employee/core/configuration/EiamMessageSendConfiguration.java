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
import org.springframework.core.task.TaskExecutor;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.common.constant.SettingConstants;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.entity.setting.config.SmsConfig;
import cn.topiam.employee.common.jackjson.encrypt.EncryptionModule;
import cn.topiam.employee.common.message.mail.DefaultMailProviderSendImpl;
import cn.topiam.employee.common.message.mail.MailProviderConfig;
import cn.topiam.employee.common.message.mail.MailProviderSend;
import cn.topiam.employee.common.message.sms.SmsNoneProviderSend;
import cn.topiam.employee.common.message.sms.SmsProviderSend;
import cn.topiam.employee.common.message.sms.SmsSendProviderFactory;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.core.setting.constant.MessageSettingConstants;
import static cn.topiam.employee.common.constant.ConfigBeanNameConstants.MAIL_PROVIDER_SEND;
import static cn.topiam.employee.common.constant.ConfigBeanNameConstants.SMS_PROVIDER_SEND;
import static cn.topiam.employee.core.help.SettingHelp.getSmsProviderConfig;

/**
 * 消息发送配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/25 23:48
 */
@Configuration
public class EiamMessageSendConfiguration {

    /**
     * 短信发送
     *
     * @return {@link SmsProviderSend}
     */
    @Bean(SMS_PROVIDER_SEND)
    @RefreshScope
    public SmsProviderSend smsProviderSend() {
        //查询当前启用的短信提供商
        SmsConfig config = getSmsProviderConfig();
        if (Objects.isNull(config.getConfig())) {
            return new SmsNoneProviderSend();
        }
        return SmsSendProviderFactory.newInstance(config.getConfig());
    }

    /**
     * 邮件发送
     *
     * @param messageSettingRepository {@link SettingRepository}
     * @return {@link MailProviderSend}
     */
    @Bean(MAIL_PROVIDER_SEND)
    @RefreshScope
    public MailProviderSend mailProviderSend(SettingRepository messageSettingRepository,
                                             TaskExecutor taskExecutor) {
        try {
            SettingEntity setting = messageSettingRepository
                .findByName(MessageSettingConstants.MESSAGE_PROVIDER_EMAIL);
            if (!Objects.isNull(setting)
                && !SettingConstants.NOT_CONFIG.equals(setting.getValue())) {
                String value = setting.getValue();
                ObjectMapper objectMapper = EncryptionModule.deserializerDecrypt();
                // 指定序列化输入的类型
                objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                    ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
                // 根据提供商序列化
                MailProviderConfig config = objectMapper.readValue(value, MailProviderConfig.class);
                return new DefaultMailProviderSendImpl(config, taskExecutor);
            }
            return null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
