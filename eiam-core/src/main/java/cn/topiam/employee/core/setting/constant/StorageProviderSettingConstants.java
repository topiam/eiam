/*
 * eiam-core - Employee Identity and Access Management
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
package cn.topiam.employee.core.setting.constant;

/**
 * 存储提供商设置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/9 21:46
 */
public final class StorageProviderSettingConstants {

    /**
     * 存储提供商前缀
     */
    public static final String STORAGE_BEAN_NAME      = "storage";

    /**
     * 存储提供商前缀
     */
    public static final String STORAGE_SETTING_PREFIX = "storage.";
    /**
     * 存储提供商KEY
     */
    public static final String STORAGE_PROVIDER_KEY   = STORAGE_SETTING_PREFIX + "provider";
}
