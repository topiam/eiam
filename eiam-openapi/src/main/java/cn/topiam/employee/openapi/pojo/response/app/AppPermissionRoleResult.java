/*
 * eiam-openapi - Employee Identity and Access Management
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
package cn.topiam.employee.openapi.pojo.response.app;

import java.io.Serializable;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 获取角色
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/26 21:45
 */
@Schema(description = "获取角色")
@Data
public class AppPermissionRoleResult implements Serializable {
    /**
     * id
     */
    @Parameter(description = "id")
    private String  id;
    /**
     * appId
     */
    @Parameter(description = "应用ID")
    private String  appId;
    /**
     * 角色名称
     */
    @Parameter(description = "角色名称")
    private String  name;

    /**
     * 角色编码
     */
    @Parameter(description = "角色编码")
    private String  code;

    /**
     * 是否启用
     */
    @Parameter(description = "是否启用")
    private Boolean enabled;

    /**
     * 备注
     */
    @Parameter(description = "备注")
    private String  remark;
}
