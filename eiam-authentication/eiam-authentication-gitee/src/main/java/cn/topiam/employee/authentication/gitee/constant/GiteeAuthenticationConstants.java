/*
 * eiam-authentication-gitee - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.gitee.constant;

/**
 * Gitee 认证常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/9 22:19
 */
public final class GiteeAuthenticationConstants {

    public static final String AUTHORIZATION_REQUEST = "https://gitee.com/oauth/authorize";
    public static final String ACCESS_TOKEN          = "https://gitee.com/oauth/token";
    public static final String USER_INFO             = "https://gitee.com/api/v5/user";
    public static final String ERROR_CODE            = "error";
    public static final String USER_INFO_SCOPE       = "user_info";
    public static final String ID                    = "id";

}
