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
package cn.topiam.employee.console.service.app.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.app.AppAccessPolicyEntity;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.app.po.AppAccessPolicyPO;
import cn.topiam.employee.common.entity.app.query.AppAccessPolicyQuery;
import cn.topiam.employee.common.repository.account.OrganizationRepository;
import cn.topiam.employee.common.repository.account.UserGroupRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.app.AppAccessPolicyRepository;
import cn.topiam.employee.common.repository.app.AppRepository;
import cn.topiam.employee.console.converter.app.AppAccessPolicyConverter;
import cn.topiam.employee.console.pojo.result.app.AppAccessPolicyResult;
import cn.topiam.employee.console.pojo.save.app.AppAccessPolicyCreateParam;
import cn.topiam.employee.console.service.app.AppAccessPolicyService;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.util.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.support.repository.base.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.base.BaseEntity.LAST_MODIFIED_TIME;

/**
 * 应用访问权限策略 Service
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/6/4 21:56
 */
@Service
@Slf4j
@AllArgsConstructor
public class AppAccessPolicyServiceImpl implements AppAccessPolicyService {

    /**
     * 查询应用授权策略列表
     *
     * @param pageModel {@link PageModel}
     * @param query     {@link AppAccessPolicyQuery}
     * @return {@link Page}
     */
    @Override
    public Page<AppAccessPolicyResult> getAppAccessPolicyList(PageModel pageModel,
                                                              AppAccessPolicyQuery query) {
        //分页条件
        PageRequest request = PageRequest.of(pageModel.getCurrent(), pageModel.getPageSize());
        //查询映射
        org.springframework.data.domain.Page<AppAccessPolicyPO> list = appAccessPolicyRepository
            .getAppPolicyList(query, request);
        return appAccessPolicyConverter.appPolicyEntityListConvertToAppPolicyResult(list);
    }

    /**
     * 创建应用授权策略
     *
     * @param param {@link AppAccessPolicyCreateParam}
     * @return @{link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createAppAccessPolicy(AppAccessPolicyCreateParam param) {
        List<AppAccessPolicyEntity> list = appAccessPolicyConverter
            .appPolicyCreateParamConvertToEntity(param);
        //判断是否已经存在
        for (AppAccessPolicyEntity policy : list) {
            Optional<AppAccessPolicyEntity> policyEntity = appAccessPolicyRepository
                .findByAppIdAndSubjectIdAndSubjectType(policy.getAppId(), policy.getSubjectId(),
                    policy.getSubjectType());
            if (policyEntity.isEmpty()) {
                appAccessPolicyRepository.save(policy);
                setAuditTarget(policy);
                continue;
            }
            AppAccessPolicyEntity entity = policyEntity.get();
            BeanUtils.merge(policy, entity, LAST_MODIFIED_TIME, LAST_MODIFIED_BY);
            appAccessPolicyRepository.save(entity);
            setAuditTarget(entity);
        }
        return true;
    }

    /**
     * 删除应用授权策略
     *
     * @param id {@link  String}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAppAccessPolicy(String id) {
        Optional<AppAccessPolicyEntity> optional = appAccessPolicyRepository.findById(id);
        //策略不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("删除失败，应用授权策略不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        AppAccessPolicyEntity entity = optional.get();
        appAccessPolicyRepository.deleteById(id);
        setAuditTarget(entity);
        return true;
    }

    /**
     * 启用应用访问授权
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean enableAppAccessPolicy(String id) {
        Optional<AppAccessPolicyEntity> optional = appAccessPolicyRepository.findById(id);
        //策略不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("启用失败，应用授权策略不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        Integer count = appAccessPolicyRepository.updateStatus(id, Boolean.TRUE);
        AppAccessPolicyEntity entity = optional.get();
        setAuditTarget(entity);
        return count > 0;
    }

    /**
     * 禁用应用访问授权
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean disableAppAccessPolicy(String id) {
        Optional<AppAccessPolicyEntity> optional = appAccessPolicyRepository.findById(id);
        //策略不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("禁用失败，应用授权策略不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        AppAccessPolicyEntity entity = optional.get();
        Integer count = appAccessPolicyRepository.updateStatus(id, Boolean.FALSE);
        setAuditTarget(entity);
        return count > 0;
    }

    private void setAuditTarget(AppAccessPolicyEntity entity) {
        Optional<AppEntity> appEntityOptional = appRepository.findById(entity.getAppId());
        appEntityOptional.ifPresent(appEntity -> AuditContext.addTarget(Target.builder()
            .id(entity.getAppId()).name(appEntity.getName()).type(TargetType.APPLICATION).build()));

        switch (entity.getSubjectType()) {
            case USER -> userRepository.findById(entity.getSubjectId()).ifPresent(
                userEntity -> AuditContext.addTarget(Target.builder().id(entity.getSubjectId())
                    .name(userEntity.getUsername()).type(TargetType.USER).build()));
            case USER_GROUP -> userGroupRepository.findById(entity.getSubjectId()).ifPresent(
                groupEntity -> AuditContext.addTarget(Target.builder().id(entity.getSubjectId())
                    .name(groupEntity.getName()).type(TargetType.USER_GROUP).build()));
            case ORGANIZATION ->
                organizationRepository.findById(entity.getSubjectId())
                    .ifPresent(organizationEntity -> AuditContext.addTarget(Target.builder()
                        .id(entity.getSubjectId()).name(organizationEntity.getName())
                        .type(TargetType.ORGANIZATION).build()));
        }
    }

    /**
     * AppPolicyConverter
     */
    private final AppAccessPolicyConverter  appAccessPolicyConverter;

    /**
     * AppPolicyRepository
     */
    private final AppAccessPolicyRepository appAccessPolicyRepository;

    /**
     * UserRepository
     */
    private final UserRepository            userRepository;

    /**
     * OrganizationRepository
     */
    private final OrganizationRepository    organizationRepository;

    /**
     * UserGroupRepository
     */
    private final UserGroupRepository       userGroupRepository;

    /**
     * AppRepository
     */
    private final AppRepository             appRepository;

}
