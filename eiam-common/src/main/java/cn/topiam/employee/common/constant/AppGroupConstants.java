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

import static cn.topiam.employee.support.constant.EiamConstants.COLON;
import static cn.topiam.employee.support.constant.EiamConstants.V1_API_PATH;

/**
 * 分组管理常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/8/31 11:07
 */
public final class AppGroupConstants {

    /**
     * 分组管理API路径
     */
    public final static String APP_GROUP_PATH              = V1_API_PATH + "/app_group";

    /**
     * 组名称
     */
    public static final String APP_GROUP_GROUP_NAME        = "分组管理";

    /**
     * 分组配置缓存前缀
     */
    public static final String APP_GROUP_CACHE_NAME_PREFIX = "app_group" + COLON;

    /**
     * 分组基本信息
     */
    public static final String APP_GROUP_CACHE_NAME        = APP_GROUP_CACHE_NAME_PREFIX + "basic";
}
