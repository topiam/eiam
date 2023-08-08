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
package cn.topiam.employee.console.pojo.setting;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import cn.topiam.employee.common.entity.setting.config.SmsConfig;
import cn.topiam.employee.common.message.enums.SmsProvider;
import cn.topiam.employee.common.message.sms.SmsProviderConfig;

import lombok.Builder;
import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 短信服务商配置查询结果
 *
 * @author TopIAM
 */
@Data
@Builder
@Schema(description = "短信服务商配置查询响应")
public class SmsProviderConfigResult implements Serializable {

    @Serial
    private static final long              serialVersionUID = -2667374916357438335L;
    /**
     * 平台
     */
    @Parameter(description = "提供商")
    private SmsProvider                    provider;
    /**
     * 配置
     */
    @Parameter(description = "参数配置")
    private SmsProviderConfig              config;

    /**
     * 配置
     */
    @Parameter(description = "模板配置")
    private List<SmsConfig.TemplateConfig> templates;
    /**
     * 描述
     */
    @Parameter(description = "描述")
    private String                         desc;

    /**
     * 是否启用
     */
    @Parameter(description = "是否启用")
    private Boolean                        enabled;

    /**
     * 语言
     */
    @Parameter(description = "语言")
    private String                         language;
}
