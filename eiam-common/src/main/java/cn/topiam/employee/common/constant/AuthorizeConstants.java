/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.constant;

import cn.topiam.employee.support.constant.EiamConstants;

/**
 * AuthorizeConstants
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/8 21:12
 */
public final class AuthorizeConstants {
    /**
     * LOGIN
     */
    public static final String LOGIN_PATH                = EiamConstants.V1_API_PATH + "/login";
    public final static String AUTHORIZE_PATH            = EiamConstants.V1_API_PATH + "/authorize";
    public static final String AUTHORIZATION_REQUEST_URI = EiamConstants.V1_API_PATH
                                                           + "/authorization";
    /**
     * form 表单登录
     */
    public static final String FORM_LOGIN                = LOGIN_PATH;

    /**
     * 验证码登录路径
     */
    public static final String OTP_LOGIN                 = LOGIN_PATH + "/otp";

    /**
     * 登录配置
     */
    public static final String LOGIN_CONFIG              = LOGIN_PATH + "/config";

    /**
     * 发送OTP
     */
    public static final String LOGIN_OTP_SEND            = LOGIN_PATH + "/otp/send";

    /**
     * 前端登录路由
     */
    public static final String FE_LOGIN                  = "/login";

    /**
     * LOGOUT
     */
    public static final String LOGOUT                    = EiamConstants.V1_API_PATH + "/logout";

}
