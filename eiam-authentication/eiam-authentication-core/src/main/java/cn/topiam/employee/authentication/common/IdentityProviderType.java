/*
 * eiam-authentication-core - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.common;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static cn.topiam.employee.common.constant.AuthnConstants.AUTHN_PATH;
import static cn.topiam.employee.support.security.constant.SecurityConstants.LOGIN_PATH;

/**
 * @author TopIAM
 * Created by support@topiam.cn on 2022/12/31 21:18
 */
public final class IdentityProviderType {

    /**
     * 飞书
     */
    public static final IdentityProviderType FEISHU_OAUTH      = new IdentityProviderType(
        "feishu_oauth", "飞书认证", "通过飞书进行身份验证");

    /**
     * 钉钉
     */
    public static final IdentityProviderType DINGTALK_OAUTH    = new IdentityProviderType(
        "dingtalk_oauth", "钉钉认证", "通过钉钉进行身份认证");

    /**
     * 微信开放平台
     */
    public static final IdentityProviderType WECHAT            = new IdentityProviderType(
        "wechat_oauth", "微信扫码登录", "通过微信扫码进行身份认证");
    /**
     * 企业微信
     */
    public static final IdentityProviderType WECHAT_WORK_OAUTH = new IdentityProviderType(
        "wechatwork_oauth", "企业微信认证", "通过企业微信同步的用户可使用企业微信扫码登录进行身份认证");

    /**
     * Gitee
     */
    public static final IdentityProviderType GITEE_OAUTH       = new IdentityProviderType(
        "gitee_oauth", "Gitee", "通过Gitee进行身份认证");

    /**
     * QQ认证
     */
    public static final IdentityProviderType QQ_OAUTH          = new IdentityProviderType(
        "qq_oauth", "QQ认证", "通过QQ进行身份认证");

    /**
     * 用户名密码
     */
    public static final IdentityProviderType USERNAME_PASSWORD = new IdentityProviderType(
        "username_password", "用户名密码认证", "通过用户名密码进行身份认证");

    /**
     * 短信验证码
     */
    public static final IdentityProviderType SMS               = new IdentityProviderType("sms",
        "短信验证码认证", "通过短信验证码进行身份认证");

    /**
     * 邮件验证码
     */
    public static final IdentityProviderType MAIL              = new IdentityProviderType("mail",
        "邮件验证码认证", "通过邮件验证码进行身份认证");

    /**
     * GITHUB认证
     */
    public static final IdentityProviderType GITHUB_OAUTH      = new IdentityProviderType(
        "github_oauth", "GITHUB认证", "通过GITHUB进行身份认证");

    /**
     * 支付宝认证
     */
    public static final IdentityProviderType ALIPAY_OAUTH      = new IdentityProviderType(
        "alipay_oauth", "支付宝认证", "通过支付宝进行身份认证");

    private final String                     value;
    private final String                     name;
    private final String                     desc;

    /**
     * Constructs an {@code IdentityProviderType} using the provided value.
     *
     * @param value the value of the authorization grant type
     */
    public IdentityProviderType(String value, String name, String desc) {
        Assert.hasText(value, "value cannot be empty");
        this.value = value;
        this.name = name;
        this.desc = desc;
    }

    /**
     * Returns the value of the authorization grant type.
     *
     * @return the value of the authorization grant type
     */
    public String value() {
        return this.value;
    }

    /**
     * getIdentityProviderType
     *
     * @param type {@link String}
     * @return {@link IdentityProviderType}
     */
    public static IdentityProviderType getIdentityProviderType(String type) {
        if (FEISHU_OAUTH.value().equals(type)) {
            return FEISHU_OAUTH;
        }
        if (DINGTALK_OAUTH.value().equals(type)) {
            return DINGTALK_OAUTH;
        }
        if (WECHAT.value().equals(type)) {
            return WECHAT;
        }
        if (WECHAT_WORK_OAUTH.value().equals(type)) {
            return WECHAT_WORK_OAUTH;
        }
        if (QQ_OAUTH.value().equals(type)) {
            return QQ_OAUTH;
        }
        if (GITHUB_OAUTH.value().equals(type)) {
            return GITHUB_OAUTH;
        }
        if (GITEE_OAUTH.value().equals(type)) {
            return GITEE_OAUTH;
        }
        if (ALIPAY_OAUTH.value().equals(type)) {
            return ALIPAY_OAUTH;
        }
        if (USERNAME_PASSWORD.value().equals(type)) {
            return USERNAME_PASSWORD;
        }
        if (MAIL.value().equals(type)) {
            return MAIL;
        }
        if (SMS.value().equals(type)) {
            return SMS;
        }
        throw new IllegalArgumentException("未知身份提供商类型");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        IdentityProviderType that = (IdentityProviderType) obj;
        return this.value().equals(that.value());
    }

    @Override
    public int hashCode() {
        return this.value().hashCode();
    }

    public String name() {
        return name;
    }

    public String desc() {
        return desc;
    }

    @JsonIgnore
    public String getLoginPathPrefix() {
        return LOGIN_PATH + "/" + value();
    }

    @JsonIgnore
    public String getAuthorizationPathPrefix() {
        return AUTHN_PATH + "/" + value();
    }

    public static int size() {
        return 9;
    }

    @Override
    public String toString() {
        return "IdentityProviderType[" + "value=" + value + ", " + "name=" + name + ", " + "desc="
               + desc + ']';
    }

}
