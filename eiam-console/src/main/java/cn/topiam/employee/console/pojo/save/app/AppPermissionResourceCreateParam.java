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
import java.util.List;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 资源创建参数
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/26 21:46
 */
@Data
@Schema(description = "创建资源入参")
public class AppPermissionResourceCreateParam implements Serializable {
    /**
     * 编码
     */
    @Schema(description = "资源编码")
    @NotBlank(message = "资源编码不能为空")
    private String                          code;
    /**
     * 名称
     */
    @Schema(description = "资源名称")
    @NotBlank(message = "资源名称不能为空")
    private String                          name;
    /**
     * 描述
     */
    @Schema(description = "资源描述")
    @NotBlank(message = "资源描述不能为空")
    private String                          desc;

    /**
     * 是否启用
     */
    private Boolean                         enabled = true;

    /**
     * 所属应用
     */
    @Schema(description = "所属应用")
    @NotNull(message = "所属应用不能为空")
    private Long                            appId;

    /**
     * 资源权限
     */
    @Schema(description = "资源权限")
    @NotNull(message = "资源权限不能为空")
    private List<AppPermissionsActionParam> actions;
}
