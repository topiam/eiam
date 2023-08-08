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
package cn.topiam.employee.console.pojo.result.setting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.topiam.employee.support.enums.BaseEnum;

import lombok.Builder;
import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 获取管理员权限策略
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/5/22 21:55
 */
@Data
@Schema(description = "获取管理员权限点列表响应")
public class AdministratorPermissionActionListResult implements Serializable {

    /**
     * 模块编码
     */
    @Parameter(description = "模块编码")
    private String         code;

    /**
     * 模块名称
     */
    @Parameter(description = "模块名称")
    private String         name;

    /**
     * 模块功能
     */
    @Parameter(description = "功能列表")
    private List<Function> functions;

    public AdministratorPermissionActionListResult(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Data
    @Schema(description = "功能项")
    public static class Function {
        /**
         * 功能编码
         */
        @Parameter(description = "功能编码")
        private String       code;

        /**
         * 功能名称
         */
        @Parameter(description = "功能名称")
        private String       name;

        /**
         * 操作项
         */
        @Parameter(description = "操作项")
        private List<Action> actions = new ArrayList<>();

        public Function(String code, String name, Object[] actions) {
            this.code = code;
            this.name = name;
            for (Object object : actions) {
                BaseEnum baseEnum = (BaseEnum) object;
                this.actions.add(new Action(baseEnum.getCode(), baseEnum.getDesc()));
            }
        }
    }

    @Data
    @Builder
    public static class Action {
        /**
         * 功能编码
         */
        @Parameter(description = "功能编码")
        private String code;

        /**
         * 功能名称
         */
        @Parameter(description = "功能名称")
        private String name;

        public Action(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }
}
