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
package cn.topiam.employee.console.pojo.save.setting;

import java.io.Serial;
import java.io.Serializable;

import cn.topiam.employee.common.message.enums.MailProvider;
import cn.topiam.employee.common.message.enums.MailSafetyType;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 保存邮件服务商配置入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/11 21:27
 */
@Data
@Schema(description = "保存邮件服务商配置入参")
public class MailProviderSaveParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -6723117700517052520L;
    /**
     * 平台
     */
    @Schema(description = "提供商")
    @NotNull(message = "邮件提供商不能为空")
    private MailProvider      provider;
    /**
     * smtp地址
     */
    @Schema(description = "smtp地址")
    private String            smtpUrl;

    /**
     * 端口
     */
    @Schema(description = "端口")
    private Integer           port;

    /**
     * 安全验证
     */
    @Schema(description = "安全验证")
    private MailSafetyType    safetyType;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String            username;

    /**
     * 密码
     */
    @Schema(description = "密码")
    private String            secret;

}
