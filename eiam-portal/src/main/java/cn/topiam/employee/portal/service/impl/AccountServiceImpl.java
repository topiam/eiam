/*
 * eiam-portal - Employee Identity and Access Management Program
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
package cn.topiam.employee.portal.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.account.UserDetailEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.common.enums.MessageNoticeChannel;
import cn.topiam.employee.common.enums.SmsType;
import cn.topiam.employee.common.exception.BindMfaNotFoundSecretException;
import cn.topiam.employee.common.exception.InvalidMfaCodeException;
import cn.topiam.employee.common.exception.PasswordValidatedFailException;
import cn.topiam.employee.common.exception.UserNotFoundException;
import cn.topiam.employee.common.repository.account.UserDetailRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.core.security.mfa.provider.TotpAuthenticator;
import cn.topiam.employee.core.security.otp.OtpContextHelp;
import cn.topiam.employee.core.security.session.SessionDetails;
import cn.topiam.employee.core.security.session.TopIamSessionBackedSessionRegistry;
import cn.topiam.employee.core.security.util.SecurityUtils;
import cn.topiam.employee.portal.converter.AccountConverter;
import cn.topiam.employee.portal.pojo.request.*;
import cn.topiam.employee.portal.pojo.result.PrepareBindMfaResult;
import cn.topiam.employee.portal.service.AccountService;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.context.ServletContextHelp;
import cn.topiam.employee.support.util.BeanUtils;

import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.support.constant.EiamConstants.TOPIAM_BIND_MFA_SECRET;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_TIME;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/3 22:20
 */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Override
    public Boolean changeInfo(UpdateUserInfoRequest param) {
        //用户信息
        UserEntity toUserEntity = accountConverter.userUpdateParamConvertToUserEntity(param);
        UserEntity user = userRepository
            .findById(Long.valueOf(SecurityUtils.getCurrentUser().getId())).orElseThrow();
        BeanUtils.merge(toUserEntity, user, LAST_MODIFIED_BY, LAST_MODIFIED_TIME);
        userRepository.save(user);
        //用户详情
        UserDetailEntity detail = userDetailsRepository
            .findByUserId(Long.valueOf(SecurityUtils.getCurrentUserId()))
            .orElse(new UserDetailEntity());
        UserDetailEntity toUserDetailsEntity = accountConverter
            .userUpdateParamConvertToUserDetailsEntity(param);
        toUserDetailsEntity.setId(detail.getId());
        BeanUtils.merge(toUserDetailsEntity, detail, LAST_MODIFIED_BY, LAST_MODIFIED_TIME);
        userDetailsRepository.save(detail);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changePassword(ChangePasswordRequest param) {
        //获取用户
        UserEntity user = getUser();
        //密码不匹配
        if (!passwordEncoder.matches(param.getOldPassword(), user.getPassword())) {
            logger.error("用户ID: [{}] 用户名: [{}] 修改密码失败, 原密码不匹配", user.getId(), user.getUsername());
            throw new PasswordValidatedFailException("原密码不正确");
        }
        //修改密码
        userRepository.updateUserPassword(Long.valueOf(SecurityUtils.getCurrentUser().getId()),
            passwordEncoder.encode(param.getNewPassword()), LocalDateTime.now());
        logger.info("用户ID: [{}] 用户名: [{}] 修改密码成功", user.getId(), user.getUsername());
        //异步下线所有用户
        String username = SecurityUtils.getCurrentUserName();
        executor.execute(() -> {
            //@formatter:off
            if (sessionRegistry instanceof TopIamSessionBackedSessionRegistry) {
                List<Object> principals = ((TopIamSessionBackedSessionRegistry<? extends Session>) sessionRegistry).getPrincipals(username);
                principals.forEach(i -> {
                    if (i instanceof SessionDetails) {
                        sessionRegistry.removeSessionInformation(((SessionDetails) i).getSessionId());
                    }
                });
            }
        });
        SecurityContextHolder.clearContext();
        //@formatter:on
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean prepareChangePhone(PrepareChangePhoneRequest param) {
        UserEntity user = validatedPassword(param.getPassword());
        OtpContextHelp otpContextHelp = ApplicationContextHelp.getApplicationContext()
            .getBean(OtpContextHelp.class);
        // 发送短信验证码
        if (StringUtils.isNotBlank(user.getPhone())) {
            otpContextHelp.sendOtp(param.getPhone(), SmsType.UPDATE_PHONE.getCode(),
                MessageNoticeChannel.SMS);
        } else {
            otpContextHelp.sendOtp(param.getPhone(), SmsType.BIND_PHONE.getCode(),
                MessageNoticeChannel.SMS);
        }
        return true;
    }

    /**
     * 修改手机
     *
     * @param param    {@link ChangePhoneRequest}
     * @return Boolean
     */
    @Override
    public Boolean changePhone(ChangePhoneRequest param) {
        userRepository.updateUserPhone(Long.valueOf(SecurityUtils.getCurrentUser().getId()),
            param.getPhone());
        return true;
    }

    /**
     * 准备绑定MFA
     *
     * @param param {@link PrepareBindTotpRequest}
     * @return {@link PrepareBindMfaResult}
     */
    @Override
    public PrepareBindMfaResult prepareBindTotp(PrepareBindTotpRequest param) {
        String password = param.getPassword();
        UserEntity user = validatedPassword(password);
        //生成key
        String secret = TotpAuthenticator.generateSecretKey();
        //保存秘钥到会话
        ServletContextHelp.getSession().setAttribute(TOPIAM_BIND_MFA_SECRET, secret);
        String barcode = TotpAuthenticator.getQrBarcode(user.getUsername(), secret,
            ServerContextHelp.getPortalPublicBaseUrl());
        return PrepareBindMfaResult.builder().qrCode(barcode).build();
    }

    /**
     * bindMfa
     *
     * @param param {@link BindTotpRequest}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean bindTotp(BindTotpRequest param) {
        UserEntity user = getUser();
        //从会话获取秘钥
        String secret = (String) ServletContextHelp.getSession()
            .getAttribute(TOPIAM_BIND_MFA_SECRET);
        if (StringUtils.isBlank(secret)) {
            logger.error("用户ID: [{}] 用户名: [{}] 绑定TOTP 失败, 不存在秘钥", user.getId(), user.getUsername());
            throw new BindMfaNotFoundSecretException();
        }
        //验证 CODE
        boolean result = new TotpAuthenticator().checkCode(secret, param.getTotp(),
            System.currentTimeMillis());
        if (!result) {
            logger.error("用户ID: [{}] 用户名: [{}] 绑定TOTP 失败, 无效 TOTP CODE", user.getId(),
                user.getUsername());
            throw new InvalidMfaCodeException();
        }
        //保存秘钥，更改状态为绑定
        if (userRepository.updateUserSharedSecretAndTotpBind(user.getId(), secret,
            Boolean.TRUE) > 0) {
            logger.info("用户ID: [{}] 用户名: [{}] 绑定TOTP 成功", user.getId(), user.getUsername());
            //清理MFA秘钥
            ServletContextHelp.getSession().removeAttribute(TOPIAM_BIND_MFA_SECRET);
            return true;
        }
        return false;
    }

    /**
     * 解绑 TOTP
     *
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unbindTotp() {
        UserEntity user = getUser();
        //保存秘钥，更改状态为绑定
        if (userRepository.updateUserSharedSecretAndTotpBind(user.getId(), null,
            Boolean.FALSE) > 0) {
            logger.info("用户ID: [{}] 用户名: [{}] 解绑 TOTP 成功", user.getId(), user.getUsername());
            return true;
        }
        return false;
    }

    /**
     * 准备修改邮箱
     *
     * @param param {@link PrepareChangeEmailRequest}
     * @return {@link Boolean}
     */
    @Override
    public Boolean prepareChangeEmail(PrepareChangeEmailRequest param) {
        UserEntity user = validatedPassword(param.getPassword());
        OtpContextHelp otpContextHelp = ApplicationContextHelp.getApplicationContext()
            .getBean(OtpContextHelp.class);
        // 发送邮箱验证码
        if (StringUtils.isNotBlank(user.getPhone())) {
            otpContextHelp.sendOtp(param.getEmail(), MailType.UPDATE_BIND_MAIL.getCode(),
                MessageNoticeChannel.MAIL);
        } else {
            otpContextHelp.sendOtp(param.getEmail(), MailType.BIND_EMAIL.getCode(),
                MessageNoticeChannel.MAIL);
        }
        return true;
    }

    /**
     * 更改邮箱
     *
     * @param param      {@link ChangeEmailRequest}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changeEmail(ChangeEmailRequest param) {
        userRepository.updateUserEmail(Long.valueOf(SecurityUtils.getCurrentUser().getId()),
            param.getEmail());
        return true;
    }

    /**
     * 获取用户信息
     *
     * 为保证安全，从数据库获取最新信息使用，并更新到当前上下文中
     *
     * @return {@link UserEntity}
     */
    public UserEntity getUser() {
        String userId = SecurityUtils.getCurrentUserId();
        Optional<UserEntity> optional = userRepository.findById(Long.valueOf(userId));
        if (optional.isPresent()) {
            return optional.get();
        }
        SecurityContextHolder.clearContext();
        logger.error("根据用户ID: [{}] 未查询到用户信息", userId);
        throw new UserNotFoundException();
    }

    /**
     * 验证密码
     *
     * @param password {@link String}
     * @return {@link UserEntity}
     */
    public UserEntity validatedPassword(String password) {
        UserEntity user = getUser();
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if (!matches) {
            logger.error("用户ID: [{}] 用户名: [{}] 密码匹配失败", user.getId(), user.getUsername());
            throw new PasswordValidatedFailException();
        }
        return user;
    }

    /**
     * Executor
     */
    private final Executor                                              executor;

    /**
     *  AccountConverter
     */
    private final AccountConverter                                      accountConverter;

    /**
     *  PasswordEncoder
     */
    private final PasswordEncoder                                       passwordEncoder;

    /**
     * UserRepository
     */
    private final UserRepository                                        userRepository;

    /**
     * 用户详情Repository
     */
    private final UserDetailRepository                                  userDetailsRepository;

    private final SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry;

    public AccountServiceImpl(AsyncConfigurer asyncConfigurer, AccountConverter accountConverter,
                              PasswordEncoder passwordEncoder, UserRepository userRepository,
                              UserDetailRepository userDetailsRepository,
                              SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry) {
        this.executor = asyncConfigurer.getAsyncExecutor();
        this.accountConverter = accountConverter;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.sessionRegistry = sessionRegistry;
    }
}
