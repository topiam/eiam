/*
 * eiam-authentication-mfa - Employee Identity and Access Management Program
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
package cn.topiam.employee.authentication.mfa.constant;

import static cn.topiam.employee.common.constants.AuthorizeConstants.LOGIN_PATH;

/**
 * Mfa 认证常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/19 23:19
 */
public final class MfaAuthenticationConstants {
    /**
     * mfa
     */
    public static final String LOGIN_MFA         = LOGIN_PATH + "/mfa";
    /**
     * mfa 登录提供者
     */
    public static final String LOGIN_MFA_FACTORS = LOGIN_MFA + "/factors";

    /**
     * maf 验证
     */
    public static final String MFA_VALIDATE      = LOGIN_MFA + "/validate";

    /**
     * 发送 OTP
     */
    public static final String OTP_SEND_OTP      = LOGIN_MFA + "/send";

}