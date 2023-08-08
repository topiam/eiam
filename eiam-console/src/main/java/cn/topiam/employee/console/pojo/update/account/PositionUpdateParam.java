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
package cn.topiam.employee.console.pojo.update.account;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 编辑岗位入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/4/13 21:32
 */
@Data
@Schema(description = "修改岗位入参")
public class PositionUpdateParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -6616249172773611157L;
    /**
     * ID
     */
    @Schema(description = "岗位ID")
    @NotBlank(message = "岗位ID不能为空")
    private String            id;
    /**
     * 岗位名称
     */
    @Schema(description = "岗位名称")
    @NotBlank(message = "岗位名称不能为空")
    private String            name;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String            remark;
}
