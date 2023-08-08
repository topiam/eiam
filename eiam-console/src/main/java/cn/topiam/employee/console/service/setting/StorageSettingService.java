/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.service.setting;

import cn.topiam.employee.console.pojo.result.setting.StorageProviderConfigResult;
import cn.topiam.employee.console.pojo.save.setting.StorageConfigSaveParam;

/**
 * 存储设置接口
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/10/1 21:19
 */
public interface StorageSettingService extends SettingService {
    /**
     * 更改存储启用禁用
     *
     * @return {@link Boolean}
     */
    Boolean disableStorage();

    /**
     * 保存存储配置
     *
     * @param param {@link StorageConfigSaveParam}
     * @return {@link Boolean}
     */
    Boolean saveStorageConfig(StorageConfigSaveParam param);

    /**
     * 获取存储配置
     *
     * @return {@link StorageProviderConfigResult}
     */
    StorageProviderConfigResult getStorageConfig();
}
