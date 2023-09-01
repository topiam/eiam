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

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.audit.event.Resource;
import cn.topiam.employee.audit.event.Type;
import cn.topiam.employee.support.security.userdetails.UserType;
import cn.topiam.employee.support.web.converter.EnumConvert;

import lombok.Getter;

/**
 * 事件类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/29 21:00
 */
@Getter
public enum EventType {
                       /**
                        * 登录门户
                        */
                       LOGIN_PORTAL(PortalEventType.LOGIN_PORTAL),

                       /**
                        * 登录控制台
                        */
                       LOGIN_CONSOLE(AuthenticationEventType.LOGIN_CONSOLE),

                       /**
                        * 退出门户
                        */
                       LOGOUT_PORTAL_PORTAL(PortalEventType.LOGOUT_PORTAL),

                       /**
                        * 退出控制台
                        */
                       LOGOUT_CONSOLE(AuthenticationEventType.LOGOUT_CONSOLE),
                       /**
                        * 创建组织
                        */
                       CREATE_ORG(AccountEventType.CREATE_ORG),
                       /**
                        * 编辑组织
                        */
                       UPDATE_ORG(AccountEventType.UPDATE_ORG),
                       /**
                        * 删除组织
                        */
                       DELETE_ORG(AccountEventType.DELETE_ORGANIZATION),
                       /**
                        * 移动组织
                        */
                       MOVE_ORGANIZATION(AccountEventType.MOVE_ORGANIZATION),

                       /**
                        * 创建用户
                        */
                       CREATE_USER(AccountEventType.CREATE_USER),

                       /**
                        * 编辑用户
                        */
                       UPDATE_USER(AccountEventType.UPDATE_USER),

                       /**
                        * 删除用户
                        */
                       DELETE_USER(AccountEventType.DELETE_USER),

                       /**
                        * 启用用户
                        */
                       ENABLE_USER(AccountEventType.ENABLE_USER),
                       /**
                        * 禁用用户
                        */
                       DISABLE_USER(AccountEventType.DISABLE_USER),

                       /**
                        * 用户离职
                        */
                       USER_RESIGN(AccountEventType.USER_RESIGN),

                       /**
                        * 修改手机号
                        */
                       MODIFY_USER_PHONE(AccountEventType.MODIFY_USER_PHONE),
                       /**
                        * 修改密码
                        */
                       MODIFY_USER_PASSWORD(AccountEventType.MODIFY_USER_PASSWORD),

                       /**
                        * 修改账户信息
                        */
                       MODIFY_ACCOUNT_INFO_PORTAL(PortalEventType.MODIFY_ACCOUNT_INFO),
                       /**
                        * 修改密码
                        */
                       MODIFY_USER_PASSWORD_PORTAL(PortalEventType.MODIFY_USER_PASSWORD),
                       /**
                        * 修改手机号
                        */
                       MODIFY_USER_PHONE_PORTAL(PortalEventType.MODIFY_USER_PHONE),

                       /**
                        * 修改邮箱
                        */
                       MODIFY_USER_EMAIL_PORTAL(PortalEventType.MODIFY_USER_EMAIL),
                       /**
                        * 绑定MFA
                        */
                       BIND_MFA(PortalEventType.BIND_MFA),

                       /**
                        * 解绑MFA
                        */
                       UNBIND_MFA(PortalEventType.UNBIND_MFA),
                       /**
                        * 绑定账号
                        */
                       BIND_IDP_USER(PortalEventType.BIND_IDP_USER),
                       /**
                        * 解绑账号
                        */
                       UNBIND_IDP_USER(PortalEventType.UNBIND_IDP_USER),

                       /**
                        * 准备修改密码
                        */
                       PREPARE_MODIFY_PASSWORD(PortalEventType.PREPARE_MODIFY_PASSWORD),

                       /**
                        * 准备修改手机
                        */
                       PREPARE_MODIFY_PHONE(PortalEventType.PREPARE_MODIFY_PHONE),

                       /**
                        * 准备修改邮箱
                        */
                       PREPARE_MODIFY_EMAIL(PortalEventType.PREPARE_MODIFY_EMAIL),

                       /**
                        * 准备绑定MFA
                        */
                       PREPARE_BIND_MFA(PortalEventType.PREPARE_BIND_MFA),

