/*
 * eiam-authentication-wechatwork - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.wechatwork.constant;

/**
 * 企业微信
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/9 22:19
 */
public final class WeChatWorkAuthenticationConstants {
    public final static String APP_ID        = "appid";
    public final static String AGENT_ID      = "agentid";
    public final static String HREF          = "href";
    public final static String LOGIN_TYPE    = "login_type";
    public final static String JSSDK         = "jssdk";
    public final static String URL_AUTHORIZE = "https://open.work.weixin.qq.com/wwopen/sso/v1/qrConnect";

    public final static String GET_USER_INFO = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo";

    public final static String GET_TOKEN     = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
}
