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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.account.UserDetailEntity;

/**
 * <p>
 * 用户详情表 Repository 接口
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020-08-07
 */
@Repository
public interface UserDetailRepository extends JpaRepository<UserDetailEntity, String>,
                                      UserDetailRepositoryCustomized {
    /**
     * 根据user id查询用户详情
     *
     * @param user {@link String}
     * @return {@link UserDetailEntity}
     */
    Optional<UserDetailEntity> findByUserId(String user);

    /**
     * 根据用户ID删除用户
     *
     * @param userId {@link  String}
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByUserId(@Param("userId") String userId);

    /**
     * 根据用户ID批量删除用户
     *
     * @param userIds {@link List}
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteAllByUserIdIn(Collection<String> userIds);

    /**
     * 根据用户ID查询用户详情
     *
     * @param userIds  {@link List}
     * @return {@link List}
     */
    List<UserDetailEntity> findAllByUserIdIn(List<String> userIds);
}
