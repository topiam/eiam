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

import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.app.AppAccessPolicyEntity;
import cn.topiam.employee.common.entity.app.po.AppAccessPolicyPO;
import cn.topiam.employee.common.entity.app.query.AppAccessPolicyQuery;
import cn.topiam.employee.common.repository.app.AppAccessPolicyRepository;
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
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_TIME;

/**
 * 应用访问权限策略 Service
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/4 21:56
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
        QPageRequest request = QPageRequest.of(pageModel.getCurrent(), pageModel.getPageSize());
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
                AuditContext
                    .setTarget(
                        Target.builder().id(policy.getSubjectId())
                            .type(
                                TargetType.getType(policy.getSubjectType().getCode().toLowerCase()))
                            .build(),
                        Target.builder().id(policy.getAppId().toString())
                            .type(TargetType.APPLICATION).build());
                continue;
            }
            AppAccessPolicyEntity entity = policyEntity.get();
            BeanUtils.merge(policy, entity, LAST_MODIFIED_TIME, LAST_MODIFIED_BY);
            appAccessPolicyRepository.save(entity);
            AuditContext.setTarget(Target.builder().id(entity.getSubjectId())
                .type(TargetType.getType(entity.getSubjectType().getCode().toLowerCase())).build(),
                Target.builder().id(entity.getAppId().toString()).type(TargetType.APPLICATION)
                    .build());
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
        Optional<AppAccessPolicyEntity> optional = appAccessPolicyRepository
            .findById(Long.valueOf(id));
        //策略不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("删除失败，应用授权策略不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        AppAccessPolicyEntity entity = optional.get();
        appAccessPolicyRepository.deleteById(Long.valueOf(id));
        AuditContext.setTarget(
            Target.builder().id(entity.getSubjectId())
                .type(TargetType.getType(entity.getSubjectType().getCode().toLowerCase())).build(),
            Target.builder().id(entity.getAppId().toString()).type(TargetType.APPLICATION).build());
        return true;
    }

    /**
     * 用户是否允许访问应用
     *
     * @param appId {@link Long}
     * @param userId {@link Long}
     * @return {@link Boolean}
     */
    @Override
    public Boolean hasAllowAccess(Long appId, Long userId) {
        return appAccessPolicyRepository.hasAllowAccess(appId, userId);
    }

    /**
     * AppPolicyConverter
     */
    private final AppAccessPolicyConverter  appAccessPolicyConverter;

    /**
     * AppPolicyRepository
     */
    private final AppAccessPolicyRepository appAccessPolicyRepository;

}
