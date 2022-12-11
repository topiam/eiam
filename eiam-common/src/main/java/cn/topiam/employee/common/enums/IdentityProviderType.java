/*
 * eiam-common - Employee Identity and Access Management Program
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
package cn.topiam.employee.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.web.converter.EnumConvert;
import static cn.topiam.employee.common.constants.AuthorizeConstants.AUTHORIZATION_REQUEST_URI;
import static cn.topiam.employee.common.constants.AuthorizeConstants.LOGIN_PATH;

/**
 * 认证提供商
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/13 22:18
 */
public enum IdentityProviderType implements BaseEnum {
                                                      /**
                                                       * 微信扫码登录
                                                       */
                                                      WECHAT_SCAN_CODE("wechat_scan_code", "微信扫码登录",
                                                                       "通过微信扫码进行身份认证"),
                                                      /**
                                                       * 钉钉扫码登录
                                                       */
                                                      DINGTALK_SCAN_CODE("dingtalk_scan_code",
                                                                         "钉钉扫码认证",

                                                                         "通过钉钉扫码进行身份认证"),
                                                      /**
                                                       * 钉钉Oauth2
                                                       */
                                                      DINGTALK_OAUTH("dingtalk_oauth", "钉钉Oauth认证",
                                                                     "通过钉钉进行身份认证"),
                                                      /**
                                                       * 企业微信
                                                       */
                                                      WECHATWORK_SCAN_CODE("wechatwork_scan_code",
                                                                           "企业微信扫码认证",

                                                                           "通过企业微信同步的用户可使用企业微信扫码登录进行身份认证"),
                                                      /**
                                                       * QQ
                                                       */
                                                      QQ("qq_oauth", "QQ认证", "通过QQ进行身份认证"),
                                                      /**
                                                       * 微博
                                                       */
                                                      WEIBO("weibo_oauth", "微博认证", "通过微博进行身份认证"),
                                                      /**
                                                       * Github
                                                       */
                                                      GITHUB("github_oauth", "Github",
                                                             "通过 GitHub 进行身份认证"),
                                                      /**
                                                       * Google
                                                       */
                                                      GOOGLE("google_oauth", "Google",
                                                             "通过 Google 进行身份认证"),
                                                      /**
                                                       * 支付宝扫码认证
                                                       */
                                                      ALIPAY("alipay_oauth", "支付宝认证",
                                                             "通过支付宝进行身份认证"),

                                                      /**
                                                       * LDAP
                                                       */
                                                      LDAP("ldap", "LDAP 认证源", "通过 LDAP 认证源进行身份验证");

    @JsonValue
    private final String code;
    private final String name;
    private final String desc;

    IdentityProviderType(String code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getAuthorizationPathPrefix() {
        return AUTHORIZATION_REQUEST_URI + "/" + getCode();
    }

    public String getLoginPathPrefix() {
        return LOGIN_PATH + "/" + getCode();
    }

    /**
     * 获取认证平台
     *
     * @param code {@link String}
     * @return {@link IdentityProviderType}
     */
    @EnumConvert
    public static IdentityProviderType getType(String code) {
        IdentityProviderType[] values = values();
        for (IdentityProviderType status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        return null;
    }
}
