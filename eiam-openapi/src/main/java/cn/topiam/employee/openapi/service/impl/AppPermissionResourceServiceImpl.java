/*
 * eiam-openapi - Employee Identity and Access Management
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
package cn.topiam.employee.openapi.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import cn.topiam.employee.common.entity.app.AppPermissionActionEntity;
import cn.topiam.employee.common.entity.app.AppPermissionResourceEntity;
import cn.topiam.employee.common.entity.app.QAppPermissionResourceEntity;
import cn.topiam.employee.common.enums.CheckValidityType;
import cn.topiam.employee.common.exception.app.AppResourceNotExistException;
import cn.topiam.employee.common.repository.app.AppPermissionActionRepository;
import cn.topiam.employee.common.repository.app.AppPermissionPolicyRepository;
import cn.topiam.employee.common.repository.app.AppPermissionResourceRepository;
import cn.topiam.employee.openapi.converter.app.AppPermissionResourceConverter;
import cn.topiam.employee.openapi.pojo.request.app.AppPermissionsActionParam;
import cn.topiam.employee.openapi.pojo.request.app.query.AppResourceListQuery;
import cn.topiam.employee.openapi.pojo.request.app.query.OpenApiPolicyQuery;
import cn.topiam.employee.openapi.pojo.request.app.save.AppPermissionResourceCreateParam;
import cn.topiam.employee.openapi.pojo.request.app.update.AppPermissionResourceUpdateParam;
import cn.topiam.employee.openapi.pojo.response.app.AppPermissionResourceGetResult;
import cn.topiam.employee.openapi.pojo.response.app.AppPermissionResourceListResult;
import cn.topiam.employee.openapi.service.AppPermissionResourceService;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 资源权限 服务实现类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-10
 */
@Service
@RequiredArgsConstructor
public class AppPermissionResourceServiceImpl implements AppPermissionResourceService {

    /**
     * 获取资源列表
     *
     * @param page  {@link PageModel}
     * @param query {@link OpenApiPolicyQuery}
     * @return {@link AppPermissionResourceListResult}
     */
    @Override
    public Page<AppPermissionResourceListResult> getPermissionResourceList(PageModel page,
                                                                           AppResourceListQuery query) {
        org.springframework.data.domain.Page<AppPermissionResourceEntity> data;
        Predicate predicate = appPermissionResourceConverter
            .resourcePaginationParamConvertToPredicate(query);
        QPageRequest request = QPageRequest.of(page.getCurrent(), page.getPageSize());
        data = appResourceRepository.findAll(predicate, request);
        return appPermissionResourceConverter.entityConvertToResourceListResult(data);
    }

    /**
     * 获取资源
     *
     * @param id {@link String}
     * @return {@link AppPermissionResourceGetResult}
     */
    @Override
    public AppPermissionResourceGetResult getPermissionResource(String id) {
        AppPermissionResourceEntity resource = appResourceRepository.findById(Long.valueOf(id))
            .orElseThrow(AppResourceNotExistException::new);
        return appPermissionResourceConverter.entityConvertToResourceGetResult(resource);
    }

