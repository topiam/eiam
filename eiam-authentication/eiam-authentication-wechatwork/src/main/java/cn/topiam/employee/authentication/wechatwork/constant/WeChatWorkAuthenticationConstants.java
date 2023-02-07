/*
 * eiam-authentication-wechatwork - Employee Identity and Access Management Program
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
package cn.topiam.employee.authentication.wechatwork.constant;

/**
 * 企业微信
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/9 22:19
 */
public final class WeChatWorkAuthenticationConstants {
    public static final String APP_ID        = "appid";
    public static final String AGENT_ID      = "agentid";
    public static final String HREF          = "href";
    public static final String LOGIN_TYPE    = "login_type";
    public static final String JSSDK         = "jssdk";
    public static final String URL_AUTHORIZE = "https://open.work.weixin.qq.com/wwopen/sso/v1/qrConnect";

    public static final String GET_USER_INFO = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo";

    public static final String GET_TOKEN     = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
}
