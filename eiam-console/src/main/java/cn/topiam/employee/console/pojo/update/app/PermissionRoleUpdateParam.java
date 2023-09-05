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
package cn.topiam.employee.console.pojo.update.app;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 * 角色修改参数
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/26 21:46
 */
@Data
@Schema(description = "修改角色入参")
public class PermissionRoleUpdateParam implements Serializable {
    @Serial
    private static final long serialVersionUID = 6021548372386059064L;
    /**
     * ID
     */
    @Schema(accessMode = READ_ONLY)
    @NotBlank(message = "ID不能为空")
    private String            id;
    /**
     * 名称
     */
    @Schema(description = "角色名称")
    private String            name;
    /**
     * 编码
     */
    @Schema(description = "角色编码")
    private String            code;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String            remark;
}
