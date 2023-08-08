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
package cn.topiam.employee.console.pojo.result.account;

import java.time.LocalDateTime;

import cn.topiam.employee.audit.enums.EventStatus;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户登录日志返回结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022年11月13日21:49:35
 */
@Data
@Schema(description = "用户登录日志返回响应")
public class UserLoginAuditListResult {

    /**
     * 应用名称
     */
    @Parameter(description = "应用名称")
    private String        appName;

    /**
     * 客户端IP
     */
    @Parameter(description = "客户端IP")
    private String        clientIp;

    /**
     * 登录结果
     */
    @Parameter(description = "浏览器")
    private String        browser;

    /**
     * 位置
     */
    @Parameter(description = "位置")
    private String        location;

    /**
     * 事件状态
     */
    @Parameter(description = "事件状态")
    private EventStatus   eventStatus;

    /**
     * 事件时间
     */
    @Parameter(description = "事件时间")
    private LocalDateTime eventTime;
}
