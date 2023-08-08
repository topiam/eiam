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
package cn.topiam.employee.console.service.setting.impl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.setting.AdministratorEntity;
import cn.topiam.employee.common.entity.setting.QAdministratorEntity;
import cn.topiam.employee.common.enums.CheckValidityType;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.common.repository.setting.AdministratorRepository;
import cn.topiam.employee.console.converter.setting.AdministratorConverter;
import cn.topiam.employee.console.pojo.query.setting.AdministratorListQuery;
import cn.topiam.employee.console.pojo.result.setting.AdministratorListResult;
import cn.topiam.employee.console.pojo.result.setting.AdministratorResult;
import cn.topiam.employee.console.pojo.save.setting.AdministratorCreateParam;
import cn.topiam.employee.console.pojo.update.setting.AdministratorUpdateParam;
import cn.topiam.employee.console.service.setting.AdministratorService;
import cn.topiam.employee.support.exception.InfoValidityFailException;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.util.PhoneNumberUtils;
import cn.topiam.employee.support.validation.annotation.ValidationPhone;

import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.console.access.DefaultAdministratorConstants.DEFAULT_ADMIN_USERNAME;
import static cn.topiam.employee.support.util.PhoneNumberUtils.getPhoneNumber;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/13 23:13
 */
@Slf4j
@Service
public class AdministratorServiceImpl implements AdministratorService {

    /**
     * 查询平台管理员列表
     *
     * @param model {@link PageModel}
     * @param query {@link AdministratorListQuery}
     * @return {@link List}
     */
    @Override
    public Page<AdministratorListResult> getAdministratorList(PageModel model,
                                                              AdministratorListQuery query) {
        Predicate predicate = administratorConverter
            .queryAdministratorListParamConvertToPredicate(query);
        //分页条件
        QPageRequest request = QPageRequest.of(model.getCurrent(), model.getPageSize());
        org.springframework.data.domain.Page<AdministratorEntity> page = administratorRepository
            .findAll(predicate, request);
        return administratorConverter.entityConvertToAdministratorPaginationResult(page);
    }

