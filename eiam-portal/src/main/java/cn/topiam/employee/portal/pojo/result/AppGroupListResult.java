/*
 * eiam-portal - Employee Identity and Access Management
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
package cn.topiam.employee.portal.pojo.result;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 获取应用列表
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/9/1 11:58
 */
@Data
@Schema(description = "获取分组应用列表")
public class AppGroupListResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1263170640092199401L;

    /**
     * 应用分组ID
     */
    @Schema(description = "应用分组ID")
    private Long              id;

    /**
     * 应用分组名称
     */
    @Schema(description = "应用分组名称")
    private String            name;

    /**
     * APP数量
     */
    @Schema(description = "APP数量")
    private Integer           appCount;

}
