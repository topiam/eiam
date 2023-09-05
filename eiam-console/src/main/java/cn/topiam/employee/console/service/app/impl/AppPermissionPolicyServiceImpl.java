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

import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.app.AppPermissionPolicyEntity;
import cn.topiam.employee.common.entity.app.po.AppPermissionPolicyPO;
import cn.topiam.employee.common.entity.app.query.AppPolicyQuery;
import cn.topiam.employee.common.exception.app.AppPolicyNotExistException;
import cn.topiam.employee.common.repository.app.AppPermissionPolicyRepository;
import cn.topiam.employee.console.converter.app.AppPermissionPolicyConverter;
import cn.topiam.employee.console.pojo.result.app.AppPermissionPolicyGetResult;
import cn.topiam.employee.console.pojo.result.app.AppPermissionPolicyListResult;
import cn.topiam.employee.console.pojo.save.app.AppPermissionPolicyCreateParam;
import cn.topiam.employee.console.pojo.update.app.AppPermissionPolicyUpdateParam;
import cn.topiam.employee.console.service.app.AppPermissionPolicyService;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 权限策略 服务实现类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-10
 */
@Service
@RequiredArgsConstructor
public class AppPermissionPolicyServiceImpl implements AppPermissionPolicyService {

    /**
     * 获取策略列表
     *
     * @param page  {@link PageModel}
     * @param query {@link AppPolicyQuery}
     * @return {@link AppPermissionPolicyListResult}
     */
    @Override
    public Page<AppPermissionPolicyListResult> getPermissionPolicyList(PageModel page,
                                                                       AppPolicyQuery query) {
        org.springframework.data.domain.Page<AppPermissionPolicyPO> data;
        QPageRequest request = QPageRequest.of(page.getCurrent(), page.getPageSize());
        data = appPermissionPolicyRepository.findPage(query, request);
        return appPermissionPolicyConverter.entityConvertToPolicyListResult(data);
    }

    /**
     * 获取策略
     *
     * @param id {@link String}
     * @return {@link AppPermissionPolicyGetResult}
     */
    @Override
    public AppPermissionPolicyGetResult getPermissionPolicy(String id) {
        return null;
    }

    /**
     * 删除策略
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean deletePermissionPolicy(String id) {
        Long policyId = Long.valueOf(id);
        appPermissionPolicyRepository.findById(policyId)
            .orElseThrow(AppPolicyNotExistException::new);
        appPermissionPolicyRepository.deleteById(policyId);
        return true;
    }

    /**
     * 创建策略
     *
     * @param param {@link AppPermissionPolicyCreateParam}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createPermissionPolicy(AppPermissionPolicyCreateParam param) {
        AppPermissionPolicyEntity resource = appPermissionPolicyConverter
            .policyCreateParamConvertToEntity(param);
        // 新增策略
        appPermissionPolicyRepository.save(resource);
        return true;
    }

    /**
     * 更新策略
     *
     * @param param {@link AppPermissionPolicyUpdateParam}
     * @return {@link Boolean}
     */
    @Override
    public Boolean updatePermissionPolicy(AppPermissionPolicyUpdateParam param) {
        AppPermissionPolicyEntity resource = appPermissionPolicyConverter
            .policyUpdateParamConvertToEntity(param);
        // 更新策略
        appPermissionPolicyRepository.save(resource);
        return null;
    }

    private final AppPermissionPolicyConverter  appPermissionPolicyConverter;

    private final AppPermissionPolicyRepository appPermissionPolicyRepository;
}