    /**
     * 创建管理员
     *
     * @param param {@link AdministratorCreateParam}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createAdministrator(AdministratorCreateParam param) {
        //@formatter:off
        if (StringUtils.isBlank(param.getPhone()) && StringUtils.isBlank(param.getEmail())) {
            throw new TopIamException("手机号或邮箱至少填写一个", HttpStatus.BAD_REQUEST);
        }
        //手机号
        if (StringUtils.isNotEmpty(param.getPhone())) {
            if (!getPhoneNumber(param.getPhone()).matches(ValidationPhone.PHONE_REGEXP)) {
                throw new InfoValidityFailException("手机号格式错误");
            }
            Boolean validityPhone = administratorParamCheck(CheckValidityType.PHONE, param.getPhone(), null);
            if (!validityPhone) {
                throw new InfoValidityFailException("手机号已存在");
            }
        }
        //邮箱
        if (StringUtils.isNotEmpty(param.getEmail())) {
            Boolean validityEmail = administratorParamCheck(CheckValidityType.EMAIL, param.getEmail(), null);
            if (!validityEmail) {
                throw new InfoValidityFailException("邮箱已存在");
            }
        }
        Boolean validityUsername = administratorParamCheck(CheckValidityType.USERNAME, param.getUsername(), null);
        if (!validityUsername) {
            throw new InfoValidityFailException("用户名已存在");
        }
        AdministratorEntity entity = administratorConverter.administratorCreateParamConvertToEntity(param);
        administratorRepository.save(entity);
        AuditContext.setTarget(Target.builder().id(entity.getId().toString())
            .name(entity.getUsername()).type(TargetType.ADMINISTRATOR)
            .typeName(TargetType.ADMINISTRATOR.getDesc()).build());
        return true;
        //@formatter:on
    }

    /**
     * 修改管理员
     *
     * @param param {@link AdministratorUpdateParam}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAdministrator(AdministratorUpdateParam param) {
        //@formatter:off
        AdministratorEntity entity = administratorRepository.findById(Long.valueOf(param.getId())).orElseThrow(() -> new TopIamException("管理员信息不存在"));
        AuditContext.setContent(entity.getUsername());
        administratorRepository.save(administratorConverter.administratorUpdateParamConvertToEntity(param, entity));
        AuditContext.setTarget(Target.builder().id(entity.getId().toString())
            .name(entity.getUsername()).type(TargetType.ADMINISTRATOR)
            .typeName(TargetType.ADMINISTRATOR.getDesc()).build());
        return true;
        //@formatter:on
    }

    /**
     * 删除管理员
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAdministrator(String id) {
        AdministratorEntity administratorEntity = getAdministrator(id, "删除失败，管理员不存在");
        if (administratorEntity.getUsername().equals(DEFAULT_ADMIN_USERNAME)) {
            AuditContext.setContent("默认超级管理员禁止删除");
            log.warn(AuditContext.getContent());
            throw new TopIamException("操作失败");
        }
        //执行删除
        administratorRepository.deleteById(Long.valueOf(id));
        AuditContext.setTarget(Target.builder().id(id).type(TargetType.ADMINISTRATOR).build());
        // 下线登录中已删除的管理员
        removeSession(administratorEntity.getUsername());
        return true;
    }

    /**
     * 更改管理员状态
     *
     * @param id     {@link String}
     * @param status {@link UserStatus}
     * @return {@link Boolean}
     */
    @Override
    public Boolean updateAdministratorStatus(String id, UserStatus status) {
        AdministratorEntity administratorEntity = getAdministrator(id,
            status.getDesc() + "失败，管理员不存在");
        AuditContext.setContent(administratorEntity.getUsername());
        Optional<AdministratorEntity> optional = administratorRepository.findById(Long.valueOf(id));
        if (optional.isPresent() && optional.get().getUsername().equals(DEFAULT_ADMIN_USERNAME)) {
            log.warn("默认超级管理员禁止禁用");
            throw new RuntimeException("操作失败");
        }
        administratorRepository.updateStatus(id, status.getCode());
        AuditContext.setTarget(Target.builder().id(id).type(TargetType.ADMINISTRATOR).build());
        if (UserStatus.DISABLE == status) {
            // 下线登录中已禁用的管理员
            removeSession(administratorEntity.getUsername());
        }
        return true;
    }

    @NotNull
    private AdministratorEntity getAdministrator(String id, String message) {
        Optional<AdministratorEntity> optional = administratorRepository.findById(Long.valueOf(id));
        //管理员不存在
        if (optional.isEmpty()) {
            AuditContext.setContent(message);
            log.warn(AuditContext.getContent());
            throw new TopIamException("操作失败");
        }
        return optional.get();
    }

    /**
     * 重置管理员密码
     *
     * @param id       {@link String}
     * @param password {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean resetAdministratorPassword(String id, String password) {
        AdministratorEntity entity = getAdministrator(id, "重置密码失败，管理员不存在");
        password = new String(
            Base64.getUrlDecoder().decode(password.getBytes(StandardCharsets.UTF_8)),
            StandardCharsets.UTF_8);
        password = passwordEncoder.encode(password);
        administratorRepository.updatePassword(id, password);
        AuditContext.setTarget(Target.builder().id(id).type(TargetType.ADMINISTRATOR).build());
        // 下线登录中已重置密码的管理员
        removeSession(entity.getUsername());
        return true;
    }

    /**
     * 下线管理员
     *
     * @param username {@link String}
     */
    private void removeSession(String username) {
        //异步下线所有用户
        executor.execute(() -> {
            //@formatter:off
            List<SessionInformation> sessions = sessionRegistry.getAllSessions(username,false);
            sessions.forEach(SessionInformation::expireNow);
            //@formatter:on
        });
    }

