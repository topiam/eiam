/*
 * eiam-openapi - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.openapi.constants;

import static cn.topiam.employee.support.constant.EiamConstants.API_PATH;

/**
 * Open API 常量
 *
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/9/5 20:58
 */
public class OpenApiConstants {
    /**
     * OpenAPI 路径
     */
    public static final String OPEN_API_PATH            = API_PATH + "/openapi";

    /**
     * 权限管理API 路径
     */
    public static final String OPEN_API_PERMISSION_PATH = API_PATH + "/openapi/permission";

    /**
     * 组名称
     */
    public static final String OPEN_API_PERMISSION_NAME = "权限管理";
}
