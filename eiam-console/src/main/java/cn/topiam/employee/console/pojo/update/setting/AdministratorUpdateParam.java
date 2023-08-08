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
package cn.topiam.employee.console.pojo.update.setting;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 * 管理员修改参数
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/26 21:46
 */
@Data
@Schema(description = "修改管理员入参")
public class AdministratorUpdateParam implements Serializable {
    @Serial
    private static final long serialVersionUID = 6021548372386059064L;
    /**
     * ID
     */
    @Schema(accessMode = READ_ONLY)
    @NotBlank(message = "ID不能为空")
    private String            id;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String            email;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String            phone;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL")
    private String            avatar;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String            remark;
}
