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
package cn.topiam.employee.audit.enums;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.audit.event.type.*;
import cn.topiam.employee.common.enums.UserType;
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
                       LOGIN_PORTAL(AuthenticationEventType.LOGIN_PORTAL),

                       /**
                        * 登录控制台
                        */
                       LOGIN_CONSOLE(AuthenticationEventType.LOGIN_CONSOLE),

                       /**
                        * 退出门户
                        */
                       LOGOUT_PORTAL(AuthenticationEventType.LOGOUT_PORTAL),

                       /**
                        * 退出控制台
                        */
                       LOGOUT_CONSOLE(AuthenticationEventType.LOGOUT_CONSOLE),

                       /**
                        * 注册
                        */
                       USER_REGISTER(AccountEventType.USER_REGISTER),

                       /**
                        * 修改账户信息
                        */
                       MODIFY_ACCOUNT_INFO(AccountEventType.MODIFY_ACCOUNT_INFO),

                       /**
                        * 修改邮箱
                        */
                       MODIFY_USER_EMAIL(AccountEventType.MODIFY_USER_EMAIL),

                       /**
                        * 修改手机号
                        */
                       MODIFY_USER_PHONE(AccountEventType.MODIFY_USER_PHONE),

                       /**
                        * 修改密码
                        */
                       MODIFY_USER_PASSWORD(AccountEventType.MODIFY_USER_PASSWORD),

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
                        * 禁用用户
                        */
                       DISABLE_USER(AccountEventType.DISABLE_USER),

                       /**
                        * 启用用户
                        */
                       ENABLE_USER(AccountEventType.ENABLE_USER),
                       /**
                        * 绑定账号
                        */
                       BIND_IDP_USER(AccountEventType.BIND_IDP_USER),
                       /**
                        * 解绑账号
                        */
                       UNBIND_IDP_USER(AccountEventType.UNBIND_IDP_USER),
                       /**
                        * 解绑应用用户
                        */
                       UNBIND_APPLICATION_USER(AccountEventType.UNBIND_APPLICATION_USER),
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
                        * 删除组织
                        */
                       MOVE_ORGANIZATION(AccountEventType.MOVE_ORGANIZATION),
                       /**
                        * 添加用户到组织
                        */
                       USER_ADD_ORG(AccountEventType.USER_ADD_ORG),
                       /**
                        * 用户转岗到其他组织
                        */
                       USER_TRANSFER_ORG(AccountEventType.USER_TRANSFER_ORG),
                       /**
                        * 从组织中移除用户
                        */
                       ORG_REMOVE_USER(AccountEventType.ORG_REMOVE_USER),
                       /**
                        * 登录应用
                        */
                       APP_SSO(AppEventType.APP_SSO),
                       /**
                        * 退出应用
                        */
                       SIGN_OUT_APP(AppEventType.SIGN_OUT_APP),
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
                       APP_DELETE_ACCESS_POLICY(AppEventType.APP_DELETE_ACCESS_POLICY),
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
                        * 添加身份提供商
                        */
                       ADD_IDP(AuthenticationEventType.ADD_IDP),
                       /**
                        * 编辑身份提供商
                        */
                       UPDATE_IDP(AuthenticationEventType.UPDATE_IDP),
                       /**
                        * 启用认证提供商
                        */
                       ENABLE_IDP(AuthenticationEventType.ENABLE_IDP),
                       /**
                        * 禁用认证提供商
                        */
                       DISABLE_IDP(AuthenticationEventType.DISABLE_IDP),

                       /**
                        * 删除认证提供商
                        */
                       DELETE_IDP(AuthenticationEventType.DELETE_IDP),

                       /**
                        * 保存安全基础设置
                        */
                       SAVE_LOGIN_SECURITY_BASIC_SETTINGS(SettingEventType.SAVE_LOGIN_SECURITY_BASIC_SETTINGS),
                       /**
                        * 密码策略
                        */
                       SAVE_PASSWORD_POLICY_SETTINGS(SettingEventType.SAVE_PASSWORD_POLICY_SETTINGS),

                       /**
                        * 多因素认证
                        */
                       SAVE_MFA_SETTINGS(SettingEventType.SAVE_MFA_SETTINGS),

                       /**
                        * 行为验证码
                        */
                       SAVE_CAPTCHA_PROVIDER(SettingEventType.SAVE_CAPTCHA_PROVIDER),

                       /**
                        * 禁用行为验证码
                        */
                       OFF_CAPTCHA_PROVIDER(SettingEventType.OFF_CAPTCHA_PROVIDER),

                       /**
                        * 添加管理员
                        */
                       ADD_ADMINISTRATOR(SettingEventType.ADD_ADMINISTRATOR),
                       /**
                        * 删除管理员
                        */
                       DELETE_ADMINISTRATOR(SettingEventType.DELETE_ADMINISTRATOR),
                       /**
                        * 修改管理员
                        */
                       UPDATE_ADMINISTRATOR(SettingEventType.UPDATE_ADMINISTRATOR),
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
                        * 开启自定义修改密码邮件模板
                        */
                       ON_CUSTOMIZE_CHANGE_PASSWORD_MAIL(SettingEventType.ON_CUSTOMIZE_CHANGE_PASSWORD_MAIL),

                       /**
                        * 开启自定义重置密码邮件模板
                        */
                       ON_CUSTOMIZE_RESET_PASSWORD_MAIL(SettingEventType.ON_CUSTOMIZE_RESET_PASSWORD_MAIL),

                       /**
                        * 开启自定义确认重置密码邮件模板
                        */
                       ON_CUSTOMIZE_CONFIRM_RESET_PASSWORD_MAIL(SettingEventType.ON_CUSTOMIZE_CONFIRM_RESET_PASSWORD_MAIL),

                       /**
                        * 开启自定义验证邮件模板
                        */
                       ON_CUSTOMIZE_VERIFY_MAIL(SettingEventType.ON_CUSTOMIZE_VERIFY_MAIL),
                       /**
                        * 开启自定义欢迎邮件模板
                        */
                       ON_CUSTOMIZE_WELCOME_MAIL(SettingEventType.ON_CUSTOMIZE_WELCOME_MAIL),
                       /**
                        * 开启自定义修改绑定邮件模板
                        */
                       ON_CUSTOMIZE_MODIFY_BINDING_MAIL(SettingEventType.ON_CUSTOMIZE_MODIFY_BINDING_MAIL),
                       /**
                        * 关闭自定义修改密码邮件模板
                        */
                       OFF_CUSTOMIZE_CHANGE_PASSWORD_MAIL(SettingEventType.OFF_CUSTOMIZE_CHANGE_PASSWORD_MAIL),

                       /**
                        * 关闭自定义重置密码邮件模板
                        */
                       OFF_CUSTOMIZE_RESET_PASSWORD_MAIL(SettingEventType.OFF_CUSTOMIZE_RESET_PASSWORD_MAIL),

                       /**
                        * 关闭自定义确认重置密码邮件模板
                        */
                       OFF_CUSTOMIZE_CONFIRM_RESET_PASSWORD_MAIL(SettingEventType.OFF_CUSTOMIZE_CONFIRM_RESET_PASSWORD_MAIL),

                       /**
                        * 关闭自定义验证邮件模板
                        */
                       OFF_CUSTOMIZE_VERIFY_MAIL(SettingEventType.OFF_CUSTOMIZE_VERIFY_MAIL),
                       /**
                        * 关闭自定义欢迎邮件模板
                        */
                       OFF_CUSTOMIZE_WELCOME_MAIL(SettingEventType.OFF_CUSTOMIZE_WELCOME_MAIL),
                       /**
                        * 关闭自定义修改绑定邮件模板
                        */
                       OFF_CUSTOMIZE_MODIFY_BINDING_MAIL(SettingEventType.OFF_CUSTOMIZE_MODIFY_BINDING_MAIL),

                       /**
                        * 保存自定义修改密码邮件模板
                        */
                       SAVE_CUSTOMIZE_CHANGE_PASSWORD_MAIL(SettingEventType.SAVE_CUSTOMIZE_CHANGE_PASSWORD_MAIL),

                       /**
                        * 保存自定义重置密码邮件模板
                        */
                       SAVE_CUSTOMIZE_RESET_PASSWORD_MAIL(SettingEventType.SAVE_CUSTOMIZE_RESET_PASSWORD_MAIL),

                       /**
                        * 保存自定义确认重置密码邮件模板
                        */
                       SAVE_CUSTOMIZE_CONFIRM_RESET_PASSWORD_MAIL(SettingEventType.SAVE_CUSTOMIZE_CONFIRM_RESET_PASSWORD_MAIL),

                       /**
                        * 保存自定义验证邮件模板
                        */
                       SAVE_CUSTOMIZE_VERIFY_MAIL(SettingEventType.SAVE_CUSTOMIZE_VERIFY_MAIL),
                       /**
                        * 保存自定义欢迎邮件模板
                        */
                       SAVE_CUSTOMIZE_WELCOME_MAIL(SettingEventType.SAVE_CUSTOMIZE_WELCOME_MAIL),
                       /**
                        * 保存自定义修改绑定邮件模板
                        */
                       SAVE_CUSTOMIZE_MODIFY_BINDING_MAIL(SettingEventType.SAVE_CUSTOMIZE_MODIFY_BINDING_MAIL),

                       /**
                        * 开启邮件服务
                        */
                       ON_MAIL_SERVICE(SettingEventType.ON_MAIL_SERVICE),
                       /**
                        * 关闭邮件服务
                        */
                       OFF_MAIL_SERVICE(SettingEventType.OFF_MAIL_SERVICE),

                       /**
                        * 保存邮件服务
                        */
                       SAVE_MAIL_SERVICE(SettingEventType.SAVE_MAIL_SERVICE),

                       /**
                        * 开启短信验证服务
                        */
                       ON_SMS_SERVICE(SettingEventType.ON_SMS_SERVICE),

                       /**
                        * 关闭短信验证服务
                        */
                       OFF_SMS_SERVICE(SettingEventType.OFF_SMS_SERVICE),

                       /**
                        * 保存短信验证服务
                        */
                       SAVE_SMS_SERVICE(SettingEventType.SAVE_SMS_SERVICE),

                       /**
                        * 开启存储服务
                        */
                       ON_STORAGE_SERVICE(SettingEventType.ON_STORAGE_SERVICE),
                       /**
                        * 关闭存储服务
                        */
                       OFF_STORAGE_SERVICE(SettingEventType.OFF_STORAGE_SERVICE),
                       /**
                        * 保存存储服务
                        */
                       SAVE_STORAGE_SERVICE(SettingEventType.SAVE_STORAGE_SERVICE),
                       /**
                        * 开启地理位置服务
                        */
                       ON_GEO_LOCATION_SERVICE(SettingEventType.ON_GEO_LOCATION_SERVICE),
                       /**
                        * 关闭地理位置服务
                        */
                       OFF_GEO_LOCATION_SERVICE(SettingEventType.OFF_GEO_LOCATION_SERVICE),
                       /**
                        * 保存地理位置服务
                        */
                       SAVE_GEO_LOCATION_SERVICE(SettingEventType.SAVE_GEO_LOCATION_SERVICE),

                       /**
                        * 下线会话
                        */
                       DOWN_LINE_SESSION(OtherEventType.DOWN_LINE_SESSION),

                       /**
                        * 批量下线会话
                        */
                       BATCH_DOWN_LINE_SESSION(OtherEventType.BATCH_DOWN_LINE_SESSION),

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
                       DELETE_USER_IDP_BIND(AppEventType.DELETE_APP_ACCOUNT),

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
                       DELETE_APP_PERMISSION_ROLE(AppEventType.DELETE_APP_PERMISSION_ROLE);

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
