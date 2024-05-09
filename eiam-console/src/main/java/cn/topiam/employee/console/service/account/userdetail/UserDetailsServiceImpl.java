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
package cn.topiam.employee.console.service.account.userdetail;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import cn.topiam.employee.common.entity.setting.AdministratorEntity;
import cn.topiam.employee.console.service.setting.AdministratorService;
import cn.topiam.employee.support.security.password.exception.PasswordValidatedFailException;
import cn.topiam.employee.support.security.userdetails.UserDetails;
import cn.topiam.employee.support.security.userdetails.UserDetailsService;

/**
 * FortressUserDetailsService
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/10/25 21:41
 */
@Component(value = "userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AdministratorEntity> optional = administratorService
            .findByUsernameOrPhoneOrEmail(username);
        //不存在该用户
        if (optional.isEmpty()) {
            logger.info("根据用户名、手机号、邮箱未查询该管理员【{}】", username);
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        AdministratorEntity administrator = optional.get();
        //锁定
        if (administrator.isLocked()) {
            logger.info("管理员【{}】被锁定", administrator.getUsername());
        }
        //禁用
        if (administrator.isDisabled()) {
            logger.info("管理员【{}】被禁用", administrator.getUsername());
        }
        return administratorService.getUserDetails(administrator);
    }

    @Override
    public void changePassword(String username, String newPassword) {
        administratorService.forceResetAdministratorPassword(username, newPassword);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        AdministratorEntity admin = administratorService.getAdministratorByUsername(username);
        boolean matches = passwordEncoder.matches(oldPassword, admin.getPassword());
        if (!matches) {
            logger.error("用户ID: [{}] 用户名: [{}] 旧密码匹配失败", admin.getId(), admin.getUsername());
            throw new PasswordValidatedFailException();
        }
        // 重置密码
        administratorService.forceResetAdministratorPassword(admin, newPassword);
    }

    /**
     * AdministratorService
     */
    private final AdministratorService administratorService;

    /**
     *  PasswordEncoder
     */
    private final PasswordEncoder      passwordEncoder;

    public UserDetailsServiceImpl(AdministratorService administratorService,
                                  PasswordEncoder passwordEncoder) {
        this.administratorService = administratorService;
        this.passwordEncoder = passwordEncoder;
    }
}
