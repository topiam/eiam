/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.controller.pojo;

import java.util.Set;

import org.springdoc.core.annotations.ParameterObject;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 审计字典结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/27 22:35
 */
@Data
@Schema(description = "字典响应")
@ParameterObject
public class DictResult {

    @Parameter(description = "分组名")
    private String         name;
    @Parameter(description = "分组编码")
    private String         code;
    @Parameter(description = "类型")
    private Set<AuditType> types;

    @Data
    @Schema(description = "审计类型")
    public static class AuditType {
        /**
         * 名称
         */
        @Parameter(description = "名称")
        private String name;

        /**
         * CODE
         */
        @Parameter(description = "CODE")
        private String code;
    }

}
