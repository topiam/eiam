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

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 邮件模板配置结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/17 23:05
 */
@Data
@Schema(description = "获取邮件模板")
public class EmailTemplateResult implements Serializable {
    @Serial
    private static final long serialVersionUID = 6499437680155500022L;
    /**
     * 名称
     */
    @Parameter(description = "名称")
    private String            name;
    /**
     * 发送人
     */
    @Parameter(description = "发送人")
    private String            sender;

    /**
     * 主题
     */
    @Parameter(description = "主题")
    private String            subject;

    /**
     * 内容
     */
    @Parameter(description = "内容")
    private String            content;

    /**
     * 描述
     */
    @Parameter(description = "描述")
    private String            desc;

    /**
     * 自定义
     */
    @Parameter(description = "自定义")
    private Boolean           custom;

}
