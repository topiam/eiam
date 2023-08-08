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

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 邮件模板配置更新参数
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/17 23:20
 */
@Data
@Schema(description = "邮件模板配置保存入参")
public class EmailCustomTemplateSaveParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -4437284094645205715L;

    /**
     * 发送人
     */
    @Schema(description = "发件人")
    @NotBlank(message = "发件人不能为空")
    private String            sender;

    /**
     * 主题
     */
    @Schema(description = "主题")
    @NotBlank(message = "邮件主题不能为空")
    private String            subject;

    /**
     * 内容
     */
    @Schema(description = "内容")
    @NotBlank(message = "邮件内容不能为空")
    private String            content;
}
