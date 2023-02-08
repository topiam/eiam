/*
 * eiam-application-oidc - Employee Identity and Access Management Program
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
package cn.topiam.employee.application.oidc.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.text.StringSubstitutor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cn.topiam.employee.application.oidc.pojo.AppOidcProtocolEndpoint;
import cn.topiam.employee.application.oidc.pojo.AppOidcStandardConfigGetResult;
import cn.topiam.employee.application.oidc.pojo.AppOidcStandardSaveConfigParam;
import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.common.entity.app.AppOidcConfigEntity;
import cn.topiam.employee.common.entity.app.po.AppOidcConfigPO;
import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.support.util.HttpUrlUtils;
import static cn.topiam.employee.common.constants.ProtocolConstants.APP_CODE;
import static cn.topiam.employee.common.constants.ProtocolConstants.OidcEndpointConstants.OIDC_AUTHORIZE_PATH;
import static cn.topiam.employee.common.constants.ProtocolConstants.OidcEndpointConstants.WELL_KNOWN_OPENID_CONFIGURATION;

/**
 * 应用映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/14 22:45
 */
@Mapper(componentModel = "spring")
public interface AppOidcStandardConfigConverter {
    /**
     * 实体转OIDC配置结果
     *
     * @param config {@link AppOidcConfigEntity}
     * @return {@link AppOidcStandardConfigGetResult}
     */
    default AppOidcStandardConfigGetResult entityConverterToOidcConfigResult(AppOidcConfigPO config) {
        AppOidcStandardConfigGetResult result = new AppOidcStandardConfigGetResult();
        if (Objects.isNull(config)) {
            return result;
        }
        //协议端点域
        result.setProtocolEndpoint(getProtocolEndpointDomain(config.getAppCode()));
        //认证授权类型
        result.setAuthGrantTypes(config.getAuthGrantTypes());
        //重定向URI
        result.setRedirectUris(config.getRedirectUris());
        //授权范围
        result.setGrantScopes(config.getGrantScopes());
        //启用PKCE
        result.setRequireProofKey(config.getRequireProofKey());
        //访问令牌有效时间
        result.setAccessTokenTimeToLive(config.getAccessTokenTimeToLive().toString());
        //刷新令牌有效时间
        result.setRefreshTokenTimeToLive(config.getRefreshTokenTimeToLive().toString());
        //ID令牌有效时间
        result.setIdTokenTimeToLive(config.getIdTokenTimeToLive().toString());
        // id 令牌签名算法
        result.setIdTokenSignatureAlgorithm(config.getIdTokenSignatureAlgorithm());
        //SSO 发起方
        result.setInitLoginType(config.getInitLoginType());
        //登录发起地址
        result.setInitLoginUrl(config.getInitLoginUrl());
        //授权类型
        result.setAuthorizationType(config.getAuthorizationType());
        return result;
    }

    /**
     * save 转 entity
     *
     * @param config {@link AppOidcConfigEntity}
     * @return {@link AppOidcConfigEntity}
     */
    @Mapping(target = "responseTypes", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "appId", ignore = true)
    AppOidcConfigEntity appOidcStandardSaveConfigParamToEntity(AppOidcStandardSaveConfigParam config);

    /**
     * 获取协议端点
     *
     * @param appCode {@link String}
     * @return {@link AppOidcProtocolEndpoint}
     */
    private AppOidcProtocolEndpoint getProtocolEndpointDomain(String appCode) {
        //@formatter:off
        AppOidcProtocolEndpoint domain = new AppOidcProtocolEndpoint();
        //issues
        Map<String,String> variables = new HashMap<>(16);
        variables.put(APP_CODE,appCode);
        StringSubstitutor sub = new StringSubstitutor(variables, "{", "}");
        //Issuer
        domain.setIssuer(sub.replace(ServerContextHelp.getPortalPublicBaseUrl()+OIDC_AUTHORIZE_PATH));
        //发现端点
        domain.setDiscoveryEndpoint(HttpUrlUtils.format(ServerContextHelp.getPortalPublicBaseUrl() + sub.replace(WELL_KNOWN_OPENID_CONFIGURATION)));
        //认证端点
        domain.setAuthorizationEndpoint(HttpUrlUtils.format(ServerContextHelp.getPortalPublicBaseUrl() +  sub.replace(ProtocolConstants.OidcEndpointConstants.AUTHORIZATION_ENDPOINT)));
        //Token端点
        domain.setTokenEndpoint(HttpUrlUtils.format(ServerContextHelp.getPortalPublicBaseUrl() + sub.replace( ProtocolConstants.OidcEndpointConstants.TOKEN_ENDPOINT)));
        //Jwks端点
        domain.setJwksEndpoint(HttpUrlUtils.format(ServerContextHelp.getPortalPublicBaseUrl() +  sub.replace(ProtocolConstants.OidcEndpointConstants.JWK_SET_ENDPOINT)));
        //撤销端点
        domain.setRevokeEndpoint(HttpUrlUtils.format(ServerContextHelp.getPortalPublicBaseUrl()+  sub.replace(ProtocolConstants.OidcEndpointConstants.TOKEN_REVOCATION_ENDPOINT)));
        //UserInfo端点
        domain.setUserinfoEndpoint(HttpUrlUtils.format(ServerContextHelp.getPortalPublicBaseUrl() +  sub.replace(ProtocolConstants.OidcEndpointConstants.OIDC_USER_INFO_ENDPOINT)));
        return domain;
        //@formatter:on
    }
}
