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
package cn.topiam.employee.application.jwt.converter;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.text.StringSubstitutor;
import org.mapstruct.Mapper;

import cn.topiam.employee.application.jwt.pojo.AppJwtConfigGetResult;
import cn.topiam.employee.application.jwt.pojo.AppJwtProtocolEndpoint;
import cn.topiam.employee.application.jwt.pojo.AppJwtSaveConfigParam;
import cn.topiam.employee.common.entity.app.AppJwtConfigEntity;
import cn.topiam.employee.common.entity.app.po.AppJwtConfigPO;
import cn.topiam.employee.core.context.ContextService;
import static cn.topiam.employee.common.constant.ProtocolConstants.APP_CODE;
import static cn.topiam.employee.common.constant.ProtocolConstants.JwtEndpointConstants.JWT_SSO_PATH;

/**
 * 应用映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/02/12 22:45
 */
@Mapper(componentModel = "spring")
public interface AppJwtConfigConverter {

    /**
     * save 转 entity
     *
     * @param config {@link AppJwtSaveConfigParam}
     * @return {@link AppJwtConfigEntity}
     */
    default AppJwtConfigEntity appJwtSaveConfigParamToEntity(AppJwtSaveConfigParam config) {
        if (config == null) {
            return null;
        }

        AppJwtConfigEntity entity = new AppJwtConfigEntity();
        entity.setRedirectUrl(config.getRedirectUrl());
        entity.setTargetLinkUrl(config.getTargetLinkUrl());
        entity.setBindingType(config.getBindingType());
        entity.setIdTokenSubjectType(config.getIdTokenSubjectType());
        entity.setIdTokenTimeToLive(Duration.ofSeconds(config.getIdTokenTimeToLive()));
        return entity;
    }

    /**
     * po 转 result
     *
     * @param po {@link AppJwtConfigPO}
     * @return {@link AppJwtConfigGetResult}
     */
    default AppJwtConfigGetResult entityConverterToFormConfigResult(AppJwtConfigPO po) {
        if (po == null) {
            return null;
        }
        AppJwtConfigGetResult result = new AppJwtConfigGetResult();
        if (po.getAppId() != null) {
            result.setAppId(po.getAppId());
        }
        result.setInitLoginUrl(po.getInitLoginUrl());
        result.setRedirectUrl(po.getRedirectUrl());
        result.setTargetLinkUrl(po.getTargetLinkUrl());
        result.setBindingType(po.getBindingType());
        result.setIdTokenSubjectType(po.getIdTokenSubjectType());
        result.setIdTokenTimeToLive(Objects.toString(po.getIdTokenTimeToLive().toSeconds(), ""));
        result.setProtocolEndpoint(getProtocolEndpointDomain(po.getAppCode()));
        return result;
    }

    /**
     * 获取协议端点
     *
     * @param appCode {@link String}
     * @return {@link AppJwtProtocolEndpoint}
     */
    private AppJwtProtocolEndpoint getProtocolEndpointDomain(String appCode) {
        //@formatter:off
        AppJwtProtocolEndpoint domain = new AppJwtProtocolEndpoint();
        Map<String,String> variables = new HashMap<>(16);
        variables.put(APP_CODE,appCode);
        StringSubstitutor sub = new StringSubstitutor(variables, "{", "}");
        //IDP SSO 端点
        domain.setIdpSsoEndpoint(sub.replace(ContextService.getPortalPublicBaseUrl()+JWT_SSO_PATH));
        return domain;
        //@formatter:on
    }
}
