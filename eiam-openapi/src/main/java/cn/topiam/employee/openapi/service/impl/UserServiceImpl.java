/*
 * eiam-openapi - Employee Identity and Access Management
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
package cn.topiam.employee.openapi.service.impl;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.querydsl.core.types.dsl.BooleanExpression;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.account.OrganizationMemberEntity;
import cn.topiam.employee.common.entity.account.QUserEntity;
import cn.topiam.employee.common.entity.account.UserDetailEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.po.UserPO;
import cn.topiam.employee.common.entity.account.query.UserListQuery;
import cn.topiam.employee.common.enums.*;
import cn.topiam.employee.common.repository.account.OrganizationMemberRepository;
import cn.topiam.employee.common.repository.account.UserDetailRepository;
import cn.topiam.employee.common.repository.account.UserGroupMemberRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.core.message.MsgVariable;
import cn.topiam.employee.core.message.mail.MailMsgEventPublish;
import cn.topiam.employee.core.message.sms.SmsMsgEventPublish;
import cn.topiam.employee.core.mq.UserMessagePublisher;
import cn.topiam.employee.core.mq.UserMessageTag;
import cn.topiam.employee.openapi.constants.OpenApiStatus;
import cn.topiam.employee.openapi.converter.account.UserConverter;
import cn.topiam.employee.openapi.exception.OpenApiException;
import cn.topiam.employee.openapi.pojo.request.account.save.account.UserCreateParam;
import cn.topiam.employee.openapi.pojo.request.account.update.account.UserUpdateParam;
import cn.topiam.employee.openapi.pojo.response.account.UserListResult;
import cn.topiam.employee.openapi.pojo.response.account.UserResult;
import cn.topiam.employee.openapi.service.UserService;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.security.password.PasswordPolicyManager;
import cn.topiam.employee.support.util.BeanUtils;
import cn.topiam.employee.support.util.PhoneNumberUtils;
import cn.topiam.employee.support.validation.annotation.ValidationPhone;

import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.audit.enums.TargetType.USER;
import static cn.topiam.employee.audit.enums.TargetType.USER_DETAIL;
import static cn.topiam.employee.core.message.sms.SmsMsgEventPublish.USERNAME;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_TIME;
import static cn.topiam.employee.support.util.PhoneNumberUtils.getPhoneNumber;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-07-31
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    /**
     * 获取用户（分页）
     *
     * @param pageModel {@link PageModel}
     * @return {@link UserListQuery}
     */
    @Override
    public Page<UserListResult> getUserList(PageModel pageModel, UserListQuery query) {
        org.springframework.data.domain.Page<UserPO> list = userRepository.getUserList(query,
            PageRequest.of(pageModel.getCurrent(), pageModel.getPageSize()));
        return userConverter.userPoConvertToUserListResult(list);
    }

    /**
     * 更改用户状态
     *
     * @param id     {@link Long}
     * @param status {@link UserStatus}
     */
    @Override
    public void changeUserStatus(Long id, UserStatus status) {
        Optional<UserEntity> optional = userRepository.findById(id);
        //用户不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("操作失败，用户不存在");
            log.warn(AuditContext.getContent());
            throw new OpenApiException(OpenApiStatus.USER_NOT_EXIST);
        }
        AuditContext.setTarget(Target.builder().id(id.toString()).type(TargetType.USER).build());
        userRepository.updateUserStatus(id, status);
        // 更新用户索引数据
        userMessagePublisher.sendUserChangeMessage(UserMessageTag.SAVE, String.valueOf(id));
    }

    /**
     * 创建用户
     *
     * @param param {@link UserCreateParam}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(UserCreateParam param) {
        if (StringUtils.isBlank(param.getPhone()) && StringUtils.isBlank(param.getEmail())) {
            throw new OpenApiException(OpenApiStatus.NO_MOBILE_OR_EMAIL);
        }
        //手机号
        if (StringUtils.isNotEmpty(param.getPhone())) {
            if (!getPhoneNumber(param.getPhone()).matches(ValidationPhone.PHONE_REGEXP)) {
                throw new OpenApiException(OpenApiStatus.MOBILE_NOT_VALID);
            }
            Boolean validityPhone = userParamCheck(CheckValidityType.PHONE, param.getPhone(), null);
            if (!validityPhone) {
                throw new OpenApiException(OpenApiStatus.MOBILE_ALREADY_EXIST);
            }
        }
        //邮箱
        if (StringUtils.isNotEmpty(param.getEmail())) {
            Boolean validityEmail = userParamCheck(CheckValidityType.EMAIL, param.getEmail(), null);
            if (!validityEmail) {
                throw new OpenApiException(OpenApiStatus.EMAIL_ALREADY_EXIST);
            }
        }
        Boolean validityUsername = userParamCheck(CheckValidityType.USERNAME, param.getUsername(),
            null);
        if (!validityUsername) {
            throw new OpenApiException(OpenApiStatus.USERNAME_ALREADY_EXIST);
        }
        //用户信息
        UserEntity user = userConverter.userCreateParamConvertToUserEntity(param);
        // 校验密码
        passwordPolicyManager.validate(user, param.getPassword());
        // 保存用户信息
        userRepository.save(user);
        //用户详情
        UserDetailEntity detail = userConverter.userCreateParamConvertToUserDetailEntity(param);
        detail.setUserId(user.getId());
        userDetailsRepository.save(detail);
        //用户组织关联关系
        OrganizationMemberEntity member = new OrganizationMemberEntity(param.getOrganizationId(),
            user.getId());
        organizationMemberRepository.save(member);
        AuditContext.setTarget(Target.builder().type(USER).id(user.getId().toString()).build(),
            Target.builder().type(USER_DETAIL).id(detail.getId().toString()).build());
        // 保存ES用户信息
        userMessagePublisher.sendUserChangeMessage(UserMessageTag.SAVE,
            String.valueOf(user.getId()));
        // 发送短信和邮件的欢迎信息（密码通知）
        UserCreateParam.PasswordInitializeConfig passwordInitializeConfig = param
            .getPasswordInitializeConfig();
        if (Objects.nonNull(passwordInitializeConfig)
            && Objects.nonNull(passwordInitializeConfig.getEnableNotice())
            && org.apache.commons.collections4.CollectionUtils
                .isNotEmpty(passwordInitializeConfig.getNoticeChannels())) {
            if (passwordInitializeConfig.getEnableNotice()) {
                List<MessageNoticeChannel> channels = passwordInitializeConfig.getNoticeChannels();
                if (channels.contains(MessageNoticeChannel.MAIL)) {
                    Map<String, Object> parameter = new HashMap<>(16);
                    parameter.put(MsgVariable.PASSWORD, param.getPassword());
                    mailMsgEventPublish.publish(MailType.WELCOME_MAIL, user.getEmail(), parameter);
                }
                if (channels.contains(MessageNoticeChannel.SMS)) {
                    LinkedHashMap<String, String> parameter = new LinkedHashMap<>();
                    parameter.put(USERNAME, user.getUsername());
                    parameter.put(MsgVariable.PASSWORD, param.getPassword());
                    smsMsgEventPublish.publish(SmsType.WELCOME_SMS, user.getPhone(), parameter);
                }
            }
        }
    }

    /**
     * 根据ID查询用户
     *
     * @param id {@link String}
     * @return {@link UserResult}
     */
    @Override
    public UserResult getUser(String id) {
        //查询
        Optional<UserEntity> user = userRepository.findById(Long.valueOf(id));
        Optional<UserDetailEntity> detail = userDetailsRepository.findByUserId(Long.valueOf(id));
        //映射
        UserEntity userEntity = user.orElse(null);
        UserResult userResult = userConverter.entityConvertToUserResult(userEntity,
            detail.orElse(null));
        if (Objects.nonNull(userEntity) && StringUtils.isNotEmpty(userEntity.getPhone())) {
            StringBuilder phoneAreaCode = new StringBuilder(
                userEntity.getPhoneAreaCode().replace(PhoneNumberUtils.PLUS_SIGN, ""));
            phoneAreaCode.insert(0, PhoneNumberUtils.PLUS_SIGN);
            userResult.setPhone(phoneAreaCode + userEntity.getPhone());
        }
        return userResult;
    }

    /**
     * 更新用户
     *
     * @param param {@link UserUpdateParam}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateParam param) {
        if (StringUtils.isNotBlank(param.getPhone())) {
            String phoneNumber = getPhoneNumber(param.getPhone());
            if (!phoneNumber.matches(ValidationPhone.PHONE_REGEXP)) {
                throw new OpenApiException(OpenApiStatus.MOBILE_NOT_VALID);
            }
            Boolean validityPhone = userParamCheck(CheckValidityType.PHONE, param.getPhone(),
                Long.valueOf(param.getId()));
            if (!validityPhone) {
                throw new OpenApiException(OpenApiStatus.MOBILE_ALREADY_EXIST);
            }
        }
        if (StringUtils.isNotBlank(param.getEmail())) {
            Boolean validityEmail = userParamCheck(CheckValidityType.EMAIL, param.getEmail(),
                Long.valueOf(param.getId()));
            if (!validityEmail) {
                throw new OpenApiException(OpenApiStatus.EMAIL_ALREADY_EXIST);
            }
        }
        //用户信息
        UserEntity toUserEntity = userConverter.userUpdateParamConvertToUserEntity(param);
        UserEntity user = getUser(Long.valueOf(param.getId()));
        BeanUtils.merge(toUserEntity, user, LAST_MODIFIED_BY, LAST_MODIFIED_TIME);
        //如果更改密码到期时间，修改为启用
        if (user.getStatus().equals(UserStatus.EXPIRED_LOCKED)) {
            if (toUserEntity.getExpireDate().isAfter(LocalDate.now())) {
                user.setStatus(UserStatus.ENABLE);
            }
        }
        userRepository.save(user);
        //用户详情
        UserDetailEntity detail = userDetailsRepository.findByUserId(Long.valueOf(param.getId()))
            .orElse(new UserDetailEntity().setUserId(user.getId()));
        UserDetailEntity toUserDetailsEntity = userConverter
            .userUpdateParamConvertToUserDetailsEntity(param);
        toUserDetailsEntity.setId(detail.getId());
        BeanUtils.merge(toUserDetailsEntity, detail, LAST_MODIFIED_BY, LAST_MODIFIED_TIME);
        userDetailsRepository.save(detail);
        AuditContext.setTarget(Target.builder().type(USER).id(user.getId().toString()).build(),
            Target.builder().type(USER_DETAIL).id(detail.getId().toString()).build());
        // 更新ES用户信息
        userMessagePublisher.sendUserChangeMessage(UserMessageTag.SAVE,
            String.valueOf(user.getId()));
    }

    /**
     * 删除用户
     *
     * @param id {@link Serializable}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String id) {
        Optional<UserEntity> optional = userRepository.findById(Long.valueOf(id));
        //管理员不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("删除失败，用户不存在");
            log.warn(AuditContext.getContent());
            throw new OpenApiException(OpenApiStatus.USER_NOT_EXIST);
        }
        //删除
        userRepository.deleteById(Long.valueOf(id));
        //删除用户详情
        userDetailsRepository.deleteByUserId(Long.valueOf(id));
        //删除组织用户关联关系
        organizationMemberRepository.deleteByUserId(Long.valueOf(id));
        //删除用户组用户详情
        userGroupMemberRepository.deleteByUserId(Long.valueOf(id));
        // 删除ES用户信息
        userMessagePublisher.sendUserChangeMessage(UserMessageTag.DELETE, id);
    }

    @Override
    public String getUserIdByParams(String externalId, String phoneNumber, String email,
                                    String username) {
        UserEntity userEntity;
        if (Objects.nonNull(externalId)) {
            userEntity = userRepository.findByExternalId(externalId).orElse(null);
        } else if (Objects.nonNull(phoneNumber)) {
            userEntity = userRepository.findByPhone(phoneNumber);
        } else if (Objects.nonNull(email)) {
            userEntity = userRepository.findByEmail(email);
        } else if (Objects.nonNull(username)) {
            userEntity = userRepository.findByUsername(username);
        } else {
            throw new OpenApiException(OpenApiStatus.INVALID_PARAMETER);
        }
        if (Objects.nonNull(userEntity)) {
            return String.valueOf(userEntity.getId());
        }
        throw new OpenApiException(OpenApiStatus.USER_NOT_EXIST);
    }

    /**
     * 获取用户信息
     *
     * @param id {@link String}
     * @return {@link UserEntity}
     */
    private UserEntity getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new OpenApiException(OpenApiStatus.USER_NOT_EXIST));
    }

    /**
     * 参数有效性验证
     *
     * @param type  {@link CheckValidityType}
     * @param value {@link String}
     * @param id    {@link Long}
     * @return {@link Boolean}
     */
    public Boolean userParamCheck(CheckValidityType type, String value, Long id) {
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        QUserEntity user = QUserEntity.userEntity;
        UserEntity entity = new UserEntity();
        boolean result = false;
        // ID存在说明是修改操作，查询一下当前数据
        if (Objects.nonNull(id)) {
            entity = userRepository.findById(id).orElse(new UserEntity());
        }
        //手机号
        if (CheckValidityType.PHONE.equals(type)) {
            try {
                //手机号未修改
                if (StringUtils.equals(value.replace(PhoneNumberUtils.PLUS_SIGN, ""),
                    entity.getPhoneAreaCode() + entity.getPhone())) {
                    return true;
                }
                Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(value,
                    "CN");
                BooleanExpression eq = user.phone
                    .eq(String.valueOf(phoneNumber.getNationalNumber()))
                    .and(user.phoneAreaCode.eq(String.valueOf(phoneNumber.getCountryCode())));
                result = !userRepository.exists(eq);
            } catch (NumberParseException e) {
                log.error("校验手机号发生异常", e);
                throw new OpenApiException(OpenApiStatus.MOBILE_NOT_VALID);
            }
        }
        //邮箱
        if (CheckValidityType.EMAIL.equals(type)) {
            if (StringUtils.equals(entity.getEmail(), value)) {
                return true;
            }
            BooleanExpression eq = user.email.eq(value);
            result = !userRepository.exists(eq);
        }
        //用户名
        if (CheckValidityType.USERNAME.equals(type)) {
            if (StringUtils.equals(entity.getUsername(), value)) {
                return true;
            }
            BooleanExpression eq = user.username.eq(value);
            result = !userRepository.exists(eq);
        }
        return result;
    }

    /**
     * 用户数据映射器
     */
    private final UserConverter                     userConverter;

    /**
     * UserRepository
     */
    private final UserRepository                    userRepository;

    /**
     * 组织成员
     */
    private final OrganizationMemberRepository      organizationMemberRepository;

    /**
     * 部门成员
     */
    private final UserGroupMemberRepository         userGroupMemberRepository;

    /**
     * 用户详情Repository
     */
    private final UserDetailRepository              userDetailsRepository;

    /**
     * 邮件消息发布
     */
    private final MailMsgEventPublish               mailMsgEventPublish;

    /**
     * 短信消息发送
     */
    private final SmsMsgEventPublish                smsMsgEventPublish;

    /**
     * PasswordPolicyManager
     */
    private final PasswordPolicyManager<UserEntity> passwordPolicyManager;

    /**
     * UserMessagePublisher
     */
    private final UserMessagePublisher              userMessagePublisher;

    public UserServiceImpl(UserConverter userConverter, UserRepository userRepository,
                           OrganizationMemberRepository organizationMemberRepository,
                           UserGroupMemberRepository userGroupMemberRepository,
                           UserDetailRepository userDetailsRepository,
                           MailMsgEventPublish mailMsgEventPublish,
                           SmsMsgEventPublish smsMsgEventPublish,
                           PasswordPolicyManager<UserEntity> passwordPolicyManager,
                           UserMessagePublisher userMessagePublisher) {
        this.userConverter = userConverter;
        this.userRepository = userRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.userGroupMemberRepository = userGroupMemberRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.mailMsgEventPublish = mailMsgEventPublish;
        this.smsMsgEventPublish = smsMsgEventPublish;
        this.passwordPolicyManager = passwordPolicyManager;
        this.userMessagePublisher = userMessagePublisher;
    }
}
