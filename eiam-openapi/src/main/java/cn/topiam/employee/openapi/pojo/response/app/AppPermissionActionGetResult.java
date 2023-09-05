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

import cn.topiam.employee.common.enums.PermissionActionType;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 权限详情
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/26 21:45
 */
@Schema(description = "权限操作")
@Data
public class AppPermissionActionGetResult implements Serializable {
    /**
     * ID
     */
    @Parameter(description = "ID")
    private String               id;
    /**
     * 权限名称
     */
    @Parameter(description = "权限名称")
    private String               name;

    /**
     * 权限值
     */
    @Parameter(description = "权限值")
    private String               value;

    /**
     * 权限类型
     */
    @Parameter(description = "权限类型")
    private PermissionActionType type;

    /**
     * 备注
     */
    @Parameter(description = "备注")
    private String               remark;
}
