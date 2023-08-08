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

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.google.common.collect.Lists;

import cn.topiam.employee.audit.access.AuditAccess;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.support.security.userdetails.UserDetails;
import cn.topiam.employee.support.util.PhoneNumberUtils;
import static cn.topiam.employee.support.security.userdetails.UserType.USER;

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
        // 状态相关
        boolean enabled = true, accountNonLocked = true;
        // 权限，默认赋予审计查看权限
        Collection<SimpleGrantedAuthority> authorities = Lists
            .newArrayList(new SimpleGrantedAuthority(AuditAccess.Audit.audit_list.getCode()));
        UserEntity user;
        // 用户名
        user = userRepository.findByUsername(username);
        if (ObjectUtils.isEmpty(user)) {
            // 手机号
            if (PhoneNumberUtils.isPhoneValidate(username)) {
                user = userRepository.findByPhone(PhoneNumberUtils.getPhoneNumber(username));
            }
            if (ObjectUtils.isEmpty(user)) {
                // 邮箱
                user = userRepository.findByEmail(username);
            }
        }
        //不存在该用户
        if (ObjectUtils.isEmpty(user)) {
            logger.info("根据用户名、手机号、邮箱未查询该用户【{}】", username);
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return getUserDetails(enabled, accountNonLocked, authorities, user);
    }

    public static UserDetails getUserDetails(boolean enabled, boolean accountNonLocked,
                                             Collection<SimpleGrantedAuthority> authorities,
                                             UserEntity user) {
        //TODO 密码是否过期

        //TODO 状态
        if (!ObjectUtils.isEmpty(user.getStatus())) {
            //锁定
            if (user.getStatus().equals(UserStatus.LOCKED)
                || user.getStatus().equals(UserStatus.PASSWORD_EXPIRED_LOCKED)
                || user.getStatus().equals(UserStatus.EXPIRED_LOCKED)) {
                logger.info("用户【{}】被锁定", user.getUsername());
                accountNonLocked = false;
            }
            //禁用
            if (user.getStatus().equals(UserStatus.DISABLE)) {
                logger.info("用户【{}】被禁用", user.getUsername());
                enabled = false;
            }
            //根据用户类型封装权限
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(USER.getType());
            authorities.add(authority);

            //封装
            return new UserDetails(String.valueOf(user.getId()), user.getUsername(),
                user.getPassword(), USER, enabled, true, true, accountNonLocked, authorities);
        }
        logger.info("用户【{}】状态异常", user.getUsername());
        throw new AccountExpiredException("用户状态异常");
    }

    /**
     * UserRepository
     */
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
