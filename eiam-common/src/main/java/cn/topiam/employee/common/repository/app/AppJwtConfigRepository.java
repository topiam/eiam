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

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.app.AppJwtConfigEntity;
import cn.topiam.employee.support.repository.LogicDeleteRepository;
import static cn.topiam.employee.common.constant.ProtocolConstants.JWT_CONFIG_CACHE_NAME;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;

/**
 * @author TopIAM
 */
@Repository
@CacheConfig(cacheNames = { JWT_CONFIG_CACHE_NAME })
public interface AppJwtConfigRepository extends LogicDeleteRepository<AppJwtConfigEntity, Long>,
                                        QuerydslPredicateExecutor<AppJwtConfigEntity>,
                                        AppJwtConfigRepositoryCustomized {
    /**
     * 按应用 ID 删除
     *
     * @param appId {@link Long}
     */
    @CacheEvict(allEntries = true)
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE app_jwt_config SET " + SOFT_DELETE_SET
                   + " WHERE app_id = :appId", nativeQuery = true)
    void deleteByAppId(@Param("appId") Long appId);

    /**
     * delete
     *
     * @param id must not be {@literal null}.
     */
    @CacheEvict(allEntries = true)
    @Override
    void deleteById(@NotNull Long id);

    /**
     * save
     *
     * @param entity must not be {@literal null}.
     * @param <S>    {@link S}
     * @return {@link AppJwtConfigEntity}
     */
    @NotNull
    @Override
    @CacheEvict(allEntries = true)
    <S extends AppJwtConfigEntity> S save(@NotNull S entity);

    /**
     * 根据应用ID获取配置
     *
     * @param appId {@link Long}
     * @return {@link AppJwtConfigEntity}
     */
    Optional<AppJwtConfigEntity> findByAppId(Long appId);
}
