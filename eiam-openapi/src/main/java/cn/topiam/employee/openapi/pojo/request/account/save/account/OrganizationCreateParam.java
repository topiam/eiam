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
package cn.topiam.employee.openapi.pojo.request.account.save.account;

import java.io.Serial;
import java.io.Serializable;

import cn.topiam.employee.common.enums.account.OrganizationType;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * 创建组织架构入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/11 23:16
 */
@Data
@Schema(description = "创建组织架构入参")
public class OrganizationCreateParam implements Serializable {
    @Serial
    private static final long serialVersionUID = 3118058164024117164L;

    /**
     * code
     */
    @Schema(description = "编码")
    private String            code;

    /**
     * 上级部门
     */
    @NotEmpty(message = "请选择上级组织")
    @Schema(description = "上级组织")
    private String            parentId;

    /**
     * 名称
     */
    @Schema(description = "架构名称")
    @NotBlank(message = "名称不能为空")
    private String            name;

    /**
     * 类型
     */
    @Schema(description = "架构类型")
    @NotNull(message = "类型不能为空")
    private OrganizationType  type;

    /**
     * 外部ID
     */
    @Schema(description = "外部ID")
    private String            externalId;

    /**
     * 区域
     */
    @Schema(description = "所在区域")
    private String            area;

    /**
     * 描述
     */
    @Schema(description = "架构描述")
    private String            desc;

    /**
     * 排序
     */
    @Schema(description = "架构排序")
    private String            order;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean           enabled;
}
