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
package cn.topiam.employee.console.service.user.impl;

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

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.setting.AdministratorEntity;
import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.common.enums.MessageNoticeChannel;
import cn.topiam.employee.common.enums.SmsType;
import cn.topiam.employee.common.exception.UserNotFoundException;
import cn.topiam.employee.common.repository.setting.AdministratorRepository;
import cn.topiam.employee.console.converter.user.UserProfileConverter;
import cn.topiam.employee.console.pojo.update.user.*;
import cn.topiam.employee.console.service.user.UserProfileService;
import cn.topiam.employee.core.message.sms.SmsMsgEventPublish;
import cn.topiam.employee.core.security.otp.OtpContextHelp;
import cn.topiam.employee.support.context.ServletContextService;
import cn.topiam.employee.support.exception.BadParamsException;
import cn.topiam.employee.support.exception.InfoValidityFailException;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.security.password.exception.PasswordValidatedFailException;
import cn.topiam.employee.support.security.util.SecurityUtils;
import cn.topiam.employee.support.util.BeanUtils;
import cn.topiam.employee.support.util.PhoneNumberUtils;

import jakarta.servlet.http.HttpSession;
import static cn.topiam.employee.core.message.sms.SmsMsgEventPublish.USERNAME;
import static cn.topiam.employee.support.constant.EiamConstants.FORGET_PASSWORD_TOKEN_ID;
import static cn.topiam.employee.support.exception.enums.ExceptionStatus.EX000102;
import static cn.topiam.employee.support.repository.base.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.base.BaseEntity.LAST_MODIFIED_TIME;
import static cn.topiam.employee.support.util.EmailUtils.isEmailValidate;
import static cn.topiam.employee.support.util.PhoneNumberUtils.isPhoneValidate;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/10/3 22:20
 */
