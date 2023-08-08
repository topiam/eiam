/*
 * eiam-identity-source-core - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.core.client;

import org.springframework.util.Assert;

import cn.topiam.employee.identitysource.core.IdentitySourceConfig;

import lombok.Getter;

/**
 * AbstractIdentityDataProcessor
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/28 03:40
 */
@Getter
public abstract class AbstractIdentitySourceClient<T extends IdentitySourceConfig>
                                                  implements IdentitySourceClient {

    public static String ACCESS_KEY = "ACCESS_KEY";

    protected AbstractIdentitySourceClient(T config) {
        Assert.notNull(config, "IdentityProviderConfig Not Null");
        this.config = config;
    }

    /**
     * 配置
     */
    private final T config;
}
