/*
 * eiam-application-jwt - Employee Identity and Access Management
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
package cn.topiam.employee.application.jwt.model;

import java.io.Serial;

import cn.topiam.employee.application.AbstractProtocolConfig;
import cn.topiam.employee.common.enums.app.JwtBindingType;
import cn.topiam.employee.common.enums.app.JwtIdTokenSubjectType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Form 协议配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/02/12 21:43
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Jacksonized
public class JwtProtocolConfig extends AbstractProtocolConfig {

    @Serial
    private static final long     serialVersionUID = -3671812647788723766L;

    /**
     * 业务系统中（或PC程序）的JWT SSO地址，在单点登录时本系统将向该地址用[GET]方式发送id_token信息，参数名为id_token，
     * 业务系统通过id_token与Public Key可获取业务系统中的用户信息，如果在业务系统（SP）发起登录，请求SP登录地址时如果携带service参数本系统会检验合法性，
     * 成功后会将浏览器重定向到该地址，并携带id_token身份令牌。
     */
    private String                redirectUrl;

    /**
     * 业务系统中在JWT SSO成功后重定向的URL，一般用于跳转到二级菜单等，
     * 若设置了该URL，在JWT SSO时会以参数target_uri优先传递该值，
     * 若未设置该值，此时若SSO中有请求参数target_uri，则会按照请求参数传递该值。此项可选。
     */
    private String                targetLinkUrl;

    /**
     * SSO 绑定类型
     */
    private JwtBindingType        bindingType;

    /**
     * Token 过期时间（秒）
     */
    private Integer               idTokenTimeToLive;

    /**
     * JWT 公钥
     */
    private String                jwtPublicKey;

    /**
     * JWT 私钥
     */
    private String                jwtPrivateKey;

    /**
     * id_token 主体类型
     */
    private JwtIdTokenSubjectType idTokenSubjectType;
}
