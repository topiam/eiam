/*
 * eiam-authentication-dingtalk - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.dingtalk.constant;

/**
 * 钉钉认证常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/9 21:19
 */
public final class DingTalkAuthenticationConstants {
    public static final String APP_ID                  = "appid";
    public static final String AUTH_CODE               = "authCode";
    public static final String GET_USERINFO_BY_CODE    = "https://oapi.dingtalk.com/sns/getuserinfo_bycode";
    public static final String SCAN_CODE_URL_AUTHORIZE = "https://oapi.dingtalk.com/connect/oauth2/sns_authorize";
    public static final String URL_AUTHORIZE           = "https://login.dingtalk.com/oauth2/auth";
    public static final String GET_USERID_BY_UNIONID   = "https://oapi.dingtalk.com/user/getUseridByUnionid";
    public static final String GET_USERINFO_BY_USERID  = "https://oapi.dingtalk.com/topapi/v2/user/get";
}
