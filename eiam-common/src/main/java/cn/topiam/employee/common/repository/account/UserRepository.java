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
package cn.topiam.employee.common.repository.account;

import java.time.LocalDateTime;
import java.util.Collection;
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

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.enums.DataOrigin;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.support.repository.LogicDeleteRepository;
import static cn.topiam.employee.common.constants.AccountConstants.USER_CACHE_NAME;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_WHERE;

/**
 * <p>
 * 用户表 Repository 接口
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-07-31
 */
@Repository
@CacheConfig(cacheNames = { USER_CACHE_NAME })
public interface UserRepository extends LogicDeleteRepository<UserEntity, Long>,
                                QuerydslPredicateExecutor<UserEntity>, UserRepositoryCustomized {
    /**
     * findById
     *
     * @param id must not be {@literal null}.
     * @return {@link UserEntity}
     */
    @NotNull
    @Override
    @Cacheable(key = "#p0", unless = "#result==null")
    Optional<UserEntity> findById(@NotNull @Param(value = "id") Long id);

    /**
     * findByIdContainsDeleted
     *
     * @param id must not be {@literal null}.
     * @return {@link UserEntity}
     */
    @NotNull
    @Cacheable(key = "#p0", unless = "#result==null")
    @Query(value = "SELECT * FROM user WHERE id_ = :id", nativeQuery = true)
    Optional<UserEntity> findByIdContainsDeleted(@NotNull @Param(value = "id") Long id);

    /**
     * findById
     *
     * @param id must not be {@literal null}.
     */
    @Override
    @CacheEvict(allEntries = true)
    void deleteById(@NotNull Long id);

    /**
     * findById
     *
     * @param ids must not be {@literal null}.
     */
    @Override
    @CacheEvict(allEntries = true)
    void deleteAllById(@NotNull Iterable<? extends Long> ids);

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
    UserEntity findByUsername(String username);

    /**
     * 根据手机号查询用户信息
     *
     * @param phone {@link String}
     * @return {@link UserEntity}
     */
    UserEntity findByPhone(String phone);

    /**
     * 根据邮件查询用户信息
     *
     * @param username {@link String}
     * @return {@link UserEntity}
     */
    UserEntity findByEmail(String username);

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
    List<UserEntity> findByExternalIdIn(Collection ids);

    /**
     * 更新用户密码
     *
     * @param id                     {@link  Long}
     * @param password               {@link  String}
     * @param lastUpdatePasswordTime {@link LocalDateTime}
     * @return {@link  Integer}
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @CacheEvict(allEntries = true)
    @Query(value = "update UserEntity set password =:password,lastUpdatePasswordTime = :lastUpdatePasswordTime where id=:id")
    Integer updateUserPassword(@Param(value = "id") Long id,
                               @Param(value = "password") String password,
                               @Param(value = "lastUpdatePasswordTime") LocalDateTime lastUpdatePasswordTime);

    /**
     * 更新用户邮箱
     *
     * @param id    {@link  Long}
     * @param email {@link  String}
     * @return {@link  Integer}
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @CacheEvict(allEntries = true)
    @Query(value = "update UserEntity set email =:email where id=:id")
    Integer updateUserEmail(@Param(value = "id") Long id, @Param(value = "email") String email);

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
    @Query(value = "update UserEntity set phone =:phone where id=:id")
    Integer updateUserPhone(@Param(value = "id") Long id, @Param(value = "phone") String phone);

    /**
     * 更新用户状态
     *
     * @param id     {@link  Long}
     * @param status {@link  UserStatus}
     * @return {@link  Integer}
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @CacheEvict(allEntries = true)
    @Query(value = "update UserEntity set status=:status where id=:id")
    Integer updateUserStatus(@Param(value = "id") Long id,
                             @Param(value = "status") UserStatus status);

    /**
     * 查找密码过期警告用户
     *
     * @param expireWarnDays {@link Integer} 即将到期日期
     * @return {@link UserEntity}
     */
    @Query(value = "SELECT * FROM `user` WHERE DATE_ADD(DATE_FORMAT(last_update_password_time,'%Y-%m-%d'), INTERVAL :expireWarnDays DAY ) <= CURDATE() and user.status_ != 'locked' AND "
                   + SOFT_DELETE_WHERE, nativeQuery = true)
    List<UserEntity> findPasswordExpireWarnUser(@Param(value = "expireWarnDays") Integer expireWarnDays);

    /**
     * 查询密码已过期用户
     *
     * @param expireDays {@link Integer} 密码过期日期
     * @return {@link UserEntity}
     */
    @Query(value = "SELECT * FROM `user` WHERE DATE_ADD(DATE_FORMAT(last_update_password_time,'%Y-%m-%d'), INTERVAL :expireDays DAY ) BETWEEN DATE_FORMAT(DATE_SUB(NOW(),INTERVAL 1 HOUR),'%Y-%m-%d %h') AND DATE_FORMAT(DATE_SUB(NOW(),INTERVAL 1 HOUR),'%Y-%m-%d %h') AND user.status_ != 'password_expired_locked' AND "
                   + SOFT_DELETE_WHERE, nativeQuery = true)
    List<UserEntity> findPasswordExpireUser(@Param(value = "expireDays") Integer expireDays);

    /**
     * 查询已到期用户
     *
     * @return {@link UserEntity}
     */
    @Query(value = "SELECT * FROM `user` WHERE expire_date <= CURDATE() and status_ != 'expired_locked' AND "
                   + SOFT_DELETE_WHERE, nativeQuery = true)
    List<UserEntity> findExpireUser();

    /**
     * 更新用户共享密钥
     *
     * @param id {@link Long}
     * @param sharedSecret {@link String}
     * @param totpBind {@link Boolean}
     * @return {@link Integer}
     */
    @CacheEvict(allEntries = true)
    @Modifying
    @Query(value = "update UserEntity set sharedSecret=:sharedSecret, totpBind=:totpBind where id=:id")
    Integer updateUserSharedSecretAndTotpBind(@Param(value = "id") Long id,
                                              @Param(value = "sharedSecret") String sharedSecret,
                                              @Param(value = "totpBind") Boolean totpBind);

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
     * 按Id查找不在Id中
     * @param ids {@link Collection}
     * @param dataOrigin {@link DataOrigin}
     * @return {@link List}
     */
    List<UserEntity> findAllByIdNotInAndDataOrigin(Collection<Long> ids, DataOrigin dataOrigin);

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
    @Query(value = "UPDATE user SET auth_total = (IFNULL(auth_total,0) +1),last_auth_ip = ?2,last_auth_time = ?3 WHERE id_ = ?1", nativeQuery = true)
    void updateAuthSucceedInfo(String id, String ip, LocalDateTime loginTime);
}
