/*
 * eiam-authentication-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.authentication.common.service;

import cn.topiam.employee.authentication.common.modal.IdpUser;
import cn.topiam.employee.core.security.userdetails.UserDetails;

/**
 * UserIdpService
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/9 21:54
 */
public interface UserIdpService {

    /**
     * 检查用户已绑定对应平台
     *
     * @param openId     {@link  String}
     * @param providerId {@link  String}
     * @return {@link  Boolean}
     */
    Boolean checkUserIdpIsAlreadyBind(String openId, String providerId);

    /**
     * 是否自动绑定
     *
     * @param providerId {@link  String} 提供商ID
     * @return {@link  Boolean}
     */
    Boolean isAutoBindUserIdp(String providerId);

    /**
     * 绑定
     *
     * @param accountId   {@link  String} 账户ID
     * @param idpUser   {@link  IdpUser} 用户信息
     * @return {@link  Boolean}
     */
    Boolean bindUserIdp(String accountId, IdpUser idpUser);

    /**
     * 更新账户信息
     *
     * @param idpUser   {@link IdpUser} 用户信息
     * @param providerId {@link  String} 提供商ID
     * @return {@link  Boolean}
     */
    Boolean updateUser(IdpUser idpUser, String providerId);

    /**
     * 获取用户详情
     *
     * @param openId     {@link String}
     * @param providerId {@link String}
     * @return {@link UserDetails}
     */
    UserDetails getUserDetails(String openId, String providerId);
}
