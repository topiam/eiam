/*
 * eiam-common - Employee Identity and Access Management Program
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
package cn.topiam.employee.common.constants;

import cn.topiam.employee.support.constant.EiamConstants;

/**
 * AuthorizeConstants
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/8 20:12
 */
public final class AuthorizeConstants {
    /**
     * LOGIN
     */
    public static final String LOGIN_PATH                = EiamConstants.API_PATH + "/login";
    public final static String AUTHORIZE_PATH            = EiamConstants.API_PATH + "/authorize";
    public static final String AUTHORIZATION_REQUEST_URI = EiamConstants.API_PATH
                                                           + "/authorization";
    /**
     * form 表单登录
     */
    public static final String FORM_LOGIN                = LOGIN_PATH;

    /**
     * sms login 路径
     */
    public static final String SMS_LOGIN                 = LOGIN_PATH + "/sms";

    /**
     * 登录配置
     */
    public static final String LOGIN_CONFIG              = LOGIN_PATH + "/config";

    /**
     * 发送OTP
     */
    public static final String LOGIN_OTP_SEND            = LOGIN_PATH + "/otp/send";

    /**
     * idp 绑定用户 路径
     */
    public static final String USER_BIND_IDP             = LOGIN_PATH + "/idp_bind_user";

    /**
     * 前端登录路由
     */
    public static final String FE_LOGIN                  = "/login";

    /**
     * LOGOUT
     */
    public static final String LOGOUT                    = EiamConstants.API_PATH + "/logout";

}
