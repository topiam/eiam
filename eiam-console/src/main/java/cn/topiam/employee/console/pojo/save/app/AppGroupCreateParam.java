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
package cn.topiam.employee.console.pojo.save.app;

import java.io.Serializable;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 分组保存入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/8/31 23:26
 */
@Data
@Schema(description = "分组保存入参")
public class AppGroupCreateParam implements Serializable {

    /**
     * 分组名称
     */
    @NotBlank(message = "分组名称不能为空")
    @Schema(description = "分组名称")
    private String name;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 分组编码
     */
    @Schema(description = "分组编码")
    private String code;

}
