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
package cn.topiam.employee.console.pojo.result.app;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import cn.topiam.employee.common.enums.PermissionActionType;

import lombok.Data;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 获取资源
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/26 21:45
 */
@Schema(description = "获取资源结果")
@Data
public class AppPermissionResourceGetResult implements Serializable {
    /**
     * 名称
     */
    @Schema(description = "资源名称")
    private String                     name;

    /**
     * 编码
     */
    @Schema(description = "资源编码")
    private String                     code;

    /**
     * 描述
     */
    @Schema(description = "资源描述")
    private String                     desc;

    /**
     * 所属应用
     */
    @Schema(description = "所属应用")
    private Long                       appId;

    /**
     * 资源权限
     */
    @Schema(description = "资源权限")
    private List<AppPermissionsAction> actions;

    /**
     * AppPermissionsActionParam
     *
     * @author TopIAM
     * Created by support@topiam.cn on  2022/9/1 00:18
     */
    @Data
    public static class AppPermissionsAction implements Serializable {

        @Serial
        private static final long    serialVersionUID = -6391182747252245592L;

        /**
         * ID
         */
        @Hidden
        @Schema(description = "ID")
        private String               id;

        /**
         * 权限类型
         */
        @Schema(description = "权限类型")
        private PermissionActionType type;

        /**
         * 权限值
         */
        @Schema(description = "权限值")
        private String               value;

        /**
         * 权限描述
         */
        @Schema(description = "权限描述")
        private String               name;
    }
}
