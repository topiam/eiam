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
package cn.topiam.employee.common.repository.identitysource;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.constant.AccountConstants;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceEntity;
import cn.topiam.employee.support.repository.LogicDeleteRepository;

/**
 * <p>
 * 身份认证源配置 Repository  接口
 * <p>
 * 部分操作使用了缓存，后期更改使用，请务必注意！
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-16
 */
@Repository
@CacheConfig(cacheNames = { AccountConstants.IDS_CACHE_NAME })
public interface IdentitySourceRepository extends LogicDeleteRepository<IdentitySourceEntity, Long>,
                                          QuerydslPredicateExecutor<IdentitySourceEntity> {
    /**
     * 根据ID查询
     *
     * @param id {@link Long}
     * @return {@link IdentitySourceEntity}
     */
    @Override
    @Cacheable(key = "#p0", unless = "#result==null")
    Optional<IdentitySourceEntity> findById(@Param(value = "id") Long id);

    /**
     * 根据ID查询
     *
     * @param id {@link Long}
     * @return {@link IdentitySourceEntity}
     */
    @Cacheable(key = "#p0", unless = "#result==null")
    @Query(value = "SELECT * FROM identity_source WHERE id_ = :id", nativeQuery = true)
    Optional<IdentitySourceEntity> findByIdContainsDeleted(@Param(value = "id") Long id);

    /**
     * 查询启用的身份源
     *
     * @return {@link List}
     */
    @Cacheable
    List<IdentitySourceEntity> findByEnabledIsTrue();

    /**
     * 更新身份源状态
     *
     * @param id      {@link  Long}
     * @param enabled {@link  Boolean}
     * @return {@link  Boolean}
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @CacheEvict(allEntries = true)
    @Query(value = "UPDATE IdentitySourceEntity SET enabled=:enabled where id=:id")
    Integer updateIdentitySourceStatus(@Param(value = "id") Long id,
                                       @Param(value = "enabled") Boolean enabled);

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity must not be {@literal null}.
     * @return the saved entity; will never be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal entity} is {@literal null}.
     */
    @Override
    @NonNull
    @CacheEvict(allEntries = true)
    <S extends IdentitySourceEntity> S save(@NonNull S entity);

    /**
     * Deletes the entity with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
     */
    @Override
    @CacheEvict(allEntries = true)
    void deleteById(@NonNull Long id);

    /**
     * 更新设分院策略
     *
     * @param id             {@link Long} 主键
     * @param strategyConfig {@link String} 策略
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @CacheEvict(allEntries = true)
    @Query(value = "UPDATE IdentitySourceEntity SET strategyConfig=:strategyConfig where id=:id")
    void updateStrategyConfig(@Param(value = "id") Long id,
                              @Param(value = "strategyConfig") String strategyConfig);

    /**
     * 根据code查询
     *
     * @param code {@link String}
     * @return {@link IdentitySourceEntity}
     */
    Optional<IdentitySourceEntity> findByCode(String code);
}
