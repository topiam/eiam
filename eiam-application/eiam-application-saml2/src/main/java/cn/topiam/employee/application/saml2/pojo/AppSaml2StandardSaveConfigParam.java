/*
 * eiam-application-saml2 - Employee Identity and Access Management Program
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
package cn.topiam.employee.application.saml2.pojo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import cn.topiam.employee.common.entity.app.AppSaml2ConfigEntity;
import cn.topiam.employee.common.enums.app.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/10 01:45
 */
@Data
@Schema(description = "保存 SAML2 应用配置参数")
public class AppSaml2StandardSaveConfigParam implements Serializable {

    @Serial
    private static final long                             serialVersionUID = 7257798528680745281L;

    /**
     * 应用ID
     */
    @Schema(description = "授权类型")
    private AuthorizationType                             authorizationType;

    /**
     * SSO 发起登录类型
     */
    @Schema(description = "SSO 发起登录类型")
    private InitLoginType                                 initLoginType;

    /**
     * SSO 发起登录URL
     */
    @Schema(description = "SSO 发起登录URL")
    private String                                        initLoginUrl;

    /**
     * ACS URL
     */
    @Schema(description = "ACS URL")
    private String                                        spAcsUrl;

    /**
     * SLO URL
     */
    @Schema(description = "SLO URL")
    private String                                        spSloUrl;

    /**
     * SP Entity ID
     */
    @Schema(description = "SP Entity ID")
    private String                                        spEntityId;

    /**
     * Name ID 类型
     */
    @Schema(description = "Name ID 类型")
    private SamlNameIdValueType                           nameIdValueType;

    /**
     * RelayState
     */
    @Schema(description = "RelayState")
    private String                                        relayState;

    /**
     * Audience
     */
    @Schema(description = "Audience")
    private String                                        audience;

    /**
     * NameID 格式
     */
    @Schema(description = "NameID 格式")
    private SamlNameIdFormatType                          nameIdFormat;

    /**
     * Acs Binding 格式
     */
    @Schema(description = "ACS Binding 格式")
    private String                                        acsBinding;

    /**
     * 是否签名断言
     */
    @Schema(description = "是否签名断言")
    private Boolean                                       assertSigned;

    /**
     * 是否签名断言
     */
    @Schema(description = "签名断言算法")
    private SamlSignAssertAlgorithmType                   assertSignAlgorithm;

    /**
     * 是否加密断言
     */
    @Schema(description = "是否加密断言")
    private Boolean                                       assertEncrypted;

    /**
     * 断言签名算法
     */
    @Schema(description = "加密断言算法")
    private SamlEncryptAssertAlgorithmType                assertEncryptAlgorithm;

    /**
     * 是否签名断言
     */
    @Schema(description = "身份认证上下文")
    private AuthnContextClassRefType                      authnContextClassRef;

    /**
     * 响应是否加密
     */
    @Parameter(description = "响应是否加密")
    private Boolean                                       responseSigned;

    /**
     * 响应签名使用的非对称算法
     */
    @Parameter(description = "响应签名使用的非对称算法")
    private SamlSignAssertAlgorithmType                   responseSignAlgorithm;

    /**
     *  在 SAML Response 中，可以将额外用户字段（例如邮箱、显示名等）返回给应用解析。
     */
    @Schema(description = "Attribute Statements")
    private List<AppSaml2ConfigEntity.AttributeStatement> attributeStatements;

    /**
     * 是否验证请求签名
     */
    @Schema(description = "验证请求签名")
    private Boolean                                       spRequestsSigned;

    /**
     * 请求验证证书
     */
    @Schema(description = "请求验证证书")
    private String                                        spSignCert;

    /**
     * 模版配置
     */
    @Schema(description = "模版配置")
    private Map<String, String>                           additionalConfig;
}
