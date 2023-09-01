/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.repository.category;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.category.CategoryEntity;
import cn.topiam.employee.support.repository.LogicDeleteRepository;
import static cn.topiam.employee.common.constant.CategoryConstants.CATEGORY_CACHE_NAME;

/**
 * @author TopIAM
 */
@Repository
@CacheConfig(cacheNames = { CATEGORY_CACHE_NAME })
public interface CategoryRepository extends LogicDeleteRepository<CategoryEntity, Long>,
                                    QuerydslPredicateExecutor<CategoryEntity> {

    /**
     * 根据分组ID查询已启用应用
     *
     * @param id {@link Long}
     * @return {@link AppEntity}
     */
    @Cacheable
    Optional<CategoryEntity> findByIdAndEnabledTrue(Long id);

    /**
     * save
     *
     * @param entity must not be {@literal null}.
     * @param <S>    {@link S}
     * @return {@link AppEntity}
     */
    @NotNull
    @Override
    @CacheEvict(allEntries = true)
    <S extends CategoryEntity> S save(@NotNull S entity);

    /**
     * 更新应用状态
     *
     * @param id      {@link  Long}
     * @param enabled {@link  Boolean}
     * @return {@link  Boolean}
     */
    @Modifying
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE category SET is_enabled = :enabled WHERE id_ = :id", nativeQuery = true)
    Integer updateCategoryStatus(@Param(value = "id") Long id,
                                 @Param(value = "enabled") Boolean enabled);

    /**
     * delete
     *
     * @param id must not be {@literal null}.
     */
    @Override
    @CacheEvict(allEntries = true)
    void deleteById(@NotNull Long id);

    /**
     * find by id
     *
     * @param id must not be {@literal null}.
     * @return {@link AppEntity}
     */
    @NotNull
    @Override
    @Cacheable
    Optional<CategoryEntity> findById(@NotNull @Param(value = "id") Long id);

    /**
     * find by id
     *
     * @param id must not be {@literal null}.
     * @return {@link AppEntity}
     */
    @NotNull
    @Cacheable
    @Query(value = "SELECT * FROM category WHERE id_ = :id", nativeQuery = true)
    Optional<AppEntity> findByIdContainsDeleted(@NotNull @Param(value = "id") Long id);

}
