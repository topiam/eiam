/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审计资源
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/29 21:07
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class ConsoleResource extends Resource {
    /**
     * 资源编码
     */
    private String code;
    /**
     * 资源名称
     */
    private String name;

    @Override
    public String toString() {
        return String.format("[%s](%s)", name, code);
    }

    /**
     * 组织与用户
     */
    public static ConsoleResource ORG_ACCOUNT_RESOURCE     = new ConsoleResource(
        "eiam:event:resource:org_account", "组织与用户");

    /**
     * 用户组管理
     */
    public static ConsoleResource USER_GROUP_RESOURCE      = new ConsoleResource(
        "eiam:event:resource:user_group", "用户组管理");

    /**
     * 身份源管理
     */
    public static ConsoleResource IDENTITY_SOURCE_RESOURCE = new ConsoleResource(
        "eiam:event:resource:identity_source", "身份源管理");

    /**
     * 认证
     */
    public static ConsoleResource AUTHENTICATION_RESOURCE  = new ConsoleResource(
        "eiam:event:resource:authentication", "认证管理");

    /**
     * 身份提供商
     */
    public static ConsoleResource IDP_RESOURCE             = new ConsoleResource(
        "eiam:event:resource:idp", "身份提供商");

    /**
     * 应用
     */
    public static ConsoleResource APP_RESOURCE             = new ConsoleResource(
        "eiam:event:resource:application", "应用管理");

    /**
     * 通用安全
     */
    public static ConsoleResource SECURITY_RESOURCE        = new ConsoleResource(
        "eiam:event:resource:security", "通用安全");

    /**
     * 密码策略
     */
    public static ConsoleResource PASSWORD_POLICY_RESOURCE = new ConsoleResource(
        "eiam:event:resource:password_policy", "密码策略");

    /**
     * 系统管理员
     */
    public static ConsoleResource ADMINISTRATOR_RESOURCE   = new ConsoleResource(
        "eiam:event:resource:administrator", "系统管理员");

    /**
     * 消息设置
     */
    public static ConsoleResource MESSAGE_RESOURCE         = new ConsoleResource(
        "eiam:event:resource:message", "消息设置");

    /**
     * IP地理库
     */
    public static ConsoleResource GEO_LOCATION_RESOURCE    = new ConsoleResource(
        "eiam:event:resource:geo_location", "IP地理库");

    /**
     * 存储配置
     */
    public static ConsoleResource STORAGE_RESOURCE         = new ConsoleResource(
        "eiam:event:resource:storage", "存储配置");

    /**
     * 会话管理
     */
    public static ConsoleResource SESSION_RESOURCE         = new ConsoleResource(
        "eiam:event:resource:session", "会话管理");
}