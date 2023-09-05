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
package cn.topiam.employee.console.service.app;

import cn.topiam.employee.common.enums.CheckValidityType;
import cn.topiam.employee.console.pojo.query.app.AppPermissionRoleListQuery;
import cn.topiam.employee.console.pojo.result.app.AppPermissionRoleListResult;
import cn.topiam.employee.console.pojo.result.app.AppPermissionRoleResult;
import cn.topiam.employee.console.pojo.save.app.AppPermissionRoleCreateParam;
import cn.topiam.employee.console.pojo.update.app.PermissionRoleUpdateParam;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-10
 */
public interface AppPermissionRoleService {

    /**
     * 获取所有角色（分页）
     *
     * @param page  {@link PageModel}
     * @param query {@link AppPermissionRoleListQuery}
     * @return {@link AppPermissionRoleListResult}
     */
    Page<AppPermissionRoleListResult> getPermissionRoleList(PageModel page,
                                                            AppPermissionRoleListQuery query);

    /**
     * 创建角色
     *
     * @param param {@link AppPermissionRoleCreateParam}
     * @return {@link Boolean}
     */
    boolean createPermissionRole(AppPermissionRoleCreateParam param);

    /**
     * 更新角色
     *
     * @param param {@link PermissionRoleUpdateParam}
     * @return {@link Boolean}
     */
    boolean updatePermissionRole(PermissionRoleUpdateParam param);

    /**
     * 删除角色
     *
     * @param ids {@link String}
     * @return {@link Boolean}
     */
    boolean deletePermissionRole(String ids);

    /**
     * 角色详情
     *
     * @param id {@link Long}
     * @return {@link AppPermissionRoleResult}
     */
    AppPermissionRoleResult getPermissionRole(Long id);

    /**
     * 参数有效性验证
     *
     * @param type     {@link CheckValidityType}
     * @param value    {@link String}
     * @param appId {@link Long}
     * @param id       {@link Long}
     * @return {@link Boolean}
     */
    Boolean permissionRoleParamCheck(CheckValidityType type, String value, Long appId, Long id);

    /**
     * 更新角色状态
     *
     * @param id     {@link String}
     * @param status {@link Boolean}
     * @return {@link Boolean}
     */
    Boolean updatePermissionRoleStatus(String id, Boolean status);
}
