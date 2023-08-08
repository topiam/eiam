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

import cn.topiam.employee.common.entity.account.query.UserListQuery;
import cn.topiam.employee.common.entity.app.po.AppAccessPolicyPO;
import cn.topiam.employee.common.entity.app.query.AppAccessPolicyQuery;

/**
 * 应用访问策略 Repository Customized
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/26 23:40
 */
public interface AppAccessPolicyRepositoryCustomized {

    /**
     * 获取应用授权策略列表
     *
     * @param pageable {@link  Pageable}
     * @param query    {@link  UserListQuery}
     * @return {@link Page}
     */
    Page<AppAccessPolicyPO> getAppPolicyList(AppAccessPolicyQuery query, Pageable pageable);

    /**
     * 用户是否允许访问应用
     *
     * @param appId {@link Long}
     * @param userId {@link Long}
     * @return {@link Boolean}
     */
    Boolean hasAllowAccess(Long appId, Long userId);
}
