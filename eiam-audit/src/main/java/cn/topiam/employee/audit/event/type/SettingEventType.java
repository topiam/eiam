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
import static cn.topiam.employee.audit.event.type.Resource.SETTING_RESOURCE;

/**
 * 系统设置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/24 22:58
 */
public class SettingEventType {

    /**
     * 保存安全基础设置
     */
    public static Type SAVE_LOGIN_SECURITY_BASIC_SETTINGS         = new Type(
        "eiam:event:setting:save_security_basic", "保存安全基础设置", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 密码策略
     */
    public static Type SAVE_PASSWORD_POLICY_SETTINGS              = new Type(
        "eiam:event:setting:save_password_policy", "保存密码策略", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 保存多因素认证
     */
    public static Type SAVE_MFA_SETTINGS                          = new Type(
        "eiam:event:setting:save_mfa", "保存多因素认证", SETTING_RESOURCE, List.of(UserType.ADMIN));

    /**
     * 保存行为验证码
     */
    public static Type SAVE_CAPTCHA_PROVIDER                      = new Type(
        "eiam:event:setting:save_captcha_provider", "保存行为验证码", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 保存行为验证码
     */
    public static Type OFF_CAPTCHA_PROVIDER                       = new Type(
        "eiam:event:setting:off_captcha_provider", "禁用行为验证码", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 添加管理员
     */
    public static Type ADD_ADMINISTRATOR                          = new Type(
        "eiam:event:setting:add_administrator", "添加管理员", SETTING_RESOURCE, List.of(UserType.ADMIN));

    /**
     * 删除管理员
     */
    public static Type DELETE_ADMINISTRATOR                       = new Type(
        "eiam:event:setting:delete_administrator", "删除管理员", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 修改管理员
     */
    public static Type UPDATE_ADMINISTRATOR                       = new Type(
        "eiam:event:setting:update_administrator", "修改管理员", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 启用管理员
     */
    public static Type ENABLE_ADMINISTRATOR                       = new Type(
        "eiam:event:setting:enable_administrator", "启用管理员", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 禁用管理员
     */
    public static Type DISABLE_ADMINISTRATOR                      = new Type(
        "eiam:event:setting:disable_administrator", "禁用管理员", SETTING_RESOURCE,
        List.of(UserType.ADMIN));
    /**
     * 重置管理员密码
     */
    public static Type RESET_ADMINISTRATOR_PASSWORD               = new Type(
        "eiam:event:setting:reset_administrator_password", "重置管理员密码", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 开启自定义修改密码邮件模板
     */
    public static Type ON_CUSTOMIZE_CHANGE_PASSWORD_MAIL          = new Type(
        "eiam:event:setting:on_customize_change_password_mail", "开启自定义修改密码邮件模板", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 开启自定义重置密码邮件模板
     */
    public static Type ON_CUSTOMIZE_RESET_PASSWORD_MAIL           = new Type(
        "eiam:event:setting:on_customize_reset_password_mail", "开启自定义重置密码邮件模板", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 开启自定义确认重置密码邮件模板
     */
    public static Type ON_CUSTOMIZE_CONFIRM_RESET_PASSWORD_MAIL   = new Type(
        "eiam:event:setting:on_customize_confirm_reset_password_mail", "开启自定义确认重置密码邮件模板",
        SETTING_RESOURCE, List.of(UserType.ADMIN));

    /**
     * 开启自定义验证邮件模板
     */
    public static Type ON_CUSTOMIZE_VERIFY_MAIL                   = new Type(
        "eiam:event:setting:on_customize_verify_mail", "开启自定义验证邮件模板", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 开启自定义欢迎邮件模板
     */
    public static Type ON_CUSTOMIZE_WELCOME_MAIL                  = new Type(
        "eiam:event:setting:on_customize_welcome_mail", "开启自定义欢迎邮件模板", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 开启自定义修改绑定邮件模板
     */
    public static Type ON_CUSTOMIZE_MODIFY_BINDING_MAIL           = new Type(
        "eiam:event:setting:on_customize_modify_binding:_mail", "开启自定义修改绑定邮件模板", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 关闭自定义修改密码邮件模板
     */
    public static Type OFF_CUSTOMIZE_CHANGE_PASSWORD_MAIL         = new Type(
        "eiam:event:setting:off_customize_change_password_mail", "关闭自定义修改密码邮件模板", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 关闭自定义重置密码邮件模板
     */
    public static Type OFF_CUSTOMIZE_RESET_PASSWORD_MAIL          = new Type(
        "eiam:event:setting:off_customize_reset_password_mail", "关闭自定义重置密码邮件模板", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 关闭自定义确认重置密码邮件模板
     */
    public static Type OFF_CUSTOMIZE_CONFIRM_RESET_PASSWORD_MAIL  = new Type(
        "eiam:event:setting:off_customize_confirm_reset_password_mail", "关闭自定义确认重置密码邮件模板",
        SETTING_RESOURCE, List.of(UserType.ADMIN));

    /**
     * 关闭自定义验证邮件模板
     */
    public static Type OFF_CUSTOMIZE_VERIFY_MAIL                  = new Type(
        "eiam:event:setting:off_customize_verify_mail", "关闭自定义验证邮件模板", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 关闭自定义欢迎邮件模板
     */
    public static Type OFF_CUSTOMIZE_WELCOME_MAIL                 = new Type(
        "eiam:event:setting:off_customize_welcome_mail", "关闭自定义欢迎邮件模板", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 关闭自定义修改绑定邮件模板
     */
    public static Type OFF_CUSTOMIZE_MODIFY_BINDING_MAIL          = new Type(
        "eiam:event:setting:off_customize_modify_binding_mail", "关闭自定义修改绑定邮件模板", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 保存自定义修改密码邮件模板
     */
    public static Type SAVE_CUSTOMIZE_CHANGE_PASSWORD_MAIL        = new Type(
        "eiam:event:setting:save_customize_change_password_mail", "保存自定义修改密码邮件模板", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 保存自定义重置密码邮件模板
     */
    public static Type SAVE_CUSTOMIZE_RESET_PASSWORD_MAIL         = new Type(
        "eiam:event:setting:save_customize_reset_password_mail", "保存自定义重置密码邮件模板", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 保存自定义确认重置密码邮件模板
     */
    public static Type SAVE_CUSTOMIZE_CONFIRM_RESET_PASSWORD_MAIL = new Type(
        "eiam:event:setting:save_customize_confirm_reset_password_mail", "保存自定义确认重置密码邮件模板",
        SETTING_RESOURCE, List.of(UserType.ADMIN));

    /**
     * 保存自定义验证邮件模板
     */
    public static Type SAVE_CUSTOMIZE_VERIFY_MAIL                 = new Type(
        "eiam:event:setting:save_customize_verify_mail", "保存自定义验证邮件模板", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 保存自定义欢迎邮件模板
     */
    public static Type SAVE_CUSTOMIZE_WELCOME_MAIL                = new Type(
        "eiam:event:setting:save_customize_welcome_mail", "保存自定义欢迎邮件模板", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 保存自定义修改绑定邮件模板
     */
    public static Type SAVE_CUSTOMIZE_MODIFY_BINDING_MAIL         = new Type(
        "eiam:event:setting:save_customize_modify_binding_mail", "保存自定义修改绑定邮件模板", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 开启邮件服务
     */
    public static Type ON_MAIL_SERVICE                            = new Type(
        "eiam:event:setting:on_mail_service", "开启邮件服务", SETTING_RESOURCE, List.of(UserType.ADMIN));

    /**
     * 关闭邮件服务
     */
    public static Type OFF_MAIL_SERVICE                           = new Type(
        "eiam:event:setting:off_mail_service", "关闭邮件服务", SETTING_RESOURCE, List.of(UserType.ADMIN));

    /**
     * 保存邮件服务
     */
    public static Type SAVE_MAIL_SERVICE                          = new Type(
        "eiam:event:setting:save_mail_service", "保存邮件服务", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 开启短信验证服务
     */
    public static Type ON_SMS_SERVICE                             = new Type(
        "eiam:event:setting:on_sms_verify_service", "开启短信验证服务", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 关闭短信验证服务
     */
    public static Type OFF_SMS_SERVICE                            = new Type(
        "eiam:event:setting:off_sms_verify_service", "关闭短信验证服务", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 保存短信验证服务
     */
    public static Type SAVE_SMS_SERVICE                           = new Type(
        "eiam:event:setting:save_sms_verify_service", "保存短信验证服务", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 开启存储服务
     */
    public static Type ON_STORAGE_SERVICE                         = new Type(
        "eiam:event:setting:on_storage_service", "开启存储服务", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 关闭存储服务
     */
    public static Type OFF_STORAGE_SERVICE                        = new Type(
        "eiam:event:setting:off_storage_service", "关闭存储服务", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 保存存储服务
     */
    public static Type SAVE_STORAGE_SERVICE                       = new Type(
        "eiam:event:setting:save_storage_service", "保存存储服务", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 开启地理位置服务
     */
    public static Type ON_GEO_LOCATION_SERVICE                    = new Type(
        "eiam:event:setting:on_geoip_service", "开启地理位置服务", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 关闭地理位置服务
     */
    public static Type OFF_GEO_LOCATION_SERVICE                   = new Type(
        "eiam:event:setting:off_geoip_service", "关闭地理位置服务", SETTING_RESOURCE,
        List.of(UserType.ADMIN));

    /**
     * 保存地理位置服务
     */
    public static Type SAVE_GEO_LOCATION_SERVICE                  = new Type(
        "eiam:event:setting:save_geoip_service", "保存地理位置服务", SETTING_RESOURCE,
        List.of(UserType.ADMIN));
}
