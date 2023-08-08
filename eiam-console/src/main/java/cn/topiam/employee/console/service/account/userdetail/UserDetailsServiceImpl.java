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
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.google.common.collect.Lists;

import cn.topiam.employee.common.entity.setting.AdministratorEntity;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.common.repository.setting.AdministratorRepository;
import cn.topiam.employee.support.security.userdetails.UserDetails;
import cn.topiam.employee.support.security.userdetails.UserDetailsService;
import cn.topiam.employee.support.security.userdetails.UserType;

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
        // 状态相关
        boolean enabled = true, accountNonLocked = true;
        // 用户名
        Optional<AdministratorEntity> optional = administratorRepository.findByUsername(username);
        if (optional.isEmpty()) {
            // 手机号
            optional = administratorRepository.findByPhone(username);
            if (optional.isEmpty()) {
                // 邮箱
                optional = administratorRepository.findByEmail(username);
            }
        }
        //不存在该用户
        if (optional.isEmpty()) {
            logger.info("根据用户名、手机号、邮箱未查询该管理员【{}】", username);
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        AdministratorEntity administrator = optional.get();
        if (!ObjectUtils.isEmpty(administrator.getStatus())) {
            //锁定
            if (administrator.getStatus().equals(UserStatus.LOCKED)
                || administrator.getStatus().equals(UserStatus.PASSWORD_EXPIRED_LOCKED)
                || administrator.getStatus().equals(UserStatus.EXPIRED_LOCKED)) {
                logger.info("管理员【{}】被锁定", administrator.getUsername());
                accountNonLocked = false;
            }
            //禁用
            if (administrator.getStatus().equals(UserStatus.DISABLE)) {
                logger.info("管理员【{}】被禁用", administrator.getUsername());
                enabled = false;
            }
            // 查询封装用户权限
            return new UserDetails(String.valueOf(administrator.getId()),
                administrator.getUsername(), administrator.getPassword(), UserType.ADMIN, enabled,
                true, true, accountNonLocked,
                Lists.newArrayList(new SimpleGrantedAuthority(UserType.ADMIN.getType())));
        }
        logger.info("管理员【{}】状态异常", administrator.getUsername());
        throw new AccountExpiredException("管理员状态异常");
    }

    /**
     * AdministratorRepository
     */
    private final AdministratorRepository administratorRepository;

    public UserDetailsServiceImpl(AdministratorRepository administratorRepository) {
        this.administratorRepository = administratorRepository;
    }
}
