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

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.account.UserGroupEntity;
import cn.topiam.employee.support.repository.LogicDeleteRepository;

/**
 * <p>
 * 用户组表 Repository 接口
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-07-31
 */
@Repository
public interface UserGroupRepository extends LogicDeleteRepository<UserGroupEntity, Long>,
                                     QuerydslPredicateExecutor<UserGroupEntity> {

    /**
     * findByIdContainsDeleted
     *
     * @param id must not be {@literal null}.
     * @return {@link UserGroupEntity}
     */
    @NotNull
    @Query(value = "SELECT * FROM user_group WHERE id_ = :id", nativeQuery = true)
    Optional<UserGroupEntity> findByIdContainsDeleted(@NotNull @Param(value = "id") Long id);
}
