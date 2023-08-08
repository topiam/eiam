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
package cn.topiam.employee.common.repository.authentication;

import java.util.List;
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

import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.support.repository.LogicDeleteRepository;

/**
 * <p>
 * 身份认证源配置 Repository  接口
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-16
 */
@Repository
@CacheConfig(cacheNames = "idp")
public interface IdentityProviderRepository extends
                                            LogicDeleteRepository<IdentityProviderEntity, Long>,
                                            QuerydslPredicateExecutor<IdentityProviderEntity> {
    /**
     * 根据平台类型查询认证源配置
     *
     * @param type {@link String}
     * @return {@link IdentityProviderEntity}
     */
    List<IdentityProviderEntity> findByType(String type);

    /**
     * 根据平台类型查询是否显示
     *
     * @return {@link List}
     */
    List<IdentityProviderEntity> findByEnabledIsTrueAndDisplayedIsTrue();

    /**
     * 查询启用的社交认证源
     *
     * @return {@link List}
     */
    List<IdentityProviderEntity> findByEnabledIsTrue();

    /**
     * save
     *
     * @param entity {@link S}
     * @param <S>    {@link S}
     * @return {@link IdentityProviderEntity}
     */
    @NotNull
    @Override
    @CacheEvict(allEntries = true)
    <S extends IdentityProviderEntity> S save(@NotNull S entity);

    /**
     * Deletes the entity with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
     */
    @Override
    @CacheEvict(allEntries = true)
    void deleteById(@NotNull Long id);

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given id or {@literal Optional#empty()} if none found.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    @NotNull
    @Override
    @Cacheable(key = "#a0")
    Optional<IdentityProviderEntity> findById(@NotNull @Param(value = "id") Long id);

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given id or {@literal Optional#empty()} if none found.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    @NotNull
    @Cacheable(key = "#a0")
    @Query(value = "SELECT * FROM identity_provider WHERE id_ = :id", nativeQuery = true)
    Optional<IdentityProviderEntity> findByIdContainsDeleted(@NotNull @Param(value = "id") Long id);

    /**
     * 更新社交认证源状态
     *
     * @param id      {@link  Long}
     * @param enabled {@link  Boolean}
     * @return {@link  Boolean}
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @CacheEvict(allEntries = true)
    @Query(value = "UPDATE IdentityProviderEntity SET enabled=:enabled where id=:id")
    Integer updateIdentityProviderStatus(@Param(value = "id") Long id,
                                         @Param(value = "enabled") Boolean enabled);

    /**
     * 根据ID查找，并且为启用
     *
     * @param id {@link Long}
     * @return {@link IdentityProviderEntity}
     */
    @Cacheable(key = "#a0", unless = "#result == null")
    Optional<IdentityProviderEntity> findByIdAndEnabledIsTrue(Long id);

    /**
     * 根据code查找，并且为启用
     *
     * @param code {@link Long}
     * @return {@link IdentityProviderEntity}
     */
    @Cacheable(key = "#a0", unless = "#result == null")
    Optional<IdentityProviderEntity> findByCodeAndEnabledIsTrue(String code);
}
