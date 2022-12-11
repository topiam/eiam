/*
 * eiam-core - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.core.security.password.exception;

import org.springframework.http.HttpStatus;

import cn.topiam.employee.support.exception.TopIamException;

/**
 * Checked exception signalling an invalid password.
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/9 23:17
 */
public class InvalidPasswordException extends TopIamException {

    public InvalidPasswordException(String msg, Throwable t) {
        super(msg, t);
    }

    public InvalidPasswordException(String msg) {
        super(msg);
    }

    public InvalidPasswordException(String msg, HttpStatus status) {
        super(msg, status);
    }

    public InvalidPasswordException(String error, String description, HttpStatus status) {
        super(error, description, status);
    }

    public InvalidPasswordException(Throwable cause, String error, String description,
                                    HttpStatus status) {
        super(cause, error, description, status);
    }
}
