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

import cn.topiam.employee.common.entity.app.po.AppSaml2ConfigPO;

/**
 * @author TopIAM
 * Created by support@topiam.cn on 2020/12/10 22:32
 */
public interface AppSaml2ConfigRepositoryCustomized {
    /**
     * 根据应用ID获取
     *
     * @param appId {@link Long}
     * @return {@link AppSaml2ConfigPO}
     */
    AppSaml2ConfigPO getByAppId(Long appId);

    /**
     * 根据应用code获取应用
     *
     * @param appCode {@link String}
     * @return {@link AppSaml2ConfigPO}
     */
    AppSaml2ConfigPO findByAppCode(String appCode);
}
