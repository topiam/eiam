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

import cn.topiam.employee.core.context.ServerContextHelp;
import static cn.topiam.employee.common.constants.ProtocolConstants.APP_CODE_VARIABLE;
import static cn.topiam.employee.common.constants.ProtocolConstants.Saml2EndpointConstants;

/**
 * Saml2ConverterUtils
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/23 22:57
 */
public class Saml2ConverterUtils {
    /**
     * 应用ID
     *
     * @param appCode {@link String}
     * @return {@link AppSaml2ProtocolEndpoint}
     */
    public static AppSaml2ProtocolEndpoint getProtocolEndpointDomain(String appCode) {
        AppSaml2ProtocolEndpoint domain = new AppSaml2ProtocolEndpoint();
        //IDP
        String baseUrl = ServerContextHelp.getPortalPublicBaseUrl();
        //元数据端点
        domain.setIdpMetaEndpoint(baseUrl + Saml2EndpointConstants.SAML_METADATA_PATH
            .replace(APP_CODE_VARIABLE, appCode));
        //EntityId端点
        domain.setIdpEntityIdEndpoint(baseUrl + Saml2EndpointConstants.SAML_METADATA_PATH
            .replace(APP_CODE_VARIABLE, appCode));
        //Sso端点
        domain.setIdpSsoEndpoint(
            baseUrl + Saml2EndpointConstants.SAML_SSO_PATH.replace(APP_CODE_VARIABLE, appCode));
        //Slo端点
        domain.setIdpSloEndpoint(
            baseUrl + Saml2EndpointConstants.SAML_LOGOUT_PATH.replace(APP_CODE_VARIABLE, appCode));
        return domain;
    }
}
