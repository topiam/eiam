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
package cn.topiam.employee.console.pojo.result.setting;

import java.io.Serial;
import java.io.Serializable;

import cn.topiam.employee.common.enums.Language;
import cn.topiam.employee.common.enums.MessageCategory;
import cn.topiam.employee.common.enums.SmsType;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 短信配置结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/17 23:05
 */
@Data
@Schema(description = "获取短信模板列表")
public class SmsTemplateListResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 5983857137670090984L;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String            name;

    /**
     * 类型
     */
    @Schema(description = "类型")
    private SmsType           type;

    /**
     * 模板类型
     */
    @Schema(description = "模板类型")
    private MessageCategory   category;

    /**
     * 内容
     */
    @Schema(description = "内容")
    private String            content;

    /**
     * Language
     */
    @Schema(description = "Language")
    private Language          language;
}