@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changeInfo(UpdateUserInfoRequest param) {
        //用户信息
        AdministratorEntity administrator = userProfileConverter
            .userUpdateParamConvertToAdministratorEntity(param);
        AdministratorEntity user = administratorRepository
            .findById(SecurityUtils.getCurrentUser().getId()).orElseThrow();
        BeanUtils.merge(administrator, user, LAST_MODIFIED_BY, LAST_MODIFIED_TIME);
        administratorRepository.save(user);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changePassword(ChangePasswordRequest param) {
        //获取用户
        AdministratorEntity administrator = getCurrentUser();
        //校验旧密码
        if (!passwordEncoder.matches(param.getOldPassword(), administrator.getPassword())) {
            logger.error("用户ID: [{}] 用户名: [{}] 修改密码失败，原密码错误", administrator.getId(),
                administrator.getUsername());
            throw new PasswordValidatedFailException("旧密码错误");
        }
        //修改密码
        administratorRepository.updatePassword(SecurityUtils.getCurrentUser().getId(),
            passwordEncoder.encode(param.getNewPassword()), LocalDateTime.now());
        logger.info("用户ID: [{}] 用户名: [{}] 修改密码成功", administrator.getId(),
            administrator.getUsername());
        //异步下线所有用户
        removeSession(SecurityUtils.getCurrentUserName());
        //@formatter:on
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean prepareChangePhone(PrepareChangePhoneRequest param) {
        AdministratorEntity user = validatedPassword(param.getPassword());
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
        AdministratorEntity user = getCurrentUser();
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
        Optional<AdministratorEntity> optionalAdministrator = administratorRepository
            .findByPhone(param.getPhone());
        if (optionalAdministrator.isPresent()
            && !user.getId().equals(optionalAdministrator.get().getId())) {
            throw new TopIamException("系统中已存在[" + param.getPhone() + "]手机号, 请先解绑");
        }
        String id = SecurityUtils.getCurrentUser().getId();
        administratorRepository.updateByIdAndPhone(id, param.getPhone());
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
        AdministratorEntity user = validatedPassword(param.getPassword());
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
        AdministratorEntity administrator = getCurrentUser();
        Boolean checkOtp;
        if (StringUtils.isNotBlank(administrator.getEmail())) {
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
        Optional<AdministratorEntity> optionalAdministrator = administratorRepository
            .findByEmail(param.getEmail());
        if (optionalAdministrator.isPresent()
            && !administrator.getId().equals(optionalAdministrator.get().getId())) {
            throw new TopIamException("系统中已存在[" + param.getEmail() + "]邮箱, 请先解绑");
        }
        administratorRepository.updateByIdAndEmail(SecurityUtils.getCurrentUser().getId(),
            param.getEmail());
        return true;
    }

    @Override
    public Boolean forgetPasswordCode(String recipient) {
        if (isEmailValidate(recipient)) {
            // 验证在库中是否有邮箱
            Optional<AdministratorEntity> byEmail = administratorRepository.findByEmail(recipient);
            if (byEmail.isPresent()) {
                otpContextHelp.sendOtp(recipient, MailType.FORGET_PASSWORD.getCode(),
                    MessageNoticeChannel.MAIL);
                return true;
            }
            logger.warn("忘记密码: 邮箱: [{}] 不存在", recipient);
        } else if (isPhoneValidate(recipient)) {
            // 验证在库中是否有手机号
            Optional<AdministratorEntity> byPhone = administratorRepository
                .findByPhone(PhoneNumberUtils.getPhoneNumber(recipient));
            if (byPhone.isPresent()) {
                otpContextHelp.sendOtp(recipient, SmsType.FORGET_PASSWORD.getCode(),
                    MessageNoticeChannel.SMS);
                return true;
            }
            logger.warn("忘记密码: 手机号: [{}] 不存在", recipient);
        }
        logger.error("忘记密码: 接受者: [{}] 格式错误", recipient);
        throw new BadParamsException("请输入正确的手机号或邮箱");
    }

    @Override
    public Boolean prepareForgetPassword(String recipient, String code) {
        // 校验验证码
        Boolean checkOtp = false;
        Optional<AdministratorEntity> user = Optional.empty();
        if (isEmailValidate(recipient)) {
            user = administratorRepository.findByEmail(recipient);
            if (user.isPresent()) {
                checkOtp = otpContextHelp.checkOtp(MailType.FORGET_PASSWORD.getCode(),
                    MessageNoticeChannel.MAIL, recipient, code);
            }
        } else if (isPhoneValidate(recipient)) {
            user = administratorRepository.findByPhone(PhoneNumberUtils.getPhoneNumber(recipient));
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
        HttpSession session = ServletContextService.getSession();
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
        HttpSession session = ServletContextService.getSession();
        String redisTokenId = session.getId() + session.getAttribute(FORGET_PASSWORD_TOKEN_ID);
        String userId = stringRedisTemplate.opsForValue().get(redisTokenId);
        if (Objects.isNull(userId)) {
            // 清除tokenId
            session.removeAttribute(FORGET_PASSWORD_TOKEN_ID);
            return false;
        }
        //修改密码
        Optional<AdministratorEntity> user = administratorRepository.findById(userId);
        if (user.isPresent()) {
            AdministratorEntity administratorEntity = user.get();
            administratorRepository.updatePassword(administratorEntity.getId(),
                passwordEncoder.encode(forgetPasswordRequest.getNewPassword()),
                LocalDateTime.now());
            logger.info("忘记密码: 用户ID: [{}] 用户名: [{}] 修改密码成功", administratorEntity.getId(),
                administratorEntity.getUsername());
            removeSession(administratorEntity.getUsername());
            stringRedisTemplate.delete(redisTokenId);
            return true;
        }
        return false;
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
    public AdministratorEntity getCurrentUser() {
        String userId = SecurityUtils.getCurrentUserId();
        Optional<AdministratorEntity> optional = administratorRepository.findById(userId);
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
    public AdministratorEntity validatedPassword(String password) {
        AdministratorEntity user = getCurrentUser();
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
    private final Executor                executor;

    /**
     *  AccountConverter
     */
    private final UserProfileConverter    userProfileConverter;

    /**
     *  PasswordEncoder
     */
    private final PasswordEncoder         passwordEncoder;

    /**
     * AdministratorRepository
     */
    private final AdministratorRepository administratorRepository;

    /**
     * SessionRegistry
     */
    private final SessionRegistry         sessionRegistry;

    /**
     * OtpContextHelp
     */
    private final OtpContextHelp          otpContextHelp;

    /**
     * SmsMsgEventPublish
     */
    private final SmsMsgEventPublish      smsMsgEventPublish;

    /**
     * StringRedisTemplate
     */
    private final StringRedisTemplate     stringRedisTemplate;

    public UserProfileServiceImpl(AsyncConfigurer asyncConfigurer,
                                  UserProfileConverter userProfileConverter,
                                  PasswordEncoder passwordEncoder,
                                  AdministratorRepository administratorRepository,
                                  SessionRegistry sessionRegistry, OtpContextHelp otpContextHelp,
                                  SmsMsgEventPublish smsMsgEventPublish,
                                  StringRedisTemplate stringRedisTemplate) {
        this.executor = asyncConfigurer.getAsyncExecutor();
        this.userProfileConverter = userProfileConverter;
        this.passwordEncoder = passwordEncoder;
        this.administratorRepository = administratorRepository;
        this.sessionRegistry = sessionRegistry;
        this.otpContextHelp = otpContextHelp;
        this.smsMsgEventPublish = smsMsgEventPublish;
        this.stringRedisTemplate = stringRedisTemplate;
    }
}
