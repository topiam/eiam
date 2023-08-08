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

import cn.topiam.employee.common.entity.app.po.AppOidcConfigPO;

/**
 * @author TopIAM
 * Created by support@topiam.cn on 2020/12/10 22:32
 */
public interface AppOidcConfigRepositoryCustomized {
    /**
     * 根据应用ID获取
     *
     * @param appId {@link Long}
     * @return {@link AppOidcConfigPO}
     */
    AppOidcConfigPO getByAppId(Long appId);

    /**
     * 根据应用 Client 获取
     *
     * @param clientId {@link String}
     * @return {@link AppOidcConfigPO}
     */
    AppOidcConfigPO getByClientId(String clientId);

    /**
     * 根据应用编码查询应用配置
     *
     * @param appCode {@link String}
     * @return {@link AppOidcConfigPO}
     */
    AppOidcConfigPO findByAppCode(String appCode);

}
