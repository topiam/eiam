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
package cn.topiam.employee.console.service.permission.impl;

import java.util.List;

import cn.topiam.employee.console.converter.permission.PermissionActionConverter;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.Predicate;

import cn.topiam.employee.common.entity.permission.AppPermissionResourceEntity;
import cn.topiam.employee.common.repository.permission.AppPermissionResourceRepository;
import cn.topiam.employee.console.pojo.query.permission.PermissionActionListQuery;
import cn.topiam.employee.console.pojo.result.permission.PermissionActionListResult;
import cn.topiam.employee.console.service.permission.PermissionActionService;

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
public class PermissionActionServiceImpl implements PermissionActionService {

    /**
     * 获取资源列表
     *
     * @param query {@link PermissionActionListQuery}
     * @return {@link PermissionActionListResult}
     */
    @Override
    public List<PermissionActionListResult> getPermissionActionList(PermissionActionListQuery query) {
        Predicate predicate = permissionActionConverter
            .appPermissionActionListQueryConvertToPredicate(query);
        List<AppPermissionResourceEntity> list = (List<AppPermissionResourceEntity>) appPermissionResourceRepository
            .findAll(predicate);
        return permissionActionConverter.entityConvertToResourceActionListResult(list);
    }

    private final AppPermissionResourceRepository appPermissionResourceRepository;

    private final PermissionActionConverter permissionActionConverter;
}
