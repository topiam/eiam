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
package cn.topiam.employee.common.storage;

import java.lang.reflect.Constructor;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/10 21:40
 */
public class StorageFactory {

    private StorageFactory() {
    }

    /**
     * 获取实例化
     *
     * @param config {@link StorageConfig}
     * @return {@link Storage}
     */
    public static Storage getStorage(StorageConfig config) {
        try {
            Constructor<? extends Storage> constructor = config.getProvider().getStorage()
                .getDeclaredConstructor(StorageConfig.class);
            return constructor.newInstance(config);
        } catch (Exception e) {
            throw new StorageProviderException(e.getMessage(), e);
        }
    }
}
