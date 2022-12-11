/*
 * eiam-audit - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.audit.event.type;

import java.util.List;

import cn.topiam.employee.common.enums.UserType;
import static cn.topiam.employee.audit.event.type.Resource.AUTHENTICATION_RESOURCE;

/**
 * 认证资源
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/24 22:59
 */
public class AuthenticationEventType {

    /**
     * 添加身份提供商
     */
    public static Type ADD_IDP        = new Type("eiam:event:idp_add", "添加身份提供商",
        AUTHENTICATION_RESOURCE, List.of(UserType.ADMIN));
    /**
     * 编辑身份提供商
     */
    public static Type UPDATE_IDP     = new Type("eiam:event:idp_update", "修改身份提供商",
        AUTHENTICATION_RESOURCE, List.of(UserType.ADMIN));
    /**
     * 启用身份提供商
     */
    public static Type ENABLE_IDP     = new Type("eiam:event:idp_enabled", "启用身份提供商",
        AUTHENTICATION_RESOURCE, List.of(UserType.ADMIN));
    /**
     * 禁用身份提供商
     */
    public static Type DISABLE_IDP    = new Type("eiam:event:idp_disabled", "禁用身份提供商",
        AUTHENTICATION_RESOURCE, List.of(UserType.ADMIN));
    /**
     * 删除身份提供商
     */
    public static Type DELETE_IDP     = new Type("eiam:event:idp_delete", "删除身份提供商",
        AUTHENTICATION_RESOURCE, List.of(UserType.ADMIN));

    /**
     * 登录控制台
     */
    public static Type LOGIN_CONSOLE  = new Type("eiam:event:login:console", "登录控制台",
        AUTHENTICATION_RESOURCE, List.of(UserType.ADMIN, UserType.USER));

    /**
     * 登录门户
     */
    public static Type LOGIN_PORTAL   = new Type("eiam:event:login:portal", "登录门户",
        AUTHENTICATION_RESOURCE, List.of(UserType.ADMIN, UserType.USER));

    /**
     * 退出控制台
     */
    public static Type LOGOUT_CONSOLE = new Type("eiam:event:logout:console", "退出控制台",
        AUTHENTICATION_RESOURCE, List.of(UserType.ADMIN, UserType.USER));

    /**
     * 退出门户
     */
    public static Type LOGOUT_PORTAL  = new Type("eiam:event:logout:portal", "退出门户",
        AUTHENTICATION_RESOURCE, List.of(UserType.ADMIN, UserType.USER));
}
