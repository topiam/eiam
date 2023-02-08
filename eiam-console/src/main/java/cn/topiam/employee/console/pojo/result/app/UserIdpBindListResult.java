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
package cn.topiam.employee.console.pojo.result.app;

import java.time.LocalDateTime;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户身份提供商绑定列表查询结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/11/3 22:13
 */
@Data
@Schema(description = "用户身份提供商绑定列表查询结果")
public class UserIdpBindListResult {

    /**
     * id
     */
    @Schema(description = "id")
    private String        id;

    /**
     * open id
     */
    @Schema(description = "open id")
    private String        openId;

    /**
     * 提供商名称
     */
    @Schema(description = "提供商名称")
    private String        idpName;

    /**
     * 提供商类型
     */
    @Schema(description = "提供商类型")
    private String        idpType;

    /**
     * 绑定时间
     */
    @Schema(description = "绑定时间")
    private LocalDateTime bindTime;
}
