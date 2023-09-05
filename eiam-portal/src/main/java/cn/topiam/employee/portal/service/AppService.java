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

import java.util.List;

import cn.topiam.employee.portal.pojo.query.GetAppListQuery;
import cn.topiam.employee.portal.pojo.result.AppGroupListResult;
import cn.topiam.employee.portal.pojo.result.GetAppListResult;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

/**
 * AppService
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/6 22:35
 */
public interface AppService {
    /**
     * 获取应用列表
     *
     * @param query     {@link GetAppListQuery}
     * @param pageModel {@link PageModel}
     * @return {@link Page}
     */
    Page<GetAppListResult> getAppList(GetAppListQuery query, PageModel pageModel);

    /**
     * 查询应用分组
     *
     * @return {@link AppGroupListResult}
     */
    List<AppGroupListResult> getAppGroupList();
}
