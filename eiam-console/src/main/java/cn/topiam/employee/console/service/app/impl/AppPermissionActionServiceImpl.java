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

import org.springframework.stereotype.Service;

import com.querydsl.core.types.Predicate;

import cn.topiam.employee.common.entity.app.AppPermissionResourceEntity;
import cn.topiam.employee.common.repository.app.AppPermissionResourceRepository;
import cn.topiam.employee.console.converter.app.AppPermissionActionConverter;
import cn.topiam.employee.console.pojo.query.app.AppPermissionActionListQuery;
import cn.topiam.employee.console.pojo.result.app.AppPermissionActionListResult;
import cn.topiam.employee.console.service.app.AppPermissionActionService;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 资源权限 服务类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-10
 */
@Service
@RequiredArgsConstructor
public class AppPermissionActionServiceImpl implements AppPermissionActionService {

    /**
     * 获取资源列表
     *
     * @param query {@link AppPermissionActionListQuery}
     * @return {@link AppPermissionActionListResult}
     */
    @Override
    public List<AppPermissionActionListResult> getPermissionActionList(AppPermissionActionListQuery query) {
        Predicate predicate = appPermissionActionConverter
            .appPermissionActionListQueryConvertToPredicate(query);
        List<AppPermissionResourceEntity> list = (List<AppPermissionResourceEntity>) appPermissionResourceRepository
            .findAll(predicate);
        return appPermissionActionConverter.entityConvertToResourceActionListResult(list);
    }

    private final AppPermissionResourceRepository appPermissionResourceRepository;

    private final AppPermissionActionConverter    appPermissionActionConverter;
}
