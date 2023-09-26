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
package cn.topiam.employee.common.repository.app;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.app.AppGroupEntity;
import cn.topiam.employee.support.repository.LogicDeleteRepository;
import static cn.topiam.employee.common.constant.AppGroupConstants.APP_GROUP_CACHE_NAME;

/**
 * @author TopIAM
 */
@Repository
@CacheConfig(cacheNames = { APP_GROUP_CACHE_NAME })
public interface AppGroupRepository extends LogicDeleteRepository<AppGroupEntity, Long>,
                                    QuerydslPredicateExecutor<AppGroupEntity>,
                                    AppGroupRepositoryCustomized {

    /**
     * save
     *
     * @param entity must not be {@literal null}.
     * @param <S>    {@link S}
     * @return {@link AppGroupEntity}
     */
    @NotNull
    @Override
    @CacheEvict(allEntries = true)
    <S extends AppGroupEntity> S save(@NotNull S entity);

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
     * @return {@link AppGroupEntity}
     */
    @NotNull
    @Override
    Optional<AppGroupEntity> findById(@NotNull Long id);

    /**
     * find by id
     *
     * @param id must not be {@literal null}.
     * @return {@link AppGroupEntity}
     */
    @NotNull
    @Cacheable
    @Query(value = "FROM AppGroupEntity WHERE id = :id")
    Optional<AppGroupEntity> findByIdContainsDeleted(@NotNull @Param(value = "id") Long id);

    /**
     * 获取所有分组列表
     *
     * @return {@link List}
     */
    @Query(value = "FROM AppGroupEntity WHERE deleted = false ")
    List<AppGroupEntity> getAppGroupList();

    /**
     * 根据code列表查询
     *
     * @param codes {@link List}
     * @return {@link List}
     */
    List<AppGroupEntity> findAllByCodeIn(@Param("codes") Collection<String> codes);

    /**
     * 根据code查询
     *
     * @param code {@link String}
     * @return {@link AppGroupEntity}
     */
    Optional<AppGroupEntity> findByCode(@Param("code") String code);

}
