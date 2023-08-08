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
package cn.topiam.employee.openapi.authorization.store;

import java.io.Serializable;
import java.util.List;

import cn.topiam.employee.openapi.authorization.AccessToken;

/**
 * TokenStorage
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/5/8 21:42
 */
public interface AccessTokenStore extends Serializable {

    /**
     * save
     *
     * @param token {@link AccessToken}
     */
    void save(AccessToken token);

    /**
     * 根据token删除
     *
     * @param token {@link String}
     */
    void deleteByToken(String token);

    /**
     * 根据token查询
     *
     * @param token {@link String}
     * @return {@link AccessToken}
     */
    AccessToken findByToken(String token);

    /**
     * 根据 clientId 查询
     *
     * @param clientId {@link String}
     * @return {@link List}
     */
    AccessToken findByClientId(String clientId);
}
