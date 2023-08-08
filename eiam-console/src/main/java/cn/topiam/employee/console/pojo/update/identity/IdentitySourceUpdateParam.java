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
package cn.topiam.employee.console.pojo.update.identity;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

/**
 * 身份源修改参数入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/25 23:04
 */
@Data
@Schema(description = "身份源修改参数")
public class IdentitySourceUpdateParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -1440230086940289961L;
    /**
     * ID
     */
    @Parameter(description = "ID")
    @NotEmpty(message = "ID不能为空")
    private String            id;

    /**
     * 名称
     */
    @Parameter(description = "名称")
    @NotEmpty(message = "名称不能为空")
    private String            name;

    /**
     * 备注
     */
    @Parameter(description = "备注")
    private String            remark;
}
