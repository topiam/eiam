/*
 * eiam-portal - Employee Identity and Access Management
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
package cn.topiam.employee.portal.constant;

import static cn.topiam.employee.common.constant.AuthorizeConstants.AUTHORIZE_PATH;
import static cn.topiam.employee.common.constant.ProtocolConstants.APP_CODE_VARIABLE;
import static cn.topiam.employee.support.constant.EiamConstants.V1_API_PATH;

/**
 * 认证常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/8 21:29
 */
public class PortalConstants {

    public static final String BIND_ACCOUNT                  = "/bind-account";
    public static final String TOPIAM_BIND_STATE_COOKIE_NAME = "topiam-bind-state-cookie";

    /**
     * OAuth2 IDP SSO 发起
     */
    public static final String IDP_OAUTH2_SSO_INITIATOR      = AUTHORIZE_PATH + "/oauth2/"
                                                               + APP_CODE_VARIABLE + "/initiator";

    /**
     * FORM IDP SSO 发起
     */
    public static final String IDP_FORM_SSO_INITIATOR        = AUTHORIZE_PATH + "/form/"
                                                               + APP_CODE_VARIABLE + "/initiator";

    /**
     * JWT IDP SSO 发起
     */
    public static final String IDP_JWT_SSO_INITIATOR         = AUTHORIZE_PATH + "/jwt/"
                                                               + APP_CODE_VARIABLE + "/initiator";

    public static final String ACCOUNT_PATH                  = V1_API_PATH + "/account";

    /**
     * 忘记密码预认证
     */
    public static final String PREPARE_FORGET_PASSWORD       = "/prepare_forget_password";

    /**
     * 忘记密码
     */
    public static final String FORGET_PASSWORD               = "/forget_password";

    /**
     * 忘记密码发送验证码
     */
    public static final String FORGET_PASSWORD_CODE          = "/forget_password_code";
}
