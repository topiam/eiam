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
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.common.entity.account.UserDetailEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.po.UserIdpBindPo;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.common.enums.MessageNoticeChannel;
import cn.topiam.employee.common.enums.SmsType;
import cn.topiam.employee.common.exception.PasswordValidatedFailException;
import cn.topiam.employee.common.exception.UserNotFoundException;
import cn.topiam.employee.common.repository.account.UserDetailRepository;
import cn.topiam.employee.common.repository.account.UserIdpRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.core.message.sms.SmsMsgEventPublish;
import cn.topiam.employee.core.mq.UserMessagePublisher;
import cn.topiam.employee.core.mq.UserMessageTag;
import cn.topiam.employee.core.security.otp.OtpContextHelp;
import cn.topiam.employee.portal.converter.AccountConverter;
import cn.topiam.employee.portal.pojo.request.*;
import cn.topiam.employee.portal.pojo.result.BoundIdpListResult;
import cn.topiam.employee.portal.service.AccountService;
import cn.topiam.employee.support.context.ServletContextHelp;
import cn.topiam.employee.support.exception.BadParamsException;
import cn.topiam.employee.support.exception.InfoValidityFailException;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.security.password.PasswordPolicyManager;
import cn.topiam.employee.support.security.util.SecurityUtils;
import cn.topiam.employee.support.util.BeanUtils;
import cn.topiam.employee.support.util.PhoneNumberUtils;

