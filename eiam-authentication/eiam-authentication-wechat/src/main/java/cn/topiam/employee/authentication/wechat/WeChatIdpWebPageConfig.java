/*
 * eiam-authentication-wechat - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.wechat;

import java.io.Serial;

import cn.topiam.employee.authentication.common.config.IdentityProviderConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 微信网页登录
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/9 22:07 21:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WeChatIdpWebPageConfig extends IdentityProviderConfig {
    @Serial
    private static final long serialVersionUID = -5831048603320371078L;
    /**
     * 客户端id
     */
    @NotBlank(message = "应用AppId不能为空")
    private String            appId;

    /**
     * 客户端Secret
     */
    @NotBlank(message = "应用AppId不能为空")
    private String            appSecret;

    /**
     * 授权范围
     */
    @NotBlank(message = "授权范围不能为空")
    private String            scope;

    /**
     * 授权URI
     */
    @NotBlank(message = "重定向URI不能为空")
    private String            redirectUri;

    /**
     * 校验文件
     */
    @NotNull(message = "域名校验文件不能为空")
    private VerifyFile        verifyFile;

    @Data
    public static class VerifyFile {
        /**
         * 域名校验文件名
         */
        @NotBlank(message = "域名校验文件名不能为空")
        private String name;

        /**
         * 域名校验文件内容
         */
        @NotBlank(message = "域名校验文件内容不能为空")
        private String content;
    }
}
