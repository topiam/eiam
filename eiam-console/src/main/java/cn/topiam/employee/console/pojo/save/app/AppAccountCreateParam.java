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

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * AppAccountCreateParam 应用账户新增入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/24 22:13
 */
@Data
@Schema(description = "应用账户新增入参")
public class AppAccountCreateParam {

    /**
     * 应用ID
     */
    @Schema(description = "应用ID")
    @NotNull(message = "应用ID不能为空")
    private Long   appId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long   userId;

    /**
     * 账户名称
     */
    @Schema(description = "账户名称")
    @NotBlank(message = "账户名称不能为空")
    private String account;

    /**
     * 账户密码
     */
    @Schema(description = "账户密码")
    private String password;
}
