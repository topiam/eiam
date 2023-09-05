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

import cn.topiam.employee.application.AppAccount;
import cn.topiam.employee.portal.pojo.request.AppAccountRequest;

/**
 * 应用账户
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/8/25 21:07
 */
public interface AppAccountService {

    /**
     * 新增应用账户
     *
     * @param param {@link AppAccountRequest}
     * @return {@link Boolean}
     */
    Boolean createAppAccount(AppAccountRequest param);

    /**
     * 删除应用账户
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    Boolean deleteAppAccount(String id);

    /**
     * 获取应用账户
     * @param appId {@link Long}
     * @return {@link AppAccount}
     */
    AppAccount getAppAccount(Long appId);
}
