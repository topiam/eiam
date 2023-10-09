/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.service.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.mapstruct.Mapper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

import cn.topiam.employee.audit.controller.pojo.AuditListQuery;
import cn.topiam.employee.audit.controller.pojo.AuditListResult;
import cn.topiam.employee.audit.entity.AuditEntity;
import cn.topiam.employee.audit.entity.QAuditEntity;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.account.OrganizationEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.UserGroupEntity;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceEntity;
import cn.topiam.employee.common.entity.setting.AdministratorEntity;
import cn.topiam.employee.common.entity.setting.MailTemplateEntity;
import cn.topiam.employee.common.repository.account.OrganizationRepository;
import cn.topiam.employee.common.repository.account.UserGroupRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.app.AppRepository;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceRepository;
import cn.topiam.employee.common.repository.setting.*;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.security.userdetails.UserType;

/**
 * 审计数据转换
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/14 22:45
 */
@Mapper(componentModel = "spring")
public interface AuditDataConverter {
    String SORT_EVENT_TIME = "eventTime";

    /**
     * searchHits 转审计列表
     *
     * @param auditEntityPage {@link Page}
     * @param page   {@link PageModel}
     * @return {@link Page}
     */
    default Page<AuditListResult> entityConvertToAuditListResult(org.springframework.data.domain.Page<AuditEntity> auditEntityPage,
                                                                 PageModel page) {
        List<AuditListResult> list = new ArrayList<>();
        //总记录数
        auditEntityPage.forEach(audit -> {
            AuditListResult result = new AuditListResult();
            result.setId(audit.getId().toString());
            result.setEventStatus(audit.getEventStatus());
            result.setEventType(audit.getEventType().getDesc());
            result.setEventTime(audit.getEventTime());
            //用户代理
            result.setUserAgent(audit.getUserAgent());
            result.setGeoLocation(audit.getGeoLocation());
            //用户ID
            result.setUserId(audit.getActorId());
            result.setUsername(getUsername(audit.getActorType(), audit.getActorId()));
            //用户类型
            result.setUserType(audit.getActorType().getType());
            //操作对象
            if (Objects.nonNull(audit.getTargets())) {
                for (Target target : audit.getTargets()) {
                    if (Objects.nonNull(target.getId())) {
                        target.setName(getTargetName(target.getType(), target.getId()));
                    }
                    target.setTypeName(target.getType().getDesc());
                }
            }
            result.setTargets(audit.getTargets());
            list.add(result);
        });
        //@formatter:off
        Page<AuditListResult> result = new Page<>();
        result.setPagination(Page.Pagination.builder()
                .total(auditEntityPage.getTotalElements())
                .totalPages(auditEntityPage.getTotalPages())
                .current(page.getCurrent() + 1)
                .build());
        result.setList(list);
        //@formatter:on
        return result;
    }

    /**
     * 获取用户名
     *
     * @param actorId   {@link String}
     * @param actorType {@link UserType}
     * @return {@link String}
     */
    private String getUsername(UserType actorType, String actorId) {
        if (!StringUtils.hasText(actorId)) {
            return null;
        }
        if (UserType.USER.equals(actorType)) {
            UserRepository repository = ApplicationContextHelp.getBean(UserRepository.class);
            UserEntity user = repository.findById(Long.valueOf(actorId)).orElse(new UserEntity());
            return org.apache.commons.lang3.StringUtils.defaultString(user.getFullName(),
                user.getUsername());
        }
        if (UserType.ADMIN.equals(actorType)) {
            AdministratorRepository repository = ApplicationContextHelp
                .getBean(AdministratorRepository.class);
            AdministratorEntity administrator = repository.findById(Long.valueOf(actorId))
                .orElse(new AdministratorEntity());
            return administrator.getUsername();
        }
        return "";
    }

    /**
     * 审计列表请求到本机搜索查询
     *
     * @param query {@link AuditListQuery}
     * @return {@link Predicate}
     */
    default Predicate auditListRequestConvertToPredicate(AuditListQuery query) {
        QAuditEntity auditEntity = QAuditEntity.auditEntity;
        Predicate predicate = ExpressionUtils.and(auditEntity.isNotNull(),
            auditEntity.deleted.eq(Boolean.FALSE));
        //用户名存在，查询用户ID
        if (StringUtils.hasText(query.getUsername())) {
            String actorId = "";
            if (UserType.USER.getType().equals(query.getUserType())) {
                UserRepository userRepository = ApplicationContextHelp
                    .getBean(UserRepository.class);
                UserEntity user = userRepository.findByUsername(query.getUsername());
                if (!Objects.isNull(user)) {
                    actorId = user.getId().toString();
                }
                // 用户类型
                predicate = ExpressionUtils.and(predicate, auditEntity.actorType.eq(UserType.USER));
            }
            if (UserType.ADMIN.getType().equals(query.getUserType())) {
                AdministratorRepository administratorRepository = ApplicationContextHelp
                    .getBean(AdministratorRepository.class);
                Optional<AdministratorEntity> optional = administratorRepository
                    .findByUsername(query.getUsername());
                if (optional.isPresent()) {
                    actorId = optional.get().getId().toString();
                }
                // 用户类型
                predicate = ExpressionUtils.and(predicate,
                    auditEntity.actorType.eq(UserType.ADMIN));
            }
            predicate = ExpressionUtils.and(predicate, auditEntity.actorId.eq(actorId));
        }
        //事件类型
        if (!CollectionUtils.isEmpty(query.getEventType())) {
            predicate = ExpressionUtils.and(predicate,
                auditEntity.eventType.in(query.getEventType()));
        }
        //事件状态
        if (Objects.nonNull(query.getEventStatus())) {
            predicate = ExpressionUtils.and(predicate,
                auditEntity.eventStatus.in(query.getEventStatus()));
        }
        //事件时间
        if (!Objects.isNull(query.getStartEventTime())
            && !Objects.isNull(query.getEndEventTime())) {
            predicate = ExpressionUtils.and(predicate,
                auditEntity.eventTime.between(query.getStartEventTime(), query.getEndEventTime()));
        }
        return predicate;
    }

