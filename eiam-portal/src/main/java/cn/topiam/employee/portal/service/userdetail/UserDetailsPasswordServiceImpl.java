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

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Service;

import cn.topiam.employee.common.repository.account.UserRepository;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/10/19 14:10
 */
@Service
public class UserDetailsPasswordServiceImpl implements UserDetailsPasswordService {

    private final Logger logger = LoggerFactory.getLogger(UserDetailsPasswordService.class);

    /**
     * Modify the specified user's password. This should change the user's password in the
     * persistent user repository (database, LDAP etc).
     *
     * @param user        the user to modify the password for
     * @param newPassword the password to change to, encoded by the configured
     *                    {@code PasswordEncoder}
     * @return the updated UserDetails with the new password
     */
    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        String userId = ((cn.topiam.employee.support.security.userdetails.UserDetails) user)
            .getId();
        userRepository.updatePassword(userId, newPassword, LocalDateTime.now());
        return user;
    }

    /**
     * UserRepository
     */
    private final UserRepository userRepository;

    public UserDetailsPasswordServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
