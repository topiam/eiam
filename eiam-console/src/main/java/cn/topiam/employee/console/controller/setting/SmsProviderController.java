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
package cn.topiam.employee.console.controller.setting;

import java.util.LinkedHashMap;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.common.enums.MessageCategory;
import cn.topiam.employee.common.enums.SmsType;
import cn.topiam.employee.console.pojo.save.setting.SmsProviderSaveParam;
import cn.topiam.employee.console.pojo.setting.SmsProviderConfigResult;
import cn.topiam.employee.console.service.setting.MessageSettingService;
import cn.topiam.employee.core.message.MsgVariable;
import cn.topiam.employee.core.message.sms.SmsMsgEventPublish;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.security.password.PasswordGenerator;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constant.SettingConstants.SETTING_PATH;
import static cn.topiam.employee.core.message.MsgVariable.EXPIRE_DAYS;
import static cn.topiam.employee.core.message.sms.SmsMsgEventPublish.USERNAME;

/**
 * 消息设置
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/7/24 22:36
 */
@Validated
@Tag(name = "短信提供商")
@RestController
@AllArgsConstructor
@RequestMapping(value = SETTING_PATH + "/message/sms_provider")
public class SmsProviderController {
    /**
     * 禁用短信提供商
     *
     * @return {@link ApiRestResult}
     */
    @Lock
    @Preview
    @Validated
    @Operation(summary = "禁用短信提供商")
    @Audit(type = EventType.OFF_SMS_SERVICE)
    @PutMapping(value = "/disable")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> disableSmsProvider() {
        Boolean result = messageSettingService.disableSmsProvider();
        return ApiRestResult.ok(result);
    }

    /**
     * 保存短信提供商配置
     *
     * @param param {@link SmsProviderSaveParam}
     * @return {@link ApiRestResult}
     */
    @Lock
    @Preview
    @Validated
    @Operation(summary = "保存短信提供商配置")
    @Audit(type = EventType.SAVE_SMS_SERVICE)
    @PostMapping("save")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> saveSmsProviderConfig(@RequestBody SmsProviderSaveParam param) {
        Boolean result = messageSettingService.saveSmsProviderConfig(param);
        return ApiRestResult.ok(result);
    }

    /**
     * 获取短信提供商配置
     *
     * @return {@link ApiRestResult}
     */
    @Operation(summary = "获取短信提供商配置")
    @GetMapping("/config")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<SmsProviderConfigResult> getSmsProviderConfig() {
        SmsProviderConfigResult result = messageSettingService.getSmsProviderConfig();
        return ApiRestResult.ok(result);
    }

    /**
     * 短信发送测试
     *
     * @param smsType {@link MailType}
     * @param receiver {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "发送测试短信")
    @GetMapping(value = "/test")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> sendSms(SmsType smsType, String receiver) {
        if (MessageCategory.NOTICE.equals(smsType.getCategory())) {
            LinkedHashMap<String, String> parameter = new LinkedHashMap<>(16);
            boolean flag = SmsType.WELCOME_SMS == smsType || SmsType.RESET_PASSWORD == smsType
                           || SmsType.RESET_PASSWORD_SUCCESS == smsType;
            if (SmsType.WARING == smsType || flag) {
                parameter.put(USERNAME, "test");
            }
            if (flag) {
                parameter.put(MsgVariable.PASSWORD, passwordGenerator.generatePassword());
            }
            if (SmsType.PASSWORD_SOON_EXPIRED_REMIND == smsType) {
                parameter.put(EXPIRE_DAYS, "3");
            }
            smsMsgEventPublish.publish(smsType, receiver, parameter);
        } else {
            smsMsgEventPublish.publishVerifyCode(receiver, smsType, "123456");
        }
        return ApiRestResult.ok();
    }

    /**
     * MessageSettingService
     */
    private final MessageSettingService messageSettingService;

    private final SmsMsgEventPublish    smsMsgEventPublish;

    private final PasswordGenerator     passwordGenerator;
}
