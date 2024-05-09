/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.repository.app;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.topiam.employee.common.entity.app.po.AppGroupPO;
import cn.topiam.employee.common.entity.app.query.AppGroupAssociationListQuery;
import cn.topiam.employee.common.entity.app.query.AppGroupQuery;

/**
 * @author TopIAM
 * Created by support@topiam.cn on 2023/9/8 19:20
 */
public interface AppGroupRepositoryCustomized {

    /**
     * 获取应用组应用列表
     *
     * @param query    {@link AppGroupAssociationListQuery}
     * @param pageable {@link Pageable}
     * @return {@link Page}
     */
    Page<AppGroupPO> getAppGroupList(AppGroupQuery query, Pageable pageable);

    /**
     * 查询应用组列表
     *
     * @param subjectIds  {@link List}
     * @param query {@link AppGroupQuery}
     * @return {@link List}
     */
    List<AppGroupPO> getAppGroupList(List<String> subjectIds, AppGroupQuery query);

    /**
     * 根据当前用户和分组获取应用数量
     *
     * @param groupId {@link String}
     * @param subjectIds {@link List}
     * @return {@link Long}
     */
    Long getAppCount(List<String> subjectIds, String groupId);
}
