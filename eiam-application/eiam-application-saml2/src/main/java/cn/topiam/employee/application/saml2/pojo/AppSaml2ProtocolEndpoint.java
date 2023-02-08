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

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
     * 协议端点域
     *
     * @author TopIAM
     * Created by support@topiam.cn on  2022/6/4 23:37
     */
@Data
@Schema(description = "协议端点")
public class AppSaml2ProtocolEndpoint implements Serializable {

    @Serial
    private static final long serialVersionUID = -2261602995152894964L;

    /**
     * IDP 元数据端点
     */
    @Parameter(description = "IDP 元数据端点")
    private String            idpMetaEndpoint;

    /**
     * IDP EntityId 端点
     */
    @Parameter(description = "IDP EntityId 端点")
    private String            idpEntityIdEndpoint;

    /**
     * IDP SSO 端点
     */
    @Parameter(description = "IDP SSO 端点")
    private String            idpSsoEndpoint;

    /**
     * IDP SLO 端点
     */
    @Parameter(description = "IDP SLO 端点")
    private String            idpSloEndpoint;
}
