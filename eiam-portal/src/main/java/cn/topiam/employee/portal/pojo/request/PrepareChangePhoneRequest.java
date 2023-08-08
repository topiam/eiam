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
package cn.topiam.employee.portal.pojo.request;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

/**
 *准备更改手机号入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/8 21:15
 */
@Data
@Schema(description = "准备更改手机号入参")
public class PrepareChangePhoneRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 5681761697876754485L;

    /**
     * 手机号
     */
    @NotEmpty(message = "手机号不能为空")
    @Parameter(description = "手机号")
    private String            phone;

    /**
     * 手机号区域
     */
    @NotEmpty(message = "手机号区域不能为空")
    @Parameter(description = "手机号区域")
    private String            phoneRegion;

    /**
     * 密码
     */
    @NotEmpty(message = "密码不能为空")
    @Parameter(description = "密码")
    private String            password;

}
