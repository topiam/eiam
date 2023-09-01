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
package cn.topiam.employee.console.pojo.save.category;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 更新分组配置入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/8/31 11:25
 */
@Data
@Schema(description = "更新分组配置入参")
public class CategorySaveConfigParam implements Serializable {

    /**
     * id
     */
    @Schema(description = "分组id")
    @NotNull(message = "ID不能为空")
    private Long                id;

    /**
     * 配置
     */
    @Schema(description = "配置")
    @NotNull(message = "配置不能为空")
    private Map<String, Object> config;
}
