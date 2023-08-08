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
package cn.topiam.employee.console.pojo.result.app;

import java.time.LocalDateTime;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AppAccountCreateParam 应用账户查询结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/24 22:13
 */
@Data
@Schema(description = "应用账户列表查询响应")
public class AppAccountListResult {

    /**
     * id
     */
    @Schema(description = "id")
    private String        id;

    /**
     * 应用ID
     */
    @Schema(description = "应用ID")
    private Long          appId;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String        appName;

    /**
     * 模板
     */
    @Schema(description = "应用模版")
    private String        appTemplate;

    /**
     * 协议
     */
    @Schema(description = "应用协议")
    private String        appProtocol;

    /**
     * 应用类型
     */
    @Schema(description = "应用类型")
    private String        appType;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long          userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String        username;

    /**
     * 账户名称
     */
    @Schema(description = "账户名称")
    private String        account;

    /**
     * 添加时间
     */
    @Schema(description = "添加时间")
    private LocalDateTime createTime;
}
