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
package cn.topiam.employee.openapi.service;

import java.util.List;

import cn.topiam.employee.openapi.pojo.request.account.save.account.OrganizationCreateParam;
import cn.topiam.employee.openapi.pojo.request.account.update.account.OrganizationUpdateParam;
import cn.topiam.employee.openapi.pojo.response.account.OrganizationChildResult;
import cn.topiam.employee.openapi.pojo.response.account.OrganizationResult;

/**
 * <p>
 * 组织架构 服务类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-09
 */
public interface OrganizationService {

    /**
     * 创建组织架构
     *
     * @param param {@link OrganizationCreateParam}
     */
    void createOrg(OrganizationCreateParam param);

    /**
     * 修改组织架构
     *
     * @param param {@link OrganizationUpdateParam}
     */
    void updateOrg(OrganizationUpdateParam param);

    /**
     * 启用/禁用
     *
     * @param id      {@link String}
     * @param enabled {@link Boolean}
     * @return {@link Boolean}
     */
    void updateStatus(String id, boolean enabled);

    /**
     * 删除组织架构
     *
     * @param id {@link List}
     * @return {@link Boolean}
     */
    void deleteOrg(String id);

    /**
     * 根据ID查询组织架构
     *
     * @param id {@link String}
     * @return {@link OrganizationResult}
     */
    OrganizationResult getOrganizationById(String id);

    /**
     * 查询子组织
     *
     * @param parentId {@link String}
     * @return {@link OrganizationChildResult}
     */
    List<OrganizationChildResult> getChildOrganization(String parentId);

    /**
     * 根据外部ID获取组织ID
     *
     * @param externalId {@link String}
     * @return {@link String}
     */
    String getOrganizationIdByExternalId(String externalId);
}
