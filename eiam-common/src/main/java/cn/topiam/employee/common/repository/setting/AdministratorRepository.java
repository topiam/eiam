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
package cn.topiam.employee.common.repository.setting;

import java.time.LocalDateTime;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.setting.AdministratorEntity;
import cn.topiam.employee.common.enums.UserStatus;
import static cn.topiam.employee.common.constant.SettingConstants.ADMIN_CACHE_NAME;

/**
 * @author TopIAM
 * Created by support@topiam.cn on 2021/11/13 22:09
 */
@Repository
@CacheConfig(cacheNames = { ADMIN_CACHE_NAME })
public interface AdministratorRepository extends JpaRepository<AdministratorEntity, String>,
                                         JpaSpecificationExecutor<AdministratorEntity> {

    /**
     * findById
     *
     * @param id must not be {@literal null}.
     * @return {@link AdministratorEntity}
     */
    @NotNull
    @Override
    @Cacheable
    Optional<AdministratorEntity> findById(@NotNull @Param(value = "id") String id);

    /**
     * findById
     *
     * @param id must not be {@literal null}.
     */
    @Override
    @CacheEvict(allEntries = true)
    void deleteById(@NotNull String id);

    /**
     * findById
     *
     * @param ids must not be {@literal null}.
     */
    @Override
    @CacheEvict(allEntries = true)
    void deleteAllById(@NotNull Iterable<? extends String> ids);

    /**
     * save
     *
     * @param entity must not be {@literal null}.
     * @return {@link AdministratorEntity}
     */
    @Override
    @CacheEvict(allEntries = true)
    <S extends AdministratorEntity> S save(@NotNull S entity);

    /**
     * 根据用户名查询
     *
     * @param username {@link String}
     * @return {@link AdministratorEntity}
     */
    Optional<AdministratorEntity> findByUsername(String username);

    /**
     * 根据手机号查询
     *
     * @param phone {@link String}
     * @return {@link AdministratorEntity}
     */
    Optional<AdministratorEntity> findByPhone(String phone);

    /**
     * 根据邮箱查询
     *
     * @param email {@link String}
     * @return {@link AdministratorEntity}
     */
    Optional<AdministratorEntity> findByEmail(String email);

    /**
     * 更新管理员状态
     *
     * @param id     {@link String}
     * @param status {@link String}
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @CacheEvict(allEntries = true)
    @Query(value = "UPDATE AdministratorEntity set status = :status where id = :id")
    void updateStatus(@Param(value = "id") String id, @Param(value = "status") UserStatus status);

    /**
     * 更新用户密码
     *
     * @param id                     {@link  String}
     * @param password               {@link  String}
     * @param lastUpdatePasswordTime {@link LocalDateTime}
     * @return {@link  Integer}
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @CacheEvict(allEntries = true)
    @Query(value = "update AdministratorEntity set password =:password, lastUpdatePasswordTime = :lastUpdatePasswordTime where id=:id")
    Integer updatePassword(@Param(value = "id") String id,
                           @Param(value = "password") String password,
                           @Param(value = "lastUpdatePasswordTime") LocalDateTime lastUpdatePasswordTime);

    /**
     * 更新认证成功信息
     *
     * @param id {@link String}
     * @param ip {@link String}
     * @param loginTime {@link LocalDateTime}
     */
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "UPDATE AdministratorEntity SET authTotal = (COALESCE(authTotal,0) +1),lastAuthIp = :ip,lastAuthTime = :loginTime WHERE id = :id")
    void updateAuthSucceedInfo(@Param("id") String id, @Param("ip") String ip,
                               @Param("loginTime") LocalDateTime loginTime);

    /**
     * 更新邮箱
     *
     * @param id {@link Long}
     * @param email {@link String}
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @CacheEvict(allEntries = true)
    @Query(value = "UPDATE AdministratorEntity SET email =:email WHERE id=:id")
    Integer updateByIdAndEmail(@Param(value = "id") String id,
                               @Param(value = "email") String email);

    /**
     * 更新用户手机号
     *
     * @param id    {@link  Long}
     * @param phone {@link  String}
     * @return {@link  Integer}
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @CacheEvict(allEntries = true)
    @Query(value = "UPDATE AdministratorEntity set phone =:phone where id=:id")
    Integer updateByIdAndPhone(@Param(value = "id") String id,
                               @Param(value = "phone") String phone);
}
