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
package cn.topiam.employee.console.converter.permission;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

import cn.topiam.employee.common.entity.permission.PermissionRoleEntity;
import cn.topiam.employee.common.entity.permission.QPermissionRoleEntity;
import cn.topiam.employee.console.pojo.query.permission.PermissionRoleListQuery;
import cn.topiam.employee.console.pojo.result.permission.PermissionRoleListResult;
import cn.topiam.employee.console.pojo.result.permission.PermissionRoleResult;
import cn.topiam.employee.console.pojo.save.permission.PermissionRoleCreateParam;
import cn.topiam.employee.console.pojo.update.permission.PermissionRoleUpdateParam;
import cn.topiam.employee.support.repository.page.domain.Page;

/**
 * 角色映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/14 22:45
 */
@Mapper(componentModel = "spring")
public interface PermissionRoleConverter {

    /**
     * 角色实体转换为角色分页结果
     *
     * @param page {@link Page}
     * @return {@link Page}
     */
    default Page<PermissionRoleListResult> entityConvertToRolePaginationResult(org.springframework.data.domain.Page<PermissionRoleEntity> page) {
        Page<PermissionRoleListResult> result = new Page<>();
        if (!CollectionUtils.isEmpty(page.getContent())) {
            List<PermissionRoleListResult> list = new ArrayList<>();
            for (PermissionRoleEntity user : page.getContent()) {
                list.add(entityConvertToRolePaginationResult(user));
            }
            //@formatter:off
            result.setPagination(Page.Pagination.builder()
                    .total(page.getTotalElements())
                    .totalPages(page.getTotalPages())
                    .current(page.getPageable().getPageNumber() + 1)
                    .build());
            //@formatter:on
            result.setList(list);
        }
        return result;
    }

    /**
     * 角色实体转换为角色分页结果
     *
     * @param page {@link PermissionRoleEntity}
     * @return {@link PermissionRoleListResult}
     */
    PermissionRoleListResult entityConvertToRolePaginationResult(PermissionRoleEntity page);

    /**
     * 角色创建参数转换为角色实体
     *
     * @param param {@link PermissionRoleCreateParam}
     * @return {@link PermissionRoleEntity}
     */
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", expression = "java(Boolean.TRUE)")
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    PermissionRoleEntity roleCreateParamConvertToEntity(PermissionRoleCreateParam param);

    /**
     * 角色更新参数转换为角色实体类
     *
     * @param param {@link PermissionRoleUpdateParam} 更新参数
     * @return {@link PermissionRoleEntity} 角色实体
     */
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "appId", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    PermissionRoleEntity roleUpdateParamConvertToEntity(PermissionRoleUpdateParam param);

    /**
     * 实体转系统详情结果
     *
     * @param role {@link PermissionRoleEntity}
     * @return {@link PermissionRoleResult}
     */
    PermissionRoleResult entityConvertToRoleDetailResult(PermissionRoleEntity role);

    /**
     * 角色分页查询参数转实体
     *
     * @param query {@link PermissionRoleListQuery}
     * @return {@link PermissionRoleEntity}
     */
    default Predicate rolePaginationParamConvertToPredicate(PermissionRoleListQuery query) {
        QPermissionRoleEntity role = QPermissionRoleEntity.permissionRoleEntity;
        Predicate predicate = ExpressionUtils.and(role.isNotNull(), role.deleted.eq(Boolean.FALSE));
        //查询条件
        //@formatter:off
        // 角色名称
        predicate = StringUtils.isBlank(query.getName()) ? predicate : ExpressionUtils.and(predicate, role.name.like("%" + query.getName() + "%"));
        // 是否启用
        predicate = ObjectUtils.isEmpty(query.getEnabled()) ? predicate : ExpressionUtils.and(predicate, role.enabled.eq(query.getEnabled()));
        // 角色编码
        predicate = StringUtils.isBlank(query.getCode()) ? predicate : ExpressionUtils.and(predicate, role.code.eq(query.getCode()));
        // 所属应用
        predicate = ObjectUtils.isEmpty(query.getAppId()) ? predicate : ExpressionUtils.and(predicate, role.appId.eq(query.getAppId()));
        //@formatter:on
        return predicate;
    }
}
