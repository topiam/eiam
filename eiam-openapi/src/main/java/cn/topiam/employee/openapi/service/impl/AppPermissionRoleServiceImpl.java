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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import cn.topiam.employee.common.entity.app.AppPermissionRoleEntity;
import cn.topiam.employee.common.entity.app.QAppPermissionRoleEntity;
import cn.topiam.employee.common.enums.CheckValidityType;
import cn.topiam.employee.common.exception.app.AppRoleNotExistException;
import cn.topiam.employee.common.repository.app.AppPermissionPolicyRepository;
import cn.topiam.employee.common.repository.app.AppPermissionRoleRepository;
import cn.topiam.employee.openapi.converter.app.AppPermissionRoleConverter;
import cn.topiam.employee.openapi.pojo.request.app.query.AppPermissionRoleListQuery;
import cn.topiam.employee.openapi.pojo.request.app.save.AppPermissionRoleCreateParam;
import cn.topiam.employee.openapi.pojo.request.app.update.PermissionRoleUpdateParam;
import cn.topiam.employee.openapi.pojo.response.app.AppPermissionRoleListResult;
import cn.topiam.employee.openapi.pojo.response.app.AppPermissionRoleResult;
import cn.topiam.employee.openapi.service.AppPermissionRoleService;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.util.BeanUtils;

import lombok.RequiredArgsConstructor;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_TIME;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-10
 */
@Service
@RequiredArgsConstructor
public class AppPermissionRoleServiceImpl implements AppPermissionRoleService {

    /**
     * 获取所有角色（分页）
     *
     * @param page {@link PageModel}
     * @return {@link AppPermissionRoleListResult}
     */
    @Override
    public Page<AppPermissionRoleListResult> getPermissionRoleList(PageModel page,
                                                                   AppPermissionRoleListQuery query) {
        org.springframework.data.domain.Page<AppPermissionRoleEntity> data;
        Predicate predicate = appPermissionRoleConverter
            .rolePaginationParamConvertToPredicate(query);
        QPageRequest request = QPageRequest.of(page.getCurrent(), page.getPageSize());
        data = appPermissionRoleRepository.findAll(predicate, request);
        return appPermissionRoleConverter.entityConvertToRolePaginationResult(data);
    }

    /**
     * 创建系统
     *
     * @param param {@link AppPermissionRoleCreateParam}
     * @return {@link Boolean}
     */
    @Override
    public boolean createPermissionRole(AppPermissionRoleCreateParam param) {
        AppPermissionRoleEntity entity = appPermissionRoleConverter
            .roleCreateParamConvertToEntity(param);
        appPermissionRoleRepository.save(entity);
        return true;
    }

    /**
     * @param param {@link PermissionRoleUpdateParam}
     * @return {@link Boolean}
     */
    @Override
    public boolean updatePermissionRole(PermissionRoleUpdateParam param) {
        AppPermissionRoleEntity source = appPermissionRoleConverter
            .roleUpdateParamConvertToEntity(param);
        AppPermissionRoleEntity target = appPermissionRoleRepository
            .findById(Long.valueOf(param.getId())).orElseThrow(AppRoleNotExistException::new);
        BeanUtils.merge(source, target, LAST_MODIFIED_TIME, LAST_MODIFIED_BY);
        appPermissionRoleRepository.save(target);
        return true;
    }

    /**
     * 删除角色
     *
     * @param ids {@link String}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePermissionRole(String ids) {
        List<String> idList = Arrays.stream(ids.split(",")).toList();
        List<Long> longIds = idList.stream().map(Long::parseLong).toList();
        appPermissionRoleRepository.deleteAllById(longIds);
        // 删除对应策略
        appPermissionPolicyRepository.deleteAllBySubjectIdIn(idList);
        appPermissionPolicyRepository.deleteAllByObjectIdIn(longIds);
        return true;
    }

    /**
     * 角色详情
     *
     * @param id {@link Long}
     * @return {@link AppPermissionRoleResult}
     */
    @Override
    public AppPermissionRoleResult getPermissionRole(Long id) {
        //查询
        Optional<AppPermissionRoleEntity> entity = appPermissionRoleRepository.findById(id);
        //映射
        return appPermissionRoleConverter.entityConvertToRoleDetailResult(entity.orElse(null));
    }

    /**
     * 参数有效性验证
     *
     * @param type     {@link CheckValidityType}
     * @param value    {@link String}
     * @param id       {@link Long}
     * @param appId {@link Long}
     * @return {@link Boolean}
     */
    @SuppressWarnings("DuplicatedCode")
    @Override
    public Boolean permissionRoleParamCheck(CheckValidityType type, String value, Long appId,
                                            Long id) {
        QAppPermissionRoleEntity role = QAppPermissionRoleEntity.appPermissionRoleEntity;
        AppPermissionRoleEntity entity = new AppPermissionRoleEntity();
        boolean result = false;
        // ID存在说明是修改操作，查询一下当前数据
        if (Objects.nonNull(id)) {
            entity = appPermissionRoleRepository.findById(id)
                .orElseThrow(AppRoleNotExistException::new);
        }
        //角色编码
        if (CheckValidityType.CODE.equals(type)) {
            if (StringUtils.equals(entity.getCode(), value)) {
                return true;
            }
            BooleanExpression eq = role.code.eq(value);
            eq.and(role.appId.eq(appId));
            result = !appPermissionRoleRepository.exists(eq);
        }
        //角色名称
        if (CheckValidityType.NAME.equals(type)) {
            if (StringUtils.equals(entity.getName(), value)) {
                return true;
            }
            BooleanExpression eq = role.name.eq(value);
            eq.and(role.appId.eq(appId));
            result = !appPermissionRoleRepository.exists(eq);
        }
        return result;
    }

    /**
     * 更新角色状态
     *
     * @param id     {@link String}
     * @param status {@link Boolean}
     * @return {@link Boolean}
     */
    @Override
    public Boolean updatePermissionRoleStatus(String id, Boolean status) {
        appPermissionRoleRepository.updateStatus(id, status);
        return true;
    }

    /**
     * 用户数据映射器
     */
    private final AppPermissionRoleConverter    appPermissionRoleConverter;
    /**
     * RoleRepository
     */
    private final AppPermissionRoleRepository   appPermissionRoleRepository;
    /**
     * PolicyRepository
     */
    private final AppPermissionPolicyRepository appPermissionPolicyRepository;
}
