/*
 * eiam-authentication-alipay - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.alipay.constant;

/**
 * 支付宝 认证常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/8/19 15:18
 */
public class AlipayAuthenticationConstants {

    public static final String AUTHORIZATION_REQUEST = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm";

    public static final String USER_INFO_SCOPE       = "auth_user";

    public static final String APP_ID                = "app_id";

    public static final String AUTH_CODE             = "auth_code";

    public static final String SUCCESS_CODE          = "200";

}
