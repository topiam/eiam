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
package cn.topiam.employee.console.service.permission.impl;

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

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.permission.PermissionRoleEntity;
import cn.topiam.employee.common.entity.permission.QPermissionRoleEntity;
import cn.topiam.employee.common.enums.CheckValidityType;
import cn.topiam.employee.common.exception.app.AppRoleNotExistException;
import cn.topiam.employee.common.repository.permission.AppPermissionPolicyRepository;
import cn.topiam.employee.common.repository.permission.AppPermissionRoleRepository;
import cn.topiam.employee.console.converter.permission.PermissionRoleConverter;
import cn.topiam.employee.console.pojo.query.permission.PermissionRoleListQuery;
import cn.topiam.employee.console.pojo.result.permission.PermissionRoleListResult;
import cn.topiam.employee.console.pojo.result.permission.PermissionRoleResult;
import cn.topiam.employee.console.pojo.save.permission.PermissionRoleCreateParam;
import cn.topiam.employee.console.pojo.update.permission.PermissionRoleUpdateParam;
import cn.topiam.employee.console.service.permission.PermissionRoleService;
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
public class PermissionRoleServiceImpl implements PermissionRoleService {

    /**
     * 获取所有角色（分页）
     *
     * @param page {@link PageModel}
     * @return {@link PermissionRoleListResult}
     */
    @Override
    public Page<PermissionRoleListResult> getPermissionRoleList(PageModel page,
                                                                PermissionRoleListQuery query) {
        org.springframework.data.domain.Page<PermissionRoleEntity> data;
        Predicate predicate = permissionRoleConverter.rolePaginationParamConvertToPredicate(query);
        QPageRequest request = QPageRequest.of(page.getCurrent(), page.getPageSize());
        data = permissionRoleRepository.findAll(predicate, request);
        return permissionRoleConverter.entityConvertToRolePaginationResult(data);
    }

    /**
     * 创建系统
     *
     * @param param {@link PermissionRoleCreateParam}
     * @return {@link Boolean}
     */
    @Override
    public boolean createPermissionRole(PermissionRoleCreateParam param) {
        PermissionRoleEntity entity = permissionRoleConverter.roleCreateParamConvertToEntity(param);
        permissionRoleRepository.save(entity);
        AuditContext.setTarget(Target.builder().id(entity.getId().toString())
            .type(TargetType.APP_PERMISSION_ROLE).build());
        return true;
    }

    /**
     * @param param {@link PermissionRoleUpdateParam}
     * @return {@link Boolean}
     */
    @Override
    public boolean updatePermissionRole(PermissionRoleUpdateParam param) {
        PermissionRoleEntity source = permissionRoleConverter.roleUpdateParamConvertToEntity(param);
        PermissionRoleEntity target = permissionRoleRepository.findById(Long.valueOf(param.getId()))
            .orElseThrow(AppRoleNotExistException::new);
        BeanUtils.merge(source, target, LAST_MODIFIED_TIME, LAST_MODIFIED_BY);
        permissionRoleRepository.save(target);
        AuditContext.setTarget(Target.builder().id(target.getId().toString())
            .type(TargetType.APP_PERMISSION_ROLE).build());
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
        permissionRoleRepository.deleteAllById(longIds);
        // 删除对应策略
        permissionPolicyRepository.deleteAllBySubjectIdIn(idList);
        permissionPolicyRepository.deleteAllByObjectIdIn(longIds);
        AuditContext
            .setTarget(Target.builder().id(ids).type(TargetType.APP_PERMISSION_ROLE).build());
        return true;
    }

    /**
     * 角色详情
     *
     * @param id {@link Long}
     * @return {@link PermissionRoleResult}
     */
    @Override
    public PermissionRoleResult getPermissionRole(Long id) {
        //查询
        Optional<PermissionRoleEntity> entity = permissionRoleRepository.findById(id);
        //映射
        return permissionRoleConverter.entityConvertToRoleDetailResult(entity.orElse(null));
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
        QPermissionRoleEntity role = QPermissionRoleEntity.permissionRoleEntity;
        PermissionRoleEntity entity = new PermissionRoleEntity();
        boolean result = false;
        // ID存在说明是修改操作，查询一下当前数据
        if (Objects.nonNull(id)) {
            entity = permissionRoleRepository.findById(id)
                .orElseThrow(AppRoleNotExistException::new);
        }
        //角色编码
        if (CheckValidityType.CODE.equals(type)) {
            if (StringUtils.equals(entity.getCode(), value)) {
                return true;
            }
            BooleanExpression eq = role.code.eq(value);
            eq.and(role.appId.eq(appId));
            result = !permissionRoleRepository.exists(eq);
        }
        //角色名称
        if (CheckValidityType.NAME.equals(type)) {
            if (StringUtils.equals(entity.getName(), value)) {
                return true;
            }
            BooleanExpression eq = role.name.eq(value);
            eq.and(role.appId.eq(appId));
            result = !permissionRoleRepository.exists(eq);
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
        permissionRoleRepository.updateStatus(id, status);
        return true;
    }

    /**
     * 用户数据映射器
     */
    private final PermissionRoleConverter       permissionRoleConverter;
    /**
     * RoleRepository
     */
    private final AppPermissionRoleRepository   permissionRoleRepository;
    /**
     * PolicyRepository
     */
    private final AppPermissionPolicyRepository permissionPolicyRepository;
}
