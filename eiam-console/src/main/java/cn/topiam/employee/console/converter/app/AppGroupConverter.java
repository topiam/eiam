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

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.google.common.collect.Lists;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

import cn.topiam.employee.common.entity.app.AppAccountEntity;
import cn.topiam.employee.common.entity.app.AppGroupEntity;
import cn.topiam.employee.common.entity.app.QAppGroupEntity;
import cn.topiam.employee.console.pojo.query.app.AppGroupQuery;
import cn.topiam.employee.console.pojo.result.app.AppGroupGetResult;
import cn.topiam.employee.console.pojo.result.app.AppGroupListResult;
import cn.topiam.employee.console.pojo.save.app.AppAccountCreateParam;
import cn.topiam.employee.console.pojo.save.app.AppGroupCreateParam;
import cn.topiam.employee.console.pojo.update.app.AppGroupUpdateParam;
import cn.topiam.employee.support.repository.page.domain.Page;

/**
 * 分组映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/8/31 15:45
 */
@Mapper(componentModel = "spring")
public interface AppGroupConverter {

    /**
     * 查询分组列表参数转换为  Querydsl  Predicate
     *
     * @param query {@link AppGroupQuery} query
     * @return {@link Predicate}
     */
    default Predicate queryAppGroupListParamConvertToPredicate(AppGroupQuery query) {
        QAppGroupEntity appGroup = QAppGroupEntity.appGroupEntity;
        Predicate predicate = ExpressionUtils.and(appGroup.isNotNull(),
            appGroup.deleted.eq(Boolean.FALSE));
        //查询条件
        //@formatter:off
        predicate = StringUtils.isBlank(query.getName()) ? predicate : ExpressionUtils.and(predicate, appGroup.name.like("%" + query.getName() + "%"));
        predicate = Objects.isNull(query.getEnabled()) ? predicate : ExpressionUtils.and(predicate, appGroup.enabled.eq(query.getEnabled()));
        //@formatter:on
        return predicate;
    }

    /**
     * 实体转换为分组列表结果
     *
     * @param entityPage {@link List}
     * @return {@link List}
     */
    default Page<AppGroupListResult> entityConvertToAppGroupListResult(org.springframework.data.domain.Page<AppGroupEntity> entityPage) {
        Page<AppGroupListResult> page = new Page<>();
        List<AppGroupListResult> list = Lists.newArrayList();
        for (AppGroupEntity entity : entityPage.getContent()) {
            AppGroupListResult result = entityConvertToAppGroupListResult(entity);
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
     * 实体转分组管理列表
     *
     * @param entity {@link AppGroupEntity}
     * @return {@link AppGroupListResult}
     */
    default AppGroupListResult entityConvertToAppGroupListResult(AppGroupEntity entity) {
        if (entity == null) {
            return null;
        }

        AppGroupListResult appGroupListResult = new AppGroupListResult();
        if (entity.getId() != null) {
            appGroupListResult.setId(String.valueOf(entity.getId()));
        }
        appGroupListResult.setName(entity.getName());
        appGroupListResult.setCode(entity.getCode());
        appGroupListResult.setEnabled(entity.getEnabled());
        appGroupListResult.setRemark(entity.getRemark());
        return appGroupListResult;
    }

    /**
     * 实体转分组返回
     *
     * @param entity {@link AppGroupEntity}
     * @return {@link AppGroupGetResult}
     */
    default AppGroupGetResult entityConvertToAppGroupResult(AppGroupEntity entity) {
        if (entity == null) {
            return null;
        }
        AppGroupGetResult appGroupGetResult = new AppGroupGetResult();

        if (entity.getId() != null) {
            appGroupGetResult.setId(String.valueOf(entity.getId()));
        }
        appGroupGetResult.setName(entity.getName());
        appGroupGetResult.setEnabled(entity.getEnabled());
        appGroupGetResult.setCreateTime(entity.getCreateTime());
        return appGroupGetResult;
    }

    /**
     * 将分组修改对象转换为entity
     *
     * @param param {@link AppGroupUpdateParam}
     * @return {@link AppGroupEntity}
     */
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "enabled", ignore = false)
    AppGroupEntity appGroupUpdateParamConverterToEntity(AppGroupUpdateParam param);

    /**
     * 分组新增参数转换分组实体
     *
     * @param param {@link AppAccountCreateParam}
     * @return {@link AppAccountEntity}
     */
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    AppGroupEntity appGroupCreateParamConvertToEntity(AppGroupCreateParam param);

}
