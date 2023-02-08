/*
 * eiam-application-cas - Employee Identity and Access Management Program
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
package cn.topiam.employee.application.cas.converter;

import org.mapstruct.Mapper;

import cn.topiam.employee.application.cas.pojo.AppCasProtocolEndpoint;
import cn.topiam.employee.application.cas.pojo.AppCasStandardConfigGetResult;
import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.common.entity.app.po.AppCasConfigPO;
import cn.topiam.employee.core.context.ServerContextHelp;
import static cn.topiam.employee.common.constants.ProtocolConstants.APP_CODE_VARIABLE;

/**
 * 配置转换
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 17:31
 */
@Mapper(componentModel = "spring")
public interface AppCasStandardConfigConverter {

    /**
     * 实体转CAS配置
     *
     * @param po {@link AppCasConfigPO}
     * @return {@link AppCasStandardConfigGetResult}
     */
    default AppCasStandardConfigGetResult entityConverterToCasConfigResult(AppCasConfigPO po) {
        AppCasStandardConfigGetResult result = new AppCasStandardConfigGetResult();
        result.setAuthorizationType(po.getAuthorizationType());
        result.setAppId(String.valueOf(po.getAppId()));
        result.setInitLoginType(po.getInitLoginType());
        result.setInitLoginUrl(po.getInitLoginUrl());
        result.setClientServiceUrl(po.getClientServiceUrl());
        result.setUserIdentityType(po.getUserIdentityType());
        result.setServiceTicketExpireTime(po.getServiceTicketExpireTime());

        //封装端点信息
        //@formatter:off
        AppCasProtocolEndpoint protocolEndpoint = new AppCasProtocolEndpoint();
        String baseUrl = ServerContextHelp.getPortalPublicBaseUrl();
        protocolEndpoint.setCasServerUrlPrefix(baseUrl+ProtocolConstants.CasEndpointConstants.CAS_AUTHORIZE_BASE_PATH.replace(APP_CODE_VARIABLE, po.getAppCode()));
        protocolEndpoint.setCasSsoEndpoint(baseUrl + ProtocolConstants.CasEndpointConstants.CAS_LOGIN_PATH.replace(APP_CODE_VARIABLE, po.getAppCode()));
        protocolEndpoint.setCasSloEndpoint(baseUrl + ProtocolConstants.CasEndpointConstants.CAS_LOGOUT_PATH.replace(APP_CODE_VARIABLE, po.getAppCode()));
        protocolEndpoint.setCasValidateEndpoint(baseUrl + ProtocolConstants.CasEndpointConstants.CAS_VALIDATE_V1_PATH.replace(APP_CODE_VARIABLE, po.getAppCode()));
        protocolEndpoint.setCasValidateV2Endpoint(baseUrl + ProtocolConstants.CasEndpointConstants.CAS_VALIDATE_V2_PATH.replace(APP_CODE_VARIABLE, po.getAppCode()));
        protocolEndpoint.setCasValidateV3Endpoint(baseUrl + ProtocolConstants.CasEndpointConstants.CAS_VALIDATE_V3_PATH.replace(APP_CODE_VARIABLE, po.getAppCode()));
        result.setProtocolEndpoint(protocolEndpoint);
        //@formatter:on
        return result;
    }
}