                       /**
                        * 创建用户组
                        */
                       CREATE_USER_GROUP(AccountEventType.CREATE_USER_GROUP),

                       /**
                        * 修改用户组
                        */
                       UPDATE_USER_GROUP(AccountEventType.UPDATE_USER_GROUP),

                       /**
                        * 删除用户组
                        */
                       DELETE_USER_GROUP(AccountEventType.DELETE_USER_GROUP),

                       /**
                        * 添加用户组成员
                        */
                       ADD_USER_GROUP_MEMBER(AccountEventType.ADD_USER_GROUP_MEMBER),

                       /**
                        * 移除用户组成员
                        */
                       REMOVE_USER_GROUP_MEMBER(AccountEventType.REMOVE_USER_GROUP_MEMBER),

                       /**
                        * 创建动态用户组
                        */
                       CREATE_USER_DYNAMIC_GROUP(AccountEventType.CREATE_USER_DYNAMIC_GROUP),

                       /**
                        * 修改动态用户组
                        */
                       UPDATE_USER_DYNAMIC_GROUP(AccountEventType.UPDATE_USER_DYNAMIC_GROUP),

                       /**
                        * 删除动态用户组
                        */
                       DELETE_USER_DYNAMIC_GROUP(AccountEventType.DELETE_USER_DYNAMIC_GROUP),

                       /**
                        * 修改动态用户组过滤规则
                        */
                       UPDATE_USER_DYNAMIC_GROUP_FILTER_RULE(AccountEventType.UPDATE_USER_DYNAMIC_GROUP_FILTER_RULE),

                       /**
                        * 创建身份源
                        */
                       CREATE_IDENTITY_RESOURCE(AccountEventType.CREATE_IDENTITY_RESOURCE),

                       /**
                        * 修改身份源
                        */
                       UPDATE_IDENTITY_RESOURCE(AccountEventType.UPDATE_IDENTITY_RESOURCE),

                       /**
                        * 删除身份源
                        */
                       DELETE_IDENTITY_RESOURCE(AccountEventType.DELETE_IDENTITY_RESOURCE),

                       /**
                        * 启用身份源
                        */
                       ENABLE_IDENTITY_RESOURCE(AccountEventType.ENABLE_IDENTITY_RESOURCE),

                       /**
                        * 禁用身份源
                        */
                       DISABLE_IDENTITY_RESOURCE(AccountEventType.DISABLE_IDENTITY_RESOURCE),
                       /**
                        * 身份源同步
                        */
                       IDENTITY_RESOURCE_SYNC(AccountEventType.IDENTITY_RESOURCE_SYNC),
                       /**
                        * 添加身份提供商
                        */
                       ADD_IDP(AuthenticationEventType.ADD_IDP),
                       /**
                        * 编辑身份提供商
                        */
                       UPDATE_IDP(AuthenticationEventType.UPDATE_IDP),

                       /**
                        * 删除认证提供商
                        */
                       DELETE_IDP(AuthenticationEventType.DELETE_IDP),
                       /**
                        * 启用认证提供商
                        */
                       ENABLE_IDP(AuthenticationEventType.ENABLE_IDP),
                       /**
                        * 禁用认证提供商
                        */
                       DISABLE_IDP(AuthenticationEventType.DISABLE_IDP),

                       /**
                        * 单点登录
                        */
                       APP_SSO(PortalEventType.APP_SSO),
                       /**
                        * 单点登出
                        */
                       APP_SLO(PortalEventType.APP_SLO),
                       /**
                        * 添加应用
                        */
                       ADD_APP(AppEventType.ADD_APP),
                       /**
                        * 启用应用
                        */
                       ENABLE_APP(AppEventType.ENABLE_APP),
                       /**
                        * 禁用应用
                        */
                       DISABLE_APP(AppEventType.DISABLE_APP),
                       /**
                        * 修改应用
                        */
                       UPDATE_APP(AppEventType.UPDATE_APP),
                       /**
                        * 保存应用配置
                        */
                       SAVE_APP_CONFIG(AppEventType.SAVE_APP_CONFIG),
                       /**
                        * 删除应用
                        */
                       DELETE_APP(AppEventType.DELETE_APP),
                       /**
                        * 应用授权
                        */
                       APP_AUTHORIZATION(AppEventType.APP_ACCESS_POLICY),
                       /**
                        * 删除应用授权
                        */
                       APP_CANCEL_ACCESS_POLICY(AppEventType.APP_CANCEL_ACCESS_POLICY),
                       /**
                        * 添加应用账户
                        */
                       ADD_APP_ACCOUNT(AppEventType.ADD_APP_ACCOUNT),
                       /**
                        * 删除应用账户
                        */
                       DELETE_APP_ACCOUNT(AppEventType.DELETE_APP_ACCOUNT),
                       /**
                        * 保存SSO配置
                        */
                       SAVE_SSO_CONFIG(AppEventType.SAVE_SSO_CONFIG),

