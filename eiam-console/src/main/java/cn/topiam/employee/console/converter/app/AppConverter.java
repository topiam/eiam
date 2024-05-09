/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.converter.app;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.jpa.domain.Specification;

import com.google.common.collect.Lists;

import cn.topiam.employee.application.ApplicationService;
import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.console.pojo.query.app.AppQuery;
import cn.topiam.employee.console.pojo.result.app.AppGetResult;
import cn.topiam.employee.console.pojo.result.app.AppListResult;
import cn.topiam.employee.console.pojo.update.app.AppUpdateParam;
import cn.topiam.employee.support.context.ApplicationContextService;
import cn.topiam.employee.support.repository.page.domain.Page;

import jakarta.persistence.criteria.Predicate;

/**
 * 应用映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/14 22:45
 */
@Mapper(componentModel = "spring")
public interface AppConverter {

    /**
     * 查询应用列表参数转换为 Specification
     *
     * @param appQuery {@link AppQuery} query
     * @return {@link AppEntity}
     */
    default Specification<AppEntity> queryAppListParamConvertToSpecification(AppQuery appQuery) {
        //查询条件
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            //名称
            if (StringUtils.isNoneBlank(appQuery.getName())) {
                predicateList
                    .add(criteriaBuilder.like(root.get("name"), "%" + appQuery.getName() + "%"));
            }
            //分组
            if (!Objects.isNull(appQuery.getProtocol())) {
                predicateList
                    .add(criteriaBuilder.equal(root.get("protocol"), appQuery.getProtocol()));
            }
            Predicate[] pre = new Predicate[predicateList.size()];
            pre = predicateList.toArray(pre);
            return query.where(pre).getRestriction();
        };
    }

    /**
     * 实体转换为应用列表结果
     *
     * @param entityPage {@link List}
     * @return {@link List}
     */
    default Page<AppListResult> entityConvertToAppListResult(org.springframework.data.domain.Page<AppEntity> entityPage) {
        Page<AppListResult> page = new Page<>();
        List<AppListResult> list = Lists.newArrayList();
        for (AppEntity entity : entityPage.getContent()) {
            AppListResult result = entityConvertToAppListResult(entity);
            // 如果备注为空、放置详情，由于 entityConvertToAppListResult 为共用，所以这里单独处理。
            if (StringUtils.isBlank(result.getRemark())) {
                ApplicationServiceLoader loader = ApplicationContextService
                    .getBean(ApplicationServiceLoader.class);
                ApplicationService applicationService = loader
                    .getApplicationService(entity.getTemplate());
                if (!Objects.isNull(applicationService)) {
                    result.setRemark(applicationService.getDescription());
                }
            }
            list.add(result);
        }
        page.setList(list);
        //@formatter:off
        page.setPagination(Page.Pagination.builder()
                .total(entityPage.getTotalElements())
                .totalPages(entityPage.getTotalPages())
                .current(entityPage.getPageable().getPageNumber() + 1)
                .build());
        //@formatter:on
        return page;
    }

    /**
     * 实体转应用管理列表
     *
     * @param entity {@link AppEntity}
     * @return {@link AppListResult}
     */
    default AppListResult entityConvertToAppListResult(AppEntity entity) {
        if (entity == null) {
            return null;
        }

        AppListResult appListResult = new AppListResult();
        if (entity.getId() != null) {
            appListResult.setId(String.valueOf(entity.getId()));
        }
        appListResult.setName(entity.getName());
        appListResult.setType(entity.getType());
        appListResult.setIcon(entity.getIcon());
        //图标未配置，所以先从模版中拿
        if (StringUtils.isBlank(entity.getIcon())) {
            ApplicationService applicationService = getApplicationServiceLoader()
                .getApplicationService(entity.getTemplate());
            appListResult.setIcon(applicationService.getBase64Icon());
        }
        appListResult.setTemplate(entity.getTemplate());
        appListResult.setProtocol(entity.getProtocol());
        appListResult.setEnabled(entity.getEnabled());
        appListResult.setRemark(entity.getRemark());
        return appListResult;
    }

    /**
     * 实体转应用返回
     *
     * @param entity {@link AppEntity}
     * @param groupIds {@link List}
     * @return {@link AppGetResult}
     */
    default AppGetResult entityConvertToAppResult(AppEntity entity, List<String> groupIds) {
        if (entity == null) {
            return null;
        }
        AppGetResult appGetResult = new AppGetResult();

        if (entity.getId() != null) {
            appGetResult.setId(String.valueOf(entity.getId()));
        }
        appGetResult.setName(entity.getName());
        appGetResult.setCode(entity.getCode());
        appGetResult.setClientId(entity.getClientId());
        appGetResult.setClientSecret(entity.getClientSecret());
        appGetResult.setType(entity.getType());
        appGetResult.setGroupIds(groupIds.stream().map(String::valueOf).toList());
        //图标未配置，所以先从模版中拿
        if (StringUtils.isBlank(entity.getIcon())) {
            ApplicationService applicationService = getApplicationServiceLoader()
                .getApplicationService(entity.getTemplate());
            appGetResult.setIcon(applicationService.getBase64Icon());
        } else {
            appGetResult.setIcon(entity.getIcon());
        }
        appGetResult.setTemplate(entity.getTemplate());
        appGetResult.setProtocol(entity.getProtocol());
        appGetResult.setAuthorizationType(entity.getAuthorizationType());
        appGetResult.setProtocolName(entity.getProtocol().getDesc());
        appGetResult.setEnabled(entity.getEnabled());
        appGetResult.setCreateTime(entity.getCreateTime());
        appGetResult.setRemark(entity.getRemark());
        return appGetResult;
    }

    /**
     * 将应用修改对象转换为entity
     *
     * @param param {@link AppUpdateParam}
     * @return {@link AppEntity}
     */
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "configured", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "initLoginUrl", ignore = true)
    @Mapping(target = "clientSecret", ignore = true)
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "template", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "protocol", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    AppEntity appUpdateParamConverterToEntity(AppUpdateParam param);

    /**
     * 获取 ApplicationServiceLoader
     *
     * @return {@link ApplicationServiceLoader}
     */
    private ApplicationServiceLoader getApplicationServiceLoader() {
        return ApplicationContextService.getBean(ApplicationServiceLoader.class);
    }
}
