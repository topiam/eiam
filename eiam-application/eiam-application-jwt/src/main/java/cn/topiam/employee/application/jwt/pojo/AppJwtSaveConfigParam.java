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
package cn.topiam.employee.application.jwt.pojo;

import java.io.Serial;
import java.io.Serializable;

import cn.topiam.employee.common.enums.app.AuthorizationType;
import cn.topiam.employee.common.enums.app.JwtBindingType;
import cn.topiam.employee.common.enums.app.JwtIdTokenSubjectType;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/02/12 22:45
 */
@Data
@Schema(description = "保存 JWT 应用配置参数")
public class AppJwtSaveConfigParam implements Serializable {

    @Serial
    private static final long     serialVersionUID = 7257798528680745281L;

    /**
     * SSO范围
     */
    @NotNull(message = "SSO范围不能为空")
    @Schema(description = "SSO范围")
    private AuthorizationType     authorizationType;

    /**
     * 业务系统中（或PC程序）的JWT SSO地址，在单点登录时本系统将向该地址用[GET]方式发送id_token信息，参数名为id_token，
     * 业务系统通过id_token与Public Key可获取业务系统中的用户信息，如果在业务系统（SP）发起登录，请求SP登录地址时如果携带service参数本系统会检验合法性，
     * 成功后会将浏览器重定向到该地址，并携带id_token身份令牌。
     */
    @NotBlank(message = "Redirect Uri不能为空")
    @Schema(description = "Redirect Url")
    private String                redirectUrl;

    /**
     * 业务系统中在JWT SSO成功后重定向的URL，一般用于跳转到二级菜单等，
     * 若设置了该URL，在JWT SSO时会以参数target_uri优先传递该值，
     * 若未设置该值，此时若SSO中有请求参数target_uri，则会按照请求参数传递该值。此项可选。
     */
    @Schema(description = "target link url")
    private String                targetLinkUrl;

    /**
     * id_token sub 类型
     */
    @NotNull(message = "id_token sub 类型不能为空")
    private JwtIdTokenSubjectType idTokenSubjectType;

    /**
     * id_token 有效期
     */
    @NotNull(message = "id token有效期不能为空")
    @Max(value = 84600)
    @Min(value = 1)
    @Schema(description = "id token有效期")
    private Integer               idTokenTimeToLive;

    /**
     * SSO 绑定类型
     */
    @NotNull(message = "SSO 绑定类型")
    private JwtBindingType        bindingType;
}
