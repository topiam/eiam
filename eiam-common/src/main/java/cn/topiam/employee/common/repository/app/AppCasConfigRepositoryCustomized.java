/*
 * eiam-common - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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

import cn.topiam.employee.common.entity.app.po.AppCasConfigPO;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 15:28
 */
public interface AppCasConfigRepositoryCustomized {
    /**
     * 根据应用ID获取
     *
     * @param appId {@link Long}
     * @return {@link AppCasConfigPO}
     */
    AppCasConfigPO getByAppId(Long appId);

    /**
     * 根据应用code获取应用
     *
     * @param appCode {@link String}
     * @return {@link AppCasConfigPO}
     */
    AppCasConfigPO findByAppCode(String appCode);
}
