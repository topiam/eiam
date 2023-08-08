/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.service.account;

import java.io.Serializable;
import java.util.List;

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.query.UserListNotInGroupQuery;
import cn.topiam.employee.common.entity.account.query.UserListQuery;
import cn.topiam.employee.common.enums.CheckValidityType;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.console.pojo.result.account.BatchUserResult;
import cn.topiam.employee.console.pojo.result.account.UserListResult;
import cn.topiam.employee.console.pojo.result.account.UserLoginAuditListResult;
import cn.topiam.employee.console.pojo.result.account.UserResult;
import cn.topiam.employee.console.pojo.save.account.UserCreateParam;
import cn.topiam.employee.console.pojo.update.account.ResetPasswordParam;
import cn.topiam.employee.console.pojo.update.account.UserUpdateParam;
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
     * 获取用户列表不在当前组
     *
     * @param page  {@link  PageModel}
     * @param query {@link UserListNotInGroupQuery }
     * @return {@link  }
     */
    Page<UserListResult> getUserListNotInGroup(PageModel page, UserListNotInGroupQuery query);

    /**
     * 重置密码
     *
     * @param param       {@link ResetPasswordParam} 用户入参
     * @return {@link Boolean} 是否成功
     */
    Boolean resetUserPassword(ResetPasswordParam param);

    /**
     * 更改用户状态
     *
     * @param id     {@link Long}
     * @param status {@link UserStatus}
     * @return {@link Boolean}
     */
    boolean changeUserStatus(Long id, UserStatus status);

    /**
     * 创建用户
     *
     * @param param {@link UserCreateParam}
     * @return {@link Boolean}
     */
    Boolean createUser(UserCreateParam param);

    /**
     * 通过外部ID获取用户
     *
     * @param id {@link String}
     * @return {@link UserEntity}
     */
    UserEntity getByExternalId(String id);

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
     * @return {@link Boolean}
     */
    boolean updateUser(UserUpdateParam param);

    /**
     * 删除用户
     *
     * @param id {@link Serializable}
     * @return {@link Boolean}
     */
    boolean deleteUser(String id);

    /**
     * 用户转岗
     *
     * @param userId {@link String}
     * @param orgId  {@link String}
     * @return {@link Boolean}
     */
    Boolean userTransfer(String userId, String orgId);

    /**
     * 批量删除用户
     *
     * @param ids {@link String}
     * @return {@link Boolean}
     */
    Boolean batchDeleteUser(String[] ids);

    /**
     * 参数有效性验证
     *
     * @param type  {@link CheckValidityType}
     * @param value {@link String}
     * @param id    {@link Long}
     * @return {@link Boolean}
     */
    Boolean userParamCheck(CheckValidityType type, String value, Long id);

    /**
     * 查看用户登录日志
     *
     * @param id {@link Long}
     * @param pageModel {@link PageModel}
     * @return {@link   List}
     */
    Page<UserLoginAuditListResult> findUserLoginAuditList(Long id, PageModel pageModel);

    /**
     * 批量获取用户信息
     *
     * @param ids {@link List}
     * @return {@link List}
     */
    List<BatchUserResult> batchGetUser(List<Long> ids);
}
