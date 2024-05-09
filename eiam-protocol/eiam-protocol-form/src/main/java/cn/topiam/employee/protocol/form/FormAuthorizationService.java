/*
 * eiam-protocol-form - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.form;

import cn.topiam.employee.protocol.code.LoginAccount;
import cn.topiam.employee.protocol.form.client.FormRegisteredClient;
import cn.topiam.employee.support.security.userdetails.UserDetails;

/**
 * @author TopIAM
 * Created by support@topiam.cn on 2023/7/8 00:23
 */
public interface FormAuthorizationService {

    /**
     * 用户是否具有登录权限
     *
     * @param client {@link FormRegisteredClient}
     * @param user {@link UserDetails}
     * @return {@link Boolean}
     */
    Boolean hasLoginPermission(FormRegisteredClient client, UserDetails user);

    /**
     * 根据客户端&用户ID查询默认应用账户
     *
     * @param client {@link String} 客户端
     * @param  user {@link UserDetails} 用户
     * @return {@link String}
     */
    LoginAccount getDefaultLoginAccount(FormRegisteredClient client, UserDetails user);
}
