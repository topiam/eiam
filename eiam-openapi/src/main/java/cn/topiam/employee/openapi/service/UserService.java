/*
 * eiam-openapi - Employee Identity and Access Management
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
package cn.topiam.employee.openapi.service;

import java.io.Serializable;

import cn.topiam.employee.common.entity.account.query.UserListQuery;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.openapi.pojo.request.account.save.account.UserCreateParam;
import cn.topiam.employee.openapi.pojo.request.account.update.account.UserUpdateParam;
import cn.topiam.employee.openapi.pojo.response.account.UserListResult;
import cn.topiam.employee.openapi.pojo.response.account.UserResult;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-07-31
 */
public interface UserService {

    /**
     * 获取用户（分页）
     *
     * @param page  {@link PageModel}
     * @param query {@link UserListQuery}
     * @return {@link UserListQuery}
     */
    Page<UserListResult> getUserList(PageModel page, UserListQuery query);

    /**
     * 更改用户状态
     *
     * @param id     {@link Long}
     * @param status {@link UserStatus}
     */
    void changeUserStatus(Long id, UserStatus status);

    /**
     * 创建用户
     *
     * @param param {@link UserCreateParam}
     */
    void createUser(UserCreateParam param);

    /**
     * 根据ID查询用户
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    UserResult getUser(String id);

    /**
     * 更新用户
     *
     * @param param {@link UserUpdateParam}
     */
    void updateUser(UserUpdateParam param);

    /**
     * 删除用户
     *
     * @param id {@link Serializable}
     */
    void deleteUser(String id);

    /**
     * 获取用户id
     *
     * @param externalId {@link String}
     * @param phoneNumber {@link String}
     * @param email {@link String}
     * @param username {@link String}
     * @return {@link String}
     */
    String getUserIdByParams(String externalId, String phoneNumber, String email, String username);
}
