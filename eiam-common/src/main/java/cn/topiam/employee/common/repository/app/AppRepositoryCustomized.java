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

import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.app.po.AppPO;
import cn.topiam.employee.common.entity.app.query.GetAppListQuery;

/**
 * 应用 Repository Customized
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/5/26 23:40
 */
public interface AppRepositoryCustomized {
    /**
     * 根据主体ID获取应用列表
     *
     * @param subjectIds {@link  List}
     * @return {@link Page}
     */
    List<AppPO> getAppList(List<String> subjectIds);

    /**
     * 根据主体ID，查询参数、分页条件获取应用列表
     *
     * @param subjectIds {@link  List}
     * @param query {@link GetAppListQuery}
     * @param pageable    {@link  Pageable}
     * @return {@link Page}
     */
    Page<AppEntity> getAppList(List<String> subjectIds, GetAppListQuery query, Pageable pageable);

    /**
     * 获取用户应用数量
     *
     * @param subjectIds {@link List}
     * @return {@link Long}
     */
    Long getAppCount(List<String> subjectIds);
}
