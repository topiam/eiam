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
package cn.topiam.employee.console.service.permission;

import cn.topiam.employee.common.entity.app.query.AppPolicyQuery;
import cn.topiam.employee.console.pojo.result.permission.PermissionPolicyGetResult;
import cn.topiam.employee.console.pojo.result.permission.PermissionPolicyListResult;
import cn.topiam.employee.console.pojo.save.permission.PermissionPolicyCreateParam;
import cn.topiam.employee.console.pojo.update.permission.PermissionPolicyUpdateParam;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

/**
 * <p>
 * 权限策略 服务类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-10
 */
public interface PermissionPolicyService {
    /**
     * 获取资源列表
     *
     * @param page  {@link PageModel}
     * @param query {@link AppPolicyQuery}
     * @return {@link PermissionPolicyListResult}
     */
    Page<PermissionPolicyListResult> getPermissionPolicyList(PageModel page,
                                                             AppPolicyQuery query);

    /**
     * 获取资源
     *
     * @param id {@link String}
     * @return {@link PermissionPolicyGetResult}
     */
    PermissionPolicyGetResult getPermissionPolicy(String id);

    /**
     * 删除资源
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    Boolean deletePermissionPolicy(String id);

    /**
     * 创建资源
     *
     * @param param {@link PermissionPolicyCreateParam}
     * @return {@link Boolean}
     */
    Boolean createPermissionPolicy(PermissionPolicyCreateParam param);

    /**
     * 更新资源
     *
     * @param param {@link PermissionPolicyUpdateParam}
     * @return {@link Boolean}
     */
    Boolean updatePermissionPolicy(PermissionPolicyUpdateParam param);
}
