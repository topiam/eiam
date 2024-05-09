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
package cn.topiam.employee.portal.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.authentication.common.IdentityProviderAuthenticationService;
import cn.topiam.employee.authentication.common.authentication.IdentityProviderUserDetails;
import cn.topiam.employee.authentication.common.exception.UserBindIdentityProviderException;
import cn.topiam.employee.common.entity.account.ThirdPartyUserEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.UserIdpBindEntity;
import cn.topiam.employee.common.entity.account.po.UserIdpBindPO;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.common.repository.account.ThirdPartyUserRepository;
import cn.topiam.employee.common.repository.account.UserIdpRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.portal.converter.AccountConverter;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.security.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 身份验证用户详细信息实现
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2021/12/11 21:10
 */
@Component
@Slf4j
@AllArgsConstructor
public class IdentityProviderAuthenticationServiceImpl implements
                                                       IdentityProviderAuthenticationService {
    /**
     * 用户是否绑定
     *
     * @param openId     {@link String}
     * @param providerId {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean checkIdpUserIsExistBind(String openId, String providerId) {
        Optional<IdentityProviderEntity> source = identityProviderRepository.findById(providerId);
        if (source.isEmpty()) {
            throw new NullPointerException("认证源不存在");
        }
        if (!source.get().getEnabled()) {
            throw new TopIamException("认证源已禁用");
        }
        Optional<UserIdpBindPO> authnBind = userIdpRepository.findByIdpIdAndOpenId(providerId,
            openId);
        return authnBind.isPresent();
    }

    /**
     * 绑定
     *
     * @param accountId   {@link  String} 账户ID
     * @param identityProviderUserDetails {@link  IdentityProviderUserDetails} 用户信息
     * @return {@link  Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean bindUserIdp(String accountId,
                               IdentityProviderUserDetails identityProviderUserDetails) {
        // 查询是否已绑定其他账号
        Optional<ThirdPartyUserEntity> thirdPartyUserEntity = this.thirdPartyUserRepository
            .findByOpenIdAndIdpId(identityProviderUserDetails.getOpenId(),
                identityProviderUserDetails.getProviderId());
        ThirdPartyUserEntity saveThirdPartyUser = accountConverter
            .thirdPartyUserConverterToEntity(identityProviderUserDetails);
        if (thirdPartyUserEntity.isPresent()) {
            long existence = userIdpRepository.countByThirdPartyUser(thirdPartyUserEntity.get());
            if (existence > 0) {
                throw new UserBindIdentityProviderException("该三方账号已绑定其他账号",
                    HttpStatus.INTERNAL_SERVER_ERROR);
            }
            ThirdPartyUserEntity thirdPartyUser = thirdPartyUserEntity.get();
            saveThirdPartyUser.setId(thirdPartyUser.getId());
            // 更新三方用户
            if (!thirdPartyUser.equals(saveThirdPartyUser)) {
                thirdPartyUserRepository.save(saveThirdPartyUser);
            }
        } else {
            thirdPartyUserRepository.save(saveThirdPartyUser);
        }
        // 创建绑定关系
        UserIdpBindEntity userIdpBind = new UserIdpBindEntity();
        userIdpBind.setUserId(accountId);
        userIdpBind.setThirdPartyUser(saveThirdPartyUser);
        userIdpBind.setBindTime(LocalDateTime.now());
        userIdpRepository.save(userIdpBind);
        return true;
    }

    /**
     * 更新三方账户信息
     *
     * @param identityProviderUserDetails   {@link IdentityProviderUserDetails}
     * @param providerId {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean updateThirdPartyUser(IdentityProviderUserDetails identityProviderUserDetails,
                                        String providerId) {
        Optional<ThirdPartyUserEntity> thirdPartyUser = thirdPartyUserRepository
            .findByOpenIdAndIdpId(identityProviderUserDetails.getOpenId(), providerId);
        if (thirdPartyUser.isPresent()) {
            // 判断是否需要更新
            ThirdPartyUserEntity entity = thirdPartyUser.get();
            ThirdPartyUserEntity newThirdPartyUser = accountConverter
                .thirdPartyUserConverterToEntity(identityProviderUserDetails);
            newThirdPartyUser.setId(entity.getId());
            if (!entity.equals(newThirdPartyUser)) {
                thirdPartyUserRepository.save(newThirdPartyUser);
            }
            return Boolean.TRUE;
        }
        log.error("三方账号信息不存在, openId:[{}], idpId:[{}]", identityProviderUserDetails.getOpenId(),
            providerId);
        return Boolean.FALSE;
    }

    /**
     * 获取用户
     *
     * @param openId     {@link  String}
     * @param providerId {@link  String}
     * @return {@link  UserDetails}
     */
    @Override
    public UserDetails getUserDetails(String openId, String providerId) {
        // 权限
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        UserEntity user = getUser(openId, providerId);
        return user.toUserDetails(authorities);
    }

    private UserEntity getUser(String openId, String providerId) {
        Optional<UserIdpBindPO> optional = userIdpRepository.findByIdpIdAndOpenId(providerId,
            openId);
        if (optional.isEmpty()) {
            throw new NullPointerException("用户未绑定");
        }
        UserIdpBindPO userIdpBind = optional.get();
        String userId = userIdpBind.getUserId();
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
     * AuthenticationSourceRepository
     */
    private final IdentityProviderRepository identityProviderRepository;

    /**
     * UserAuthnBindRepository
     */
    private final UserIdpRepository          userIdpRepository;

    /**
     * ThirdPartyUserRepository
     */
    private final ThirdPartyUserRepository   thirdPartyUserRepository;

    /**
     * AccountConverter
     */
    private final AccountConverter           accountConverter;

}
