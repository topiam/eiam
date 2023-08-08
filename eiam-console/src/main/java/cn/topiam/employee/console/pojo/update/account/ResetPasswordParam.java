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
package cn.topiam.employee.console.pojo.update.account;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import cn.topiam.employee.common.enums.ListEnumDeserializer;
import cn.topiam.employee.common.enums.MessageNoticeChannel;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 重置密码入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/11 23:16
 */
@Data
@Schema(description = "重置密码入参")
public class ResetPasswordParam implements Serializable {
    @Serial
    private static final long   serialVersionUID = -6616249172773611157L;
    /**
     * ID
     */
    @Schema(description = "用户ID")
    @NotBlank(message = "用户ID不能为空")
    private String              id;

    /**
     * 密码
     */
    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String              password;

    /**
     * 重置密码配置
     */
    @Schema(description = "重置密码配置")
    private PasswordResetConfig passwordResetConfig;

    @Data
    public static class PasswordResetConfig implements Serializable {
        /**
         * 启用通知
         */
        @Schema(description = "启用通知")
        private Boolean                    enableNotice;

        /**
         * 消息类型
         */
        @Schema(description = "消息类型")
        @JsonDeserialize(using = ListEnumDeserializer.class)
        private List<MessageNoticeChannel> noticeChannels;
    }
}
