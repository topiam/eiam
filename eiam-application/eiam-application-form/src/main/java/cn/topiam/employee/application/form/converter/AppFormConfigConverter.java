/*
 * eiam-application-form - Employee Identity and Access Management Program
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
package cn.topiam.employee.application.form.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cn.topiam.employee.application.form.model.FormProtocolConfig;
import cn.topiam.employee.application.form.pojo.AppFormConfigGetResult;
import cn.topiam.employee.application.form.pojo.AppFormProtocolEndpoint;
import cn.topiam.employee.application.form.pojo.AppFormSaveConfigParam;
import cn.topiam.employee.common.entity.app.AppFormConfigEntity;
import cn.topiam.employee.common.entity.app.po.AppFormConfigPO;
import cn.topiam.employee.core.context.ServerContextHelp;
import static cn.topiam.employee.common.constants.ProtocolConstants.APP_CODE;
import static cn.topiam.employee.common.constants.ProtocolConstants.FormEndpointConstants.FORM_SSO_PATH;

/**
 * 应用映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/14 22:45
 */
@Mapper(componentModel = "spring")
public interface AppFormConfigConverter {

    /**
     * save 转 entity
     *
     * @param config {@link AppFormSaveConfigParam}
     * @return {@link AppFormConfigEntity}
     */
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "appId", ignore = true)
    AppFormConfigEntity appFormSaveConfigParamToEntity(AppFormSaveConfigParam config);

    /**
     * entity转config
     *
     * @param po {@link AppFormConfigPO}
     * @return {@link FormProtocolConfig}
     */
    FormProtocolConfig appFormEntityToConfig(AppFormConfigPO po);

    /**
     * po 转 result
     *
     * @param po {@link AppFormConfigPO}
     * @return {@link AppFormConfigGetResult}
     */
    default AppFormConfigGetResult entityConverterToFormConfigResult(AppFormConfigPO po) {
        if (po == null) {
            return null;
        }
        AppFormConfigGetResult result = new AppFormConfigGetResult();
        if (po.getAppId() != null) {
            result.setAppId(String.valueOf(po.getAppId()));
        }
        result.setInitLoginType(po.getInitLoginType());
        result.setInitLoginUrl(po.getInitLoginUrl());
        result.setAuthorizationType(po.getAuthorizationType());
        result.setLoginUrl(po.getLoginUrl());
        result.setUsernameField(po.getUsernameField());
        result.setPasswordField(po.getPasswordField());
        result.setSubmitType(po.getSubmitType());
        List<AppFormConfigEntity.OtherField> list = po.getOtherField();
        if (list != null) {
            result.setOtherField(new ArrayList<>(list));
        }
        result.setProtocolEndpoint(getProtocolEndpointDomain(po.getAppCode()));
        return result;
    }

    /**
     * 获取协议端点
     *
     * @param appCode {@link String}
     * @return {@link AppFormProtocolEndpoint}
     */
    private AppFormProtocolEndpoint getProtocolEndpointDomain(String appCode) {
        //@formatter:off
        AppFormProtocolEndpoint domain = new AppFormProtocolEndpoint();
        Map<String,String> variables = new HashMap<>(16);
        variables.put(APP_CODE,appCode);
        StringSubstitutor sub = new StringSubstitutor(variables, "{", "}");
        //IDP SSO 端点
        domain.setIdpSsoEndpoint(sub.replace(ServerContextHelp.getPortalPublicBaseUrl()+FORM_SSO_PATH));
        return domain;
        //@formatter:on
    }
}
