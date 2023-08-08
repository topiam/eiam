/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.entity.setting.config;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import cn.topiam.employee.common.enums.Language;
import cn.topiam.employee.common.enums.SmsType;
import cn.topiam.employee.common.message.enums.SmsProvider;
import cn.topiam.employee.common.message.sms.SmsProviderConfig;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * 短信配置
 *
 * @author TopIAM
 */
@Data
public class SmsConfig implements Serializable {

    @Serial
    private static final long    serialVersionUID = 5293005308937620292L;

    /**
     * 提供商
     */
    private SmsProvider          provider;

    /**
     * 语言
     */
    private Language             language;

    /**
     * 配置
     */
    private SmsProviderConfig    config;

    /**
     * 模版配置
     */
    private List<TemplateConfig> templates;

    public SmsConfig() {
    }

    @Data
    public static class TemplateConfig implements Serializable {

        @Serial
        private static final long serialVersionUID = 2801844583775238689L;

        @Parameter(description = "短信类型")
        private SmsType           type;
        @Parameter(description = "模板ID/CODE")
        private String            code;

        public TemplateConfig() {
        }
    }
}
