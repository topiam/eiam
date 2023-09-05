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

import lombok.Data;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 获取应用资源权限列表
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/11 23:08
 */
@Data
@Accessors(chain = true)
@Schema(description = "获取应用资源权限列表")
public class AppPermissionActionListResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 3320953184046791392L;
    /**
     * 资源ID
     */
    @Parameter(description = "资源ID")
    private String            id;

    /**
     * 资源编码
     */
    @Parameter(description = "资源编码")
    private String            code;

    /**
     * 资源名称
     */
    @Parameter(description = "资源名称")
    private String            name;

    /**
     * 所属应用
     */
    @Parameter(description = "所属应用")
    private String            appId;

    /**
     * desc
     */
    @Parameter(description = "描述")
    private String            desc;

    /**
     * 是否启用
     */
    @Parameter(description = "是否启用")
    private Boolean           enabled;

    /**
     * 路由权限
     */
    @Parameter(description = "菜单权限")
    private List<Action>      menus;

    /**
     * 操作权限
     */
    @Parameter(description = "操作权限")
    private List<Action>      buttons;

    /**
     * 接口权限
     */
    @Parameter(description = "接口权限")
    private List<Action>      apis;

    /**
     * 操作权限
     */
    @Parameter(description = "数据权限")
    private List<Action>      datas;

    /**
     * 其他权限
     */
    @Parameter(description = "其他权限")
    private List<Action>      others;

    @Data
    @Schema(description = "权限项")
    public static class Action implements Serializable {

        /**
         * 权限ID
         */
        @Parameter(description = "权限ID")
        private String id;

        /**
         * 权限名称
         */
        @Parameter(description = "权限名称")
        private String name;

        /**
         * 权限标识
         */
        @Parameter(description = "权限标识")
        private String access;
    }
}
