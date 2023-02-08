/*
 * eiam-common - Employee Identity and Access Management Program
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
package cn.topiam.employee.common.repository.app;

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

import cn.topiam.employee.common.entity.app.AppAccountEntity;
import cn.topiam.employee.support.repository.LogicDeleteRepository;
import static cn.topiam.employee.common.constants.ProtocolConstants.APP_ACCOUNT_CACHE_NAME;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;

/**
 * 应用账户
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/3 22:26
 */
@Repository
@CacheConfig(cacheNames = { APP_ACCOUNT_CACHE_NAME })
public interface AppAccountRepository extends LogicDeleteRepository<AppAccountEntity, Long>,
                                      QuerydslPredicateExecutor<AppAccountEntity>,
                                      AppAccountRepositoryCustomized {
    /**
     * save
     *
     * @param entity must not be {@literal null}.
     * @param <S>    {@link S}
     * @return {@link AppAccountEntity}
     */
    @NotNull
    @Override
    @CacheEvict(allEntries = true)
    <S extends AppAccountEntity> S save(@NotNull S entity);

    /**
     * delete
     *
     * @param id must not be {@literal null}.
     */
    @CacheEvict(allEntries = true)
    @Override
    void deleteById(@NotNull Long id);

    /**
     * 根据应用ID，用户ID查询应用账户
     *
     * @param appId  {@link Long}
     * @param userId {@link Long}
     * @return {@link Optional}
     */
    @Cacheable
    Optional<AppAccountEntity> findByAppIdAndUserId(Long appId, Long userId);

    /**
     * 根据appid删除所有的数据
     *
     * @param appId {@link Long}
     */
    @CacheEvict(allEntries = true)
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE app_account SET " + SOFT_DELETE_SET
                   + " WHERE app_id = :appId", nativeQuery = true)
    void deleteAllByAppId(@Param("appId") Long appId);

    /**
     * 根据userId 删除用户数据
     *
     * @param userId {@link Long}
     */
    @CacheEvict(allEntries = true)
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE app_account SET " + SOFT_DELETE_SET
                   + " WHERE user_id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") Long userId);
}
