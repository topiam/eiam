/*
 * eiam-portal - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.portal.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import cn.topiam.employee.authentication.common.modal.IdpUser;
import cn.topiam.employee.authentication.common.service.UserIdpService;
import cn.topiam.employee.common.entity.account.UserDetailEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.UserIdpBindEntity;
import cn.topiam.employee.common.entity.account.po.UserIdpBindPo;
import cn.topiam.employee.common.entity.authentication.IdentityProviderEntity;
import cn.topiam.employee.common.repository.account.UserDetailRepository;
import cn.topiam.employee.common.repository.account.UserIdpRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.portal.converter.AccountConverter;
import cn.topiam.employee.portal.service.userdetail.UserDetailsServiceImpl;
import cn.topiam.employee.support.exception.TopIamException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 身份验证用户详细信息实现
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/11 21:10
 */
@Component
@Slf4j
@AllArgsConstructor
public class UserIdpServiceImpl implements UserIdpService {
    /**
     * 用户是否绑定
     *
     * @param openId     {@link String}
     * @param providerId {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean checkUserIdpIsAlreadyBind(String openId, String providerId) {
        Optional<IdentityProviderEntity> source = identityProviderRepository
            .findById(Long.valueOf(providerId));
        if (source.isEmpty()) {
            throw new NullPointerException("认证源不存在");
        }
        if (!source.get().getEnabled()) {
            throw new TopIamException("认证源已禁用");
        }
        Optional<UserIdpBindPo> authnBind = userIdpRepository.findByIdpIdAndOpenId(providerId,
            openId);
        return authnBind.isPresent();
    }

    /**
     * 是否自动绑定账户
     *
     * @param providerId {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean isAutoBindUserIdp(String providerId) {
        return false;
    }

    /**
     * 绑定
     *
     * @param accountId   {@link  String} 账户ID
     * @param idpUser {@link  IdpUser} 用户信息
     * @return {@link  Boolean}
     */
    @Override
    public Boolean bindUserIdp(String accountId, IdpUser idpUser) {
        UserIdpBindEntity userIdpBind = accountConverter
            .accountBindIdpRequestConverterToEntity(accountId, idpUser);
        userIdpRepository.save(userIdpBind);
        return true;
    }

    /**
     * 更新账户信息
     *
     * @param idpUser   {@link IdpUser}
     * @param providerId {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean updateUser(IdpUser idpUser, String providerId) {
        return true;
    }

    /**
     * 获取用户
     *
     * @param openId     {@link  String}
     * @param providerId {@link  String}
     * @return {@link  UserDetails}
     */
    @SuppressWarnings("DuplicatedCode")
    @Override
    public UserDetails getUserDetails(String openId, String providerId) {
        // 状态相关
        boolean enabled = true, accountNonLocked = true;
        // 权限
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        UserEntity user = getUser(openId, providerId);
        //用户详情
        Optional<UserDetailEntity> userDetail = userDetailRepository.findByUserId(user.getId());
        return UserDetailsServiceImpl.getUserDetails(enabled, accountNonLocked, authorities, user,
            userDetail.orElse(new UserDetailEntity()));
    }

    private UserEntity getUser(String openId, String providerId) {
        Optional<UserIdpBindPo> bindEntity = userIdpRepository.findByIdpIdAndOpenId(providerId,
            openId);
        if (bindEntity.isEmpty()) {
            throw new NullPointerException("用户未绑定");
        }
        Long userId = bindEntity.get().getUserId();
        Optional<UserEntity> entity = userRepository.findById(userId);
        if (entity.isEmpty()) {
            throw new NullPointerException("用户不存在");
        }
        return entity.get();
    }

    /**
     * UserRepository
     */
    private final UserRepository             userRepository;

    /**
     * UserDetailRepository
     */
    private final UserDetailRepository       userDetailRepository;

    /**
     * AuthenticationSourceRepository
     */
    private final IdentityProviderRepository identityProviderRepository;

    /**
     * UserAuthnBindRepository
     */
    private final UserIdpRepository          userIdpRepository;

    private final AccountConverter           accountConverter;

}
