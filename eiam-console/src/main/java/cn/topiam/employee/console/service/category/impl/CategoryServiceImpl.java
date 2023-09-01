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
package cn.topiam.employee.console.service.category.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.app.QAppEntity;
import cn.topiam.employee.common.entity.category.CategoryEntity;
import cn.topiam.employee.common.repository.category.CategoryRepository;
import cn.topiam.employee.console.converter.category.CategoryConverter;
import cn.topiam.employee.console.pojo.query.category.CategoryQuery;
import cn.topiam.employee.console.pojo.result.category.CategoryGetResult;
import cn.topiam.employee.console.pojo.result.category.CategoryListResult;
import cn.topiam.employee.console.pojo.save.category.CategoryCreateParam;
import cn.topiam.employee.console.pojo.update.category.CategoryUpdateParam;
import cn.topiam.employee.console.service.category.CategoryService;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.util.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_TIME;

/**
 * CategoryServiceImpl
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/8/31 14:23
 */
@Service
@Slf4j
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    /**
     * 获取分组（分页）
     *
     * @param pageModel {@link PageModel}
     * @param query     {@link CategoryQuery}
     * @return {@link CategoryListResult}
     */
    @Override
    public Page<CategoryListResult> getCategoryList(PageModel pageModel, CategoryQuery query) {
        //查询条件
        Predicate predicate = categoryConverter.queryCategoryListParamConvertToPredicate(query);
        OrderSpecifier<LocalDateTime> desc = QAppEntity.appEntity.updateTime.desc();
        //分页条件
        QPageRequest request = QPageRequest.of(pageModel.getCurrent(), pageModel.getPageSize(),
            desc);
        //查询映射
        org.springframework.data.domain.Page<CategoryEntity> list = categoryRepository
            .findAll(predicate, request);
        return categoryConverter.entityConvertToCategoryListResult(list);
    }

    /**
     * 创建分组
     *
     * @param param {@link CategoryCreateParam}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createCategory(CategoryCreateParam param) {
        // TODO 创建后没有数据权限
        CategoryEntity entity = categoryConverter.categoryCreateParamConvertToEntity(param);
        categoryRepository.save(entity);
        AuditContext.setTarget(
            Target.builder().id(String.valueOf(entity.getId())).type(TargetType.CATEGORY).build());
        return true;
    }

    /**
     * 修改分组
     *
     * @param param {@link CategoryUpdateParam}
     * @return {@link Boolean}
     */
    @Override
    public boolean updateCategory(CategoryUpdateParam param) {
        CategoryEntity category = categoryRequireNonNull(param.getId());
        CategoryEntity entity = categoryConverter.categoryUpdateParamConverterToEntity(param);
        BeanUtils.merge(entity, category, LAST_MODIFIED_TIME, LAST_MODIFIED_BY);
        categoryRepository.save(category);
        AuditContext.setTarget(
            Target.builder().id(param.getId().toString()).type(TargetType.CATEGORY).build());
        return true;
    }

    /**
     * 删除分组
     *
     * @param id {@link  String}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long id) {
        categoryRequireNonNull(id);
        categoryRepository.deleteById(id);
        AuditContext
            .setTarget(Target.builder().id(id.toString()).type(TargetType.CATEGORY).build());
        return true;
    }

    /**
     * 获取单个分组详情
     *
     * @param id {@link Long}
     * @return {@link CategoryEntity}
     */
    @Override
    public CategoryGetResult getCategory(Long id) {
        Optional<CategoryEntity> optional = categoryRepository.findById(id);
        if (optional.isPresent()) {
            CategoryEntity entity = optional.get();
            return categoryConverter.entityConvertToCategoryResult(entity);
        }
        return null;

    }

    /**
     * 启用分组
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean enableCategory(String id) {
        categoryRequireNonNull(Long.valueOf(id));
        Integer count = categoryRepository.updateCategoryStatus(Long.valueOf(id), Boolean.TRUE);
        AuditContext.setTarget(Target.builder().id(id).type(TargetType.CATEGORY).build());
        return count > 0;
    }

    /**
     * 禁用分组
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean disableCategory(String id) {
        categoryRequireNonNull(Long.valueOf(id));
        Integer count = categoryRepository.updateCategoryStatus(Long.valueOf(id), Boolean.FALSE);
        AuditContext.setTarget(Target.builder().id(id).type(TargetType.CATEGORY).build());
        return count > 0;
    }

    /**
     * 查询并检查分组是否为空，非空返回
     *
     * @param id {@link Long}
     * @return {@link CategoryEntity}
     */
    private CategoryEntity categoryRequireNonNull(Long id) {
        Optional<CategoryEntity> optional = categoryRepository.findById(id);
        if (optional.isEmpty()) {
            AuditContext.setContent("操作失败，分组不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        return optional.get();
    }

    /**
     * CategoryRepository
     */
    private final CategoryRepository categoryRepository;

    /**
     * CategoryConverter
     */
    private final CategoryConverter  categoryConverter;
}
