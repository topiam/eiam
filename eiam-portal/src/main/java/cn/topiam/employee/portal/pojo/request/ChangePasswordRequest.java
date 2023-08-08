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

import cn.topiam.employee.common.enums.MessageNoticeChannel;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * 更改密码入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/8 21:15
 */
@Data
@Schema(description = "更改密码入参")
public class ChangePasswordRequest implements Serializable {

    @Serial
    private static final long    serialVersionUID = 5681761697876754485L;

    /**
     * 新密码
     */
    @NotEmpty(message = "新密码不能为空")
    @Parameter(description = "新密码")
    private String               newPassword;

    /**
     * 验证码
     */
    @NotEmpty(message = "验证码不能为空")
    @Parameter(description = "验证码")
    private String               verifyCode;

    /**
     * 消息类型
     */
    @NotNull(message = "消息类型不能为空")
    @Parameter(description = "消息类型")
    private MessageNoticeChannel channel;
}
