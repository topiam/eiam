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
package cn.topiam.employee.console.pojo.query.setting;

import org.springdoc.core.annotations.ParameterObject;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/14 21:36
 */
@Data
@Schema(description = "管理员列表查询")
@ParameterObject
public class AdministratorListQuery {
    /**
     * username
     */
    @Parameter(description = "用户名")
    private String username;

    /**
     * phone
     */
    @Parameter(description = "手机号码")
    private String phone;

    /**
     * email
     */
    @Parameter(description = "邮箱地址")
    private String email;

}
