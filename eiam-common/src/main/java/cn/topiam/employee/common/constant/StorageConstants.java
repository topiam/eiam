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
package cn.topiam.employee.common.constant;

import cn.topiam.employee.support.constant.EiamConstants;

/**
 * 存储常量
 *
 * @author TopIAM
 */
public final class StorageConstants {

    /**
     * 存储API路径
     */
    public final static String STORAGE_PATH       = EiamConstants.V1_API_PATH + "/storage";

    /**
     * 文件存储
     */
    public static final String STORAGE_GROUP_NAME = "文件存储";

    /**
     * 存储缓存 cacheName
     */
    public static final String STORAGE_CACHE_NAME = "storage";

    /**
     * url 正则
     */
    public static final String URL_REGEXP         = "^https?://[\\w.-]+(:\\d+)?$";
}