                       /**
                        * 保存应用资源
                        */
                       SAVE_APP_PERMISSION_RESOURCE(AppEventType.SAVE_APP_PERMISSION_RESOURCE),
                       /**
                        * 修改应用资源
                        */
                       UPDATE_APP_PERMISSION_RESOURCE(AppEventType.UPDATE_APP_PERMISSION_RESOURCE),
                       /**
                        * 删除应用资源
                        */
                       DELETE_APP_PERMISSION_RESOURCE(AppEventType.DELETE_APP_PERMISSION_RESOURCE),
                       /**
                        * 启用应用资源
                        */
                       ENABLE_APP_PERMISSION_RESOURCE(AppEventType.ENABLE_APP_PERMISSION_RESOURCE),
                       /**
                        * 禁用应用资源
                        */
                       DISABLE_APP_PERMISSION_RESOURCE(AppEventType.DISABLE_APP_PERMISSION_RESOURCE),
                       /**
                        * 删除用户身份提供商绑定
                        */
                       DELETE_USER_IDP_BIND(AppEventType.DELETE_USER_IDP_BIND),

                       /**
                        * 添加应用角色
                        */
                       SAVE_APP_PERMISSION_ROLE(AppEventType.SAVE_APP_PERMISSION_ROLE),

                       /**
                        * 修改应用角色
                        */
                       UPDATE_APP_PERMISSION_ROLE(AppEventType.UPDATE_APP_PERMISSION_ROLE),

                       /**
                        * 删除应用角色
                        */
                       DELETE_APP_PERMISSION_ROLE(AppEventType.DELETE_APP_PERMISSION_ROLE),
                       /**
                        * 保存安全基础设置
                        */
                       SAVE_LOGIN_SECURITY_BASIC_SETTINGS(SettingEventType.SAVE_LOGIN_SECURITY_BASIC_SETTINGS),
                       /**
                        * 保存安全防御策略
                        */
                       SAVE_SECURITY_POLICY_SETTINGS(SettingEventType.SAVE_SECURITY_POLICY_SETTINGS),

                       /**
                        * 保存密码策略
                        */
                       SAVE_PASSWORD_POLICY(SettingEventType.SAVE_PASSWORD_POLICY),

                       /**
                        * 修改密码策略
                        */
                       UPDATE_PASSWORD_POLICY(SettingEventType.UPDATE_PASSWORD_POLICY),

                       /**
                        * 删除密码策略
                        */
                       DELETE_PASSWORD_POLICY(SettingEventType.DELETE_PASSWORD_POLICY),
                       /**
                        * 启用密码策略
                        */
                       ENABLE_PASSWORD_POLICY(SettingEventType.ENABLE_PASSWORD_POLICY),

                       /**
                        * 禁用密码策略
                        */
                       DISABLE_PASSWORD_POLICY(SettingEventType.DISABLE_PASSWORD_POLICY),

                       /**
                        * 更新密码策略优先级
                        */
                       SORT_PASSWORD_POLICY(SettingEventType.SORT_PASSWORD_POLICY),
                       /**
                        * 添加管理员
                        */
                       ADD_ADMINISTRATOR(SettingEventType.ADD_ADMINISTRATOR),
                       /**
                        * 修改管理员
                        */
                       UPDATE_ADMINISTRATOR(SettingEventType.UPDATE_ADMINISTRATOR),
                       /**
                        * 删除管理员
                        */
                       DELETE_ADMINISTRATOR(SettingEventType.DELETE_ADMINISTRATOR),

