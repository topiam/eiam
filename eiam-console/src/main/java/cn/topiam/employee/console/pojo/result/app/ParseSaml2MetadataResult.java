/*
 * eiam-console - Employee Identity and Access Management Program
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
package cn.topiam.employee.console.pojo.result.app;

import java.io.Serializable;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 解析SAML2 元数据
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/27 23:12
 */
@Data
@Schema(description = "解析 SAML2 元数据返回值")
public class ParseSaml2MetadataResult implements Serializable {

    /**
     * EntityId
     */
    @Parameter(description = "EntityId")
    private String  entityId;

    /**
     * 签名断言
     */
    @Parameter(description = "是否对断言签名")
    private Boolean wantAssertionsSigned;

    /**
     * 请求签名
     */
    @Parameter(description = "是否对SAML Request签名进行验证")
    private Boolean authnRequestsSigned;

    /**
     * ssoAcsUrl
     */
    @Parameter(description = "SSO ACS 地址")
    private String  acsUrl;

    /**
     * defaultNameIdFormat
     */
    @Parameter(description = "默认 NameId 格式")
    private String  defaultNameIdFormat;

    /**
     * binding
     */
    @Parameter(description = "默认 ACS 绑定方式")
    private String  defaultAcsBinding;

    /**
     * 签名证书
     */
    @Parameter(description = "签名证书")
    private String  signCert;

    /**
     * 单点登出启用
     */
    @Parameter(description = "单点登出是否启用")
    private Boolean sloEnabled;

    /**
     * 单点登出地址
     */
    @Parameter(description = "单点登出地址")
    private String  slsUrl;

    /**
     * 单点登出绑定模式
     */
    @Parameter(description = "单点登出绑定模式")
    private String  slsBinding;

}
