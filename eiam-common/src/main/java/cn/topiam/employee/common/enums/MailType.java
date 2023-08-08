/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.enums.BaseEnum;
import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * 邮件模板类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/13 23:02
 */
public enum MailType implements BaseEnum {

                                          /**
                                           * 绑定邮箱
                                           */
                                          BIND_EMAIL("bind_email", "绑定邮箱", "每当用户绑定邮箱时，都会发送此电子邮件。",
                                                     "您正在绑定邮箱，验证码为：${verify_code}", "系统账户",
                                                     getMailContentPath() + "bind-mail-content.html"),
                                          /**
                                           * 修改绑定邮件
                                           */
                                          UPDATE_BIND_MAIL("update_bind_mail", "修改绑定邮件",
                                                           "每当用户需要修改绑定邮箱、发送验证码时，都会发送此电子邮件。",
                                                           "您正在修改绑定邮箱，验证码为：${verify_code}", "系统账户",
                                                           getMailContentPath() + "update-bind-mail-content.html"),

                                          /**
                                           * 忘记密码
                                           */
                                          FORGET_PASSWORD("forget_password", "忘记密码",
                                                          "每当用户忘记密码时，都会发送此电子邮件。",
                                                          "您正在修改密码，验证码为：${verify_code}", "系统账户",
                                                          getMailContentPath() + "update-password-content.html"),
                                          /**
                                           * 修改密码
                                           */
                                          UPDATE_PASSWORD("update_password", "修改密码",
                                                          "每当用户要求更改密码时，都会发送此电子邮件。",
                                                          "您正在修改密码，验证码为：${verify_code}", "系统账户",
                                                          getMailContentPath() + "update-password-content.html"),
                                          /**
                                           * 重置密码
                                           */
                                          RESET_PASSWORD("reset_password", "重置密码",
                                                         "每当用户要求更改密码时，都会发送此电子邮件。",
                                                         "您正在重置密码，验证码为：${verify_code}", "系统账户",
                                                         getMailContentPath() + "reset-password-content.html"),
                                          /**
                                           * 重置密码成功
                                           */
                                          RESET_PASSWORD_CONFIRM("reset_password_confirm", "重置密码成功",
                                                                 "每当用户重置密码成功时，都会发送此电子邮件。",
                                                                 "您已成功重置密码，您重置后的密码为：${password} 如非本人操作请立即修改账户密码",
                                                                 "系统账户", getMailContentPath()
                                                                         + "reset-password-confirm-content.html"),

                                          /**
                                           * 登录验证
                                           */
                                          LOGIN("login", "登录验证", "每当用户选择邮箱验证码登录时，都会发送此电子邮件。",
                                                "您正在登录TopIAM系统，验证码为：${verify_code}，该验证码${expire_time}分钟内有效，请勿泄露于他人。",
                                                "系统账户",
                                                getMailContentPath() + "login-content.html"),

                                          //                                          /**
                                          //                                           * 验证邮箱
                                          //                                           */
                                          //                                          VERIFY_EMAIL("verify_email", "验证邮箱",
                                          //                                                       "用户用邮箱初次注册时会发送一封验证邮箱的链接，用户点击邮件内的网址即可完成验证。",
                                          //                                                       "请点击此链接进行验证： ${verify_link}", "系统账户",
                                          //                                                       getMailContentPath() + "verify-email-content.html"),
                                          /**
                                           * 二次验证
                                           */
                                          AGAIN_VERIFY("again_verify", "二次验证",
                                                       "管理员开启邮件二次认证后，用户使用账号密码登录，选择邮件二次认证时。",
                                                       "您正在进行二次认证，验证码为：${verify_code}，${expire_time}分钟内有效。",
                                                       "系统账户", getMailContentPath()
                                                               + "again-verify-mail-content.html"),
                                          /**
                                           * 欢迎邮件
                                           */
                                          WELCOME_MAIL("welcome_mail", "欢迎邮件",
                                                       "一旦用户验证其电子邮件地址，将发送此电子邮件。 如果验证电子邮件已关闭， 则会在用户第一次注册或登录时发送。",
                                                       "您已注册成功",

                                                       "系统账户", getMailContentPath()
                                                               + "welcome-mail-content.html"),

                                          /**
                                           * 密码过期提醒
                                           */
                                          PASSWORD_SOON_EXPIRED_REMIND("password_soon_expired_remind",
                                                                       "密码即将到期提醒",
                                                                       "用户距离上次修改密码有效期即将过期时，都会发送此电子邮件。",
                                                                       "您的密码将于${expire_days}天后过期, 请尽快修改密码。",
                                                                       "系统账户", getMailContentPath()
                                                                               + "password-soon-expired-content.html"),
                                          /**
                                           * 风险预警 暂不实现
                                           */
                                          WARNING("warning", "风险预警", "用户每次执行风险操作，都会发送此电子邮件。",
                                                  "您的账号存在风险操作, 验证码为：${verify_code}。", "系统账户",
                                                  getMailContentPath() + "warning-content.html");

    /**
     * code
     */
    @JsonValue
    private final String code;
    /**
     * 名称
     */
    private final String name;
    /**
     * desc
     */
    private final String desc;
    /**
     * 主题
     */
    private final String subject;
    /**
     * 发件人
     */
    private final String sender;
    /**
     * 内容
     */
    private final String content;

    MailType(String code, String name, String desc, String subject, String sender, String content) {
        this.code = code;
        this.name = name;
        this.desc = desc;
        this.subject = subject;
        this.sender = sender;
        this.content = content;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public String getContent() {
        return content;
    }

    public String getSubject() {
        return subject;
    }

    public String getName() {
        return name;
    }

    public String getSender() {
        return sender;
    }

    static String getMailContentPath() {
        return "mail/content/";
    }

    @EnumConvert
    public static MailType getType(String code) {
        MailType[] values = values();
        for (MailType status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        throw new NullPointerException("未找到该类型");
    }

    @Override
    public String toString() {
        return this.code;
    }

}
