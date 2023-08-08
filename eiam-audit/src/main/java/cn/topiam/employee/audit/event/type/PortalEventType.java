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

import cn.topiam.employee.audit.event.ConsoleResource;
import cn.topiam.employee.audit.event.Type;
import cn.topiam.employee.support.security.userdetails.UserType;
import static cn.topiam.employee.audit.event.PortalResource.*;

/**
 * 门户端审计事件类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/24 22:58
 */
public class PortalEventType {
    /**
     * 单点登录
     */
    public static Type APP_SSO                 = new Type("eiam:event:app:sso", "单点登录",
        MY_APP_RESOURCE, List.of(UserType.USER));
    /**
     * 退出应用
     */
    public static Type APP_SLO                 = new Type("eiam:event:app:login", "单点登出",
        MY_APP_RESOURCE, List.of(UserType.USER));

    /**
     * 绑定账号
     */
    public static Type BIND_IDP_USER           = new Type("eiam:event:account:bind_idp_user",
        "绑定 IDP", MY_ACCOUNT_RESOURCE, List.of(UserType.USER));
    /**
     * 解绑账号
     */
    public static Type UNBIND_IDP_USER         = new Type("eiam:event:account:unbind_idp_user",
        "解绑 IDP", MY_ACCOUNT_RESOURCE, List.of(UserType.USER));

    /**
     * 修改账户信息
     */
    public static Type MODIFY_ACCOUNT_INFO     = new Type("eiam:event:account:update_account_info",
        "修改账户", MY_ACCOUNT_RESOURCE, List.of(UserType.USER));

    /**
     * 修改邮箱
     */
    public static Type MODIFY_USER_EMAIL       = new Type("eiam:event:account:update_email", "修改邮箱",
        MY_ACCOUNT_RESOURCE, List.of(UserType.USER));
    /**
     * 修改手机号
     */
    public static Type MODIFY_USER_PHONE       = new Type("eiam:event:account:update_phone",
        "修改手机号", MY_ACCOUNT_RESOURCE, List.of(UserType.USER));
    /**
     * 修改密码
     */
    public static Type MODIFY_USER_PASSWORD    = new Type("eiam:event:account:update_password",
        "修改密码", MY_ACCOUNT_RESOURCE, List.of(UserType.USER));

    /**
     * 绑定MFA
     */
    public static Type BIND_MFA                = new Type("eiam:event:account:bind_maf", "绑定 MFA",
        MY_ACCOUNT_RESOURCE, List.of(UserType.USER));

    /**
     * 准备修改密码
     */
    public static Type PREPARE_MODIFY_PASSWORD = new Type(
        "eiam:event:account:prepare_modify_password", "准备修改密码", MY_ACCOUNT_RESOURCE,
        List.of(UserType.USER));

    /**
     * 准备修改手机
     */
    public static Type PREPARE_MODIFY_PHONE    = new Type("eiam:event:account:prepare_modify_phone",
        "准备修改手机", MY_ACCOUNT_RESOURCE, List.of(UserType.USER));

    /**
     * 准备修改邮箱
     */
    public static Type PREPARE_MODIFY_EMAIL    = new Type("eiam:event:account:prepare_modify_email",
        "准备修改邮箱", MY_ACCOUNT_RESOURCE, List.of(UserType.USER));

    /**
     * 准备绑定MFA
     */
    public static Type PREPARE_BIND_MFA        = new Type("eiam:event:account:prepare_bind_mfa",
        "准备绑定MFA", MY_ACCOUNT_RESOURCE, List.of(UserType.USER));

    /**
     * 解绑mfa
     */
    public static Type UNBIND_MFA              = new Type("eiam:event:account:unbind_maf", "解绑 MFA",
        MY_ACCOUNT_RESOURCE, List.of(UserType.USER));

    /**
     * 登录门户
     */
    public static Type LOGIN_PORTAL            = new Type("eiam:event:login:portal", "登录门户",
        ConsoleResource.AUTHENTICATION_RESOURCE, List.of(UserType.USER));

    /**
     * 退出门户
     */
    public static Type LOGOUT_PORTAL           = new Type("eiam:event:logout:portal", "退出门户",
        ConsoleResource.AUTHENTICATION_RESOURCE, List.of(UserType.USER));
}
