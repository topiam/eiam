/*
 * eiam-application-oidc - Employee Identity and Access Management
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
package cn.topiam.employee.application.oidc.pojo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import org.hibernate.validator.constraints.URL;

import cn.topiam.employee.common.enums.app.AuthorizationType;
import cn.topiam.employee.common.enums.app.InitLoginType;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/10 01:45
 */
@Data
@Schema(description = "保存 OIDC 应用配置参数")
public class AppOidcStandardSaveConfigParam implements Serializable {

    @Serial
    private static final long                                                                serialVersionUID = 7257798528680745281L;

    /**
     * 授权类型
     */
    @NotNull(message = "授权类型不能为空")
    @Schema(description = "授权类型")
    private List<@NotBlank(message = "授权类型不能为空") String>                                     authGrantTypes;

    /**
     * 登录重定向URI
     */
    @NotNull(message = "登录重定向URI不能为空")
    @Schema(description = "登录重定向URI")
    private List<@NotBlank(message = "登录重定向URI不能为空") @URL(message = "登录重定向URI格式不正确") String> redirectUris;

    /**
     * 登出重定向URI
     */
    @NotNull(message = "登出重定向URI不能为空")
    @Schema(description = "登出重定向URI")
    private List<@NotBlank(message = "登出重定向URI不能为空") @URL(message = "登出重定向URI格式不正确") String> postLogoutRedirectUris;

    /**
     * SSO范围
     */
    @NotNull(message = "SSO范围不能为空")
    @Schema(description = "SSO范围")
    private AuthorizationType                                                                authorizationType;

    /**
     * SSO发起方
     */
    @NotNull(message = "SSO发起方不能为空")
    @Schema(description = "SSO发起方")
    private InitLoginType                                                                    initLoginType;

    /**
     * SSO 发起登录URL
     */
    @Schema(description = "SSO 发起登录URL")
    private String                                                                           initLoginUrl;

    /**
     * 授予范围
     */
    @NotNull(message = "授予范围不能为空")
    @Schema(description = "授予范围")
    private List<String>                                                                     grantScopes;

    /**
     * 客户端身份验证方法
     */
    @Schema(description = "客户端身份验证方法")
    private List<String>                                                                     clientAuthMethods;

    /**
     * 是否需要PKCE
     */
    @Schema(description = "是否需要PKCE")
    private Boolean                                                                          requireProofKey  = false;

    /**
     * Access Token 生存时间
     */
    @NotBlank(message = "Access Token 生存时间不能为空")
    @Schema(description = "Access Token 生存时间")
    private String                                                                           accessTokenTimeToLive;

    /**
     * Access Token 格式
     */
    @Schema(description = "Access Token 格式")
    private String                                                                           accessTokenFormat;

    /**
     * Refresh Token 生存时间
     */
    @NotBlank(message = "Refresh Token 生存时间不能为空")
    @Schema(description = "Refresh Token 生存时间")
    private String                                                                           refreshTokenTimeToLive;

    /**
     * Id Token 生存时间
     */
    @NotBlank(message = "Id Token 生存时间不能为空")
    @Schema(description = "Id Token 生存时间")
    private String                                                                           idTokenTimeToLive;

    /**
     * ID Token签名算法
     */
    @NotBlank(message = "ID Token签名算法不能为空")
    @Schema(description = "ID Token签名算法")
    private String                                                                           idTokenSignatureAlgorithm;

    /**
     * Token签名算法
     */
    @Schema(description = "令牌端点身份验证签名算法")
    private String                                                                           tokenEndpointAuthSigningAlgorithm;

    /**
     * 重用刷新令牌
     */
    @Schema(description = "重用刷新令牌")
    private Boolean                                                                          reuseRefreshToken;

    /**
     * 需要授权同意
     */
    @Schema(description = "需要授权同意")
    private Boolean                                                                          requireAuthConsent;

}