                       /**
                        * 启用管理员
                        */
                       ENABLE_ADMINISTRATOR(SettingEventType.ENABLE_ADMINISTRATOR),
                       /**
                        * 禁用管理员
                        */
                       DISABLE_ADMINISTRATOR(SettingEventType.DISABLE_ADMINISTRATOR),
                       /**
                        * 重置管理员密码
                        */
                       RESET_ADMINISTRATOR_PASSWORD(SettingEventType.RESET_ADMINISTRATOR_PASSWORD),

                       /**
                        * 添加角色
                        */
                       ADD_ADMINISTRATOR_ROLE(SettingEventType.ADD_ADMINISTRATOR_ROLE),
                       /**
                        * 修改角色
                        */
                       UPDATE_ADMINISTRATOR_ROLE(SettingEventType.UPDATE_ADMINISTRATOR_ROLE),
                       /**
                        * 删除角色
                        */
                       DELETE_ADMINISTRATOR_ROLE(SettingEventType.DELETE_ADMINISTRATOR_ROLE),

                       /**
                        * 启用角色
                        */
                       ENABLE_ADMINISTRATOR_ROLE(SettingEventType.ENABLE_ADMINISTRATOR_ROLE),
                       /**
                        * 禁用角色
                        */
                       DISABLE_ADMINISTRATOR_ROLE(SettingEventType.DISABLE_ADMINISTRATOR_ROLE),
                       /**
                        * 保存邮件模板
                        */
                       SAVE_MAIL_TEMPLATE(SettingEventType.SAVE_MAIL_TEMPLATE),
                       /**
                        * 保存邮件服务
                        */
                       SAVE_MAIL_SERVICE(SettingEventType.SAVE_MAIL_SERVICE),

                       /**
                        * 关闭邮件服务
                        */
                       OFF_MAIL_SERVICE(SettingEventType.OFF_MAIL_SERVICE),

                       /**
                        * 保存短信验证服务
                        */
                       SAVE_SMS_SERVICE(SettingEventType.SAVE_SMS_SERVICE),

                       /**
                        * 关闭短信验证服务
                        */
                       OFF_SMS_SERVICE(SettingEventType.OFF_SMS_SERVICE),

                       /**
                        * 保存地理位置服务
                        */
                       SAVE_GEO_LOCATION_SERVICE(SettingEventType.SAVE_GEO_LOCATION_SERVICE),

                       /**
                        * 关闭地理位置服务
                        */
                       OFF_GEO_LOCATION_SERVICE(SettingEventType.OFF_GEO_LOCATION_SERVICE),
                       /**
                        * 保存存储服务
                        */
                       SAVE_STORAGE_SERVICE(SettingEventType.SAVE_STORAGE_SERVICE),

                       /**
                        * 关闭存储服务
                        */
                       OFF_STORAGE_SERVICE(SettingEventType.OFF_STORAGE_SERVICE),

                       /**
                        * 下线会话
                        */
                       DOWN_LINE_SESSION(MonitorEventType.DOWN_LINE_SESSION),

                       /**
                        * 添加应用分组
                        */
                       ADD_APP_GROUP(AppEventType.ADD_APP_GROUP),
                       /**
                        * 启用应用分组
                        */
                       ENABLE_APP_GROUP(AppEventType.ENABLE_APP_GROUP),
                       /**
                        * 禁用应用分组
                        */
                       DISABLE_APP_GROUP(AppEventType.DISABLE_APP_GROUP),
                       /**
                        * 修改应用分组
                        */
                       UPDATE_APP_GROUP(AppEventType.UPDATE_APP_GROUP),
                       /**
                        * 删除应用分组
                        */
                       DELETE_APP_GROUP(AppEventType.DELETE_APP_GROUP);

    /**
     * code
     */
    @JsonValue
    private final String         code;
    /**
     * desc
     */
    private final String         desc;
    /**
     * 用户类型
     */
    private final List<UserType> userTypes;
    /**
     * 归属资源
     */
    private final Resource       resource;

    EventType(Type type) {
        this.code = type.getCode();
        this.desc = type.getName();
        this.resource = type.getResource();
        this.userTypes = type.getUserTypes();
    }

    /**
     * 获取审计类型
     *
     * @param code {@link String}
     * @return {@link EventType}
     */
    @EnumConvert
    public static EventType getType(String code) {
        EventType[] values = values();
        for (EventType status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        return null;
    }
}
