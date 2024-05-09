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
package cn.topiam.employee.portal.service.userdetail;

import org.springframework.stereotype.Service;

import cn.topiam.employee.portal.service.UserService;
import cn.topiam.employee.support.security.session.RefreshCurrentSessionPrincipalService;
import cn.topiam.employee.support.security.userdetails.UserDetails;

/**
 * 刷新当前用户会话 Principal 实现
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2024/3/2 14:06
 */
@Service
public class RefreshCurrentSessionPrincipalServiceImpl implements
                                                       RefreshCurrentSessionPrincipalService {
    /**
     * 获取 Principal
     *
     * @param userId {@link String}
     * @return {@link UserDetails}
     */
    @Override
    public UserDetails getPrincipal(String userId) {
        return userService.getUserDetails(userId);
    }

    private final UserService userService;

    public RefreshCurrentSessionPrincipalServiceImpl(UserService userService) {
        this.userService = userService;
    }
}
