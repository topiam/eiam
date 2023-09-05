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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.app.AppPermissionRoleEntity;
import cn.topiam.employee.support.repository.LogicDeleteRepository;

/**
 * <p>
 * 角色表 Repository 接口
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-10
 */
@Repository
public interface AppPermissionRoleRepository extends
                                             LogicDeleteRepository<AppPermissionRoleEntity, Long>,
                                             QuerydslPredicateExecutor<AppPermissionRoleEntity> {
    /**
     * 更新角色状态
     *
     * @param id      {@link String}
     * @param enabled {@link String}
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update app_permission_role set is_enabled = ?2 where id_ = ?1", nativeQuery = true)
    void updateStatus(@Param(value = "id") String id, @Param(value = "enabled") Boolean enabled);

    /**
     * findByIdContainsDeleted
     *
     * @param id must not be {@literal null}.
     * @return {@link AppPermissionRoleEntity}
     */
    @NotNull
    @Cacheable
    @Query(value = "SELECT * FROM app_permission_role WHERE id_ = :id", nativeQuery = true)
    Optional<AppPermissionRoleEntity> findByIdContainsDeleted(@NotNull @Param(value = "id") Long id);
}
