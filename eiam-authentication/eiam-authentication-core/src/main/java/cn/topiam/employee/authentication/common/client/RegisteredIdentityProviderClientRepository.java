/*
 * eiam-authentication-core - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.common.client;

import java.util.Optional;

/**
 * 身份提供商客户端服务
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2024/3/24 18:30
 */
public interface RegisteredIdentityProviderClientRepository {

    /**
     * 根据code查询身份提供商配置
     *
     * @param code {@link String}
     * @return {@link RegisteredIdentityProviderClient}
     */
    <T extends IdentityProviderConfig> Optional<RegisteredIdentityProviderClient<T>> findByCode(String code);
}
