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
package cn.topiam.employee.console.converter.category;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.google.common.collect.Lists;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

import cn.topiam.employee.common.entity.app.AppAccountEntity;
import cn.topiam.employee.common.entity.category.CategoryEntity;
import cn.topiam.employee.common.entity.category.QCategoryEntity;
import cn.topiam.employee.console.pojo.query.category.CategoryQuery;
import cn.topiam.employee.console.pojo.result.category.CategoryGetResult;
import cn.topiam.employee.console.pojo.result.category.CategoryListResult;
import cn.topiam.employee.console.pojo.save.app.AppAccountCreateParam;
import cn.topiam.employee.console.pojo.save.category.CategoryCreateParam;
import cn.topiam.employee.console.pojo.update.category.CategoryUpdateParam;
import cn.topiam.employee.support.repository.page.domain.Page;

/**
 * 分组映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/8/31 15:45
 */
@Mapper(componentModel = "spring")
public interface CategoryConverter {

    /**
     * 查询分组列表参数转换为  Querydsl  Predicate
     *
     * @param query {@link CategoryQuery} query
     * @return {@link Predicate}
     */
    default Predicate queryCategoryListParamConvertToPredicate(CategoryQuery query) {
        QCategoryEntity category = QCategoryEntity.categoryEntity;
        Predicate predicate = ExpressionUtils.and(category.isNotNull(),
            category.deleted.eq(Boolean.FALSE));
        //查询条件
        //@formatter:off
        predicate = StringUtils.isBlank(query.getName()) ? predicate : ExpressionUtils.and(predicate, category.name.like("%" + query.getName() + "%"));
        predicate = Objects.isNull(query.getEnabled()) ? predicate : ExpressionUtils.and(predicate, category.enabled.eq(query.getEnabled()));
        //@formatter:on
        return predicate;
    }

    /**
     * 实体转换为分组列表结果
     *
     * @param entityPage {@link List}
     * @return {@link List}
     */
    default Page<CategoryListResult> entityConvertToCategoryListResult(org.springframework.data.domain.Page<CategoryEntity> entityPage) {
        Page<CategoryListResult> page = new Page<>();
        List<CategoryListResult> list = Lists.newArrayList();
        for (CategoryEntity entity : entityPage.getContent()) {
            CategoryListResult result = entityConvertToCategoryListResult(entity);
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
     * @param entity {@link CategoryEntity}
     * @return {@link CategoryListResult}
     */
    default CategoryListResult entityConvertToCategoryListResult(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }

        CategoryListResult categoryListResult = new CategoryListResult();
        if (entity.getId() != null) {
            categoryListResult.setId(String.valueOf(entity.getId()));
        }
        categoryListResult.setName(entity.getName());
        categoryListResult.setSort(entity.getSort());
        categoryListResult.setEnabled(entity.getEnabled());
        categoryListResult.setRemark(entity.getRemark());
        return categoryListResult;
    }

    /**
     * 实体转分组返回
     *
     * @param entity {@link CategoryEntity}
     * @return {@link CategoryGetResult}
     */
    default CategoryGetResult entityConvertToCategoryResult(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        CategoryGetResult categoryGetResult = new CategoryGetResult();

        if (entity.getId() != null) {
            categoryGetResult.setId(String.valueOf(entity.getId()));
        }
        categoryGetResult.setName(entity.getName());
        categoryGetResult.setEnabled(entity.getEnabled());
        categoryGetResult.setCreateTime(entity.getCreateTime());
        return categoryGetResult;
    }

    /**
     * 将分组修改对象转换为entity
     *
     * @param param {@link CategoryUpdateParam}
     * @return {@link CategoryEntity}
     */
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "sort", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    CategoryEntity categoryUpdateParamConverterToEntity(CategoryUpdateParam param);

    /**
     * 分组新增参数转换分组实体
     *
     * @param param {@link AppAccountCreateParam}
     * @return {@link AppAccountEntity}
     */
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sort", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    CategoryEntity categoryCreateParamConvertToEntity(CategoryCreateParam param);

}
