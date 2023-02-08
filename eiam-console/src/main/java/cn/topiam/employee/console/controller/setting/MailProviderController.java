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
package cn.topiam.employee.console.controller.setting;

import java.util.HashMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.enums.EventType;
import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.console.pojo.result.setting.EmailProviderConfigResult;
import cn.topiam.employee.console.pojo.save.setting.MailProviderSaveParam;
import cn.topiam.employee.console.service.setting.MessageSettingService;
import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.core.message.MsgVariable;
import cn.topiam.employee.core.message.mail.MailMsgEventPublish;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constants.SettingConstants.SETTING_PATH;

/**
 * 消息设置
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/7/24 22:36
 */
@Validated
@Tag(name = "邮件提供商")
@RestController
@AllArgsConstructor
@RequestMapping(value = SETTING_PATH + "/message/mail_provider")
public class MailProviderController {

    /**
     * 保存邮件服务商配置
     *
     * @param param {@link MailProviderSaveParam}
     * @return {@link ApiRestResult}
     */
    @Lock
    @Preview
    @Validated
    @Operation(summary = "保存邮件服务提供商配置")
    @Audit(type = EventType.SAVE_MAIL_SERVICE)
    @PostMapping("save")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<Boolean> saveEmailProviderConfig(@RequestBody MailProviderSaveParam param) {
        Boolean result = messageSettingService.saveMailProviderConfig(param);
        return ApiRestResult.ok(result);
    }

    /**
     * 获取邮件服务商配置
     *
     * @return {@link ApiRestResult}
     */
    @Operation(summary = "获取邮件服务提供商配置")
    @GetMapping("config")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<EmailProviderConfigResult> getEmailProviderConfig() {
        return ApiRestResult.ok(messageSettingService.getMailProviderConfig());
    }

    /**
     * 禁用邮件提供商
     *
     * @return {@link ApiRestResult}
     */
    @Lock
    @Preview
    @Validated
    @Operation(summary = "禁用邮件服务提供商")
    @Audit(type = EventType.OFF_MAIL_SERVICE)
    @PutMapping(value = "/disable")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<Boolean> disableMailProvider() {
        Boolean result = messageSettingService.disableMailProvider();
        return ApiRestResult.ok(result);
    }

    /**
     * 邮件发送测试
     *
     * @param mailType {@link MailType}
     * @param receiver {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "发送测试邮件")
    @GetMapping(value = "/test")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<Boolean> sendMail(MailType mailType, String receiver) {
        HashMap<String, Object> map = new HashMap<>(16);
        if (mailType == MailType.UPDATE_PASSWORD || mailType == MailType.UPDATE_BIND_MAIL
            || mailType == MailType.VERIFY_EMAIL || mailType == MailType.RESET_PASSWORD) {
            map.put(MsgVariable.VERIFY_CODE, RandomStringUtils.randomAlphanumeric(6));
        }
        map.put(MsgVariable.TEST, "(TEST)");
        map.put(MsgVariable.EXPIRE_DAYS, "3");
        map.put("verify_link", ServerContextHelp.getPortalPublicBaseUrl());
        mailMsgEventPublish.publish(mailType, receiver, map);
        return ApiRestResult.ok();
    }

    /**
     * MailMsgEventPublish
     */
    private final MailMsgEventPublish   mailMsgEventPublish;
    /**
     * MessageSettingService
     */
    private final MessageSettingService messageSettingService;
}
