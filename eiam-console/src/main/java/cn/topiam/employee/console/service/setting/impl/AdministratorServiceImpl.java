/*
 * eiam-console - Employee Identity and Access Management Program
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
package cn.topiam.employee.console.service.setting.impl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import cn.topiam.employee.core.security.session.SessionDetails;
import cn.topiam.employee.core.security.session.TopIamSessionBackedSessionRegistry;
import cn.topiam.employee.core.security.util.SecurityUtils;
import cn.topiam.employee.support.exception.InfoValidityFailException;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.util.BeanUtils;

import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_TIME;

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
    public Boolean createAdministrator(AdministratorCreateParam param) {
        // 判断用户名、手机号、邮箱是否存在
        Boolean validityPhone = administratorParamCheck(CheckValidityType.PHONE, param.getPhone(),
            null);
        if (!validityPhone) {
            throw new InfoValidityFailException("手机号已存在");
        }
        Boolean validityEmail = administratorParamCheck(CheckValidityType.EMAIL, param.getEmail(),
            null);
        if (!validityEmail) {
            throw new InfoValidityFailException("邮箱已存在");
        }
        Boolean validityUsername = administratorParamCheck(CheckValidityType.USERNAME,
            param.getUsername(), null);
        if (!validityUsername) {
            throw new InfoValidityFailException("用户名已存在");
        }
        AdministratorEntity entity = administratorConverter
            .administratorCreateParamConvertToEntity(param);
        //密码处理
        String password = passwordEncoder.encode(entity.getPassword());
        entity.setPassword(password);
        administratorRepository.save(entity);
        AuditContext.setTarget(Target.builder().id(entity.getId().toString())
            .name(entity.getUsername()).type(TargetType.ADMINISTRATOR)
            .typeName(TargetType.ADMINISTRATOR.getDesc()).build());
        return true;
    }

    /**
     * 修改管理员
     *
     * @param param {@link AdministratorUpdateParam}
     * @return {@link Boolean}
     */
    @Override
    public Boolean updateAdministrator(AdministratorUpdateParam param) {
        AdministratorEntity source = administratorConverter
            .administratorUpdateParamConvertToEntity(param);
        AdministratorEntity target = administratorRepository.findById(Long.valueOf(param.getId()))
            .orElse(new AdministratorEntity());
        AuditContext.setContent(source.getUsername());
        BeanUtils.merge(source, target, LAST_MODIFIED_TIME, LAST_MODIFIED_BY);
        administratorRepository.save(target);
        AuditContext.setTarget(Target.builder().id(target.getId().toString())
            .name(target.getUsername()).type(TargetType.ADMINISTRATOR)
            .typeName(TargetType.ADMINISTRATOR.getDesc()).build());
        return true;
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
        Optional<AdministratorEntity> optional = administratorRepository.findById(Long.valueOf(id));
        //管理员不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("删除失败，管理员不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException("操作失败");
        }
        long count = administratorRepository.count();
        if (count == 1) {
            AuditContext.setContent("禁止删除，系统必须存在一个管理员");
            log.warn(AuditContext.getContent());
            throw new TopIamException("操作失败");
        }
        //执行删除
        administratorRepository.deleteById(Long.valueOf(id));
        AuditContext
            .setTarget(Target.builder().id(id.toString()).type(TargetType.ADMINISTRATOR).build());
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
        Optional<AdministratorEntity> optional = administratorRepository.findById(Long.valueOf(id));
        optional.ifPresent(
            administratorEntity -> AuditContext.setContent(administratorEntity.getUsername()));
        long count = administratorRepository.count();
        if (count == 1 && !status.equals(UserStatus.ENABLE)) {
            log.warn("禁止删除，当前系统只存在一个管理员");
            throw new RuntimeException("操作失败");
        }
        administratorRepository.updateStatus(id, status.getCode());
        AuditContext.setTarget(Target.builder().id(id).type(TargetType.ADMINISTRATOR).build());
        return true;
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
        Optional<AdministratorEntity> optional = administratorRepository.findById(Long.valueOf(id));
        //管理员不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("删除失败，管理员不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException("操作失败");
        }
        password = new String(
            Base64.getUrlDecoder().decode(password.getBytes(StandardCharsets.UTF_8)),
            StandardCharsets.UTF_8);
        password = passwordEncoder.encode(password);
        administratorRepository.updatePassword(id, password);
        AuditContext.setTarget(Target.builder().id(id).type(TargetType.ADMINISTRATOR).build());
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
        return true;
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
            if (StringUtils.equals(entity.getPhone(), value)) {
                return true;
            }
            BooleanExpression eq = administrator.phone.eq(value);
            result = !administratorRepository.exists(eq);
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
     * @param id {@link String}
     * @param ip {@link String}
     * @param loginTime {@link LocalDateTime}
     */
    public Boolean updateAuthSucceedInfo(String id, String ip, LocalDateTime loginTime) {
        administratorRepository.updateAuthSucceedInfo(id,ip,loginTime);
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
        AdministratorEntity entity = administratorRepository.findById(Long.valueOf(id))
                .orElse(null);
        return administratorConverter.entityConvertToAdministratorDetailsResult(entity);
    }


    /**
     * Executor
     */
    private final Executor executor;

    /**
     * AdministratorConverter
     */
    private final AdministratorConverter administratorConverter;

    /**
     * AdministratorRepository
     */
    private final AdministratorRepository administratorRepository;

    /**
     * PasswordEncoder
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * SessionRegistry
     */
    private final SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry;

    public AdministratorServiceImpl(AdministratorConverter administratorConverter, AdministratorRepository administratorRepository, PasswordEncoder passwordEncoder, AsyncConfigurer asyncConfigurer, SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry) {
        this.administratorConverter = administratorConverter;
        this.administratorRepository = administratorRepository;
        this.passwordEncoder = passwordEncoder;
        this.executor = asyncConfigurer.getAsyncExecutor();
        this.sessionRegistry = sessionRegistry;
    }
}
