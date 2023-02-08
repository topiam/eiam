/*
 * eiam-authentication-sms - Employee Identity and Access Management Program
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
package cn.topiam.employee.authentication.sms.constant;

import static cn.topiam.employee.common.constants.AuthorizeConstants.LOGIN_PATH;

/**
 * Sms认证常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/19 23:19
 */
public final class SmsAuthenticationConstants {

    /**
     * sms login 路径
     */
    public static final String SMS_LOGIN    = LOGIN_PATH + "/sms";

    /**
     * 发送短信OTP
     */
    public static final String SMS_SEND_OTP = SMS_LOGIN + "/send";

    public static final String PHONE_KEY    = "phone";
    public static final String CODE_KEY     = "code";

}