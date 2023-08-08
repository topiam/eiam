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
package cn.topiam.employee.audit.event.type;

import java.util.List;

import cn.topiam.employee.audit.event.Type;
import static cn.topiam.employee.audit.event.ConsoleResource.*;
import static cn.topiam.employee.support.security.userdetails.UserType.ADMIN;

/**
 * 账户资源
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/24 22:58
 */
public class AccountEventType {

    /**
     * 创建用户
     */
    public static Type CREATE_USER                           = new Type(
        "eiam:event:account:create_user", "创建用户", ORG_ACCOUNT_RESOURCE, List.of(ADMIN));
    /**
     * 编辑用户
     */
    public static Type UPDATE_USER                           = new Type(
        "eiam:event:account:update_user", "修改用户", ORG_ACCOUNT_RESOURCE, List.of(ADMIN));
    /**
     * 删除用户
     */
    public static Type DELETE_USER                           = new Type(
        "eiam:event:account:delete_user", "删除用户", ORG_ACCOUNT_RESOURCE, List.of(ADMIN));
    /**
     * 禁用用户
     */
    public static Type DISABLE_USER                          = new Type(
        "eiam:event:account:disabled_user", "禁用用户", ORG_ACCOUNT_RESOURCE, List.of(ADMIN));
    /**
     * 启用用户
     */
    public static Type ENABLE_USER                           = new Type(
        "eiam:event:account:enabled_user", "启用用户", ORG_ACCOUNT_RESOURCE, List.of(ADMIN));
    /**
     * 创建组织
     */
    public static Type CREATE_ORG                            = new Type(
        "eiam:event:account:create_organization", "创建组织", ORG_ACCOUNT_RESOURCE, List.of(ADMIN));
    /**
     * 编辑组织
     */
    public static Type UPDATE_ORG                            = new Type(
        "eiam:event:account:update_organization", "修改组织", ORG_ACCOUNT_RESOURCE, List.of(ADMIN));
    /**
     * 删除组织
     */
    public static Type DELETE_ORGANIZATION                   = new Type(
        "eiam:event:account:delete_organization", "删除组织", ORG_ACCOUNT_RESOURCE, List.of(ADMIN));

    /**
     * 移动组织
     */
    public static Type MOVE_ORGANIZATION                     = new Type(
        "eiam:event:account:move_organization", "移动组织", ORG_ACCOUNT_RESOURCE, List.of(ADMIN));

    /**
     * 用户离职
     */
    public static Type USER_RESIGN                           = new Type(
        "eiam:event:account:user_resign", "用户离职", ORG_ACCOUNT_RESOURCE, List.of(ADMIN));

    /**
     * 修改邮箱
     */
    public static Type MODIFY_USER_EMAIL                     = new Type(
        "eiam:event:account:update_email", "修改邮箱", ORG_ACCOUNT_RESOURCE, List.of(ADMIN));
    /**
     * 修改手机号
     */
    public static Type MODIFY_USER_PHONE                     = new Type(
        "eiam:event:account:update_phone", "修改手机号", ORG_ACCOUNT_RESOURCE, List.of(ADMIN));
    /**
     * 修改密码
     */
    public static Type MODIFY_USER_PASSWORD                  = new Type(
        "eiam:event:account:update_password", "修改密码", ORG_ACCOUNT_RESOURCE, List.of(ADMIN));

    /**
     * 创建用户组
     */
    public static Type CREATE_USER_GROUP                     = new Type(
        "eiam:event:account:create_user_group", "创建静态用户组", USER_GROUP_RESOURCE, List.of(ADMIN));
    /**
     * 编辑用户组
     */
    public static Type UPDATE_USER_GROUP                     = new Type(
        "eiam:event:account:update_user_group", "修改静态用户组", USER_GROUP_RESOURCE, List.of(ADMIN));
    /**
     * 删除用户组
     */
    public static Type DELETE_USER_GROUP                     = new Type(
        "eiam:event:account:delete_user_group", "删除静态用户组", USER_GROUP_RESOURCE, List.of(ADMIN));

    /**
     * 添加用户组成员
     */
    public static Type ADD_USER_GROUP_MEMBER                 = new Type(
        "eiam:event:account:add_user_group_member", "添加静态用户组成员", USER_GROUP_RESOURCE,
        List.of(ADMIN));
    /**
     * 移除用户组成员
     */
    public static Type REMOVE_USER_GROUP_MEMBER              = new Type(
        "eiam:event:account:remove_user_group_member", "移除静态用户组成员", USER_GROUP_RESOURCE,
        List.of(ADMIN));

    /**
     * 创建动态用户组
     */
    public static Type CREATE_USER_DYNAMIC_GROUP             = new Type(
        "eiam:event:account:create_user_dynamic_group", "创建动态用户组", USER_GROUP_RESOURCE,
        List.of(ADMIN));
    /**
     * 编辑动态用户组
     */
    public static Type UPDATE_USER_DYNAMIC_GROUP             = new Type(
        "eiam:event:account:update_user_dynamic_group", "修改动态用户组", USER_GROUP_RESOURCE,
        List.of(ADMIN));

    /**
     * 编辑动态用户组过滤规则
     */
    public static Type UPDATE_USER_DYNAMIC_GROUP_FILTER_RULE = new Type(
        "eiam:event:account:update_user_dynamic_group:filter_rule", "编辑动态用户组过滤规则",
        USER_GROUP_RESOURCE, List.of(ADMIN));

    /**
     * 删除动态用户组
     */
    public static Type DELETE_USER_DYNAMIC_GROUP             = new Type(
        "eiam:event:account:delete_user_dynamic_group", "删除动态用户组", USER_GROUP_RESOURCE,
        List.of(ADMIN));

    /**
     * 创建身份源
     */
    public static Type CREATE_IDENTITY_RESOURCE              = new Type(
        "eiam:event:account:create_identity_resource", "创建身份源", IDENTITY_SOURCE_RESOURCE,
        List.of(ADMIN));
    /**
     * 编辑身份源
     */
    public static Type UPDATE_IDENTITY_RESOURCE              = new Type(
        "eiam:event:account:update_identity_resource", "修改身份源", IDENTITY_SOURCE_RESOURCE,
        List.of(ADMIN));
    /**
     * 删除身份源
     */
    public static Type DELETE_IDENTITY_RESOURCE              = new Type(
        "eiam:event:account:delete_identity_resource", "删除身份源", IDENTITY_SOURCE_RESOURCE,
        List.of(ADMIN));

    /**
     * 启用身份源
     */
    public static Type ENABLE_IDENTITY_RESOURCE              = new Type(
        "eiam:event:account:enable_identity_resource", "启用身份源", IDENTITY_SOURCE_RESOURCE,
        List.of(ADMIN));

    /**
     * 禁用身份源
     */
    public static Type DISABLE_IDENTITY_RESOURCE             = new Type(
        "eiam:event:account:disable_identity_resource", "禁用身份源", IDENTITY_SOURCE_RESOURCE,
        List.of(ADMIN));

    /**
     * 禁用身份源
     */
    public static Type IDENTITY_RESOURCE_SYNC                = new Type(
        "eiam:event:account:identity_resource_sync", "身份源同步", IDENTITY_SOURCE_RESOURCE,
        List.of(ADMIN));

}
