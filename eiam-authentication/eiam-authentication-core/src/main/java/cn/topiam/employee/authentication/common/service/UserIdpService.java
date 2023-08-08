/*
 * eiam-authentication-core - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.common.service;

import cn.topiam.employee.authentication.common.authentication.IdpUserDetails;
import cn.topiam.employee.support.security.userdetails.UserDetails;

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
    Boolean checkIdpUserIsExistBind(String openId, String providerId);

    /**
     * 绑定
     *
     * @param accountId   {@link  String} 账户ID
     * @param idpUserDetails   {@link  IdpUserDetails} 用户信息
     * @return {@link  Boolean}
     */
    Boolean bindUserIdp(String accountId, IdpUserDetails idpUserDetails);

    /**
     * 更新账户信息
     *
     * @param idpUserDetails   {@link IdpUserDetails} 用户信息
     * @param providerId {@link  String} 提供商ID
     * @return {@link  Boolean}
     */
    Boolean updateUser(IdpUserDetails idpUserDetails, String providerId);

    /**
     * 获取用户详情
     *
     * @param openId     {@link String}
     * @param providerId {@link String}
     * @return {@link UserDetails}
     */
    UserDetails getUserDetails(String openId, String providerId);
}