    /**
     * 删除资源
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePermissionResource(String id) {
        Long resourceId = Long.valueOf(id);
        AppPermissionResourceEntity resource = appResourceRepository.findById(resourceId)
            .orElseThrow(AppResourceNotExistException::new);
        List<AppPermissionActionEntity> actionList = appPermissionActionRepository
            .findAllByResource(resource);
        List<Long> objectIdList = new ArrayList<>(
            actionList.stream().map(AppPermissionActionEntity::getId).toList());
        objectIdList.add(resourceId);
        appPermissionPolicyRepository.deleteAllByObjectIdIn(objectIdList);
        appResourceRepository.deleteById(resourceId);
        return true;
    }

    /**
     * 创建资源
     *
     * @param param {@link AppPermissionResourceCreateParam}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createPermissionResource(AppPermissionResourceCreateParam param) {
        AppPermissionResourceEntity resource = appPermissionResourceConverter
            .resourceCreateParamConvertToEntity(param);
        buildActions(param.getActions(), resource);
        // 新增资源
        appResourceRepository.save(resource);
        return true;
    }

    /**
     * 更新资源
     *
     * @param param {@link AppPermissionResourceUpdateParam}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePermissionResource(AppPermissionResourceUpdateParam param) {
        AppPermissionResourceEntity resource = appPermissionResourceConverter
            .resourceUpdateParamConvertToEntity(param);
        buildActions(param.getActions(), resource);
        // 查询资源下所有权限
        List<AppPermissionActionEntity> actionList = appPermissionActionRepository
            .findAllByResource(resource);
        // 取出未删除的权限id
        Set<Long> reservedSet = resource.getActions().stream().map(AppPermissionActionEntity::getId)
            .collect(Collectors.toSet());
        // 过滤要删除的权限id
        List<Long> removeActions = actionList.stream()
            .filter(item -> reservedSet.contains(item.getId()))
            .map(AppPermissionActionEntity::getId).toList();
        appPermissionPolicyRepository.deleteAllByObjectIdIn(removeActions);
        // 更新资源
        appResourceRepository.save(resource);
        return true;
    }

    /**
     * 参数有效性验证
     *
     * @param type     {@link CheckValidityType}
     * @param value    {@link String}
     * @param appId {@link Long}
     * @param id       {@link Long}
     * @return {@link Boolean}
     */
    @SuppressWarnings("DuplicatedCode")
    @Override
    public Boolean permissionResourceParamCheck(CheckValidityType type, String value, Long appId,
                                                Long id) {
        QAppPermissionResourceEntity role = QAppPermissionResourceEntity.appPermissionResourceEntity;
        AppPermissionResourceEntity entity = new AppPermissionResourceEntity();
        boolean result = false;
        // ID存在说明是修改操作，查询一下当前数据
        if (Objects.nonNull(id)) {
            entity = appResourceRepository.findById(id)
                .orElseThrow(AppResourceNotExistException::new);
        }
        //资源名称
        if (CheckValidityType.NAME.equals(type)) {
            if (StringUtils.equals(entity.getName(), value)) {
                return true;
            }
            BooleanExpression eq = role.name.eq(value);
            eq.and(role.appId.eq(appId));
            result = !appResourceRepository.exists(eq);
        }
        //资源编码
        if (CheckValidityType.CODE.equals(type)) {
            if (StringUtils.equals(entity.getCode(), value)) {
                return true;
            }
            BooleanExpression eq = role.code.eq(value);
            eq.and(role.appId.eq(appId));
            result = !appResourceRepository.exists(eq);
        }
        return result;
    }

    /**
     * 批量处理actions
     *
     * @param permissions {@link List<AppPermissionsActionParam>}
     * @param resource {@link AppPermissionResourceEntity>}
     */
    private void buildActions(List<AppPermissionsActionParam> permissions,
                              AppPermissionResourceEntity resource) {
        // 权限
        List<AppPermissionActionEntity> list = new ArrayList<>();
        for (AppPermissionsActionParam p : permissions) {
            AppPermissionActionEntity entity = new AppPermissionActionEntity();
            entity.setResource(resource);
            entity.setType(p.getType());
            entity.setName(p.getName());
            //API需要单独处理
            entity.setValue(p.getValue());
            list.add(entity);
        }
        resource.setActions(list);
    }

    private final AppPermissionResourceConverter  appPermissionResourceConverter;

    private final AppPermissionResourceRepository appResourceRepository;
    /**
     * PolicyRepository
     */
    private final AppPermissionPolicyRepository   appPermissionPolicyRepository;
    /**
     * ActionRepository
     */
    private final AppPermissionActionRepository   appPermissionActionRepository;
}
