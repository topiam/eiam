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
 * 更改电子邮件入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/8 21:15
 */
@Data
@Schema(description = "更改电子邮件入参")
public class ChangeEmailRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 5681761697876754485L;

    /**
     * OTP
     */
    @NotEmpty(message = "OTP验证码不能为空")
    @Parameter(description = "OTP")
    private String            otp;

    /**
     * 邮箱
     */
    @NotEmpty(message = "邮箱不能为空")
    @Parameter(description = "邮箱")
    private String            email;

}
