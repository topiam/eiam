/*
 * eiam-authentication-core - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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
import static cn.topiam.employee.common.constants.AuthorizeConstants.AUTHORIZATION_REQUEST_URI;
import static cn.topiam.employee.common.constants.AuthorizeConstants.LOGIN_PATH;

/**
 * @author SanLi
 * Created by qinggang.zuo@gmail.com / 2689170096@qq.com on  2022/12/31 15:18
 */
public record IdentityProviderType(String value,String name,String desc){
/**
 * 飞书
 */
public static final IdentityProviderType FEISHU_OAUTH=new IdentityProviderType("feishu_oauth","飞书认证","通过飞书进行身份验证");
/**
 * 钉钉
 */
public static final IdentityProviderType DINGTALK_OAUTH=new IdentityProviderType("dingtalk_oauth","钉钉Oauth认证","通过钉钉进行身份认证");
/**
 * 钉钉扫码
 */
public static final IdentityProviderType DINGTALK_QR=new IdentityProviderType("dingtalk_qr","钉钉扫码认证","通过钉钉扫码进行身份认证");
/**
 * 微信开放平台
 */
public static final IdentityProviderType WECHAT_QR=new IdentityProviderType("wechat_qr","微信扫码登录","通过微信扫码进行身份认证");
/**
 * 企业微信
 */
public static final IdentityProviderType WECHAT_WORK_QR=new IdentityProviderType("wechatwork_qr","企业微信扫码认证","通过企业微信同步的用户可使用企业微信扫码登录进行身份认证");

/**
 * QQ认证
 */
public static final IdentityProviderType QQ=new IdentityProviderType("qq_oauth","QQ认证","通过QQ进行身份认证");

/**
 * IDAP
 */
public static final IdentityProviderType LDAP=new IdentityProviderType("ldap","LDAP认证","通过 LDAP 进行身份验证");

/**
 * 用户名密码
 */
public static final IdentityProviderType USERNAME_PASSWORD=new IdentityProviderType("username_password","用户名密码认证","通过用户名密码进行身份认证");

/**
 * 短信验证码
 */
public static final IdentityProviderType SMS=new IdentityProviderType("sms","短信验证码认证","通过短信验证码进行身份认证");

/**
 * Constructs an {@code IdentityProviderType} using the provided value.
 *
 * @param value the value of the authorization grant type
 */
public IdentityProviderType{Assert.hasText(value,"value cannot be empty");}

/**
 * Returns the value of the authorization grant type.
 *
 * @return the value of the authorization grant type
 */
@Override public String value(){return this.value;}

@Override public boolean equals(Object obj){if(this==obj){return true;}if(obj==null||this.getClass()!=obj.getClass()){return false;}IdentityProviderType that=(IdentityProviderType)obj;return this.value().equals(that.value());}

@Override public int hashCode(){return this.value().hashCode();}

    @Override
    public String name() {
        return name;
    }

    @Override
    public String desc() {
        return desc;
    }

    public String getLoginPathPrefix() {
        return LOGIN_PATH + "/" + value();
    }

    public String getAuthorizationPathPrefix() {
        return AUTHORIZATION_REQUEST_URI + "/" + value();
    }

    public static int size() {
        return 9;
    }
}
