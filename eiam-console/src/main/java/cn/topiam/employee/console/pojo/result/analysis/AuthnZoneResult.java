/*
 * eiam-console - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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
 * 登录区域结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/01/24 23:16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录区域结果")
public class AuthnZoneResult {

    /**
     * 省份code
     */
    @Schema(description = "省份code")
    private String name;

    /**
     * 登录次数
     */
    @Schema(description = "登录次数")
    private Long   count;
}
