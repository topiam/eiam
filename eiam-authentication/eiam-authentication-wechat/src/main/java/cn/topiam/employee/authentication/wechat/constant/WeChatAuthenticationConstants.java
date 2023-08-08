/*
 * eiam-authentication-wechat - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.wechat.constant;

/**
 * 微信认证常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/9 22:19
 */
public final class WeChatAuthenticationConstants {
    /**
     * 微信扫码登录常量
     */
    public static class QrConnect {

        public static final String QR_CONNECT_AUTHORIZATION_REQUEST = "https://open.weixin.qq.com/connect/qrconnect";
        public static final String ACCESS_TOKEN                     = "https://api.weixin.qq.com/sns/oauth2/access_token";
        public static final String USER_INFO                        = "https://api.weixin.qq.com/sns/userinfo";
        public static final String APP_ID                           = "appId";
        public static final String SNSAPI_LOGIN                     = "snsapi_login";
        public static final String ERROR_CODE                       = "errcode";
        public static final String SECRET                           = "secret";
        public static final String HREF                             = "href";
    }

    public static class WebPage {

        /**
         * 微信公众号webpage登录
         */
        public static final String WEB_PAGE_AUTHORIZATION_REQUEST = "https://open.weixin.qq.com/connect/oauth2/authorize";
        public static final String ACCESS_TOKEN                   = "https://api.weixin.qq.com/sns/oauth2/access_token";
        public static final String USER_INFO                      = "https://api.weixin.qq.com/sns/userinfo";

        public static final String APP_ID                         = "appId";

        public static final String SNSAPI_BASE                    = "snsapi_base";
        public static final String SNSAPI_USERINFO                = "snsapi_userinfo";
        /**
         * 无论直接打开还是做页面302重定向时候，必须带此参数
         */
        public static final String WECHAT_REDIRECT                = "wechat_redirect";
    }

}
