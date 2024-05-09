/*
 * eiam-portal - Employee Identity and Access Management
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
package cn.topiam.employee.portal.converter;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.google.common.collect.Lists;

import cn.topiam.employee.application.ApplicationService;
import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.enums.app.AppProtocol;
import cn.topiam.employee.core.context.ContextService;
import cn.topiam.employee.portal.pojo.result.GetAppListResult;
import cn.topiam.employee.support.context.ApplicationContextService;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import static cn.topiam.employee.common.constant.ProtocolConstants.APP_CODE_VARIABLE;
import static cn.topiam.employee.common.constant.ProtocolConstants.FormEndpointConstants.IDP_FORM_SSO_INITIATOR;
import static cn.topiam.employee.common.constant.ProtocolConstants.JwtEndpointConstants.IDP_JWT_SSO_INITIATOR;
import static cn.topiam.employee.common.constant.ProtocolConstants.OidcEndpointConstants.AUTHORIZATION_ENDPOINT;

/**
 * Converter
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/7/7 23:05
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppConverter {

    /**
     * 实体转应用列表返回
     *
     * @param entityList      {@link AppEntity}
     * @param pageModel      {@link PageModel}
     * @return {@link GetAppListResult}
     */
    default Page<GetAppListResult> entityConvertToAppListResult(List<AppEntity> entityList,
                                                                PageModel pageModel) {
        //@formatter:off
        Page<GetAppListResult> page = new Page<>();
        List<GetAppListResult> list = Lists.newArrayList();
        for (AppEntity entity : entityList) {
            GetAppListResult result = entityConvertToAppListResult(entity);
            list.add(result);
        }
        int startIndex = (pageModel.getCurrent()) * pageModel.getPageSize();
        int endIndex = Math.min(startIndex + pageModel.getPageSize(), list.size());
        page.setList(list.subList(startIndex, endIndex));
        //@formatter:off
        long total = list.size();
        page.setPagination(Page.Pagination.builder()
                .total(total)
                .totalPages((int) Math.ceil((double) list.size() / pageModel.getPageSize()))
                .current(pageModel.getCurrent())
                .build());
        //@formatter:on
        return page;
    }

    /**
     * 实体转应用管理列表
     *
     * @param entity {@link AppEntity}
     * @return {@link GetAppListResult}
     */
    default GetAppListResult entityConvertToAppListResult(AppEntity entity) {
        if (entity == null) {
            return null;
        }

        GetAppListResult result = new GetAppListResult();
        result.setId(entity.getId());
        result.setName(entity.getName());
        result.setType(entity.getType());
        result.setProtocol(entity.getProtocol());
        result.setInitLoginUrl(StringUtils.defaultIfBlank(entity.getInitLoginUrl(),
            getIdpInitUrl(entity.getProtocol(), entity.getCode())));
        result.setIcon(entity.getIcon());
        //图标未配置，所以先从模版中拿
        if (StringUtils.isBlank(entity.getIcon())) {
            ApplicationService applicationService = getApplicationServiceLoader()
                .getApplicationService(entity.getTemplate());
            result.setIcon(applicationService.getBase64Icon());
        }
        result.setTemplate(entity.getTemplate());
        result.setProtocol(entity.getProtocol());
        result.setCode(entity.getCode());
        result.setDescription(entity.getRemark());
        // 如果备注为空、放置详情，由于 entityConvertToAppListResult 为共用，所以这里单独处理。
        if (StringUtils.isBlank(result.getDescription())) {
            ApplicationServiceLoader loader = ApplicationContextService
                .getBean(ApplicationServiceLoader.class);
            ApplicationService applicationService = loader
                .getApplicationService(entity.getTemplate());
            if (!Objects.isNull(applicationService)) {
                result.setDescription(applicationService.getDescription());
            }
        }
        return result;
    }

    /**
     * 获取 ApplicationServiceLoader
     *
     * @return {@link ApplicationServiceLoader}
     */
    private ApplicationServiceLoader getApplicationServiceLoader() {
        return ApplicationContextService.getBean(ApplicationServiceLoader.class);
    }

    /**
     * 获取idp init url
     *
     * @param protocol {@link AppProtocol}
     * @param appCode {@link String}
     * @return {@link String}
     */
    private String getIdpInitUrl(AppProtocol protocol, String appCode) {
        //OAuth2
        if (AppProtocol.OIDC.equals(protocol)) {
            return getPortalInitUrl(AUTHORIZATION_ENDPOINT.replace(APP_CODE_VARIABLE, appCode));
        }
        //Form
        if (AppProtocol.FORM.equals(protocol)) {
            return getPortalInitUrl(IDP_FORM_SSO_INITIATOR.replace(APP_CODE_VARIABLE, appCode));
        }
        //JWT
        if (AppProtocol.JWT.equals(protocol)) {
            return getPortalInitUrl(IDP_JWT_SSO_INITIATOR.replace(APP_CODE_VARIABLE, appCode));
        }
        return null;
    }

    private String getPortalInitUrl(String path) {
        return ContextService.getPortalPublicBaseUrl() + path;
    }
}
