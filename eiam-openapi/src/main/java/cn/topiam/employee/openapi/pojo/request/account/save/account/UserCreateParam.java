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
package cn.topiam.employee.openapi.pojo.request.account.save.account;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import cn.topiam.employee.common.enums.ListEnumDeserializer;
import cn.topiam.employee.common.enums.MessageNoticeChannel;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 用户创建请求入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/11 23:16
 */
@Data
@Schema(description = "创建用户入参")
public class UserCreateParam implements Serializable {
    @Serial
    private static final long        serialVersionUID = -6044649488381303849L;
    /**
     * 组织机构
     */
    @Schema(description = "组织机构")
    @NotBlank(message = "组织机构不能为空")
    private String                   organizationId;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String                   username;

    /**
     * 密码
     */
    @Schema(description = "登录密码")
    @NotBlank(message = "登录密码不能为空")
    private String                   password;

    /**
     * 邮箱
     */
    @Email
    @Schema(description = "邮箱")
    private String                   email;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String                   phone;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Schema(description = "姓名")
    private String                   fullName;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String                   nickName;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String                   remark;

    /**
     * 过期日期
     */
    @Schema(description = "过期日期")
    private LocalDate                expireDate;

    /**
     * 密码初始化配置
     */
    @Schema(description = "密码初始化配置")
    private PasswordInitializeConfig passwordInitializeConfig;

    @Data
    public static class PasswordInitializeConfig implements Serializable {
        /**
         * 启用通知
         */
        @Schema(description = "启用通知")
        private Boolean                    enableNotice = false;

        /**
         * 消息类型
         */
        @Schema(description = "消息类型")
        @JsonDeserialize(using = ListEnumDeserializer.class)
        private List<MessageNoticeChannel> noticeChannels;
    }
}
