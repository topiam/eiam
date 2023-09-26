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

import java.util.List;

import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;

import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.app.po.AppGroupPO;
import cn.topiam.employee.common.entity.app.query.AppGroupQuery;
import cn.topiam.employee.common.entity.app.query.GetAppListQuery;
import cn.topiam.employee.common.repository.app.AppGroupRepository;
import cn.topiam.employee.common.repository.app.AppRepository;
import cn.topiam.employee.portal.converter.AppConverter;
import cn.topiam.employee.portal.converter.AppGroupConverter;
import cn.topiam.employee.portal.pojo.result.AppGroupListResult;
import cn.topiam.employee.portal.pojo.result.GetAppListResult;
import cn.topiam.employee.portal.service.AppService;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.security.util.SecurityUtils;

/**
 * AppService
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/6 22:36
 */
@Service
public class AppServiceImpl implements AppService {

    /**
     * 获取应用列表
     *
     * @param query {@link GetAppListQuery}
     * @return {@link Page}
     */
    @Override
    public Page<GetAppListResult> getAppList(GetAppListQuery query, PageModel pageModel) {
        Long userId = Long.valueOf(SecurityUtils.getCurrentUserId());
        org.springframework.data.domain.Page<AppEntity> list = appRepository.getAppList(userId,
            query, QPageRequest.of(pageModel.getCurrent(), pageModel.getPageSize()));
        return appConverter.entityConvertToAppListResult(list);
    }

    /**
     * 查询应用分组
     *
     * @param query {@link AppGroupQuery}
     * @return {@link AppGroupListResult}
     */
    @Override
    public List<AppGroupListResult> getAppGroupList(AppGroupQuery query) {
        Long userId = Long.valueOf(SecurityUtils.getCurrentUserId());
        List<AppGroupPO> list = appGroupRepository.getAppGroupList(userId, query);
        return appGroupConverter.entityConvertToAppGroupListResult(list);
    }

    /**
     * 获取应用数量
     *
     * @param groupId {@link String}
     * @return {@link Integer}
     */
    @Override
    public Long getAppCount(String groupId) {
        Long userId = Long.valueOf(SecurityUtils.getCurrentUserId());
        return appGroupRepository.getAppCount(groupId, userId);
    }

    /**
     * 获取应用数量
     *
     * @return {@link Long}
     */
    @Override
    public Long getAppCount() {
        Long userId = Long.valueOf(SecurityUtils.getCurrentUserId());
        return appRepository.getAppCount(userId);
    }

    /**
     * AppRepository
     */
    private final AppRepository      appRepository;

    /**
     * AppGroupRepository
     */
    private final AppGroupRepository appGroupRepository;

    /**
     * AppConverter
     */
    private final AppConverter       appConverter;

    /**
     * AppGroupConverter
     */
    private final AppGroupConverter  appGroupConverter;

    public AppServiceImpl(AppRepository appRepository, AppGroupRepository appGroupRepository,
                          AppConverter appConverter, AppGroupConverter appGroupConverter) {
        this.appRepository = appRepository;
        this.appGroupRepository = appGroupRepository;
        this.appConverter = appConverter;
        this.appGroupConverter = appGroupConverter;
    }

}
