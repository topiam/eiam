/*
 * eiam-console - Employee Identity and Access Management Program
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
package cn.topiam.employee.console.converter.app;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

import cn.topiam.employee.common.entity.app.AppPermissionResourceEntity;
import cn.topiam.employee.common.entity.app.QAppPermissionResourceEntity;
import cn.topiam.employee.console.pojo.query.app.AppResourceListQuery;
import cn.topiam.employee.console.pojo.result.app.AppPermissionResourceGetResult;
import cn.topiam.employee.console.pojo.result.app.AppPermissionResourceListResult;
import cn.topiam.employee.console.pojo.save.app.AppPermissionResourceCreateParam;
import cn.topiam.employee.console.pojo.update.app.AppPermissionResourceUpdateParam;
import cn.topiam.employee.support.repository.page.domain.Page;

/**
 * 资源映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/14 22:45
 */
@Mapper(componentModel = "spring", uses = AppPermissionActionConverter.class)
public interface AppPermissionResourceConverter {

    /**
     * 资源分页查询参数转实体
     *
     * @param query {@link AppResourceListQuery}
     * @return {@link Predicate}
     */
    default Predicate resourcePaginationParamConvertToPredicate(AppResourceListQuery query) {
        QAppPermissionResourceEntity resource = QAppPermissionResourceEntity.appPermissionResourceEntity;
        Predicate predicate = ExpressionUtils.and(resource.isNotNull(),
            resource.isDeleted.eq(Boolean.FALSE));
        //查询条件
        //@formatter:off
        // 资源名称
        predicate = StringUtils.isBlank(query.getName()) ? predicate : ExpressionUtils.and(predicate, resource.name.like("%" + query.getName() + "%"));
        // 所属应用
        predicate = ObjectUtils.isEmpty(query.getAppId()) ? predicate : ExpressionUtils.and(predicate, resource.appId.eq(query.getAppId()));
        //@formatter:on
        return predicate;
    }

    /**
     * 资源创建参数转实体类
     *
     * @param param {@link AppPermissionResourceCreateParam}
     * @return {@link AppPermissionResourceEntity}
     */
    @Mapping(target = "actions", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    AppPermissionResourceEntity resourceCreateParamConvertToEntity(AppPermissionResourceCreateParam param);

    /**
     * 资源修改参数转实体类
     *
     * @param param {@link AppPermissionResourceCreateParam}
     * @return {@link AppPermissionResourceEntity}
     */
    @Mapping(target = "actions", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    AppPermissionResourceEntity resourceUpdateParamConvertToEntity(AppPermissionResourceUpdateParam param);

    /**
     * 资源转换为资源列表结果
     *
     * @param page {@link Page}
     * @return {@link Page}
     */
    default Page<AppPermissionResourceListResult> entityConvertToResourceListResult(org.springframework.data.domain.Page<AppPermissionResourceEntity> page) {
        Page<AppPermissionResourceListResult> result = new Page<>();
        List<AppPermissionResourceEntity> pageList = page.getContent();
        if (!CollectionUtils.isEmpty(pageList)) {
            List<AppPermissionResourceListResult> list = new ArrayList<>();
            for (AppPermissionResourceEntity resource : pageList) {
                list.add(entityConvertToResourceListResult(resource));
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
     * 实体转换为资源列表结果
     *
     * @param data {@link AppPermissionResourceEntity}
     * @return {@link AppPermissionResourceListResult}
     */
    AppPermissionResourceListResult entityConvertToResourceListResult(AppPermissionResourceEntity data);

    /**
     * 实体转获取详情返回
     *
     * @param resource {@link AppPermissionResourceEntity}
     * @return {@link AppPermissionResourceGetResult}
     */
    @Mapping(target = "actions", source = "actions")
    AppPermissionResourceGetResult entityConvertToResourceGetResult(AppPermissionResourceEntity resource);
}
