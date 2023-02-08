/*
 * eiam-portal - Employee Identity and Access Management Program
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
package cn.topiam.employee.portal.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import cn.topiam.employee.application.ApplicationService;
import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.enums.app.AppProtocol;
import cn.topiam.employee.portal.constant.PortalConstants;
import cn.topiam.employee.portal.pojo.result.GetAppListResult;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.repository.page.domain.Page;
import static cn.topiam.employee.common.constants.ProtocolConstants.APP_CODE_VARIABLE;
import static cn.topiam.employee.common.enums.app.InitLoginType.PORTAL_OR_APP;

/**
 * Converter
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/7 23:05
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppConverter {
    /**
     * 实体转应用列表返回
     *
     * @param list {@link AppEntity}
     * @return {@link GetAppListResult}
     */
    default Page<GetAppListResult> entityConvertToAppListResult(org.springframework.data.domain.Page<AppEntity> list) {
       //@formatter:off
       Page<GetAppListResult> page = new Page<>();
       List<GetAppListResult> results = new ArrayList<>();
       for (AppEntity entity : list) {
           GetAppListResult result = new GetAppListResult();
           result.setId(entity.getId().toString());
           result.setName(entity.getName());
           result.setType(entity.getType());
           result.setProtocol(entity.getProtocol());
           result.setTemplate(entity.getTemplate());
           result.setInitLoginType(entity.getInitLoginType());
           //登录发起URL
           if (PORTAL_OR_APP.equals(entity.getInitLoginType())){
               result.setInitLoginUrl(StringUtils.defaultString(entity.getInitLoginUrl(), getIdpInitUrl(entity.getProtocol(), entity.getCode())));
           }
           result.setIcon(entity.getIcon());
           //图标未配置，所以先从模版中拿
           if (StringUtils.isBlank(entity.getIcon())){
               ApplicationService applicationService = getApplicationServiceLoader()
                       .getApplicationService(entity.getTemplate());
               result.setIcon(applicationService.getBase64Icon());
           }
           if (StringUtils.isNotBlank(entity.getRemark())){
               result.setDescription(entity.getRemark());
           }else {
               ApplicationServiceLoader loader = ApplicationContextHelp.getBean(ApplicationServiceLoader.class);
               ApplicationService applicationService = loader.getApplicationService(entity.getTemplate());
               if (!Objects.isNull(applicationService)){
                   result.setDescription(applicationService.getDescription());
               }
           }
           results.add(result);
       }
       page.setList(results);
       page.setPagination(Page.Pagination.builder()
               .total(list.getTotalElements())
               .totalPages(list.getTotalPages())
               .current(list.getPageable().getPageNumber() + 1)
               .build());
       //@formatter:on
        return page;
    }

    /**
     * 获取idp init url
     *
     * @param protocol {@link AppProtocol}
     * @param appCode {@link String}
     * @return {@link String}
     */
    private String getIdpInitUrl(AppProtocol protocol, String appCode) {
        //SAML2
        if (AppProtocol.SAML2.equals(protocol)) {
            return PortalConstants.IDP_SAML2_SSO_INITIATOR.replace(APP_CODE_VARIABLE, appCode);
        }
        //OAuth2
        if (AppProtocol.OIDC.equals(protocol)) {
            return PortalConstants.IDP_OAUTH2_SSO_INITIATOR.replace(APP_CODE_VARIABLE, appCode);
        }
        //Form
        if (AppProtocol.FORM.equals(protocol)) {
            return PortalConstants.IDP_FORM_SSO_INITIATOR.replace(APP_CODE_VARIABLE, appCode);
        }
        //JWT
        if (AppProtocol.JWT.equals(protocol)) {
            return PortalConstants.IDP_JWT_SSO_INITIATOR.replace(APP_CODE_VARIABLE, appCode);
        }
        //CAS
        if (AppProtocol.CAS.equals(protocol)) {
            return PortalConstants.IDP_CAS_SSO_INITIATOR.replace(APP_CODE_VARIABLE, appCode);
        }
        return null;
    }

    /**
     * 获取 ApplicationServiceLoader
     *
     * @return {@link ApplicationServiceLoader}
     */
    private ApplicationServiceLoader getApplicationServiceLoader() {
        return ApplicationContextHelp.getBean(ApplicationServiceLoader.class);
    }

}
