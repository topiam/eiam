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
package cn.topiam.employee.common.repository.account;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
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

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.enums.UserStatus;
import static cn.topiam.employee.common.constant.AccountConstants.USER_CACHE_NAME;

/**
 * <p>
 * 用户表 Repository 接口
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020-07-31
 */
@Repository
@CacheConfig(cacheNames = { USER_CACHE_NAME })
public interface UserRepository extends JpaRepository<UserEntity, String>,
                                JpaSpecificationExecutor<UserEntity>, UserRepositoryCustomized {
    /**
     * findById
     *
     * @param id must not be {@literal null}.
     * @return {@link UserEntity}
     */
    @NotNull
    @Override
    @Cacheable(key = "#p0", unless = "#result==null")
    Optional<UserEntity> findById(@NotNull @Param(value = "id") String id);

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
     * @return {@link UserEntity}
     * @param <S> {@link UserEntity}
     */
    @Override
    @CacheEvict(allEntries = true)
    <S extends UserEntity> S save(@NotNull S entity);

    /**
     * 根据用户名查询用户信息
     *
     * @param username {@link String}
     * @return {@link UserEntity}
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * 根据手机号查询用户信息
     *
     * @param phone {@link String}
     * @return {@link UserEntity}
     */
    Optional<UserEntity> findByPhone(String phone);

    /**
     * 根据邮件查询用户信息
     *
     * @param email {@link String}
     * @return {@link UserEntity}
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * 根据扩展ID查询用户信息
     *
     * @param id {@link String}
     * @return {@link UserEntity}
     */
    Optional<UserEntity> findByExternalId(String id);

    /**
     * 根据扩展ID查询用户信息
     *
     * @param ids {@link Collection}
     * @return {@link UserEntity}
     */
    List<UserEntity> findByExternalIdIn(Collection<String> ids);

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
    @Query(value = "update UserEntity set password =:password,lastUpdatePasswordTime = :lastUpdatePasswordTime where id=:id")
    Integer updatePassword(@Param(value = "id") String id,
                           @Param(value = "password") String password,
                           @Param(value = "lastUpdatePasswordTime") LocalDateTime lastUpdatePasswordTime);

    /**
     * 更新用户邮箱
     *
     * @param id    {@link  String}
     * @param email {@link  String}
     * @return {@link  Integer}
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @CacheEvict(allEntries = true)
    @Query(value = "UPDATE UserEntity SET email=:email WHERE id=:id")
    Integer updateByIdAndEmail(@Param(value = "id") String id,
                               @Param(value = "email") String email);

    /**
     * 更新用户手机号
     *
     * @param id    {@link  String}
     * @param phone {@link  String}
     * @return {@link  Integer}
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @CacheEvict(allEntries = true)
    @Query(value = "UPDATE UserEntity SET phone=:phone WHERE id=:id")
    Integer updateByIdAndPhone(@Param(value = "id") String id,
                               @Param(value = "phone") String phone);

    /**
     * 更新用户状态
     *
     * @param id     {@link  String}
     * @param status {@link  UserStatus}
     * @return {@link  Integer}
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @CacheEvict(allEntries = true)
    @Query(value = "update UserEntity set status=:status where id=:id")
    Integer updateUserStatus(@Param(value = "id") String id,
                             @Param(value = "status") UserStatus status);

    /**
     * 查询已到期用户
     *
     * @return {@link UserEntity}
     */
    @Query(value = "FROM UserEntity WHERE expireDate <= CURRENT_DATE and status != 'expired_locked'")
    List<UserEntity> findExpireUser();

    /**
     * 根据用户名查询全部
     *
     * @param usernames {@link String}
     * @return {@link List}
     */
    List<UserEntity> findAllByUsernameIn(@Param("usernames") Collection<String> usernames);

    /**
     * 根据手机号查询全部
     *
     * @param phones {@link String}
     * @return {@link List}
     */
    List<UserEntity> findAllByPhoneIn(@Param("phones") Collection<String> phones);

    /**
     * 根据email模糊查询
     *
     * @param emails {@link String}
     * @return {@link List}
     */
    List<UserEntity> findAllByEmailIn(@Param("emails") Collection<String> emails);

    /**
     * 根据status模糊查询
     *
     * @param status {@link UserStatus}
     * @return {@link List}
     */
    List<UserEntity> findAllByStatusIn(Collection<UserStatus> status);

    /**
     * 根据status查询
     *
     * @param status {@link UserStatus}
     * @return {@link List}
     */
    List<UserEntity> findAllByStatus(UserStatus status);

    /**
     * 按Id查找不在Id中
     *
     * @param ids {@link Collection}
     * @param dataOrigin {@link String}
     * @return {@link List}
     */
    List<UserEntity> findAllByIdNotInAndDataOrigin(Collection<String> ids, String dataOrigin);

    /**
     * 根据身份源ID查询用户列表
     *
     * @param identitySourceId {@link String}
     * @return {@link List}
     */
    List<UserEntity> findAllByIdentitySourceId(String identitySourceId);

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
    @Query(value = "UPDATE UserEntity SET authTotal = (COALESCE(authTotal,0) +1),lastAuthIp = :ip,lastAuthTime = :loginTime WHERE id = :id")
    void updateAuthSucceedInfo(@Param("id") String id, @Param("ip") String ip,
                               @Param("loginTime") LocalDateTime loginTime);

    /**
     * 根据状态擦护心用户数
     *
     * @param status {@link UserStatus}
     * @return {@link Long}
     */
    Long countByStatus(UserStatus status);

    /**
     * 根据姓名模糊查询
     *
     * @param fullName {@link String}
     * @return {@link UserEntity}
     */
    List<UserEntity> findByFullNameLike(String fullName);
}
