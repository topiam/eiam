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
package cn.topiam.employee.openapi.pojo.save.account;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 保存组织用户关系
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/9/23 21:27
 */
@Data
@Schema(description = "保存组织用户关系")
public class OrganizationMemberCreateParam implements Serializable {

    @Serial
    private static final long  serialVersionUID = 5599713546299698344L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long               userId;

    /**
     * 组织信息
     */
    @Schema(description = "组织信息")
    @Valid
    @Size(min = 1, message = "至少选择一个组织")
    private List<Organization> organizationList;

    @Data
    @Schema(description = "组织信息")
    public static class Organization {

        /**
         * 组织ID
         */
        @Schema(description = "组织ID")
        @NotNull(message = "组织ID不能为空")
        private String  orgId;

        /**
         * 是否主组织
         */
        @Schema(description = "是否主组织")
        private Boolean primary;
    }
}
