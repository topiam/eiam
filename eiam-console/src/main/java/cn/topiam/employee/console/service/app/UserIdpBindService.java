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

import java.util.List;

import cn.topiam.employee.console.pojo.result.app.UserIdpBindListResult;

/**
 * 用户身份提供商绑定
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/11 21:10
 */
public interface UserIdpBindService {
    /**
     * 解绑用户IDP
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    Boolean unbindUserIdpBind(String id);

    /**
     * 查询用户身份提供商绑定
     *
     * @param userId {@link String}
     * @return {@link List<UserIdpBindListResult>}
     */
    List<UserIdpBindListResult> getUserIdpBindList(String userId);
}
