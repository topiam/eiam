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
package cn.topiam.employee.audit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.support.web.converter.EnumConvert;

import lombok.Getter;

/**
 * 目标类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/27 23:46
 */
@Getter
public enum TargetType {

                        /**
                         * 用户
                         */
                        USER("user", "用户"),

                        /**
                         * 用户详情
                         */
                        USER_DETAIL("user_detail", "用户详情"),

                        /**
                         * 用户组
                         */
                        USER_GROUP("user_group", "用户组"),

                        /**
                         * 用户组成员
                         */
                        USER_GROUP_MEMBER("user_group_member", "用户组成员"),

                        /**
                         * 身份源
                         */
                        IDENTITY_SOURCE("identity_source", "身份源"),

                        /**
                         * 组织机构
                         */
                        ORGANIZATION("organization", "组织机构"),
                        /**
                         * 应用
                         */
                        APPLICATION("application", "应用"),

                        /**
                         * 应用账户
                         */
                        APPLICATION_ACCOUNT("application_account", "应用账户"),

                        /**
                         * 会话管理
                         */
                        SESSION("session", "会话管理"),

                        /**
                         * 应用权限
                         */
                        APP_PERMISSION_RESOURCE("app_permission_resource", "应用权限"),

                        /**
                         * 应用权限策略
                         */
                        APP_PERMISSION_POLICY("app_permission_policy", "应用权限策略"),

                        /**
                         * 应用权限策略
                         */
                        APP_PERMISSION_ROLE("app_permission_role", "应用权限角色"),
                        /**
                         * 系统角色
                         */
                        ROLE("role", "系统角色"),
                        /**
                         * 管理员
                         */
                        ADMINISTRATOR("administrator", "管理员"),
                        /**
                         * 密码策略
                         */
                        PASSWORD_POLICY("password_policy", "密码策略"),
                        /**
                         * 邮件模版
                         */
                        MAIL_TEMPLATE("mail_template", "邮件模版"),

                        /**
                         * 身份认证提供商
                         */
                        IDENTITY_PROVIDER("identity_provider", "身份认证提供商"),

                        /**
                         * 控制台
                         */
                        CONSOLE("console", "控制台"),

                        /**
                         * 门户端
                         */
                        PORTAL("portal", "门户端");

    @JsonValue
    private final String code;
    private final String desc;

    TargetType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取类型
     *
     * @param code {@link String}
     * @return {@link EventType}
     */
    @EnumConvert
    public static TargetType getType(String code) {
        TargetType[] values = values();
        for (TargetType status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        return null;
    }

}
