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
package cn.topiam.employee.console.service.setting.impl;

import org.springframework.stereotype.Service;

import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.console.converter.setting.MessageSettingConverter;
import cn.topiam.employee.console.pojo.result.setting.EmailProviderConfigResult;
import cn.topiam.employee.console.pojo.save.setting.MailProviderSaveParam;
import cn.topiam.employee.console.pojo.save.setting.SmsProviderSaveParam;
import cn.topiam.employee.console.pojo.setting.SmsProviderConfigResult;
import cn.topiam.employee.console.service.setting.MessageSettingService;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import static cn.topiam.employee.common.constant.ConfigBeanNameConstants.MAIL_PROVIDER_SEND;
import static cn.topiam.employee.common.constant.ConfigBeanNameConstants.SMS_PROVIDER_SEND;
import static cn.topiam.employee.core.setting.constant.MessageSettingConstants.MESSAGE_PROVIDER_EMAIL;
import static cn.topiam.employee.core.setting.constant.MessageSettingConstants.MESSAGE_SMS_PROVIDER;

/**
 * 消息设置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/10/1 21:19
 */
@Service
public class MessageSettingServiceImpl extends SettingServiceImpl implements MessageSettingService {

    /**
     * 保存配置
     *
     * @param param {@link SettingEntity}
     * @return {@link Boolean}
     */
    @Override
    public Boolean saveMailProviderConfig(MailProviderSaveParam param) {
        SettingEntity entity = messageSettingConverter.mailProviderConfigToEntity(param);
        Boolean setting = saveSetting(entity);
        ApplicationContextHelp.refresh(MAIL_PROVIDER_SEND);
        return setting;
    }

    /**
     * 保存邮件验证配置
     *
     * @param param {@link SmsProviderSaveParam}
     * @return {@link Boolean}
     */
    @Override
    public Boolean saveSmsProviderConfig(SmsProviderSaveParam param) {
        SettingEntity entity = messageSettingConverter.smsProviderConfigToEntity(param);
        Boolean setting = saveSetting(entity);
        ApplicationContextHelp.refresh(SMS_PROVIDER_SEND);
        return setting;
    }

    /**
     * 禁用短信验证服务
     *
     * @return {@link Boolean}
     */
    @Override
    public Boolean disableSmsProvider() {
        Boolean setting = removeSetting(MESSAGE_SMS_PROVIDER);
        // refresh
        ApplicationContextHelp.refresh(SMS_PROVIDER_SEND);
        return setting;
    }

    /**
     * 禁用邮件提供商
     *
     * @return {@link Boolean}
     */
    @Override
    public Boolean disableMailProvider() {
        Boolean setting = removeSetting(MESSAGE_PROVIDER_EMAIL);
        // refresh
        ApplicationContextHelp.refresh(MAIL_PROVIDER_SEND);
        return setting;
    }

    /**
     * 获取邮件提供商配置
     *
     * @return {@link EmailProviderConfigResult}
     */
    @Override
    public EmailProviderConfigResult getMailProviderConfig() {
        SettingEntity entity = getSetting(MESSAGE_PROVIDER_EMAIL);
        return messageSettingConverter.entityToMailProviderConfig(entity);
    }

    /**
     * 获取短信验证服务配置
     *
     * @return {@link SmsProviderConfigResult}
     */
    @Override
    public SmsProviderConfigResult getSmsProviderConfig() {
        SettingEntity entity = getSetting(MESSAGE_SMS_PROVIDER);
        return messageSettingConverter.entityToSmsProviderConfig(entity);
    }

    private final MessageSettingConverter messageSettingConverter;

    public MessageSettingServiceImpl(SettingRepository settingsRepository,
                                     MessageSettingConverter messageSettingConverter) {
        super(settingsRepository);
        this.messageSettingConverter = messageSettingConverter;
    }
}
