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
package cn.topiam.employee.console.pojo.save.setting;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.common.entity.setting.config.SmsConfig;
import cn.topiam.employee.common.enums.Language;
import cn.topiam.employee.common.message.enums.SmsProvider;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 保存短信服务商创建请求入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/7/31 21:34
 */
@Data
@Schema(description = "保存短信验证服务入参")
public class SmsProviderSaveParam implements Serializable {
    @Serial
    private static final long              serialVersionUID = 4125843198392920166L;

    /**
     * 平台
     */
    @Schema(description = "提供商")
    @NotNull(message = "短信提供商不能为空")
    private SmsProvider                    provider;

    /**
     * 配置JSON串
     */
    @Schema(description = "配置JSON串")
    @NotNull(message = "配置不能为空")
    private JSONObject                     config;

    /**
     * 场景语言
     */
    @Schema(description = "场景语言")
    @NotNull(message = "场景语言不能为空")
    private Language                       language;

    /**
     * 短信模板配置
     */
    @Schema(description = "短信模板配置")
    @NotNull(message = "短信模板配置不能为空")
    private List<SmsConfig.TemplateConfig> templates;
}
