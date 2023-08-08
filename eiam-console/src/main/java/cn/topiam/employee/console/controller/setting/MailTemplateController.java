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

import java.util.List;
import java.util.Objects;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.console.pojo.result.setting.EmailTemplateListResult;
import cn.topiam.employee.console.pojo.result.setting.EmailTemplateResult;
import cn.topiam.employee.console.pojo.save.setting.EmailCustomTemplateSaveParam;
import cn.topiam.employee.console.service.setting.MailTemplateService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constant.SettingConstants.SETTING_PATH;

/**
 * 邮件配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/7/24 22:36
 */
@Validated
@Tag(name = "邮件模板")
@RestController
@AllArgsConstructor
@RequestMapping(value = SETTING_PATH
                        + "/mail_template", produces = MediaType.APPLICATION_JSON_VALUE)
public class MailTemplateController {

    /**
     * 获取邮件模板列表
     *
     * @return {@link EmailTemplateResult}
     */
    @Validated
    @GetMapping(value = "/list")
    @Operation(summary = "获取邮件模板列表")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<List<EmailTemplateListResult>> getEmailTemplateList() {
        List<EmailTemplateListResult> list = templateService.getEmailTemplateList();
        return ApiRestResult.<List<EmailTemplateListResult>> builder().result(list).build();
    }

    /**
     * 根据模板类型查询邮件模板
     *
     * @param type {@link String}
     * @return {@link EmailTemplateResult}
     */
    @Operation(summary = "获取邮件模板信息")
    @GetMapping(value = "/{type}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<EmailTemplateResult> getEmailTemplate(@PathVariable(value = "type") String type) {
        MailType templateType = MailType.getType(type);
        EmailTemplateResult result = templateService.getEmailTemplate(templateType);
        return ApiRestResult.<EmailTemplateResult> builder().result(result).build();
    }

    /**
     * 保存邮件模板
     *
     * @param type {@link String}
     * @return {@link EmailTemplateResult}
     */
    @Lock
    @Preview
    @PutMapping(value = "/save_custom/{type}")
    @Audit(type = EventType.SAVE_MAIL_TEMPLATE)
    @Operation(summary = "保存邮件模板")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> saveCustomEmailTemplate(@PathVariable(value = "type") String type,
                                                          @RequestBody @Validated EmailCustomTemplateSaveParam param) {
        MailType templateType = MailType.getType(type);
        return ApiRestResult.<Boolean> builder()
            .result(!Objects.isNull(templateService.saveCustomEmailTemplate(templateType, param)))
            .build();
    }

    /**
     * 关闭自定义邮件模板
     *
     * @return {@link EmailTemplateResult}
     */
    @Lock
    @Preview
    @PutMapping(value = "/disable_custom/{type}")
    @Operation(summary = "禁用自定义邮件模板")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> disableCustomEmailTemplate(@PathVariable(value = "type") String type) {
        templateService.disableCustomEmailTemplate(MailType.getType(type));
        return ApiRestResult.ok();
    }

    /**
     * 邮件模板服务类
     */
    private final MailTemplateService templateService;
}