    /**
     * 获取目标名称
     *
     * @param targetType {@link TargetType}
     * @param id         {@link String}
     * @return {@link String}
     */
    @SuppressWarnings("AlibabaMethodTooLong")
    default String getTargetName(TargetType targetType, String id) {
        //@formatter:off
        String name = "";
        if (TargetType.USER.equals(targetType) || TargetType.USER_DETAIL.equals(targetType)) {
            UserRepository userRepository = ApplicationContextHelp.getBean(UserRepository.class);
            Optional<UserEntity> user = userRepository.findByIdContainsDeleted(Long.valueOf(id));
            if (user.isPresent()) {
                UserEntity entity = user.get();
                name = org.apache.commons.lang3.StringUtils.defaultString(entity.getFullName(),
                    entity.getUsername());
            }
        }
        //用户组
        if (TargetType.USER_GROUP.equals(targetType)) {
            UserGroupRepository userGroupRepository = ApplicationContextHelp.getBean(UserGroupRepository.class);
            Optional<UserGroupEntity> userGroup = userGroupRepository.findByIdContainsDeleted(Long.valueOf(id));
            if (userGroup.isPresent()) {
                name = userGroup.get().getName();
            }
        }
        //身份源
        if (TargetType.IDENTITY_SOURCE.equals(targetType)) {
            IdentitySourceRepository identitySourceRepository = ApplicationContextHelp.getBean(IdentitySourceRepository.class);
            Optional<IdentitySourceEntity> identitySource = identitySourceRepository.findByIdContainsDeleted(Long.valueOf(id));
            if (identitySource.isPresent()) {
                name = identitySource.get().getName();
            }
        }
        //组织机构
        if (TargetType.ORGANIZATION.equals(targetType)) {
            OrganizationRepository organizationRepository = ApplicationContextHelp.getBean(OrganizationRepository.class);
            Optional<OrganizationEntity> organizationEntity = organizationRepository.findByIdContainsDeleted(id);
            if (organizationEntity.isPresent()) {
                name = organizationEntity.get().getName();
            }
        }
        //应用
        if (TargetType.APPLICATION.equals(targetType)) {
            AppRepository appRepository = ApplicationContextHelp.getBean(AppRepository.class);
            Optional<AppEntity> appEntity = appRepository.findByIdContainsDeleted(Long.valueOf(id));
            if (appEntity.isPresent()) {
                name = appEntity.get().getName();
            }
        }
        //应用账户
        if (TargetType.APPLICATION_ACCOUNT.equals(targetType)) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(id)) {
                name = id;
            }
        }
        //管理员
        if (TargetType.ADMINISTRATOR.equals(targetType)) {
            AdministratorRepository administratorRepository = ApplicationContextHelp.getBean(AdministratorRepository.class);
            Optional<AdministratorEntity> administratorEntity = administratorRepository.findByIdContainsDeleted(Long.valueOf(id));
            if (administratorEntity.isPresent()) {
                name = administratorEntity.get().getUsername();
            }
        }

        //邮件模版
        if (TargetType.MAIL_TEMPLATE.equals(targetType)) {
            MailTemplateRepository mailTemplateRepository = ApplicationContextHelp.getBean(MailTemplateRepository.class);
            Optional<MailTemplateEntity> mailTemplateEntity = mailTemplateRepository.findByIdContainsDeleted(Long.valueOf(id));
            if (mailTemplateEntity.isPresent()) {
                name = mailTemplateEntity.get().getSender();
            }
        }
        //身份提供商
        if (TargetType.IDENTITY_PROVIDER.equals(targetType)) {
            IdentityProviderRepository identityProviderRepository = ApplicationContextHelp.getBean(IdentityProviderRepository.class);
            Optional<IdentityProviderEntity> identityProviderEntity = identityProviderRepository.findByIdContainsDeleted(Long.valueOf(id));
            if (identityProviderEntity.isPresent()) {
                name = identityProviderEntity.get().getName();
            }
        }
        return name;
        //@formatter:on
    }
}
