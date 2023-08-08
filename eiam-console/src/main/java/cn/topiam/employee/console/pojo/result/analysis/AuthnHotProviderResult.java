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
package cn.topiam.employee.console.pojo.result.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 热点认证方式结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/11/22 23:16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "热点认证方式响应")
public class AuthnHotProviderResult {

    /**
     * 认证方式
     */
    @Schema(description = "认证方式")
    private String name;

    /**
     * 使用次数
     */
    @Schema(description = "使用次数")
    private Long   count;
}
