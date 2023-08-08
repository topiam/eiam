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
package cn.topiam.employee.console.pojo.result.setting;

import java.io.Serial;
import java.io.Serializable;

import cn.topiam.employee.common.message.enums.MailProvider;
import cn.topiam.employee.common.message.enums.MailSafetyType;

import lombok.Builder;
import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 邮件服务商配置查询结果
 *
 * @author TopIAM
 */
@Data
@Builder
@Schema(description = "邮件服务商配置查询响应")
public class EmailProviderConfigResult implements Serializable {
    @Serial
    private static final long serialVersionUID = 8584300384703986791L;

    /**
     * smtp地址
     */
    @Parameter(description = "smtp地址")
    private String            smtpUrl;

    /**
     * 端口
     */
    @Parameter(description = "端口")
    private Integer           port;

    /**
     * 安全验证
     */
    @Parameter(description = "安全验证")
    private MailSafetyType    safetyType;

    /**
     * 用户名
     */
    @Parameter(description = "用户名")
    private String            username;

    /**
     * 秘钥
     */
    @Parameter(description = "秘钥")
    private String            secret;

    /**
     * 配置JSON串
     */
    @Parameter(description = "配置JSON串")
    private String            config;

    /**
     * 平台
     */
    @Parameter(description = "平台")
    private MailProvider      provider;

    /**
     * 描述
     */
    @Parameter(description = "描述")
    private String            desc;
    /**
     * 是否启用
     */
    @Parameter(description = "是否启用")
    private Boolean           enabled;
}
