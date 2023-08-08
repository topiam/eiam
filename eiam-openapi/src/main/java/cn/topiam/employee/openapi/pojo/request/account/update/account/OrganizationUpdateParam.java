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
package cn.topiam.employee.openapi.pojo.request.account.update.account;

import java.io.Serial;
import java.io.Serializable;

import cn.topiam.employee.common.enums.account.OrganizationType;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 编辑组织架构入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/11 23:16
 */
@Data
@Schema(description = "修改组织机构入参")
public class OrganizationUpdateParam implements Serializable {
    @Serial
    private static final long serialVersionUID = 4570955457331971748L;

    /**
     * id
     */
    @Schema(description = "组织ID")
    @NotBlank(message = "ID不能为空")
    private String            id;

    /**
     * 名称
     */
    @Schema(description = "组织名称")
    @NotBlank(message = "组织名称不能为空")
    private String            name;

    /**
     * 类型
     */
    @Schema(description = "组织类型")
    @NotNull(message = "组织类型不能为空")
    private OrganizationType  type;

    /**
     * 描述
     */
    @Schema(description = "组织描述")
    private String            desc;

    /**
     * 排序
     */
    @Schema(description = "组织排序")
    private String            order;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean           enabled;

}
