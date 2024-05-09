/*
 * eiam-protocol-core - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.code;

import org.springframework.lang.Nullable;

/**
 * 客户端存储库
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/12/17 20:11
 */
public interface RegisteredClientRepository<T extends RegisteredClient> {
    /**
     * save 客户端
     *
     * @param client {@link T}
     */
    void save(T client);

    /**
     * 根据ID查询已注册客户端
     *
     * @param id {@link String}
     * @return {@link T}
     */
    @Nullable
    T findById(String id);

    /**
     * 根据客户端ID查询已注册客户端
     *
     * @param clientId {@link String}
     * @return {@link T}
     */
    @Nullable
    T findByClientId(String clientId);

}
