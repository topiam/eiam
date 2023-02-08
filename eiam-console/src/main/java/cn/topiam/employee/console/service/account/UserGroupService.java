/*
 * eiam-console - Employee Identity and Access Management Program
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
package cn.topiam.employee.console.service.account;

import java.util.List;

import cn.topiam.employee.common.entity.account.UserGroupEntity;
import cn.topiam.employee.common.entity.account.query.UserGroupMemberListQuery;
import cn.topiam.employee.console.pojo.query.account.UserGroupListQuery;
import cn.topiam.employee.console.pojo.result.account.UserGroupListResult;
import cn.topiam.employee.console.pojo.result.account.UserGroupMemberListResult;
import cn.topiam.employee.console.pojo.save.account.UserGroupCreateParam;
import cn.topiam.employee.console.pojo.update.account.UserGroupUpdateParam;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

/**
 * 用户组service
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/10/11 21:29
 */
public interface UserGroupService {
    /**
     * 查询用户分组列表
     *
     * @param page  {@link PageModel}
     * @param query {@link UserGroupListQuery}
     * @return {@link UserGroupListResult}
     */
    Page<UserGroupListResult> getUserGroupList(PageModel page, UserGroupListQuery query);

    /**
     * 创建用户组
     *
     * @param param {@link UserGroupCreateParam}
     * @return {@link Boolean}
     */
    Boolean createUserGroup(UserGroupCreateParam param);

    /**
     * 更新用户组
     *
     * @param param {@link UserGroupUpdateParam}
     * @return {@link Boolean}
     */
    Boolean updateUserGroup(UserGroupUpdateParam param);

    /**
     * 删除用户组
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    Boolean deleteUserGroup(String id);

    /**
     * 根据ID查询用户分组
     *
     * @param id {@link Long}
     * @return {@link UserGroupEntity}
     */
    UserGroupEntity getUserGroup(Long id);

    /**
     * 获取用户组成员
     *
     * @param query {@link UserGroupMemberListQuery}
     * @param page  {@link PageModel}
     * @return {@link UserGroupMemberListResult}
     */
    Page<UserGroupMemberListResult> getUserGroupMemberList(PageModel page,
                                                           UserGroupMemberListQuery query);

    /**
     * 从用户组移除用户
     *
     * @param id     {@link String}
     * @param userId {@link String}
     * @return {@link Boolean}
     */
    Boolean removeMember(String id, String userId);

    /**
     * 添加用户
     *
     * @param userIds {@link String}
     * @param groupId {@link String}
     * @return {@link Boolean}
     */
    Boolean addMember(String groupId, String[] userIds);

    /**
     * 批量移除用户
     *
     * @param id      {@link String}
     * @param userIds {@link String}
     * @return {@link Boolean}
     */
    Boolean batchRemoveMember(String id, List<String> userIds);

    /**
     * 查询用户组成员数量
     *
     * @param groupId {@link  String}
     * @return {@link  Long}
     */
    Long getUserGroupMemberCount(String groupId);
}
