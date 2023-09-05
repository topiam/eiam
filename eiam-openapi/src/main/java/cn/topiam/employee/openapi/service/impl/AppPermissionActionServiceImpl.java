/*
 * eiam-openapi - Employee Identity and Access Management
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
package cn.topiam.employee.openapi.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import cn.topiam.employee.openapi.pojo.request.app.query.AppPermissionListQuery;
import cn.topiam.employee.openapi.pojo.request.app.save.AppPermissionActionCreateParam;
import cn.topiam.employee.openapi.pojo.request.app.update.ResourceActionUpdateParam;
import cn.topiam.employee.openapi.pojo.response.app.AppPermissionActionGetResult;
import cn.topiam.employee.openapi.pojo.response.app.AppPermissionActionListResult;
import cn.topiam.employee.openapi.service.AppPermissionActionService;

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
     * 获取权限列表
     *
     * @param query {@link AppPermissionListQuery}
     * @return {@link AppPermissionActionListResult}
     */
    @Override
    public List<AppPermissionActionListResult> getPermissionActionList(AppPermissionListQuery query) {
        return null;
    }

    /**
     * 获取权限详情
     *
     * @param id {@link String}
     * @return {@link AppPermissionActionGetResult}
     */
    @Override
    public AppPermissionActionGetResult getPermissionAction(String id) {
        return null;
    }

    /**
     * 删除权限
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean deletePermissionAction(String id) {
        return null;
    }

    /**
     * 创建权限
     *
     * @param param {@link AppPermissionActionCreateParam}
     * @return {@link Boolean}
     */
    @Override
    public Boolean createPermissionAction(AppPermissionActionCreateParam param) {
        return null;
    }

    /**
     * 更新权限
     *
     * @param param {@link ResourceActionUpdateParam}
     * @return {@link Boolean}
     */
    @Override
    public Boolean updatePermissionAction(ResourceActionUpdateParam param) {
        return null;
    }
}
