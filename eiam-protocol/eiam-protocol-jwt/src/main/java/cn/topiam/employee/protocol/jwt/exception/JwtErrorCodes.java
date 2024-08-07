/*
 * eiam-protocol-jwt - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.jwt.exception;

/**
 * 异常CODE
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/7/19 21:19
 */
public final class JwtErrorCodes {

    /**
     * 无效请求
     */
    public static final String INVALID_REQUEST         = "invalid_request";

    /**
     * 服务器异常
     */
    public static final String SERVER_ERROR            = "server_error";

    /**
     * 生成ID token 异常
     */
    public static final String GENERATE_ID_TOKEN_ERROR = "generate_id_token_error";

    /**
     * 配置错误
     */
    public static final String CONFIG_ERROR            = "config_error";

    /**
     * 应用账户不存在
     */
    public static final String APP_ACCOUNT_NOT_EXIST   = "app_account_not_exist";

}
