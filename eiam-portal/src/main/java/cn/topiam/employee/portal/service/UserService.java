/*
 * eiam-portal - Employee Identity and Access Management
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
package cn.topiam.employee.portal.service;

import java.util.Optional;

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.support.security.userdetails.UserDetails;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2024/3/2 22:21
 */
public interface UserService {
    /**
     * 获取用户详情
     *
     * @return {@link UserDetails}
     */
    UserDetails getUserDetails(String userId);

    /**
     * 获取用户详情
     *
     * @return {@link UserEntity}
     */
    UserDetails getUserDetails(UserEntity user);

    /**
     * 根据用户名、手机号、邮箱查询用户
     *
     * @return {@link UserEntity}
     */
    Optional<UserEntity> findByUsernameOrPhoneOrEmail(String keyword);
}
