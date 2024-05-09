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

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.app.AppGroupEntity;
import static cn.topiam.employee.common.constant.AppConstants.APP_GROUP_CACHE_NAME_PREFIX;

/**
 * @author TopIAM
 */
@Repository
@CacheConfig(cacheNames = { APP_GROUP_CACHE_NAME_PREFIX })
public interface AppGroupRepository extends JpaRepository<AppGroupEntity, String>,
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
    void deleteById(@NotNull String id);

    /**
     * find by id
     *
     * @param id must not be {@literal null}.
     * @return {@link AppGroupEntity}
     */
    @NotNull
    @Override
    Optional<AppGroupEntity> findById(@NotNull String id);

    /**
     * 获取所有分组列表
     *
     * @return {@link List}
     */
    @Query(value = "FROM AppGroupEntity")
    List<AppGroupEntity> getAppGroupList();

    /**
     * 根据code查询
     *
     * @param code {@link String}
     * @return {@link AppGroupEntity}
     */
    Optional<AppGroupEntity> findByCode(@Param("code") String code);
}
