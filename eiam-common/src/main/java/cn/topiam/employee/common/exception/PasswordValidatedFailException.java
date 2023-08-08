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

import org.springframework.http.HttpStatus;

import cn.topiam.employee.support.exception.TopIamException;

/**
 * 密码验证异常
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/8 22:09
 */
public class PasswordValidatedFailException extends TopIamException {
    public PasswordValidatedFailException() {
        super("password_validated_fail_error", "密码错误，身份验证失败", HttpStatus.BAD_REQUEST);
    }

    public PasswordValidatedFailException(String description) {
        super("password_validated_fail_error", description, HttpStatus.BAD_REQUEST);
    }
}