import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpSession;
import static cn.topiam.employee.audit.enums.TargetType.*;
import static cn.topiam.employee.core.message.sms.SmsMsgEventPublish.USERNAME;
import static cn.topiam.employee.support.constant.EiamConstants.FORGET_PASSWORD_TOKEN_ID;
import static cn.topiam.employee.support.exception.enums.ExceptionStatus.EX000102;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_TIME;
import static cn.topiam.employee.support.util.EmailUtils.isEmailValidate;
import static cn.topiam.employee.support.util.PhoneNumberUtils.isPhoneValidate;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/3 22:20
 */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changeInfo(UpdateUserInfoRequest param) {
        //用户信息
        UserEntity toUserEntity = accountConverter.userUpdateParamConvertToUserEntity(param);
        UserEntity user = userRepository
            .findById(Long.valueOf(SecurityUtils.getCurrentUser().getId())).orElseThrow();
        BeanUtils.merge(toUserEntity, user, LAST_MODIFIED_BY, LAST_MODIFIED_TIME);
        userRepository.save(user);
        //用户详情
        String currentUserId = SecurityUtils.getCurrentUserId();
        UserDetailEntity detail = userDetailsRepository.findByUserId(Long.valueOf(currentUserId))
            .orElse(new UserDetailEntity().setUserId(user.getId()));
        UserDetailEntity toUserDetailsEntity = accountConverter
            .userUpdateParamConvertToUserDetailsEntity(param);
        toUserDetailsEntity.setId(detail.getId());
        BeanUtils.merge(toUserDetailsEntity, detail, LAST_MODIFIED_BY, LAST_MODIFIED_TIME);
        userDetailsRepository.save(detail);
        // 更新ES用户信息
        userMessagePublisher.sendUserChangeMessage(UserMessageTag.SAVE,
            String.valueOf(detail.getId()));
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changePassword(ChangePasswordRequest param) {
        //获取用户
        UserEntity user = getCurrentUser();
        Boolean checkOtp = otpContextHelp.checkOtp(
            MessageNoticeChannel.SMS == param.getChannel() ? SmsType.UPDATE_PASSWORD.getCode()
                : MailType.UPDATE_PASSWORD.getCode(),
            param.getChannel(),
            MessageNoticeChannel.SMS == param.getChannel() ? user.getPhone() : user.getEmail(),
            param.getVerifyCode());
        if (!checkOtp) {
            throw new InfoValidityFailException(EX000102.getMessage());
        }
        // 校验密码
        passwordPolicyManager.validate(user, param.getNewPassword());
        //修改密码
        userRepository.updateUserPassword(Long.valueOf(SecurityUtils.getCurrentUser().getId()),
            passwordEncoder.encode(param.getNewPassword()), LocalDateTime.now());
        logger.info("用户ID: [{}] 用户名: [{}] 修改密码成功", user.getId(), user.getUsername());
        //异步下线所有用户
        removeSession(SecurityUtils.getCurrentUserName());
        //@formatter:on
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean prepareChangePhone(PrepareChangePhoneRequest param) {
        UserEntity user = validatedPassword(param.getPassword());
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
     * @param param {@link ChangePhoneRequest}
     * @return Boolean
     */
    @Override
    public Boolean changePhone(ChangePhoneRequest param) {
        UserEntity user = getCurrentUser();
        Boolean checkOtp;
        if (StringUtils.isNotBlank(user.getPhone())) {
            checkOtp = otpContextHelp.checkOtp(SmsType.UPDATE_PHONE.getCode(),
                MessageNoticeChannel.SMS, param.getPhone(), param.getOtp());
        } else {
            checkOtp = otpContextHelp.checkOtp(SmsType.BIND_PHONE.getCode(),
                MessageNoticeChannel.SMS, param.getPhone(), param.getOtp());
        }
        if (!checkOtp) {
            throw new InfoValidityFailException(EX000102.getMessage());
        }
        // 校验是否已经存在
        UserEntity byEmail = userRepository.findByPhone(param.getPhone());
        if (Objects.nonNull(byEmail) && !user.getId().equals(byEmail.getId())) {
            throw new TopIamException("系统中已存在[" + param.getPhone() + "]手机号, 请先解绑");
        }
        Long id = Long.valueOf(SecurityUtils.getCurrentUser().getId());
        userRepository.updateUserPhone(id, param.getPhone());
        // 更新ES用户手机号信息
        userMessagePublisher.sendUserChangeMessage(UserMessageTag.SAVE, String.valueOf(id));
        // 修改手机号成功发送短信
        LinkedHashMap<String, String> parameter = Maps.newLinkedHashMap();
        parameter.put(USERNAME, user.getUsername());
        smsMsgEventPublish.publish(SmsType.BIND_PHONE_SUCCESS, param.getPhone(), parameter);
        return true;
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
        // 发送邮箱验证码
        if (StringUtils.isNotBlank(user.getEmail())) {
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
     * @param param {@link ChangeEmailRequest}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changeEmail(ChangeEmailRequest param) {
        UserEntity user = getCurrentUser();
        Boolean checkOtp;
        if (StringUtils.isNotBlank(user.getEmail())) {
            checkOtp = otpContextHelp.checkOtp(MailType.UPDATE_BIND_MAIL.getCode(),
                MessageNoticeChannel.MAIL, param.getEmail(), param.getOtp());
        } else {
            checkOtp = otpContextHelp.checkOtp(MailType.BIND_EMAIL.getCode(),
                MessageNoticeChannel.MAIL, param.getEmail(), param.getOtp());
        }
        if (!checkOtp) {
            throw new InfoValidityFailException(EX000102.getMessage());
        }
        // 校验是否已经存在
        UserEntity byEmail = userRepository.findByEmail(param.getEmail());
        if (Objects.nonNull(byEmail) && !user.getId().equals(byEmail.getId())) {
            throw new TopIamException("系统中已存在[" + param.getEmail() + "]邮箱, 请先解绑");
        }
        userRepository.updateUserEmail(Long.valueOf(SecurityUtils.getCurrentUser().getId()),
            param.getEmail());
        // 更新ES用户邮箱信息
        userMessagePublisher.sendUserChangeMessage(UserMessageTag.SAVE,
            String.valueOf(user.getId()));
        return true;
    }

    @Override
    public Boolean prepareChangePassword(PrepareChangePasswordRequest param) {
        UserEntity user = getCurrentUser();
        // 发送短信验证码
        if (MessageNoticeChannel.SMS == param.getChannel()) {
            otpContextHelp.sendOtp(user.getPhone(), SmsType.UPDATE_PASSWORD.getCode(),
                MessageNoticeChannel.SMS);
        } else {
            otpContextHelp.sendOtp(user.getEmail(), MailType.UPDATE_PASSWORD.getCode(),
                MessageNoticeChannel.MAIL);
        }
        return true;
    }

    @Override
    public Boolean forgetPasswordCode(String recipient) {
        if (isEmailValidate(recipient)) {
            // 验证在库中是否有邮箱
            Optional<UserEntity> byEmail = Optional
                .ofNullable(userRepository.findByEmail(recipient));
            if (byEmail.isPresent()) {
                otpContextHelp.sendOtp(recipient, MailType.FORGET_PASSWORD.getCode(),
                    MessageNoticeChannel.MAIL);
                return true;
            }
            log.warn("忘记密码: : 邮箱: [{}] 不存在", recipient);
        } else if (isPhoneValidate(recipient)) {
            // 验证在库中是否有手机号
            Optional<UserEntity> byPhone = Optional
                .ofNullable(userRepository.findByPhone(PhoneNumberUtils.getPhoneNumber(recipient)));
            if (byPhone.isPresent()) {
                otpContextHelp.sendOtp(recipient, SmsType.FORGET_PASSWORD.getCode(),
                    MessageNoticeChannel.SMS);
                return true;
            }
            log.warn("忘记密码: : 手机号: [{}] 不存在", recipient);
        }
        log.error("忘记密码: : 接受者: [{}] 格式错误", recipient);
        throw new BadParamsException("请输入正确的手机号或邮箱");
    }

    @Override
    public Boolean prepareForgetPassword(String recipient, String code) {
        // 校验验证码
        Boolean checkOtp = false;
        Optional<UserEntity> user = Optional.empty();
        if (isEmailValidate(recipient)) {
            user = Optional.ofNullable(userRepository.findByEmail(recipient));
            if (user.isPresent()) {
                checkOtp = otpContextHelp.checkOtp(MailType.FORGET_PASSWORD.getCode(),
                    MessageNoticeChannel.MAIL, recipient, code);
            }
        } else if (isPhoneValidate(recipient)) {
            user = Optional
                .ofNullable(userRepository.findByPhone(PhoneNumberUtils.getPhoneNumber(recipient)));
            if (user.isPresent()) {
                checkOtp = otpContextHelp.checkOtp(SmsType.FORGET_PASSWORD.getCode(),
                    MessageNoticeChannel.SMS, recipient, code);
            }
        }
        if (!checkOtp) {
            throw new InfoValidityFailException(EX000102.getMessage());
        }

        // 生成忘记密码TOKEN ID
        String tokenId = UUID.randomUUID().toString();
        HttpSession session = ServletContextHelp.getSession();
        // 保存用户ID到Redis, 有效期10分钟
        stringRedisTemplate.opsForValue().set(session.getId() + tokenId,
            String.valueOf(user.get().getId()), 10, TimeUnit.MINUTES);
        // 保存TOKEN ID到会话
        session.setAttribute(FORGET_PASSWORD_TOKEN_ID, tokenId);
        return true;
    }

    @Override
    public Boolean forgetPassword(ForgetPasswordRequest forgetPasswordRequest) {
        // 验证TOKEN
        HttpSession session = ServletContextHelp.getSession();
        String redisTokenId = session.getId() + session.getAttribute(FORGET_PASSWORD_TOKEN_ID);
        String userId = stringRedisTemplate.opsForValue().get(redisTokenId);
        if (Objects.isNull(userId)) {
            // 清除tokenId
            session.removeAttribute(FORGET_PASSWORD_TOKEN_ID);
            return false;
        }
        //修改密码
        Optional<UserEntity> user = userRepository.findById(Long.valueOf(userId));
        if (user.isPresent()) {
            UserEntity userEntity = user.get();
            userRepository.updateUserPassword(userEntity.getId(),
                passwordEncoder.encode(forgetPasswordRequest.getNewPassword()),
                LocalDateTime.now());
            logger.info("忘记密码: 用户ID: [{}] 用户名: [{}] 修改密码成功", userEntity.getId(),
                userEntity.getUsername());
            removeSession(userEntity.getUsername());
            stringRedisTemplate.delete(redisTokenId);
            return true;
        }
        return false;
    }

    @Override
    public List<BoundIdpListResult> getBoundIdpList() {
        //获取idp
        List<IdentityProviderEntity> identityProviderList = identityProviderRepository
            .findByEnabledIsTrueAndDisplayedIsTrue();
        // 获取已绑定idp
        Iterable<UserIdpBindPo> userIdpBindList = userIdpRepository
            .getUserIdpBindList(Long.valueOf(SecurityUtils.getCurrentUserId()));
        return accountConverter.entityConverterToBoundIdpListResult(identityProviderList,
            userIdpBindList);
    }

    @Override
    public Boolean unbindIdp(String id) {
        userIdpRepository.deleteByUserIdAndIdpId(SecurityUtils.getCurrentUserId(), id);
        AuditContext.setTarget(
            Target.builder().type(USER).id(SecurityUtils.getCurrentUserId()).build(),
            Target.builder().type(USER_DETAIL).id(SecurityUtils.getCurrentUserId()).build(),
            Target.builder().type(IDENTITY_PROVIDER).id(id).build());
        return true;
    }

    /**
     * 异步下线所有用户
     *
     * @param username {@link String}
     */
    private void removeSession(String username) {
        executor.execute(() -> {
            List<SessionInformation> sessions = sessionRegistry.getAllSessions(username, false);
            sessions.forEach(SessionInformation::expireNow);
            //@formatter:on
        });
        SecurityContextHolder.clearContext();
    }

    /**
     * 获取用户信息
     * 为保证安全，从数据库获取最新信息使用，并更新到当前上下文中
     *
     * @return {@link UserEntity}
     */
    public UserEntity getCurrentUser() {
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
        UserEntity user = getCurrentUser();
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
    private final Executor                          executor;

    /**
     *  AccountConverter
     */
    private final AccountConverter                  accountConverter;

    /**
     *  PasswordEncoder
     */
    private final PasswordEncoder                   passwordEncoder;

    /**
     * UserRepository
     */
    private final UserRepository                    userRepository;

    /**
     * 用户详情Repository
     */
    private final UserDetailRepository              userDetailsRepository;

    /**
     * SessionRegistry
     */
    private final SessionRegistry                   sessionRegistry;

    /**
     * OtpContextHelp
     */
    private final OtpContextHelp                    otpContextHelp;

    /**
     * SmsMsgEventPublish
     */
    private final SmsMsgEventPublish                smsMsgEventPublish;

    /**
     * StringRedisTemplate
     */
    private final StringRedisTemplate               stringRedisTemplate;

    /**
     * PasswordPolicyManager
     */
    private final PasswordPolicyManager<UserEntity> passwordPolicyManager;

    /**
     * IdentityProviderRepository
     */
    private final IdentityProviderRepository        identityProviderRepository;

    /**
     * UserAuthnBindRepository
     */
    private final UserIdpRepository                 userIdpRepository;

    /**
     * MessagePublisher
     */
    private final UserMessagePublisher              userMessagePublisher;

    public AccountServiceImpl(AsyncConfigurer asyncConfigurer, AccountConverter accountConverter,
                              PasswordEncoder passwordEncoder, UserRepository userRepository,
                              UserDetailRepository userDetailsRepository,
                              SessionRegistry sessionRegistry, OtpContextHelp otpContextHelp,
                              SmsMsgEventPublish smsMsgEventPublish,
                              StringRedisTemplate stringRedisTemplate,
                              PasswordPolicyManager<UserEntity> passwordPolicyManager,
                              IdentityProviderRepository identityProviderRepository,
                              UserIdpRepository userIdpRepository,
                              UserMessagePublisher userMessagePublisher) {
        this.executor = asyncConfigurer.getAsyncExecutor();
        this.accountConverter = accountConverter;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.sessionRegistry = sessionRegistry;
        this.otpContextHelp = otpContextHelp;
        this.smsMsgEventPublish = smsMsgEventPublish;
        this.stringRedisTemplate = stringRedisTemplate;
        this.passwordPolicyManager = passwordPolicyManager;
        this.identityProviderRepository = identityProviderRepository;
        this.userIdpRepository = userIdpRepository;
        this.userMessagePublisher = userMessagePublisher;
    }
}
