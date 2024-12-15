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
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.portal.service.UserService;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.security.password.exception.PasswordValidatedFailException;
import cn.topiam.employee.support.security.userdetails.UserDetails;
import cn.topiam.employee.support.security.userdetails.UserDetailsService;

/**
 * UserDetailsServiceImpl
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/10/25 21:41
 */
@Component(value = "userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

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
        UserEntity user = userService.findByUsernameOrPhoneOrEmail(username).orElseThrow(() -> {
            logger.info("根据用户名、手机号、邮箱未查询该用户【{}】", username);
            return new UsernameNotFoundException("用户名或密码错误");
        });
        return userService.getUserDetails(user);
    }

    @Override
    public UserDetails loadUserByPhone(String phone) throws UsernameNotFoundException {
        Optional<UserEntity> optional = userRepository.findByPhone(phone);
        //不存在该用户
        if (optional.isEmpty()) {
            logger.info("根据手机号未查询该管理员【{}】", phone);
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return getUserDetails(optional.get());
    }

    @Override
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        Optional<UserEntity> optional = userRepository.findByEmail(email);
        //不存在该用户
        if (optional.isEmpty()) {
            logger.info("根据邮箱未查询该管理员【{}】", email);
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return getUserDetails(optional.get());
    }

    private UserDetails getUserDetails(UserEntity user) {
        //锁定
        if (user.isLocked()) {
            logger.info("用户【{}】被锁定", user.getUsername());
        }
        //禁用
        if (user.isDisabled()) {
            logger.info("用户【{}】被禁用", user.getUsername());
        }
        return userService.getUserDetails(user);
    }

    public void forceResetUserPassword(UserEntity user, String password) {
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if (matches) {
            logger.error("用户ID: [{}] 用户名: [{}] 新密码与旧密码相同", user.getId(), user.getUsername());
            throw new PasswordValidatedFailException("新密码不允许与旧密码相同");
        }
        password = passwordEncoder.encode(password);
        user.setPassword(password);
        user.setLastUpdatePasswordTime(LocalDateTime.now());
        user.setNeedChangePassword(false);
        // 更新密码
        userRepository.save(user);
        AuditContext.setTarget(Target.builder().id(user.getId()).name(user.getUsername())
            .type(TargetType.USER).build());
    }

    @Override
    public void changePassword(String username, String newPassword) {
        UserEntity user = findByUsername(username);
        forceResetUserPassword(user, newPassword);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        UserEntity user = findByUsername(username);
        boolean matches = passwordEncoder.matches(oldPassword, user.getPassword());
        if (!matches) {
            logger.error("用户ID: [{}] 用户名: [{}] 旧密码匹配失败", user.getId(), user.getUsername());
            throw new PasswordValidatedFailException();
        }
        forceResetUserPassword(user, newPassword);
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> {
            AuditContext.setContent("重置密码失败，用户不存在");
            logger.warn(AuditContext.getContent());
            return new TopIamException("操作失败");
        });
    }

    /**
     * UserService
     */
    private final UserRepository  userRepository;

    /**
     *  PasswordEncoder
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * UserService
     */
    private final UserService     userService;

    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                  UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }
}
