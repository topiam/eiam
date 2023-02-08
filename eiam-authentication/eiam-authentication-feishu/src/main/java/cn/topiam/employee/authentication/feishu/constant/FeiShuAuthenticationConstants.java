/*
 * eiam-authentication-feishu - Employee Identity and Access Management Program
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
package cn.topiam.employee.authentication.feishu.constant;

/**
 * 飞书认证常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/19 23:19
 */
public final class FeiShuAuthenticationConstants {

    public static final String AUTHORIZATION_REQUEST = "https://passport.feishu.cn/suite/passport/oauth/authorize";
    public static final String ACCESS_TOKEN          = "https://passport.feishu.cn/suite/passport/oauth/token";
    public static final String USER_INFO             = "https://passport.feishu.cn/suite/passport/oauth/userinfo";

    public static final String CLIENT_ID             = "client_id";
    public static final String CLIENT_SECRET         = "client_secret";
    public static final String OPEN_ID               = "open_id";

    public static final String CODE                  = "code";
    public static final String HREF                  = "href";

}