    /**
     * 参数有效性验证
     *
     * @param type  {@link CheckValidityType}
     * @param value {@link String}
     * @param id    {@link Long}
     * @return {@link Boolean} false 不可用 true 可用
     */
    @Override
    public Boolean administratorParamCheck(CheckValidityType type, String value, Long id) {
        QAdministratorEntity administrator = QAdministratorEntity.administratorEntity;
        AdministratorEntity entity = new AdministratorEntity();
        boolean result = false;
        // ID存在说明是修改操作，查询一下当前数据
        if (Objects.nonNull(id)) {
            entity = administratorRepository.findById(id).orElse(new AdministratorEntity());
        }
        //邮箱
        if (CheckValidityType.EMAIL.equals(type)) {
            if (StringUtils.equals(entity.getEmail(), value)) {
                return true;
            }
            BooleanExpression eq = administrator.email.eq(value);
            result = !administratorRepository.exists(eq);
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
                BooleanExpression eq = administrator.phone
                    .eq(String.valueOf(phoneNumber.getNationalNumber()))
                    .and(administrator.phoneAreaCode
                        .eq(String.valueOf(phoneNumber.getCountryCode())));
                result = !administratorRepository.exists(eq);
            } catch (NumberParseException e) {
                log.error("校验手机号发生异常", e);
                throw new TopIamException("校验手机号发生异常");
            }
        }
        //用户名
        if (CheckValidityType.USERNAME.equals(type)) {
            if (StringUtils.equals(entity.getUsername(), value)) {
                return true;
            }
            BooleanExpression eq = administrator.username.eq(value);
            result = !administratorRepository.exists(eq);
        }
        return result;
    }

    /**
     * 更新认证成功信息
     *
     * @param id        {@link String}
     * @param ip        {@link String}
     * @param loginTime {@link LocalDateTime}
     */
    @Override
    public Boolean updateAuthSucceedInfo(String id, String ip, LocalDateTime loginTime) {
        administratorRepository.updateAuthSucceedInfo(id, ip, loginTime);
        return true;
    }

    /**
     * 查询管理员详情
     *
     * @param id {@link String}
     * @return {@link AdministratorResult}
     */
    @Override
    public AdministratorResult getAdministrator(String id) {
        AdministratorEntity administrator = administratorRepository.findById(Long.valueOf(id))
            .orElse(null);
        AdministratorResult result = administratorConverter
            .entityConvertToAdministratorDetailsResult(administrator);
        if (Objects.nonNull(administrator) && StringUtils.isNotEmpty(administrator.getPhone())) {
            StringBuilder phoneAreaCode = new StringBuilder(
                administrator.getPhoneAreaCode().replace(PhoneNumberUtils.PLUS_SIGN, ""));
            phoneAreaCode.insert(0, PhoneNumberUtils.PLUS_SIGN);
            result.setPhone(phoneAreaCode + administrator.getPhone());
        }
        return result;
    }

    /**
     * Executor
     */
    private final Executor                executor;

    /**
     * AdministratorConverter
     */
    private final AdministratorConverter  administratorConverter;

    /**
     * AdministratorRepository
     */
    private final AdministratorRepository administratorRepository;

    /**
     * PasswordEncoder
     */
    private final PasswordEncoder         passwordEncoder;

    /**
     * SessionRegistry
     */
    private final SessionRegistry         sessionRegistry;

    public AdministratorServiceImpl(AdministratorConverter administratorConverter,
                                    AdministratorRepository administratorRepository,
                                    PasswordEncoder passwordEncoder,
                                    AsyncConfigurer asyncConfigurer,
                                    SessionRegistry sessionRegistry) {
        this.administratorConverter = administratorConverter;
        this.administratorRepository = administratorRepository;
        this.passwordEncoder = passwordEncoder;
        this.executor = asyncConfigurer.getAsyncExecutor();
        this.sessionRegistry = sessionRegistry;
    }
}
