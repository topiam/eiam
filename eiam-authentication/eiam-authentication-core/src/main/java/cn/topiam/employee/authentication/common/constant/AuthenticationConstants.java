/*
 * eiam-authentication-core - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.common.constant;

/**
 * 认证常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/20 23:19
 */
public final class AuthenticationConstants {

    /**
     * 提供商ID
     */
    public static final String PROVIDER_CODE                              = "providerId";

    public static final String INVALID_STATE_PARAMETER_ERROR_CODE         = "invalid_state_parameter";
    public static final String INVALID_NONCE_PARAMETER_ERROR_CODE         = "invalid_nonce_parameter";
    public static final String INVALID_CODE_PARAMETER_ERROR_CODE          = "invalid_code_parameter";

    public static final String AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE = "authorization_request_not_found";
}