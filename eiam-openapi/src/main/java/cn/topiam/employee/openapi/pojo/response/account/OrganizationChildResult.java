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
package cn.topiam.employee.openapi.pojo.response.account;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 获取子组织
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/11 21:27
 */
@Data
@Schema(description = "获取子组织")
public class OrganizationChildResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -150631305460653395L;

    /**
     * 主键ID
     */
    @Schema(description = "ID")
    private String            id;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String            name;

    /**
     * 父级
     */
    @Schema(description = "父级")
    private String            parentId;

    /**
     * 显示路径
     */
    @Schema(description = "显示路径")
    private String            displayPath;

    /**
     * 编码
     */
    @Schema(description = "编码")
    private String            code;

    /**
     * 外部ID
     */
    @Schema(description = "外部ID")
    private String            externalId;
    /**
     * 来源
     */
    @Schema(description = "类型")
    private String            type;
    /**
     * 来源
     */
    @Schema(description = "数据来源")
    private String            dataOrigin;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer           order;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean           enabled;
    /**
     * 是否叶子节点
     */
    @JsonProperty(value = "isLeaf")
    @Schema(description = "是否叶子节点")
    private Boolean           leaf;

}
