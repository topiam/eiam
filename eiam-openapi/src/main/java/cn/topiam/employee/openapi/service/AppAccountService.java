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

import cn.topiam.employee.common.entity.app.query.AppAccountQuery;
import cn.topiam.employee.openapi.pojo.request.app.save.AppAccountCreateParam;
import cn.topiam.employee.openapi.pojo.response.app.AppAccountListResult;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

/**
 * 应用账户
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/4 19:07
 */
public interface AppAccountService {

    /**
     * 查询应用账户
     *
     * @param pageModel {@link PageModel}
     * @param query     {@link  AppAccountQuery}
     * @return {@link Page}
     */
    Page<AppAccountListResult> getAppAccountList(PageModel pageModel, AppAccountQuery query);

    /**
     * 新增应用账户
     *
     * @param param {@link AppAccountCreateParam}
     * @return {@link Boolean}
     */
    Boolean createAppAccount(AppAccountCreateParam param);

    /**
     * 删除应用账户
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    Boolean deleteAppAccount(String id);
}
