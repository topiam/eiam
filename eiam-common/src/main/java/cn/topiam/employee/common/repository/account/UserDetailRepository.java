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

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.account.UserDetailEntity;

/**
 * <p>
 * 用户详情表 Repository 接口
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-07
 */
@Repository
public interface UserDetailRepository extends PagingAndSortingRepository<UserDetailEntity, Long>,
                                      CrudRepository<UserDetailEntity, Long>,
                                      QuerydslPredicateExecutor<UserDetailEntity>,
                                      UserDetailRepositoryCustomized {
    /**
     * 根据user id查询用户详情
     *
     * @param user {@link String}
     * @return {@link UserDetailEntity}
     */
    Optional<UserDetailEntity> findByUserId(Long user);

    /**
     * 根据用户ID删除用户
     *
     * @param userId {@link  Long}
     */
    void deleteByUserId(Long userId);

    /**
     * 根据用户ID批量删除用户
     *
     * @param userIds {@link String}
     */
    @Modifying
    @Query(value = "DELETE  FROM UserDetailEntity WHERE userId IN (:userIds)")
    void deleteAllByUserId(@Param("userIds") Iterable<Long> userIds);

    /**
     * 根据用户ID查询用户详情
     *
     * @param userIds  {@link List}
     * @return {@link List}
     */
    @Modifying
    @Query(value = "SELECT *  FROM user_detail WHERE user_detail.user_id IN :userIds", nativeQuery = true)
    List<UserDetailEntity> findAllByUserIds(@Param("userIds") Iterable<Long> userIds);
}
