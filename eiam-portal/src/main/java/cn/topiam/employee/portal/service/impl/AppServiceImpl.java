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

import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;

import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.repository.app.AppRepository;
import cn.topiam.employee.portal.converter.AppConverter;
import cn.topiam.employee.portal.pojo.query.GetAppListQuery;
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
            query.getName(), QPageRequest.of(pageModel.getCurrent(), pageModel.getPageSize()));
        return appConverter.entityConvertToAppListResult(list);
    }

    private final AppRepository appRepository;

    private final AppConverter  appConverter;

    public AppServiceImpl(AppRepository appRepository, AppConverter appConverter) {
        this.appRepository = appRepository;
        this.appConverter = appConverter;
    }

}
