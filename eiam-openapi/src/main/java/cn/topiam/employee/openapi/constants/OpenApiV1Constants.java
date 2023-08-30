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
package cn.topiam.employee.openapi.constants;

import static cn.topiam.employee.support.constant.EiamConstants.V1_API_PATH;

/**
 * Open API 常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/9/5 21:58
 */
public class OpenApiV1Constants {
    /**
     * OpenAPI 路径
     */
    public final static String  OPEN_API_V1_PATH         = V1_API_PATH;

    public final static Integer ACCESS_TOKEN_EXPIRES_IN  = 7200;

    /**
     * 组名称
     */
    public static final String  OPEN_API_NAME            = "开放接口";

    /**
     * 访问凭证
     */
    public final static String  AUTH_PATH                = OPEN_API_V1_PATH + "/auth";

    /**
     * 账户
     */
    public final static String  ACCOUNT_PATH             = OPEN_API_V1_PATH + "/account";

    /**
     * 用户
     */
    public final static String  USER_PATH                = ACCOUNT_PATH + "/user";

    /**
     * 组织
     */
    public final static String  ORGANIZATION_PATH        = ACCOUNT_PATH + "/organization";

    /**
     * 权限管理API 路径
     */
    public final static String  OPEN_API_PERMISSION_PATH = OPEN_API_V1_PATH + "/permission";
}
