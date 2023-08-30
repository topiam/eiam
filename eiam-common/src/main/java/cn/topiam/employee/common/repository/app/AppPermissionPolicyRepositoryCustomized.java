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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.topiam.employee.common.entity.app.po.AppPermissionPolicyPO;
import cn.topiam.employee.common.entity.app.query.AppPolicyQuery;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/4 22:44
 */
public interface AppPermissionPolicyRepositoryCustomized {
    /**
     * 分页查询权限策略
     *
     * @param query {@link AppPolicyQuery}
     * @param request {@link Pageable}
     * @return {@link AppPermissionPolicyPO}
     */
    Page<AppPermissionPolicyPO> findPage(AppPolicyQuery query, Pageable request);
}
