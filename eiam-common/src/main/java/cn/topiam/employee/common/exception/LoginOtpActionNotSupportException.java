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
package cn.topiam.employee.common.exception;

import cn.topiam.employee.support.exception.TopIamException;

/**
 * 登录OTP action 不支持
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/7 23:24
 */
public class LoginOtpActionNotSupportException extends TopIamException {
    public LoginOtpActionNotSupportException() {
        super("login_otp_not_support_error", "登录 OTP Action 不支持", DEFAULT_STATUS);
    }
}
