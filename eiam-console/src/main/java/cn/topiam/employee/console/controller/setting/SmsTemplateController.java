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

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.topiam.employee.common.enums.Language;
import cn.topiam.employee.console.pojo.result.setting.SmsTemplateListResult;
import cn.topiam.employee.console.service.setting.SmsTemplateService;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import static cn.topiam.employee.common.constant.SettingConstants.SETTING_PATH;

/**
 * 短信模版配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/7/24 22:36
 */
@Validated
@Tag(name = "短信模板")
@RestController
@AllArgsConstructor
@RequestMapping(value = SETTING_PATH + "/sms_template", produces = MediaType.APPLICATION_JSON_VALUE)
public class SmsTemplateController {

    /**
     * 获取短信模板列表
     *
     * @param language {@link Language}
     * @return {@link SmsTemplateListResult}
     */
    @GetMapping(value = "/list")
    @Operation(summary = "获取短信模板列表")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<List<SmsTemplateListResult>> getSmsTemplateList(@NotNull(message = "语言类型不能为空") @Parameter(description = "语言") Language language) {
        List<SmsTemplateListResult> list = smsTemplateService.getSmsTemplateList(language);
        return ApiRestResult.<List<SmsTemplateListResult>> builder().result(list).build();
    }

    /**
     * 短信模板服务类
     */
    private final SmsTemplateService smsTemplateService;
}
