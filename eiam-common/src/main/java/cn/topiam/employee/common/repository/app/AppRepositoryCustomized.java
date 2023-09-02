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

/**
 * 应用 Repository Customized
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/26 23:40
 */
public interface AppRepositoryCustomized {

    /**
     *
     * 获取我的应用列表
     *
     * @param name {@link  String}
     * @param userId {@link  Long}
     * @param pageable    {@link  Pageable}
     * @return {@link List}
     */
    Page<AppEntity> getAppList(Long userId, String name, Long groupId, Pageable pageable);
}
